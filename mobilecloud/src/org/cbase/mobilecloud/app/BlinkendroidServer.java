package org.cbase.mobilecloud.app;

import java.nio.ByteBuffer;

import org.cbase.mobilecloud.CloudFactory;
import org.cbase.mobilecloud.CloudManager;
import org.cbase.mobilecloud.CommunicationManager;
import org.cbase.mobilecloud.Node;

public class BlinkendroidServer {
    CommunicationManager communicationManager;
    CloudManager cloudManager;
    public void main(String[] args){
	//start server thread
	Node myServerNode=null;
	cloudManager = CloudFactory.getInstance().getCloudManager();
	communicationManager = CloudFactory.getInstance().getCommunicationManager();
	
	//verbindet sich zur Cloud
	cloudManager.connect(myServerNode);
	
	//registriert keinen protocolhandler es gibt keine Nachrichten vom Client zum Server
	//communicationManager.registerProtocolHandler(new BlinkendroidServerProtocolHandler());
	
	//registriert sein cloudlistener
	cloudManager.registerCloudListener(new BlinkendroidServerCloudListener() );
	
	
	//starte des timerthreads
	new Thread() {
	    
	    public void run() {
		//send time broadcast
		ByteBuffer data=null;
		communicationManager.send(null, data);
	    }
	}.start();
	
	
    }
}
