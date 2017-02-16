#ifndef PID_h
#define PID_h

#include "Arduino.h"

class  PID
{
	public:

		//Function Mode
		#define START true
		#define STOP false

		//Constructor for PID
		PID(long* input, long* output, long* setPoint,
			const double Kp, const double Kd);

		//Preforms the calculation, get called in the main loop
		bool Compute();

		//More for setup
		//Set Kp, Ki and Kd
		void SetTuningParams(double Kp,  double Kd);

		//Sett the output limit of the PID
		void SetOutputLimits(double outMin, double outMax);

		//Set PID to Auto - 1 or Manual - 0
		void SetMode(bool Mode);




	private:
		//Start the PID
		void Initialize();

		//(P)ropotional Tuning Parameter
		double Kp;
		//(I)ntegral Tuning Parameter
		double Ki;
		//(D)erivative Tuning Parameter
		double Kd;

		//Pointers to the Current Input, 
		//Current Output and 
		//Pre set Setpoint Variables
		long *input;
		long *output;
		long *setPoint;

		//Min and Max output of the PID 
		double outMin, outMax;

		//Time Tracking
		unsigned long lastTime;

		//Variable for Derivative 
		long lastInput;

		//Indicate is the PID started
		bool isStarted;

};
#endif
