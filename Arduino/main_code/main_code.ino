#include "Arduino.h"
#include "Constant.h"
#include "PinChangeInterrupt.h"
#include "MotionController.h"

MotionController* MC;

void setup() {

  //Serial comm to show encoder
  Serial.begin(115200);
  Serial.println("Dual VNH5019 Motor Shield");
  
   MC = new MotionController();
   MC->move(FOWARD);
   delay(1000);
   MC->turn(RIGHT);
   delay(1000);
   MC->move(FOWARD);
   delay(1000);
   MC->turn(LEFT);
   delay(1000);
   MC->move(FOWARD);
   delay(1000);
   MC->turn(RIGHT);
   delay(1000);
   MC->move(FOWARD);
   delay(1000);
   MC->turn(LEFT);
   delay(1000);
   MC->move(FOWARD);
}

void loop() {

}
