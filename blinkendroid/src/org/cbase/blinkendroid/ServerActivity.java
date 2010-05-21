package org.cbase.blinkendroid;

import java.net.SocketAddress;

import org.cbase.blinkendroid.network.ConnectionListener;
import org.cbase.blinkendroid.network.multicast.SenderThread;
import org.cbase.blinkendroid.player.bml.BLMManager;
import org.cbase.blinkendroid.player.bml.BLMManager.BLMManagerListener;
import org.cbase.blinkendroid.server.BlinkendroidServer;
import org.cbase.blinkendroid.utils.NetworkUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ServerActivity extends Activity implements ConnectionListener, BLMManagerListener {

    private SenderThread senderThread;
    private BlinkendroidServer blinkendroidServer;
    private BLMManager blmManager;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	setContentView(R.layout.server_content);

	final TextView serverNameView = (TextView) findViewById(R.id.server_name);
	final Spinner movieSpinner = (Spinner) findViewById(R.id.server_movie);
	final Button startButton = (Button) findViewById(R.id.server_start);
	final Button stopButton = (Button) findViewById(R.id.server_stop);
	final Button clientButton = (Button) findViewById(R.id.server_client);

	adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	movieSpinner.setAdapter(adapter);
	//add default video
	adapter.add("Blinkendroid");
	adapter.add("Random");
	blmManager=new BLMManager();
	//read videos from /sdcard/blinkendroid
	blmManager.readMovies(this);

	movieSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	    public void onItemSelected(AdapterView<?> arg0, View arg1,
		    int arg2, long arg3) {
		//already running?
		if(null!=blinkendroidServer && blinkendroidServer.isRunning()){
		    blinkendroidServer.switchMovie(blmManager.getBLMHeader(arg2));
		}
	    }

	    public void onNothingSelected(AdapterView<?> arg0) {

	    }
	});
	
	startButton.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {

		senderThread = new SenderThread(serverNameView.getText()
			.toString());
		senderThread.start();

		blinkendroidServer = new BlinkendroidServer(
			ServerActivity.this, Constants.SERVER_PORT);
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
		intent.putExtra(PlayerActivity.INTENT_EXTRA_PORT,
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

    public void connectionClosed(SocketAddress socketAddress) {
	runOnUiThread(new ToastPost("removed " + socketAddress.toString(),
		Toast.LENGTH_SHORT));
    }

    public void connectionOpened(SocketAddress socketAddress) {
	runOnUiThread(new ToastPost("joined " + socketAddress.toString(),
		Toast.LENGTH_SHORT));
    }

    private class ToastPost implements Runnable {
	String toast;
	int length;

	public ToastPost(String toast, int length) {
	    this.toast = toast;
	    this.length = length;
	}

	public void run() {
	    Toast.makeText(ServerActivity.this, toast, length).show();
	}
    }

    public void moviesReady() {
	runOnUiThread(new Runnable() {
	    public void run() {
		blmManager.fillArrayAdapter(adapter);
		Toast.makeText(ServerActivity.this, "Movies ready", Toast.LENGTH_SHORT).show();
	    }
	});
    }
}
