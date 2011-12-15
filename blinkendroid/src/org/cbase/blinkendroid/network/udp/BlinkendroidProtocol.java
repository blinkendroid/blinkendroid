package org.cbase.blinkendroid.network.udp;

public abstract class BlinkendroidProtocol {

    // Commands from server to client
    public static final Integer COMMAND_CLIP = 17;
    public static final Integer COMMAND_PLAY = 11;
    public static final Integer COMMAND_INIT = 77;
    public static final Integer COMMAND_HEARTBEAT = ConnectionState.Command.HEARTBEAT.ordinal();
    public static final Integer COMMAND_MOLE = 61;
    public static final Integer COMMAND_BLINK = 911;
    // Commands from client server
    public static final Integer COMMAND_LOCATEME = 109;
    public static final Integer COMMAND_HITMOLE = 110;
    public static final Integer COMMAND_MISSEDMOLE = 111;
    public static final Integer COMMAND_TOUCH = 112;

    public static final int OPTION_PLAY_TYPE_MOVIE = 1;
    public static final int OPTION_PLAY_TYPE_IMAGE = 2;
}
