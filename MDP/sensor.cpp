#include "sensor.h"
#include "QDebug.h"

Sensor::Sensor(int attachX, int attachY, int length, int relativeRotation)
{
    this->attachX = attachX;
    this->attachY = attachY;
    this->length = length;
    this->relativeRotation = relativeRotation;
}

void Sensor::update(int parentRotation, int parentX, int parentY)
{
    absoluteRotation = (relativeRotation + parentRotation)%360;
    int x, y;
    switch(absoluteRotation){
    case 0: x = attachX;
        y = attachY;
        facingX = 1;
        facingY = 0;
        break;
    case 90: x = -attachY;
        y = attachX;
        facingX = 0;
        facingY = -1;
        break;
    case 180: x = - attachX;
        y = - attachY;
        facingX = -1;
        facingY = 0;
        break;
    case 270: x = attachY;
        y = -attachX;
        facingX = 0;
        facingY = 1;
        break;
    }

    absoluteX = parentX + x;
    absoluteY = parentY + y;
}

sensorFeedback* Sensor::Sense(int** mapArray)
{
    sensorFeedback* feedbackPtr = new sensorFeedback[4];

    /*for(int i = 0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            qDebug() << mapArray[i][j];
        }
    }*/

    bool obstacleFlag = false;
    bool outBoundFlag = false;
    for(int i = 1; i <= length; ++i){
        //check absolute + i*facing
       // qDebug() << i;
        //qDebug() << absoluteY-i*facingY << absoluteX+i*facingX;

        if(outBoundFlag || absoluteX+i*facingX < 0 || absoluteX+i*facingX > 19 || absoluteY-i*facingY < 0 || absoluteY-i*facingY > 14){
            outBoundFlag = true;
            feedbackPtr[i] = {QPoint(-1, -1), -2};
            continue;
        }
        if(obstacleFlag){
            feedbackPtr[i] = {QPoint(absoluteX+i*facingX, absoluteY-i*facingY), -1};
            continue;
        }

        if(mapArray[absoluteY-i*facingY][absoluteX+i*facingX] == 1){
            //qDebug() << "find obstacle";
            obstacleFlag = true;
            feedbackPtr[i] = {QPoint(absoluteX+i*facingX, absoluteY-i*facingY), 1};
            continue;
        }

        feedbackPtr[i] = {QPoint(absoluteX+i*facingX, absoluteY-i*facingY), 0};
    }

    return feedbackPtr;
}

int Sensor::getLength()
{
    return length;
}
