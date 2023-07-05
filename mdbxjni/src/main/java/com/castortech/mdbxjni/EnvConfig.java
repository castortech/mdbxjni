package com.castortech.mdbxjni;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class EnvConfig implements Cloneable {
	/** Extra validation of DB structure and pages content. */
	private boolean validation = false;
	private boolean noSubDir = false;
	private boolean readOnly = false;
	private boolean exclusive = false;
	private boolean accede = false;
	private boolean writeMap = false;
	private boolean noTLS = false;
	private boolean noReadAhead = false;
	private boolean noMemInit = false;
	private boolean coalesce = false;
	private boolean lifoReclaim = false;
	private boolean pagePerturb = false;
	private boolean syncDurable = false;
	private boolean noMetaSync = false;
	private boolean safeNoSync = false;
	private boolean mapAsync = false;
	private boolean utterlyNoSync = false;

	private List<EnvOption> options = new ArrayList<>();

	private int mode = 0644;  //this is octal
	private int maxReaders = -1;
	private long maxDbs = -1;
	private long pageSize = -1;

	private long mapLower = -1;
	private long mapSize = -1;  //represents geo now
	private long mapUpper = -1;
	private long mapGrowth = -1;
	private long mapShrink = -1;

	//Section of local settings (i.e. not part of what is configured mdbx but this library)

	/** True if cursors should be pooled and use bind/renew instead of standard open/close */
	private boolean usePooledCursors = false;

	/** Time between runs of the evictor thread */
	private Duration pooledCursorTimeBetweenEvictionRuns = Duration.ofMinutes(3);

	/** Maximum number of idle cursors to maintain in the pool */
	private int pooledCursorMaxIdle = 100;

	/** Minimum time that a cursor has to be idle before it can be discarded/closed */
	private Duration pooledCursorMinEvictableIdleTime = Duration.ofMinutes(1);

	/** Maximum number of seconds to wait for active cursors to be released when the pool is closing */
	private int pooledCloseMaxWaitSeconds = 10;

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMaxReaders() {
		return maxReaders;
	}

	public void setMaxReaders(int maxReaders) {
		this.maxReaders = maxReaders;
	}

	public long getMaxDbs() {
		return maxDbs;
	}

	public void setMaxDbs(long maxDbs) {
		this.maxDbs = maxDbs;
	}

	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	public long getMapLower() {
		return mapLower;
	}

	public void setMapLower(long mapLower) {
		this.mapLower = mapLower;
	}

	public long getMapSize() {
		return mapSize;
	}

	public void setMapSize(long mapSize) {
		this.mapSize = mapSize;
	}

	public long getMapUpper() {
		return mapUpper;
	}

	public void setMapUpper(long mapUpper) {
		this.mapUpper = mapUpper;
	}

	public long getMapGrowth() {
		return mapGrowth;
	}

	public void setMapGrowth(long mapGrowth) {
		this.mapGrowth = mapGrowth;
	}

	public long getMapShrink() {
		return mapShrink;
	}

	public void setMapShrink(long mapShrink) {
		this.mapShrink = mapShrink;
	}

	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
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

	/**
	 * @deprecated
	 * This method has been renamed to {@link EnvConfig#isSafeNoSync().
	 */
	@Deprecated
	public boolean isNoSync() {
		return isSafeNoSync();
	}

	/**
	 * @deprecated
	 * This method has been renamed to {@link EnvConfig#setSafeNoSync(boolean).
	 */
	@Deprecated
	public void setNoSync(boolean noSync) {
		setSafeNoSync(noSync);
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

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public boolean isAccede() {
		return accede;
	}

	public void setAccede(boolean accede) {
		this.accede = accede;
	}

	public boolean isSyncDurable() {
		return syncDurable;
	}

	public void setSyncDurable(boolean syncDurable) {
		this.syncDurable = syncDurable;
	}

	public boolean isSafeNoSync() {
		return safeNoSync;
	}

	public void setSafeNoSync(boolean safeNoSync) {
		this.safeNoSync = safeNoSync;
	}

	public boolean isUsePooledCursors() {
		return usePooledCursors;
	}

	/**
	 * @see #usePooledCursors
	 * @param usePooledCursors
	 */
	public void setUsePooledCursors(boolean usePooledCursors) {
		this.usePooledCursors = usePooledCursors;
	}

	public Duration getPooledCursorTimeBetweenEvictionRuns() {
		return pooledCursorTimeBetweenEvictionRuns;
	}

	/**
	 * @see #pooledCursorTimeBetweenEvictionRuns
	 * @param pooledCursorTimeBetweenEvictionRuns
	 */
	public void setPooledCursorTimeBetweenEvictionRuns(Duration pooledCursorTimeBetweenEvictionRuns) {
		this.pooledCursorTimeBetweenEvictionRuns = pooledCursorTimeBetweenEvictionRuns;
	}

	public int getPooledCursorMaxIdle() {
		return pooledCursorMaxIdle;
	}

	/**
	 * @see #pooledCursorMaxIdle
	 * @param pooledCursorMaxIdle
	 */
	public void setPooledCursorMaxIdle(int pooledCursorMaxIdle) {
		this.pooledCursorMaxIdle = pooledCursorMaxIdle;
	}

	public Duration getPooledCursorMinEvictableIdleTime() {
		return pooledCursorMinEvictableIdleTime;
	}

	/**
	 * @see #pooledCursorMinEvictableIdleTime
	 * @param pooledCursorMinEvictableIdleTime
	 */
	public void setPooledCursorMinEvictableIdleTime(Duration pooledCursorMinEvictableIdleTime) {
		this.pooledCursorMinEvictableIdleTime = pooledCursorMinEvictableIdleTime;
	}

	public int getPooledCloseMaxWaitSeconds() {
		return pooledCloseMaxWaitSeconds;
	}

	/**
	 * @see #pooledCloseMaxWaitSeconds
	 * @param pooledCloseMaxWaitSeconds
	 */
	public void setPooledCloseMaxWaitSeconds(int pooledCloseMaxWaitSeconds) {
		this.pooledCloseMaxWaitSeconds = pooledCloseMaxWaitSeconds;
	}

	public List<EnvOption> getOptions() {
		return options;
	}

	public void setOptions(List<EnvOption> options) {
		this.options = options;
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