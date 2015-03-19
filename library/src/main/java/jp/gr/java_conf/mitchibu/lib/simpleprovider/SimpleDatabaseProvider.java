package jp.gr.java_conf.mitchibu.lib.simpleprovider;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;

import jp.gr.java_conf.mitchibu.lib.simpleprovider.annotation.Database;

public class SimpleDatabaseProvider extends SimpleProvider {
	public static final String SQL_INSERT_OR_REPLACE = "__sql_insert_or_replace__";

//	public static String getAuthority(Class<? extends SimpleProvider> clazz) {
//		return clazz.getName();
//	}

	private UriMatcher matcher = null;
	private SQLiteOpenHelper helper = null;

	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
		SQLiteDatabase db = null;
		try {
			db = helper().getWritableDatabase();
			db.beginTransaction();

			ContentProviderResult[] result = super.applyBatch(operations);
			db.setTransactionSuccessful();
			return result;
		} finally {
			if(db != null && db.inTransaction()) db.endTransaction();
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase db = null;
		try {
			db = helper().getWritableDatabase();
			db.beginTransaction();

			int count = 0;
			for(ContentValues v : values) {
				if(insert(uri, v) != null) ++ count;
			}
			db.setTransactionSuccessful();
			return count;
		} finally {
			if(db != null && db.inTransaction()) db.endTransaction();
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = table(uri);
		if(table == null) return 0;

		int count = helper().getWritableDatabase().delete(table, selection, selectionArgs);
		if(count > 0) notifyChange(uri);
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String table = table(uri);
		if(table == null) return null;

		boolean replace = false;
		if(values.containsKey(SQL_INSERT_OR_REPLACE)) {
			replace = values.getAsBoolean(SQL_INSERT_OR_REPLACE);
			values.remove(SQL_INSERT_OR_REPLACE);
	    }

		long id;
		if(replace) id = helper().getWritableDatabase().replace(table, null, values);
		else id = helper().getWritableDatabase().insert(table, null, values);
		if(id < 0) return null;

		uri = ContentUris.withAppendedId(uri, id);
		notifyChange(uri);
		return uri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		String table = table(uri);
		if(table == null) return 0;

		int count = helper().getWritableDatabase().update(table, values, selection, selectionArgs);
		if(count > 0) notifyChange(uri);
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder builder = buildQuery(uri);
		if(builder == null) return null;

		Cursor cursor = builder.query(helper().getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

//	@Override
//	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
//		return openFileHelper(uri, mode);
//	}

	protected void notifyChange(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
	}

	protected void addURI(String path, int code) {
		if(matcher == null) matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(getAuthority(), path, code);
	}

	protected int match(Uri uri) {
		return matcher.match(uri);
	}

	private SQLiteOpenHelper helper() {
		if(helper != null) return helper;
		Database annotation = getClass().getAnnotation(Database.class);
		if(annotation == null) return helper;
		String name = annotation.name();
		int version = annotation.version();
		return helper = new SQLiteOpenHelper(getContext(), TextUtils.isEmpty(name) ? null : name, null, version) {
			@Override
			public void onCreate(SQLiteDatabase db) {
				onCreateDatabase(db);
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				onUpdateDatabase(db, oldVersion, newVersion);
			}

			@Override
			public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				onUpdateDatabase(db, oldVersion, newVersion);
			}
		};
	}

	protected SQLiteQueryBuilder buildQuery(Uri uri) {
		String table = table(uri);
		if(table == null) return null;

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(table);
		return builder;
	}

	private String table(Uri uri) {
		return uri.getPathSegments().get(0);
	}

	protected void onCreateDatabase(SQLiteDatabase db) {
	}

	protected void onUpdateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
