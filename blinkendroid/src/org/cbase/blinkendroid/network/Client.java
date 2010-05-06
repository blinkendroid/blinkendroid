/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
