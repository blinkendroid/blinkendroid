package org.cbase.blinkendroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
}
