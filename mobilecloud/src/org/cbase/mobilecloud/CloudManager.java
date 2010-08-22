package org.cbase.mobilecloud;

public interface CloudManager {

    // Cloud Information
    void registerCloudListener(CloudListener cloudListener);

    Cloud getCloud();

    void connect(Node myNode);

}
