package org.cbase.mobilecloud;

import java.util.List;

public class CloudFactory {

    public static CloudFactory getInstance() {
	return new CloudFactory();
    }

    public List<Cloud> getClouds() {
	return null;
    }

    public Cloud createCloud() {
	return null;
    }
}
