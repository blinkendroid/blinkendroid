package org.cbase.blinkendroid.server;


public interface ClientQueueListener {

    public abstract void clientWaiting(String ip);

    public abstract void clientNoLongerWaiting(String ip);

}