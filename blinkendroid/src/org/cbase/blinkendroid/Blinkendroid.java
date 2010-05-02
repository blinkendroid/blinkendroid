package org.cbase.blinkendroid;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Blinkendroid extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Get the button and set the listener
        Button b = (Button) this.findViewById(R.id.vibrate);
        b.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		vibrate();
	    }
	});
    }
    /**
     * Vibrates the device.
     */
    public void vibrate() {
	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	vibrator.vibrate(2000l);
    }
}