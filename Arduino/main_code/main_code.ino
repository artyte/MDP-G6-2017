#include "Arduino.h"
#include "Constant.h"
#include "PinChangeInterrupt.h"
#include "MotionController.h"

MotionController* MC;

void setup() {
  attachPCINT(digitalPinToPinChangeInterrupt(Constant::M1EC), MotionController::leftEncodeCountInc, CHANGE);
  attachPCINT(digitalPinToPCINT(Constant::M2EC), MotionController::rightEncodeCountInc, CHANGE);
  //Serial comm to show encoder
  Serial.begin(115200);
  Serial.println("Dual VNH5019 Motor Shield");
  
   MC = new MotionController();

   /*
   MC->move(FOWARD);
   delay(100);
   MC->turn(RIGHT);
   delay(100);
   MC->move(FOWARD);
   delay(100);
   MC->turn(LEFT);
   delay(100);
   MC->move(FOWARD);
   delay(100);
   MC->turn(RIGHT);
   delay(100);
   MC->move(FOWARD);
   delay(100);
   MC->turn(LEFT);
   delay(100);
   MC->move(FOWARD);
    delay(100);
   MC->move(FOWARD);
   */
}

void loop() {
   MC->moveTest();
}
