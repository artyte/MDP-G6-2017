#ifndef SensorController_h
#define SensorController

#include "Arduino.h"
#include "Constant.h"

class SensorController
{
public:
	SensorController();

	//All sensors read function
	int FMRF_Read();
	int FLRF_Read();
	int FRRF_Read();
	int LTRF_Read();
	int LBRF_Read();
	int RRF_Read();

private:

};
#endif 
