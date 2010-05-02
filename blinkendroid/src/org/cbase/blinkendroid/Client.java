package org.cbase.blinkendroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;
import android.widget.Toast;


public class Client extends Thread {
	private Blinkendroid blinkendroid;
	private String ip;
	private String chat;


	public Client(Blinkendroid blinkendroid, String ip, String chat) {
		this.blinkendroid =blinkendroid;
		this.ip=ip;
		this.chat=chat;
	}

	@Override
	public void run() {
		Socket socket;
		try {
			socket = new Socket(ip, 4444);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			
			out.write(chat+'\n');
			out.flush();
			String response	=	in.readLine();
			Log.i(Blinkendroid.LOG_TAG, "Response: "+response);
			new ToastPoster(blinkendroid,response,Toast.LENGTH_LONG);
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
