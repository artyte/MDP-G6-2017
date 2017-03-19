package mdp.robotxplorer.common;

public interface Protocol {
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    String TOAST = "toast";
    String DEVICE_NAME = "device_name";

    String MESSAGE_TYPE   = "MESSAGE_TYPE";
    String MESSAGE_BYTES  = "MESSAGE_BYTES";
    String MESSAGE_BUFFER = "MESSAGE_BUFFER";
    String MESSAGE_ARG1   = "MESSAGE_ARG1";

    String TURN_LEFT    = "pL";
    String TURN_RIGHT   = "pD";
    String MOVE_FORWARD = "pF";

    String CALIBRATE = "az";
    String START_EXPLORATION = "pe";
    String STOP_EXPLORATION  = "ps";
    String START_FASTEST = "pf";
    //String SEND_ARENA = "pse";

    //========== Protocol for communication with AMDTool  ==========
    String AMD_TURN_LEFT    = "A";
    String AMD_TURN_RIGHT   = "D";
    String AMD_MOVE_FORWARD = "W";

    String AMD_SEND_ARENA  = "sendArena";
    String AMD_START_EXPLORATION = "explore";
    String AMD_START_FASTEST = "fastest";
    //==========   ==========   ==========   ==========   ==========
}
