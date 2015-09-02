package jp.gr.java_conf.mitchibu.simpleprovider;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Cursor c = getContentResolver().query(TestTable.getUri(this), null, null, null, null);
		c.close();
	}
}
