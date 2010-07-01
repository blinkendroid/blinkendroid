package org.cbase.mobilecloud.blinkendroid;

import org.cbase.mobilecloud.CloudFactory;
import org.cbase.mobilecloud.CloudManager;
import org.cbase.mobilecloud.CommunicationManager;
import org.cbase.mobilecloud.Node;

public class BlinkendroidClient {

    /**
     * @param args
     */
    public static void main(String[] args) {
	Node myNode=null;
	CloudManager cloudManager = CloudFactory.getInstance().getCloudManager();
	CommunicationManager communicationManager = CloudFactory.getInstance().getCommunicationManager();
	
	//verbindet sich zur Cloud
	cloudManager.connect(myNode);
	
	//registriert sein protocolhandler
	communicationManager.registerProtocolHandler(new BlinkendroidClientProtocolHandler());
	
	//registriert sein cloudlistener
	cloudManager.registerCloudListener(new BlinkendroidClientCloudListener() );
    }

}
