package org.cbase.blinkendroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LoginActivity extends Activity {

    final List<String> serverList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	setContentView(R.layout.login);

	final ServerListAdapter serverListAdapter = new ServerListAdapter(
		serverList);

	final Button startServerButton = (Button) findViewById(R.id.login_start_server);
	startServerButton.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {
		Toast.makeText(getBaseContext(), "not yet", Toast.LENGTH_SHORT)
			.show();
		serverList.add("" + Math.random());
		serverListAdapter.notifyDataSetChanged();
	    }
	});

	final ListView serverListView = (ListView) findViewById(R.id.login_server_list);
	serverListView.setAdapter(serverListAdapter);
	serverListView.setOnItemClickListener(new OnItemClickListener() {

	    public void onItemClick(AdapterView<?> parent, View v,
		    int position, long id) {
		Toast.makeText(getBaseContext(), "not yet", Toast.LENGTH_SHORT)
			.show();
	    }
	});
    }

    private class ServerListAdapter extends BaseAdapter {

	private final List<String> serverList;

	public ServerListAdapter(List<String> serverList) {
	    this.serverList = serverList;
	}

	public View getView(int position, View row, ViewGroup parent) {

	    if (row == null)
		row = getLayoutInflater().inflate(
			android.R.layout.two_line_list_item, null);

	    final String s = serverList.get(position);

	    ((TextView) row.findViewById(android.R.id.text1)).setText("name "
		    + s);

	    ((TextView) row.findViewById(android.R.id.text2)).setText("ip");

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
