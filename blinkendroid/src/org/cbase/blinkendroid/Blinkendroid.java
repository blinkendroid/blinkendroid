package org.cbase.blinkendroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

public class Blinkendroid extends TabActivity {

    private static final String TAG_OLD = "old";
    private static final String TAG_FREQUENCY = "frequency";
    private static final String TAG_DEBUG = "debug";
    private static final String TAG_IDENTIFY = "identify";

    private boolean fullscreen = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	requestWindowFeature(Window.FEATURE_NO_TITLE);
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

	tabHost.addTab(tabHost.newTabSpec(TAG_IDENTIFY).setIndicator(
		"identify", res.getDrawable(R.drawable.blinkendroid))
		.setContent(new Intent(this, IdentifyActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

	getMenuInflater().inflate(R.menu.blinkenlights_options, menu);
	return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

	menu.findItem(R.id.blinkendroid_options_enter_fullscreen).setVisible(
		!fullscreen);
	menu.findItem(R.id.blinkendroid_options_exit_fullscreen).setVisible(
		fullscreen);
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

	case R.id.blinkendroid_options_enter_fullscreen:
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    getTabWidget().setVisibility(View.GONE);
	    fullscreen = true;
	    return true;

	case R.id.blinkendroid_options_exit_fullscreen:
	    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    getTabWidget().setVisibility(View.VISIBLE);
	    fullscreen = false;
	    return true;
	}

	return false;
    }
}
