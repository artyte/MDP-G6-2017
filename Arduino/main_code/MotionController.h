#ifndef MotionController_h
#define MotionController_h

#include "Arduino.h"
#include "Constant.h"
#include "PID.h"
#include <PinChangeInterrupt.h>
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

	//Constructor
	MotionController();

	//Motion
	void move(bool direction);
	void turn(bool direction);
	
	//MLCount - Motor Left Encoder Count, MRCount - Motor Right Encoder Count
	static long MLCount, MRCount;

	//Motor Current Shield lib
	DualVNH5019MotorShield motorShield;

	//Encoder Interrupt Update
	static void leftEncodeCountInc();
	static void rightEncodeCountInc();

	//Method to Tune PID 
	void moveTest();

private:

	//PID to make the motor go straight
	//Left and Right Motor Encoder Count
	PID* pid;

	long differenceOutput , speed;
	static long distanceTick, count;
	static bool reached;

	//Setting variable to get ready to move
	void moveInitialise(long distanceTick);

	//Initialise PID
	void initPid();

	//Method to debug 
	void printTest();
};
#endif
