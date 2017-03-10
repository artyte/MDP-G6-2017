package sg.edu.ntu.mdp.common;

public interface Protocol {
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    String DEVICE_NAME = "device_name";
    String TOAST = "toast";
    String MESSAGE_TYPE = "MESSAGE_TYPE";
    String MESSAGE_BYTES = "MESSAGE_BYTES";
    String MESSAGE_BUFFER = "MESSAGE_BUFFER";
    String MESSAGE_ARG1 = "MESSAGE_ARG1";

    String CALIBRATE = "az";
    String SEND_ARENA = "pse";
    String TURN_LEFT  = "pL";
    String TURN_RIGHT   = "pD";
    String MOVE_FORWARD   = "pF";
    String START_EXPLORATION = "pe";
    String START_FASTEST = "pf";
}
