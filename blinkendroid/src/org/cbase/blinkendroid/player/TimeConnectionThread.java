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

package org.cbase.blinkendroid.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

public class TimeConnectionThread extends Thread {
	private Socket socket;
	private PlayerThread playerThread;
	public TimeConnectionThread(PlayerThread playerThread, Socket clientSocket) {
		socket	=	clientSocket;
		this.playerThread =	playerThread;
	}

	@Override
	public void run() {
		try {
			Log.i(Constants.LOG_TAG,"TimeConnectionThread started");
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {	
			   long globalTime	=	Long.parseLong(inputLine);
			   playerThread.setGlobalTime(globalTime);
			}			

			in.close();
			socket.close();
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Could not get Streams",e);
		}
	}
}
