package org.cbase.blinkendroid.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.OldBlinkendroid;
import org.cbase.blinkendroid.utils.ToastPoster;

import android.util.Log;
import android.widget.Toast;

public class Client extends Thread {
    private OldBlinkendroid blinkendroid;
    private String ip;
    private String chat;

    public Client(OldBlinkendroid blinkendroid, String ip, String chat) {
	this.blinkendroid = blinkendroid;
	this.ip = ip;
	this.chat = chat;
    }

    @Override
    public void run() {
	Socket socket;
	try {
	    socket = new Socket(ip, 4444);
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(socket
		    .getInputStream()));

	    out.write(chat + '\n');
	    out.flush();
	    String response = in.readLine();
	    Log.i(Constants.LOG_TAG, "Response: " + response);
	    new ToastPoster(blinkendroid, response, Toast.LENGTH_LONG);
	    out.close();
	    in.close();
	    socket.close();
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
