package jp.gr.java_conf.mitchibu.simpleprovider;

import android.database.sqlite.SQLiteDatabase;

import jp.gr.java_conf.mitchibu.lib.simpleprovider.SimpleDatabaseProvider;
import jp.gr.java_conf.mitchibu.lib.simpleprovider.SimpleTable;
import jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation.Database;

@Database()
public class MainProvider extends SimpleDatabaseProvider {
	@Override
	protected void onCreateDatabase(SQLiteDatabase db) {
		try {
			SimpleTable.create(TestTable.class, db);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
