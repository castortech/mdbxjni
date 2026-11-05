# Generates the small FFM binding for libmdbx from mdbx.h.
# Requires C:\apps\jextract-25 and the Windows SDK / MSVC headers, because mdbx.h includes windows.h.
# Does not pass -l: the generated class uses SymbolLookup.loaderLookup(), and Java loads mdbx.dll.

param(
    [string] $MdbxHome = "C:\ALPIC\git\libmdbx",
    [string] $Jextract = "C:\apps\jextract-25\bin\jextract.bat",
    [switch] $DumpIncludes,
    [switch] $RebuildIncludes
)

$ErrorActionPreference = "Stop"

$Sdk = "C:\Program Files (x86)\Windows Kits\10\Include\10.0.22621.0"
$Msvc = "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Tools\MSVC\14.42.34433\include"
$Header = Join-Path $MdbxHome "mdbx.h"
$Includes = Join-Path $PSScriptRoot "mdbx.includes"
$Output = Join-Path $PSScriptRoot "generated"

if (-not (Test-Path $Jextract)) {
    throw "jextract not found: $Jextract"
}
if (-not (Test-Path $Header)) {
    throw "mdbx.h not found: $Header"
}

$includeDirs = @(
    $MdbxHome,
    (Join-Path $Sdk "ucrt"),
    (Join-Path $Sdk "um"),
    (Join-Path $Sdk "shared"),
    $Msvc
)
foreach ($dir in $includeDirs) {
    if (-not (Test-Path $dir)) {
        throw "Include directory not found: $dir"
    }
}

$clangArgs = @()
foreach ($dir in $includeDirs) {
    $clangArgs += @("-I", $dir)
}

$dump = Join-Path $PSScriptRoot "mdbx.dump.includes"
if ($DumpIncludes -or $RebuildIncludes) {
    & $Jextract @clangArgs --dump-includes $dump $Header
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    Write-Output "Wrote $dump"
    if (-not $RebuildIncludes) {
        exit 0
    }
}

if ($RebuildIncludes) {
    $headerText = Get-Content -LiteralPath $Header -Raw
    $exported = [System.Collections.Generic.HashSet[string]]::new()
    foreach ($match in [regex]::Matches($headerText, 'LIBMDBX_API\b')) {
        $paren = $headerText.IndexOf('(', $match.Index + $match.Length)
        if ($paren -lt 0) { continue }
        $between = $headerText.Substring($match.Index + $match.Length, $paren - $match.Index - $match.Length)
        $name = [regex]::Match($between, '([A-Za-z_][A-Za-z0-9_]*)\s*$')
        if ($name.Success) {
            [void]$exported.Add($name.Groups[1].Value)
        }
    }

    $skipFunction = {
        param($name)
        $name -eq 'mdbx_setup_debug' -or $name -like 'mdbx_env_chk*' -or $name -like '*_chk_*'
    }
    $skipType = {
        param($name)
        $name -in @('MDBX_env', 'MDBX_txn', 'MDBX_cursor', 'MDBX_debug_func') -or ($name -like 'MDBX_chk*' -and $name -ne 'MDBX_chk_histogram')
    }

    $kept = [System.Collections.Generic.List[string]]::new()
    $kept.Add('# Generated from libmdbx exports. Inline wrappers are omitted; they have no DLL symbol.')
    $kept.Add('# mdbx_setup_debug and the chk tool API are omitted. The logger callback takes va_list.')
    foreach ($line in Get-Content -LiteralPath $dump) {
        if ($line -notmatch '^(--include-(function|constant|struct|typedef))\s+(\S+)') { continue }
        $kind = $Matches[2]
        $name = $Matches[3]
        $keep = $false
        if ($kind -eq 'function') {
            $keep = $exported.Contains($name) -and -not (& $skipFunction $name)
        }
        elseif ($kind -eq 'constant') {
            $keep = $name.StartsWith('MDBX_')
        }
        else {
            $keep = (($name.StartsWith('MDBX_')) -or $name -in @('iovec', 'mdbx_mode_t', 'mdbx_pid_t', 'mdbx_tid_t', 'mdbx_filehandle_t')) -and -not (& $skipType $name)
        }
        if ($keep) {
            $kept.Add("$($Matches[1]) $name")
        }
    }
    [System.IO.File]::WriteAllLines($Includes, $kept)
    Write-Output "Wrote $($kept.Count) entries to $Includes"
}

$filterArgs = @()
Get-Content -LiteralPath $Includes | ForEach-Object {
    $line = $_.Trim()
    if ($line.Length -eq 0 -or $line.StartsWith("#")) {
        return
    }
    $comment = $line.IndexOf("#")
    if ($comment -ge 0) {
        $line = $line.Substring(0, $comment).Trim()
    }
    if ($line.Length -gt 0) {
        $filterArgs += $line.Split(" ", [System.StringSplitOptions]::RemoveEmptyEntries)
    }
}

if (Test-Path $Output) {
    Remove-Item -LiteralPath $Output -Recurse -Force
}

$argFile = Join-Path $PSScriptRoot "mdbx.jextract.args"
[System.IO.File]::WriteAllLines($argFile, $filterArgs)
& $Jextract @clangArgs "@$argFile" `
    --output $Output `
    --target-package com.castortech.mdbxjni.ffm.gen `
    --header-class-name mdbx_h `
    $Header

if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Output "Generated $Output"

$sharedClass = Join-Path $Output "com\castortech\mdbxjni\ffm\gen\mdbx_h`$shared.java"
$sharedText = [System.IO.File]::ReadAllText($sharedClass)
$pinnedLong = 'public static final ValueLayout.OfInt C_LONG = ValueLayout.JAVA_INT;'
$pinnedText = [regex]::Replace(
    $sharedText,
    'public static final ValueLayout\.Of(?:Int|Long) C_LONG = \(ValueLayout\.Of(?:Int|Long)\) Linker\.nativeLinker\(\)\.canonicalLayouts\(\)\.get\("long"\);',
    $pinnedLong)
if ($pinnedText -eq $sharedText) {
    throw "jextract C_LONG declaration was not found in $sharedClass"
}
[System.IO.File]::WriteAllText($sharedClass, $pinnedText)
Write-Output "Pinned C_LONG to ValueLayout.JAVA_INT"

function Format-HeaderJavadoc {
    param(
        [string] $Body,
        [string] $Indent,
        [string] $Newline
    )
    $escaped = $Body.Replace('&', '&amp;').Replace('<', '&lt;').Replace('*/', '*\/').Replace('{@', '{&#64;')
    $lines = $escaped -split '\r?\n'
    $clean = New-Object System.Collections.Generic.List[string]
    foreach ($line in $lines) {
        $text = $line -replace '^\s*\*\s?', ''
        if ($text -match '^\s*@') {
            $text = [regex]::Replace($text, '@', '&#64;', 1)
        }
        $clean.Add($text)
    }
    while ($clean.Count -gt 0 -and [string]::IsNullOrWhiteSpace($clean[0])) {
        $clean.RemoveAt(0)
    }
    while ($clean.Count -gt 0 -and [string]::IsNullOrWhiteSpace($clean[$clean.Count - 1])) {
        $clean.RemoveAt($clean.Count - 1)
    }
    if ($clean.Count -eq 0) {
        return ''
    }
    $doc = [System.Text.StringBuilder]::new()
    foreach ($text in $clean) {
        [void]$doc.Append("$Indent * $text$Newline")
    }
    [void]$doc.Append("$Indent *$Newline")
    return $doc.ToString()
}

function Add-MdbxHeaderComments {
    param(
        [string] $HeaderPath,
        [string] $GeneratedDir
    )
    $headerText = [System.IO.File]::ReadAllText($HeaderPath)
    $docs = @{}
    $commentPattern = [regex]::new('(?s)/\*\*(?!\s*<)(.*?)\*/')
    foreach ($match in $commentPattern.Matches($headerText)) {
        $tailStart = $match.Index + $match.Length
        $tailLen = [Math]::Min(1200, $headerText.Length - $tailStart)
        if ($tailLen -le 0) { continue }
        $tail = $headerText.Substring($tailStart, $tailLen)
        $name = $null
        if ($tail -match '(?s)^\s*(?:[A-Z][A-Z0-9_]*\s+)*LIBMDBX_INLINE_API\s*\(\s*[^,]+,\s*([A-Za-z_][A-Za-z0-9_]*)') {
            $name = $Matches[1]
        }
        elseif ($tail -match '(?s)^\s*(?:[A-Z][A-Z0-9_]*\s+)*LIBMDBX_API\b') {
            $apiIndex = $tail.IndexOf('LIBMDBX_API')
            $rest = $tail.Substring($apiIndex)
            $paren = $rest.IndexOf('(')
            if ($paren -gt 0) {
                $id = [regex]::Match($rest.Substring(0, $paren), '([A-Za-z_][A-Za-z0-9_]*)\s*$')
                if ($id.Success) { $name = $id.Groups[1].Value }
            }
        }
        elseif ($tail -match '(?s)^\s*typedef\s+enum\s+([A-Za-z_][A-Za-z0-9_]*)') {
            $name = $Matches[1]
        }
        elseif ($tail -match '(?s)^\s*typedef\s+struct\s+([A-Za-z_][A-Za-z0-9_]*)\s+([A-Za-z_][A-Za-z0-9_]*)') {
            $name = $Matches[2]
        }
        elseif ($tail -match '(?s)^\s*typedef\b[\s\S]{0,400}?\(\s*\*\s*([A-Za-z_][A-Za-z0-9_]*)\s*\)') {
            $name = $Matches[1]
        }
        elseif ($tail -match '(?s)^\s*typedef\s+[^;{]*\s([A-Za-z_][A-Za-z0-9_]*)\s*;') {
            $name = $Matches[1]
        }
        elseif ($tail -match '(?s)^\s*enum\s+([A-Za-z_][A-Za-z0-9_]*)') {
            $name = $Matches[1]
        }
        elseif ($tail -match '(?s)^\s*([A-Za-z_][A-Za-z0-9_]*)\s*(?:=|,|\})') {
            $name = $Matches[1]
        }
        if ($name) {
            $docs[$name] = $match.Groups[1].Value
        }
    }

    $javaPattern = [regex]::new('(?m)^([ \t]*)/\*\*\r?\n\1 \* \{@snippet lang=c :\r?\n(?:.*\r?\n)*?\1 \*/\r?\n\1public static [^\r\n(]+ ([A-Za-z_][A-Za-z0-9_]*)\(')
    $utf8 = New-Object System.Text.UTF8Encoding $false
    $attached = 0
    foreach ($file in (Get-ChildItem -LiteralPath $GeneratedDir -Filter *.java -Recurse)) {
        $text = [System.IO.File]::ReadAllText($file.FullName)
        $found = $javaPattern.Matches($text)
        if ($found.Count -eq 0) { continue }
        $builder = [System.Text.StringBuilder]::new()
        $cursor = 0
        $changed = $false
        foreach ($hit in $found) {
            $symbol = $hit.Groups[2].Value
            if (-not $docs.ContainsKey($symbol)) { continue }
            $newline = if ($hit.Value.Contains("`r`n")) { "`r`n" } else { "`n" }
            $indent = $hit.Groups[1].Value
            $opening = "$indent/**$newline"
            $body = Format-HeaderJavadoc $docs[$symbol] $indent $newline
            if (-not $hit.Value.StartsWith($opening) -or $body.Length -eq 0) { continue }
            [void]$builder.Append($text.Substring($cursor, $hit.Index - $cursor))
            [void]$builder.Append($opening)
            [void]$builder.Append($body)
            [void]$builder.Append($hit.Value.Substring($opening.Length))
            $cursor = $hit.Index + $hit.Length
            $changed = $true
            $attached++
        }
        if (-not $changed) { continue }
        [void]$builder.Append($text.Substring($cursor))
        [System.IO.File]::WriteAllText($file.FullName, $builder.ToString(), $utf8)
    }
    Write-Output "Attached $attached header comments from $($docs.Count) declarations"
}

Add-MdbxHeaderComments -HeaderPath $Header -GeneratedDir $Output
