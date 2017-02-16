#include "SensorController.h"

SensorController::SensorController()
{
}

int SensorController::FMRF_Read()
{
	return analogRead(Constant::FMRF);
}

int SensorController::FLRF_Read()
{
	return analogRead(Constant::FLRF);
}

int SensorController::FRRF_Read()
{
	return analogRead(Constant::FRRF);
}

int SensorController::LTRF_Read()
{
	return analogRead(Constant::LTRF);
}

int SensorController::LBRF_Read()
{
	return analogRead(Constant::LBRF);
}

int SensorController::RRF_Read()
{
	return analogRead(Constant::RRF);
}
