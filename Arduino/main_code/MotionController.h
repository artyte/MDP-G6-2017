


#ifndef MotionController_h
#define MotionController_h

#include "Arduino.h"
#include "Constant.h"
#include "PID.h"
#include <DualVNH5019MotorShield.h>

class MotionController
{
public:
	//Move Direction
	#define FOWARD true
	#define BACKWARD false

	//Turn Direction
	#define RIGHT true
	#define LEFT false

	MotionController();

	void move(bool direction);
	void turn(bool direction);
  void moveTest();

	static long MLCount, MRCount;

	DualVNH5019MotorShield motorShield;

	//Encoder Interrupt Update
	static void leftEncodeCountInc();
	static void rightEncodeCountInc();

private:

	//PID to make the motor go straight
	//Left and Right Motor Encoder Count


	PID* pid;

	long differenceOutput , speed;
	static long distanceTick, count;
	static bool reached;

    void printTest();
	void moveInitialise(long distanceTick);

	//Initialise PID
	void initPid();
};
#endif
