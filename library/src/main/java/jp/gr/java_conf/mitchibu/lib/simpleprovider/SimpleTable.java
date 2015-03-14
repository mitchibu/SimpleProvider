package jp.gr.java_conf.mitchibu.lib.simpleprovider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation.Column;
import jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation.Table;
import jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation.View;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public abstract class SimpleTable {
	public static String getName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if(table == null) {
			View view = clazz.getAnnotation(View.class);
			if(view == null) {
				return clazz.getSimpleName().toLowerCase(Locale.getDefault());
			} else {
				return view.value();
			}
		} else {
			return table.value();
		}
	}

	public static void create(Class<?> clazz, SQLiteDatabase db) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
		Table table = clazz.getAnnotation(Table.class);
		if(table == null) {
			View view = clazz.getAnnotation(View.class);
			if(view == null) {
				throw new IllegalArgumentException();
			} else {
				createView(clazz, db, view.value());
			}
		} else {
			createTable(clazz, db, table.value());
		}
	}

	private static void createTable(Class<?> clazz, SQLiteDatabase db, String name) throws IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(name);
		sb.append('(');

		boolean first = true;
		List<String> primaryKey = new ArrayList<String>();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if(column != null) {
				String columnName = (String)field.get(null);
				if(TextUtils.isEmpty(columnName)) columnName = field.getName().toLowerCase(Locale.getDefault());
				if(first) first = false;
				else sb.append(',');

				if(column.primaryKey()) primaryKey.add(columnName);
				sb.append(columnName).append(' ').append(column.type().name());
				if(!column.isNull()) sb.append(' ').append("not null");
				if(column.unique()) sb.append(' ').append("unique");
				if(column.autoIncrement()) sb.append(' ').append("autoincrement");
				String defaultValue = column.defaultValue();
				if(!TextUtils.isEmpty(defaultValue)) sb.append(' ').append("default").append(' ').append(defaultValue);
			}
		}

		first = true;
		if(!primaryKey.isEmpty()) {
			sb.append(",primary key(");
			for(String columnName : primaryKey) {
				if(first) first = false;
				else sb.append(',');
				sb.append(columnName);
			}
			sb.append(')');
		}
		sb.append(')');
		db.execSQL(sb.toString());
	}

	private static void createView(Class<?> clazz, SQLiteDatabase db, String name) throws IllegalAccessException, NoSuchFieldException {
		StringBuilder sb = new StringBuilder();
		sb.append("create view ").append(name);
		sb.append(" as ").append((String)clazz.getDeclaredField("_CONDITION").get(null));
		db.execSQL(sb.toString());
	}
}
