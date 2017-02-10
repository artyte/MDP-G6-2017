#include "MotionController.h"

long MotionController::MLCount = 0;
long MotionController::MRCount = 0;
long MotionController::distanceTick = 0;
long MotionController::count = 0;
bool MotionController::reached = false;

MotionController::MotionController()
{
	//REMEMBER TO INITIALISE STUPID
	this->motorShield.init();
	MotionController::initPid();
	speed = 300;
}

void MotionController::moveTest()
{
  this->pid->Compute();
  motorShield.setSpeeds(speed + differenceOutput, speed - differenceOutput);

  if(MotionController::MLCount == MotionController::MRCount)
   MotionController::count++;
  Serial.print(MotionController::MLCount);
  Serial.print(',');
  Serial.print(MotionController::MRCount);
  Serial.print(',');
  Serial.print(differenceOutput);
  Serial.print(',');
  Serial.println(MotionController::count);
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
			MotionController::printTest();
		}
		motorShield.setBrakes(387, 400);
	}
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
			MotionController::printTest();
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
			MotionController::printTest();
		}
	}
  motorShield.setBrakes(385, 400);
}


void MotionController::initPid()
{
	//This PID is to let the wheel go in the same pace (straight)
	this->pid = new PID(&(this->MLCount), &(this->differenceOutput), &(this->MRCount),
						Constant::Kp, Constant::Ki, Constant::Kd);

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
