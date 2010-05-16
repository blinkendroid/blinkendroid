package org.cbase.blinkendroid;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	setContentView(R.layout.login);

	final Button startServer = (Button) findViewById(R.id.login_start_server);
	startServer.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {
		Toast.makeText(getBaseContext(), "not yet", Toast.LENGTH_SHORT)
			.show();
	    }
	});

	final ListView serverList = (ListView) findViewById(R.id.login_server_list);
	serverList.setAdapter(new BaseAdapter() {

	    public View getView(int position, View row, ViewGroup parent) {

		if (row == null)
		    row = getLayoutInflater().inflate(
			    android.R.layout.two_line_list_item, null);

		((TextView) row.findViewById(android.R.id.text1))
			.setText("name");

		((TextView) row.findViewById(android.R.id.text2)).setText("ip");

		return row;
	    }

	    public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	    }

	    public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	    }

	    public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	    }
	});
	serverList.setOnItemClickListener(new OnItemClickListener() {

	    public void onItemClick(AdapterView<?> parent, View v,
		    int position, long id) {
		Toast.makeText(getBaseContext(), "not yet", Toast.LENGTH_SHORT)
			.show();
	    }
	});
    }
}
