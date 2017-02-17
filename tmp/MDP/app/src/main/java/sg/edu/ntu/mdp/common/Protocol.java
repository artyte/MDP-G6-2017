/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sg.edu.ntu.mdp.common;

public interface Protocol {
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    public static final String MESSAGE_BYTES = "MESSAGE_BYTES";
    public static final String MESSAGE_BUFFER = "MESSAGE_BUFFER";
    public static final String MESSAGE_ARG1 = "MESSAGE_ARG1";
    public static final String SEND_ARENA  = "sendArena";
    public static final String TURN_LEFT  = "A";
    public static final String TURN_RIGHT   = "D";
    public static final String MOVE_FORWARD   = "W";
    public static final String EXPLORE_DONE   = "F";
    public static final String SEPARATOR    = "|";
    public static final String READ_SENSOR_VALUES = "E";
    public static final String DONE = "Y";
    public static final String START_EXPLORATION = "explore";
    public static final String START_FASTEST = "fastest";
    public static final String CALIBRATE = "C";
    public static final String CALIBRATE_PATTERN = ":1:[0-9]:1:[0-9]:[0-9][|]";
}
