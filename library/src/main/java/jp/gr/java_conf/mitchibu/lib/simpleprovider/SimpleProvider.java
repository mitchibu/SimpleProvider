package jp.gr.java_conf.mitchibu.lib.simpleprovider;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class SimpleProvider extends ContentProvider {
	public static String getAuthority(Context context, Class<? extends ContentProvider> clazz) {
		List<ProviderInfo> list = context.getPackageManager().queryContentProviders(null, 0, 0);
		for(ProviderInfo info : list) {
			if(info.name.equals(clazz.getName())) return info.authority;
		}
		return null;
	}

	public static Uri getUri(Context context, Class<? extends SimpleProvider> clazz, String path) {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority(context, clazz)).path(path).build();
	}

	private String authority;

	protected String getAuthority() {
		return authority;
	}

	protected Uri getUri(String path) {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).path(path).build();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		authority = getAuthority(getContext(), getClass());
		return !TextUtils.isEmpty(authority);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}
}
