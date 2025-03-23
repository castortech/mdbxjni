package com.castortech.mdbxjni.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import com.castortech.mdbxjni.Cursor;

/**
 * Cursor pool configuration
 *
 * @author Alain Picard
 */
public class CursorPoolConfig extends GenericKeyedObjectPoolConfig<Cursor> {
	private int closeMaxWaitSeconds = 10;

	/**
	 * Default Constructor
	 */
	public CursorPoolConfig() {
		setMaxTotal(-1);  //no limit on cursor total, to be managed by idle
		setMaxTotalPerKey(-1);  //no limit on cursor per key, to be managed by idle

		//settings expected to come from post constructor settings from EnvConfig
	}

	/**
	 * Close max wait seconds
	 * @return value for close max wait seconds
	 */
	public int getCloseMaxWaitSeconds() {
		return closeMaxWaitSeconds;
	}

	/**
	 * Set Close max wait seconds
	 *
	 * @param closeMaxWaitSeconds max wait close time in seconds
	 */
	public void setCloseMaxWaitSeconds(int closeMaxWaitSeconds) {
		this.closeMaxWaitSeconds = closeMaxWaitSeconds;
	}
}