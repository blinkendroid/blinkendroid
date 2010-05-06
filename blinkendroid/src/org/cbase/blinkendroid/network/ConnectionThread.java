/*
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
import java.util.StringTokenizer;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.OldBlinkendroid;
import org.cbase.blinkendroid.utils.DeviceUtils;
import org.cbase.blinkendroid.utils.ToastPoster;



import android.util.Log;
import android.widget.Toast;

public class ConnectionThread extends Thread {
	private Socket socket;
	private OldBlinkendroid blinkendroid;
	public ConnectionThread(OldBlinkendroid oldBlinkendroid, Socket clientSocket) {
		socket	=	clientSocket;
		this.blinkendroid =	oldBlinkendroid;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
			String request 	=	 in.readLine();
			Log.i(Constants.LOG_TAG, "Request: "+request);
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
			Log.e(Constants.LOG_TAG, "Could not get Streams",e);
		}
	}

	private String processInput(String inputLine) {
		new ToastPoster(blinkendroid, inputLine, Toast.LENGTH_SHORT);
		//Here are the commands to send or get data from the device:
		if (inputLine.equals("getImei")) {
		    return DeviceUtils.getImei(blinkendroid);
			//Further getter examples
		} else if (inputLine.equals("getLocationX")) {
		    return Integer.toString(blinkendroid.getLocationX());
		} else if (inputLine.equals("getLocationY")) {
		    return Integer.toString(blinkendroid.getLocationY()); 
		} else if (inputLine.equals("getLocationInMatrix")) {
		    return blinkendroid.getLocationX() + "/" + blinkendroid.getLocationY();
		    
		    //Example for a setter:
		} else if (inputLine.startsWith("setMatrixSize")) {
		    StringTokenizer tokenizer = new StringTokenizer(inputLine, " ");
		    if (tokenizer.countTokens() == 3) {
			return "Matrix size set to " + tokenizer.nextToken() + " " + tokenizer.nextToken();
		    } else {
			return "tokencount invalid";
		    }
		} else {
		    return new StringBuffer(inputLine).reverse().toString();
		}
	}


	
}
