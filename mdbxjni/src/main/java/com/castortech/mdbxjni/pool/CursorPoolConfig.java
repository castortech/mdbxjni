package com.castortech.mdbxjni.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import com.castortech.mdbxjni.Cursor;

public class CursorPoolConfig extends GenericKeyedObjectPoolConfig<Cursor> {
	private int closeMaxWaitSeconds = 10;

	public CursorPoolConfig() {
		setMaxTotal(-1);  //no limit on cursor total, to be managed by idle
		setMaxTotalPerKey(-1);  //no limit on cursor per key, to be managed by idle

		//settings expected to come from post constructor settings from EnvConfig
	}

	public int getCloseMaxWaitSeconds() {
		return closeMaxWaitSeconds;
	}

	public void setCloseMaxWaitSeconds(int closeMaxWaitSeconds) {
		this.closeMaxWaitSeconds = closeMaxWaitSeconds;
	}
}