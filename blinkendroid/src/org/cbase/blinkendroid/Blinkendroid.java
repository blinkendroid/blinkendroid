package org.cbase.blinkendroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class Blinkendroid extends TabActivity {

    private static final String TAG_OLD = "old";
    private static final String TAG_FREQUENCY = "frequency";
    private static final String TAG_DEBUG = "debug";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.blinkendroid);

	final TabHost tabHost = getTabHost();
	final Resources res = getResources();

	tabHost.addTab(tabHost.newTabSpec(TAG_OLD).setIndicator("old",
		res.getDrawable(R.drawable.blinkendroid)).setContent(
		new Intent(this, OldBlinkendroid.class)));

	tabHost.addTab(tabHost.newTabSpec(TAG_FREQUENCY).setIndicator(
		"frequency", res.getDrawable(R.drawable.blinkendroid))
		.setContent(new Intent(this, FrequencyActivity.class)));

	tabHost.addTab(tabHost.newTabSpec(TAG_DEBUG).setIndicator("debug",
		res.getDrawable(R.drawable.blinkendroid)).setContent(
		new Intent(this, DebugActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

	getMenuInflater().inflate(R.menu.blinkenlights_options, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

	switch (item.getItemId()) {

	case R.id.blinkendroid_options_exit:
	    // releaseWakeLock();
	    Log.i(this.getClass().getName(), "Exit Button pressed");
	    System.exit(0);
	    return true;
	}

	return false;
    }
}
