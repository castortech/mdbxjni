package com.castortech.mdbxjni;

import java.util.List;
import java.util.Map;

public class TestUtils {

	@SuppressWarnings("nls")
	public static String getStats(Env env, Transaction txn, Map<String, String> options) {
		StringBuilder sb = new StringBuilder();
		float percentFull;

		sb.append("Env Version:");
		sb.append(Env.version());
		Stat stat = env.stat(txn);
		sb.append(" Stats:");
		sb.append(stat.toString());
		sb.append('\n');

		EnvInfo info = env.info(txn);
		sb.append("Info:");
		sb.append(info.toString());
		sb.append('\n');

		List<String> databases = env.listDatabases(txn);
		sb.append("Dbs:\n");

		databases.stream()
				.sorted()
				.forEach(dbName -> {
					Database db = env.openDatabase(txn, dbName, 0);
					try {
						Stat dbStat = db.stat(txn);
						sb.append('\t');
						sb.append(dbName);
						sb.append(':');
						sb.append(dbStat.toString());
						sb.append('\n');
					}
					finally {
						db.close();
					}
		});

		sb.append("Cursor Pool Stats:");
		sb.append(env.getPoolStats());
		sb.append('\n');

		percentFull = env.percentageFull(txn);

		sb.append("% Full:");
		sb.append(percentFull + "%");
		sb.append('\n');

		return sb.toString();
	}
}