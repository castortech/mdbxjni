package com.castortech.mdbxjni;

public class EnvConfig {
	private boolean noSubDir = false;
	private boolean noSync = false;
	private boolean readOnly = false;
	private boolean noMetaSync = false;
	private boolean writeMap = false;
	private boolean mapAsync = false;
	private boolean noTLS = false;
	private boolean noReadAhead = false;
	private boolean noMemInit = false;
	private boolean coalesce = false;
	private boolean lifoReclaim = false;
	private boolean utterlyNoSync = false;
	private boolean pagePerturb = false;
	private int mode = 644;
	private long maxReaders = -1;
	private long maxDbs = -1;
	private long mapSize = -1;

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public long getMaxReaders() {
		return maxReaders;
	}

	public void setMaxReaders(long maxReaders) {
		this.maxReaders = maxReaders;
	}

	public long getMaxDbs() {
		return maxDbs;
	}

	public void setMaxDbs(long maxDbs) {
		this.maxDbs = maxDbs;
	}

	public long getMapSize() {
		return mapSize;
	}

	public void setMapSize(long mapSize) {
		this.mapSize = mapSize;
	}

	public boolean isNoSubDir() {
		return noSubDir;
	}

	public void setNoSubDir(boolean noSubDir) {
		this.noSubDir = noSubDir;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isWriteMap() {
		return writeMap;
	}

	public void setWriteMap(boolean writeMap) {
		this.writeMap = writeMap;
	}

	public boolean isNoMetaSync() {
		return noMetaSync;
	}

	public void setNoMetaSync(boolean noMetaSync) {
		this.noMetaSync = noMetaSync;
	}

	public boolean isNoSync() {
		return noSync;
	}

	public void setNoSync(boolean noSync) {
		this.noSync = noSync;
	}

	public boolean isMapAsync() {
		return mapAsync;
	}

	public void setMapAsync(boolean mapAsync) {
		this.mapAsync = mapAsync;
	}

	public boolean isNoTLS() {
		return noTLS;
	}

	public void setNoTLS(boolean noTLS) {
		this.noTLS = noTLS;
	}

	public boolean isNoReadAhead() {
		return noReadAhead;
	}

	public void setNoReadAhead(boolean noReadAhead) {
		this.noReadAhead = noReadAhead;
	}

	public boolean isNoMemInit() {
		return noMemInit;
	}

	public void setNoMemInit(boolean noMemInit) {
		this.noMemInit = noMemInit;
	}

	public boolean isCoalesce() {
		return coalesce;
	}

	public void setCoalesce(boolean coalesce) {
		this.coalesce = coalesce;
	}

	public boolean isLifoReclaim() {
		return lifoReclaim;
	}

	public void setLifoReclaim(boolean lifoReclaim) {
		this.lifoReclaim = lifoReclaim;
	}

	public boolean isUtterlyNoSync() {
		return utterlyNoSync;
	}

	public void setUtterlyNoSync(boolean utterlyNoSync) {
		this.utterlyNoSync = utterlyNoSync;
	}

	public boolean isPagePerturb() {
		return pagePerturb;
	}

	public void setPagePerturb(boolean pagePerturb) {
		this.pagePerturb = pagePerturb;
	}

	/**
	 * Returns a copy of this configuration object.
	 */
	public EnvConfig cloneConfig() {
		try {
			return (EnvConfig)super.clone();
		}
		catch (CloneNotSupportedException willNeverOccur) {
			return null;
		}
	}
}
