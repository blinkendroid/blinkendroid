package org.cbase.blinkendroid.enums;

public enum TelnetCommand {

    getImei("getImei"),
    getLocationX("getLocationX"),
    getLocationY("getLocationY"),
    getLocationInMatrix("getLocationInMatrix"),
    setMatrixSize("setMatrixSize");
    
    /**
     * The accepted command
     */
    private String command;
    
    private TelnetCommand(String commandName) {
	
    }
    /**
     * Gets the command that is accepted by the telnet console.
     * @return 
     */
    public String getCommand(){
	return command;
    }
}
