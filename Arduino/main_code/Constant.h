#ifndef Constant_h
#define Constant_h

#include "Arduino.h"

class Constant
{
	public:

		//DEBUG Constrain
		static const bool isDebug = false;


		//PID Tuning Constants
		//*NOTE* ONLY POSITIVE NUMERICAL ALLOW
    //Nearly Perfect PD Controller
		static constexpr double Kp = 10; 
		static constexpr double Ki = 0.0;
		static constexpr double Kd = 0.2;

		//Pins Configuration
		//Digital Pins
		//Motor 1 : Left Motor, Motor 2 :Right Motor
		//Digital Pin 0 - Serial RX
		//Digital Pin 1 - Serial TX
		//Digital Pin 2 - M1INA, Motor 1 Direction Input A
		//Digital Pin 3 - M1EA, Motor 1 Encoder Pulse A
		static const unsigned char M1EC = 3;
		//Digital Pin 4 - M1NB, Motor 1 Direction Input B
		//Digital Pin 5 - M2EA, Motor 2 Encoder Pulse B
		static const unsigned char M2EC = 5;
		//Digital Pin 6  - M1EN/DIAG, Motor 1 enable input/fault output 
		//Digital Pin 7 - M2INA, Motor 2 Direction Input A
		//Digital Pin 8 - M2INB, Motor 2 Direction Input B
		//Digital Pin 9 - M1PWM, Motor 1 Speed Input
		//Digital Pin 10 - M2PWM, Motor 2 Speed Input
		//Digital Pin 11 - 

		//Digital Pin 12 - M2EN/DIAG, Motor 2 enable input/fault output 
		//Digital Pin 13 - 

		//Analog Pins

		//Analog Pin 0 -
		//Analog Pin 1 -
		//Analog Pin 2 -
		//Analog Pin 3 -
		//Analog Pin 4 -


    //Encoder Tick counts
    static const long rightTurnTick = 745;
    static const long leftTurnTick = 742;
	  static const long fowardTick = 537;
	  static const long backWardTick = 500;

};

#endif
