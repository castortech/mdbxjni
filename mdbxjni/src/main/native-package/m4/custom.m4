dnl ---------------------------------------------------------------------------
dnl  Copyright (C) 2009-2011 FuseSource Corp.
dnl  http://fusesource.com
dnl  
dnl  Licensed under the Apache License, Version 2.0 (the "License");
dnl  you may not use this file except in compliance with the License.
dnl  You may obtain a copy of the License at
dnl  
dnl     http://www.apache.org/licenses/LICENSE-2.0
dnl  
dnl  Unless required by applicable law or agreed to in writing, software
dnl  distributed under the License is distributed on an "AS IS" BASIS,
dnl  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
dnl  See the License for the specific language governing permissions and
dnl  limitations under the License.
dnl ---------------------------------------------------------------------------

AC_DEFUN([CUSTOM_M4_SETUP],
[
  AC_CHECK_HEADER([pthread.h],[AC_DEFINE([HAVE_PTHREAD_H], [1], [Define to 1 if you have the <pthread.h> header file.])])

  AC_ARG_WITH([mdbx],
    [AS_HELP_STRING([--with-mdbx@<:@=PATH@:>@], [Directory where mdbx was built. Example: --with-mdbx=/opt/lmdb])],
    [
      CFLAGS="$CFLAGS -I${withval}"
      CXXFLAGS="$CXXFLAGS -I${withval}"
      AC_SUBST(CXXFLAGS)
      LDFLAGS="$LDFLAGS -lmdbx -L${withval}"
      AC_SUBST(LDFLAGS)
    ]
  )

  AC_CHECK_HEADER([mdbx.h],,AC_MSG_ERROR([cannot find headers for mdbx]))
  AC_CHECK_HEADER([sys/errno.h],[AC_DEFINE([HAVE_SYS_ERRNO_H], [1], [Define to 1 if you have the <sys/errno.h> header file.])])
])