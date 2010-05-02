package org.cbase.blinkendroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class ConnectionThread extends Thread {
	Socket socket;
	private Blinkendroid blinkendroid;
	public ConnectionThread(Blinkendroid blinkendroid, Socket clientSocket) {
		socket	=	clientSocket;
		this.blinkendroid =	blinkendroid;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
			String request 	=	 in.readLine();
			Log.i(Blinkendroid.LOG_TAG, "Request: "+request);
			out.println(processInput(request));
			
//			while ((inputLine = in.readLine()) != null) {	
//			    outputLine = processInput(inputLine);
//			    out.println(outputLine);
//			    if (outputLine.equals("Bye"))
//			        break;
//			}			
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			Log.e(Blinkendroid.LOG_TAG, "Could not get Streams",e);
		}
	}

	private String processInput(String inputLine) {
		new ToastPoster(blinkendroid,":"+inputLine,Toast.LENGTH_SHORT);
		return inputLine;
	}


	
}
