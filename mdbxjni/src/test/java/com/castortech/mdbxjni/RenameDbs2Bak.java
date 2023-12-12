/**
 * Copyright (C) 2013, RedHat, Inc.
 *
 *		http://www.redhat.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.castortech.mdbxjni;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for the MDBX API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings("nls")
public class RenameDbs2Bak {
	private static String path;

	static {
		Setup.setLibraryPaths(Setup.RELEASE_MODE);
		Optional.ofNullable(System.getProperty("db.path")).ifPresent(val -> path = val);
	}

	private Env env;

	@Before
	public void before() throws Exception {
		if (path == null || path.isEmpty()) {
			throw new IllegalStateException("Path must be set via db.path");
		}
		System.out.println("Using path:" + path);
		openEnv();
	}

	private void openEnv() {
		EnvConfig envConfig = new EnvConfig();
//		envConfig.setDELifoReclaim(true);
//
//		envConfig.setMapSize(2000L * 1024 * 1024);	//2gb
//		envConfig.setMapGrowth(1000L * 1024 * 1024);	//1gb

		env = new Env();
		env.setMaxDbs(100);
		Env.pushMemoryPool(1024*512);
		env.open(path, envConfig);
	}

	@After
	public void after() {
		Env.popMemoryPool();
		env.close();
	}

	@Test
	public void testRenameDb() throws Exception {
		try (Transaction tx = env.createWriteTransaction()) {
			List<String> databases = env.listDatabases(tx);

			databases.stream()
					.sorted()
					.forEach(dbName -> {
						Database db = env.openDatabase(tx, dbName, 0);
						try {
							db.rename(tx, dbName + "_bak");
						}
						finally {
							db.close();
						}
			});
		}

		System.out.println("After renaming DBs");
		try (Transaction tx = env.createWriteTransaction()) {
			List<String> databases = env.listDatabases(tx);

			databases.stream()
					.sorted()
					.forEach(dbName -> {
						System.out.println("found:" + dbName);
			});
		}
	}
}