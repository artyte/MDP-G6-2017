#include "MotionController.h"

long MotionController::MLCount = 0;
long MotionController::MRCount = 0;
long MotionController::distanceTick = 0;
long MotionController::count = 0;
bool MotionController::reached = false;

//Constructor
MotionController::MotionController()
{
	//Init Interrupt


	this->motorShield.init();
	MotionController::initPid();
	//Max speed is 350
	speed = 300;
}

//Initialise Functions
void MotionController::initPid()
{
	//This PID is to let the wheel go in the same pace (straight)
	this->pid = new PID(&(this->MLCount), &(this->differenceOutput), &(this->MRCount),
		Constant::Kp,  Constant::Kd);

	//Output lim out PID to +-50, hence max speed is 350
	this->pid->SetOutputLimits(-50, 50);

	this->pid->SetMode(START);
}

void MotionController::moveInitialise(long distanceTick)
{
	MotionController::MLCount = 0;
	MotionController::MRCount = 0;
	MotionController::distanceTick = distanceTick;
	MotionController::reached = false;
}

void MotionController::move(bool direction)
{	
	MotionController::moveInitialise(direction ? Constant::fowardTick : Constant::backWardTick);

	if (direction == FOWARD)
	{
		while (true)
		{
			this->pid->Compute();
			motorShield.setSpeeds(speed + differenceOutput, speed - differenceOutput);
			if (reached)
				break;
        printTest();
		}
	}
	else
	{
		while (true)
		{
			this->pid->Compute();
			motorShield.setSpeeds(-speed - differenceOutput, -speed + differenceOutput);
			if (reached)
				break;
		}
	}

	motorShield.setBrakes(387, 400);
}

void MotionController::turn(bool direction)
{
	MotionController::moveInitialise(direction?Constant::rightTurnTick:Constant::leftTurnTick);

	if (direction == RIGHT)
	{
		while (true)
		{
			this->pid->Compute();
			motorShield.setSpeeds(speed + differenceOutput, -speed + differenceOutput);
			if (reached)
				break;
        printTest();
		}
	}
	else
	{
		while (true)
		{
			this->pid->Compute();
			motorShield.setSpeeds(-speed - differenceOutput, +speed - differenceOutput);
			if (reached)
				break;
        printTest();
		}
	}
  motorShield.setBrakes(387, 400);
}


//Encoder Interrupt Function
void MotionController::leftEncodeCountInc()
{
	MotionController::MLCount++;
	if (MLCount >= distanceTick)
	  reached = true;
}

void MotionController::rightEncodeCountInc()
{
	MotionController::MRCount++;
}


//For Debuging
//For PID
void MotionController::moveTest()
{
	this->pid->Compute();
	motorShield.setSpeeds(speed + differenceOutput, speed - differenceOutput);

	if (MotionController::MLCount == MotionController::MRCount)
		MotionController::count++;
	Serial.print(MotionController::MLCount);
	Serial.print(',');
	Serial.print(MotionController::MRCount);
	Serial.print(',');
	Serial.print(differenceOutput);
	Serial.print(',');
	Serial.println(MotionController::count);
}

//General Debuging method
void MotionController::printTest()
{
	Serial.print(MotionController::MLCount);
	Serial.print(',');
	Serial.print(MotionController::MRCount);
	Serial.print(',');
	Serial.print(MotionController::distanceTick);
	Serial.print(',');
	Serial.println(MotionController::reached);

}

