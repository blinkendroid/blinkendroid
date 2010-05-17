package org.cbase.blinkendroid;

import org.cbase.blinkendroid.network.BlinkendroidServer;
import org.cbase.blinkendroid.network.multicast.SenderThread;
import org.cbase.blinkendroid.utils.NetworkUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ServerActivity extends Activity {

    private SenderThread senderThread;
    private BlinkendroidServer blinkendroidServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	setContentView(R.layout.server_content);

	final TextView serverNameView = (TextView) findViewById(R.id.server_name);
	final Button startButton = (Button) findViewById(R.id.server_start);
	final Button stopButton = (Button) findViewById(R.id.server_stop);
	final Button clientButton = (Button) findViewById(R.id.server_client);

	startButton.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {

		senderThread = new SenderThread(serverNameView.getText()
			.toString());
		senderThread.start();

		blinkendroidServer = new BlinkendroidServer(
			Constants.SERVER_PORT);
		blinkendroidServer.start();

		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		clientButton.setEnabled(true);
	    }
	});

	stopButton.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {

		senderThread.shutdown();
		senderThread = null;

		blinkendroidServer.shutdown();
		blinkendroidServer = null;

		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		clientButton.setEnabled(false);
	    }
	});

	clientButton.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {

		final Intent intent = new Intent(ServerActivity.this,
			PlayerActivity.class);
		intent.putExtra(PlayerActivity.INTENT_EXTRA_IP, NetworkUtils
			.getLocalIpAddress());
		intent
			.putExtra(PlayerActivity.INTENT_EXTRA_PORT,
				Constants.SERVER_PORT);
		startActivity(intent);
	    }
	});
    }

    @Override
    protected void onDestroy() {

	if (senderThread != null) {
	    senderThread.shutdown();
	    senderThread = null;
	}

	if (blinkendroidServer != null) {
	    blinkendroidServer.shutdown();
	    blinkendroidServer = null;
	}

	super.onDestroy();
    }
}
