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
		//static constexpr double Ki = 0.0;
		static constexpr double Kd = 0.3;

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
		//Digital Pin 11 - Nil

		//Digital Pin 12 - M2EN/DIAG, Motor 2 enable input/fault output 
		//Digital Pin 13 - Nil

		//Analog Pins
		//Analog Pin 0 - Front mid Short Range RF sensor
		static const unsigned char FMRF = A0;
		//Analog Pin 1 - Front left Short Range RF sensor
		static const unsigned char FLRF = A1;
		//Analog Pin 2 - Front right Short Range RF sensor
		static const unsigned char FRRF = A2;
		//Analog Pin 3 - Left top Short Range RF Sensor
		static const unsigned char LTRF = A3;
		//Analog Pin 4 - Left bottom Short Range RF Sensor
		static const unsigned char LBRF = A4;
		//Analog Pin 5 - Right Long Range RF Sensor
		static const unsigned char RRF = A5;


	    //Encoder Tick counts
		//90 degree right and left
		static const long rightTurnTick = 745;
	    static const long leftTurnTick = 742;
		//One Grid - 10cm
		static const long fowardTick = 537;
		//Not tuned
		static const long backWardTick = 500;

};

#endif
