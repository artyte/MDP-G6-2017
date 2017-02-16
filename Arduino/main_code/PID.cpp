#include "PID.h"

PID::PID(long* input, long* output, long* setPoint,
	double Kp, double Kd)
{
	this->input = input;
	this->output = output;
	this->setPoint = setPoint;
	this->isStarted = false;

	PID::SetTuningParams(Kp, Kd);
}

//Using Ziegler-Nichols Tuning Formula for Discrete PID Tuning
bool PID::Compute()
{
	if (!this->isStarted) return false;
		/*Compute all the working error variables*/
		double input = *(this->input);
		double error = *(this->setPoint) - input;

		double dInput = (input - this->lastInput);


		/*Compute PID Output*/
		double computeOtput = this->Kp * error  - this->Kd * dInput;

		if (computeOtput > outMax) computeOtput = outMax;
		else if (computeOtput < outMin) computeOtput = outMin;

		*(this->output) = computeOtput;

		/*Save some variables for next time*/
		this->lastInput = input;

		return true;
	
}

void PID::SetTuningParams(double Kp, double Kd)
{
	if (Kp<0  || Kd<0) return;

	this->Kp = Kp;
	this->Kd = Kd;
}

void PID::SetOutputLimits(double outMin, double outMax)
{
	if (outMin >= outMax) return;
	this->outMin = outMin;
	this->outMax = outMax;

	if (this->isStarted)
	{
		if (*(this->output) > outMax)
			*(this->output) = outMax;
		else if (*(this->output) < outMin)
			*(this->output) = outMin;

	}
}


void PID::SetMode(bool Mode)
{
	if (Mode == !this->isStarted)
		PID::Initialize();
	this->isStarted = Mode;
}

void PID::Initialize()
{
	// Get the previous input term for integral
	this->lastInput = *output;

}
