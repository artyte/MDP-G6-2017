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
    String SEND_ARENA  = "sendArena";
    String TURN_LEFT  = "A";
    String TURN_RIGHT   = "D";
    String MOVE_FORWARD   = "W";
    String START_EXPLORATION = "explore";
    String START_FASTEST = "fastest";

    String ACTUAL_SEND_ARENA = "11sendArena";
    String ACTUAL_TURN_LEFT  = "11A";
    String ACTUAL_TURN_RIGHT   = "11D";
    String ACTUAL_MOVE_FORWARD   = "11W";
    String ACTUAL_START_EXPLORATION = "11explore";
    String ACTUAL_START_FASTEST = "11fastest";
}
