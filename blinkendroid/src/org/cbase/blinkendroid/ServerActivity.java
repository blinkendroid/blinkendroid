package org.cbase.blinkendroid;

import org.cbase.blinkendroid.network.BlinkendroidServer;
import org.cbase.blinkendroid.network.multicast.SenderThread;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ServerActivity extends Activity {

    private SenderThread senderThread;
    private BlinkendroidServer blinkendroidServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	setContentView(R.layout.server);

	TextView serverNameView = (TextView) findViewById(R.id.server_name);
	serverNameView.setOnEditorActionListener(new OnEditorActionListener() {

	    public boolean onEditorAction(TextView v, int actionId,
		    KeyEvent event) {
		senderThread = new SenderThread(v.getText().toString());
		senderThread.start();
		blinkendroidServer = new BlinkendroidServer(4444);
		blinkendroidServer.start();
		return true;
	    }
	});
    }

    @Override
    protected void onDestroy() {

	if (senderThread != null) {
	    senderThread.shutdown();
	    senderThread = null;
	}

	if(blinkendroidServer != null){
	    blinkendroidServer.end();
	}
	super.onDestroy();
    }
}
