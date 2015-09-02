package jp.gr.java_conf.mitchibu.simpleprovider;

import android.content.Context;
import android.net.Uri;

import jp.gr.java_conf.mitchibu.lib.simpleprovider.SimpleProvider;
import jp.gr.java_conf.mitchibu.lib.simpleprovider.SimpleTable;
import jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation.Column;
import jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation.ColumnType;
import jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation.Table;

@Table("_test")
public class TestTable {
	@Column(type = ColumnType.INTEGER, primaryKey = true)
	public static String ID = "_id";
	@Column(type = ColumnType.INTEGER, indices = {"idx1", "idx2"})
	public static String TEST1 = "_test1";
	@Column(type = ColumnType.TEXT, indices = {"idx1"})
	public static String TEST2 = "_test2";
	@Column(type = ColumnType.REAL, indices = {"idx2"})
	public static String TEST3 = "_test3";

	private static Uri URI = null;

	public static Uri getUri(Context context) {
		if(URI == null) URI = SimpleProvider.getUri(context, MainProvider.class, SimpleTable.getName(TestTable.class));
		return URI;
	}
}
