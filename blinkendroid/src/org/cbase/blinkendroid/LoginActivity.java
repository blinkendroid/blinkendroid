package org.cbase.blinkendroid;

import java.util.ArrayList;
import java.util.List;

import org.cbase.blinkendroid.network.multicast.IServerHandler;
import org.cbase.blinkendroid.network.multicast.ReceiverThread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LoginActivity extends Activity {

    private final List<ListEntry> serverList = new ArrayList<ListEntry>();
    private ServerListAdapter serverListAdapter;
    private ListView serverListView;
    private ReceiverThread receiverThread;

    private static class ListEntry {
	public String name;
	public String ip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	setContentView(R.layout.login);
	final Button startServerButton = (Button) findViewById(R.id.login_start_server);
	serverListView = (ListView) findViewById(R.id.login_server_list);

	serverListAdapter = new ServerListAdapter(serverList);

	startServerButton.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {
		startActivity(new Intent(LoginActivity.this,
			ServerActivity.class));
	    }
	});

	serverListView.setAdapter(serverListAdapter);
	serverListView.setOnItemClickListener(new OnItemClickListener() {

	    public void onItemClick(AdapterView<?> parent, View v,
		    int position, long id) {
		final ListEntry entry = serverList.get(position);
		final Intent intent = new Intent(LoginActivity.this,
			Player.class);
		intent.putExtra(Player.INTENT_EXTRA_IP, entry.ip);
		intent.putExtra(Player.INTENT_EXTRA_PORT, 4444);
		startActivity(intent);
	    }
	});
    }

    @Override
    protected void onResume() {

	super.onResume();

	receiverThread = new ReceiverThread();
	receiverThread.addHandler(new IServerHandler() {
	    public void foundServer(final String serverName,
		    final String serverIp) {
		runOnUiThread(new Runnable() {
		    public void run() {
			Log.d(Constants.LOG_TAG, "=== " + serverName + " "
				+ serverIp);

			final ListEntry entry = new ListEntry();
			entry.name = serverName;
			entry.ip = serverIp;

			serverList.add(entry);
			serverListAdapter.notifyDataSetChanged();
			serverListView.setVisibility(View.VISIBLE);
		    }
		});
	    }
	});
	receiverThread.start();
    }

    @Override
    protected void onPause() {

	if (receiverThread != null) {
	    receiverThread.shutdown();
	    receiverThread = null;
	}

	super.onPause();
    }

    private class ServerListAdapter extends BaseAdapter {

	private final List<ListEntry> serverList;

	public ServerListAdapter(List<ListEntry> serverList) {
	    this.serverList = serverList;
	}

	public View getView(int position, View row, ViewGroup parent) {

	    if (row == null)
		row = getLayoutInflater().inflate(
			android.R.layout.two_line_list_item, null);

	    final ListEntry entry = serverList.get(position);

	    ((TextView) row.findViewById(android.R.id.text1))
		    .setText(entry.name);

	    ((TextView) row.findViewById(android.R.id.text2)).setText(entry.ip);

	    return row;
	}

	public long getItemId(int position) {
	    return position;
	}

	public Object getItem(int position) {
	    return serverList.get(position);
	}

	public int getCount() {
	    return serverList.size();
	}
    }
}
