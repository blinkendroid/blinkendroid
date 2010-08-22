package org.cbase.mobilecloud.blinkendroid;

import java.util.List;

import org.cbase.mobilecloud.Cloud;
import org.cbase.mobilecloud.CloudFactory;
import org.cbase.mobilecloud.CloudManager;
import org.cbase.mobilecloud.CommunicationManager;
import org.cbase.mobilecloud.Node;

public class BlinkendroidClient {

    /**
     * @param args
     */
    public static void main(String[] args) {
	Node myNode = null;

	// alle clouds anzeigen & auswählen
	List<Cloud> clouds = CloudFactory.getInstance().getClouds();

	Cloud myCloud = clouds.get(0);
	CloudManager cloudManager = myCloud.getCloudManager();
	CommunicationManager communicationManager = myCloud
		.getCommunicationManager();

	// verbindet sich zur Cloud
	cloudManager.connect(myNode);

	// registriert sein protocolhandler
	communicationManager
		.registerProtocolHandler(new BlinkendroidClientProtocolHandler());

	// registriert sein cloudlistener
	cloudManager
		.registerCloudListener(new BlinkendroidClientCloudListener());
    }

}
