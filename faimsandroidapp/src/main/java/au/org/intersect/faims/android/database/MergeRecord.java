package au.org.intersect.faims.android.database;

import java.io.File;

import jsqlite.Stmt;

import au.org.intersect.faims.android.log.FLog;

public class MergeRecord extends Database {
	
	public MergeRecord(File dbFile) {
		super(dbFile);
	}

	public void dumpDatabaseTo(File file) throws Exception {
		FLog.d("dumping database to " + file.getAbsolutePath());
		jsqlite.Database db = null;
		try {
			db = openDB(jsqlite.Constants.SQLITE_OPEN_READWRITE);
			String query = DatabaseQueries.DUMP_DATABASE_TO(file.getAbsolutePath());
			db.exec(query, createCallback());
		} catch (Exception e) {
			FLog.d(e.getMessage().toString());
		} finally {
			closeDB(db);
		}
	}
	
	public void dumpDatabaseTo(File file, String fromTimestamp) throws Exception {
		FLog.d("dumping database to " + file.getAbsolutePath());
		jsqlite.Database db = null;
		try {
			db = openDB(jsqlite.Constants.SQLITE_OPEN_READWRITE);
			String query = DatabaseQueries.DUMP_DATABASE_TO(file.getAbsolutePath(), fromTimestamp);
			db.exec(query, createCallback());
		} finally {
			closeDB(db);
		}
	}
	
	public boolean isEmpty(File file) throws Exception {
		FLog.d("checking if database " + file.getAbsolutePath() + " is empty");
		jsqlite.Database db = null;
		try {
			db = openDB(file, jsqlite.Constants.SQLITE_OPEN_READONLY);
			
			if (!isTableEmpty(db, "archentity")) return false;
			if (!isTableEmpty(db, "aentvalue")) return false;
			if (!isTableEmpty(db, "relationship")) return false;
			if (!isTableEmpty(db, "relnvalue")) return false;
			if (!isTableEmpty(db, "aentreln")) return false;
			
			return true;
		} finally {
			closeDB(db);
		}
	}
	
	private boolean isTableEmpty(jsqlite.Database db, String table) throws Exception {
		Stmt st = null;
		try {
			st = db.prepare("select count(*) from " + table + ";");
			st.step();
			int count = st.column_int(0);
			if (count == 0) {
				return true;
			}
			return false;
		} finally {
			closeStmt(st);
		}
	}

	private boolean hasPasswordColumn(jsqlite.Database db) throws Exception {
		Stmt st = null;
		try {
			st = db.prepare(DatabaseQueries.TEST_FOR_PASSWORD_COLUMN);
			st.step();
			int hasPassword = Integer.parseInt(st.column(0).toString());
			if (hasPassword == 1) {
				return true;
			}
			return false;
		} finally {
			closeStmt(st);
		}
	}
	public void mergeDatabaseFrom(File file) throws Exception {
		FLog.d("merging database");
		jsqlite.Database db = null;
		try {
			db = openDB(jsqlite.Constants.SQLITE_OPEN_READWRITE);
			String query = DatabaseQueries.MERGE_DATABASE_FROM(file.getAbsolutePath(),hasPasswordColumn(db));
			db.exec(query, createCallback());
		} finally {
			closeDB(db);
		}
	}

}
