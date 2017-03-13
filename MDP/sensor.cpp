#include "sensor.h"
#include "QDebug.h"

Sensor::Sensor(int attachX, int attachY, int length, int relativeRotation)
{
    this->attachX = attachX;
    this->attachY = attachY;
    this->length = length;
    this->relativeRotation = relativeRotation;

    offsetArray = new QPoint[length*4];
    for(int i = 1; i <= length; ++i){
        //0
        offsetArray[i*4-4] = QPoint(-i-attachX, -attachY);
        //90
        offsetArray[i*4-3] = QPoint(attachY, -i-attachX);
        //180
        offsetArray[i*4-2] = QPoint(i+attachX, attachY);
        //270
        offsetArray[i*4-1] = QPoint(-attachY, i+attachX);
    }

    /*for(int i = 0; i < length*4; ++i){
        qDebug() << offsetArray[i];
    }*/
}

void Sensor::update(int parentRotation, int parentX, int parentY)
{
    absoluteRotation = (360 + relativeRotation + parentRotation)%360;
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
    sensorFeedback* feedbackPtr = new sensorFeedback[6];

    bool obstacleFlag = false;
    bool outBoundFlag = false;
    for(int i = 1; i <= length; ++i){
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
            obstacleFlag = true;
            feedbackPtr[i] = {QPoint(absoluteX+i*facingX, absoluteY-i*facingY), 1};
            continue;
        }

        feedbackPtr[i] = {QPoint(absoluteX+i*facingX, absoluteY-i*facingY), 0};
    }

    return feedbackPtr;
}

sensorFeedback *Sensor::processSignal(int msg)
{
    sensorFeedback* feedbackPtr = new sensorFeedback[6];

    bool outBoundFlag = false;

    for(int i = 1; i <= length; ++i){
        //qDebug() << "sense grid" << absoluteX+i*facingX << absoluteY-i*facingY;
        if(outBoundFlag || absoluteX+i*facingX < 0 || absoluteX+i*facingX > 19 || absoluteY-i*facingY < 0 || absoluteY-i*facingY > 14){
            outBoundFlag = true;
            feedbackPtr[i] = {QPoint(-1, -1), -2};
            continue;
        }
        if(i < msg){
            feedbackPtr[i] = {QPoint(absoluteX+i*facingX, absoluteY-i*facingY), 0};
        }else if(i == msg){
            feedbackPtr[i] = {QPoint(absoluteX+i*facingX, absoluteY-i*facingY), 1};
        }else{
            feedbackPtr[i] = {QPoint(absoluteX+i*facingX, absoluteY-i*facingY), -2};
        }
    }
    return feedbackPtr;

}

int Sensor::getLength()
{
    return length;
}

bool Sensor::isCovering(int **robotMapArray, QPoint target)
{

    for(int i = 1; i <= length; ++i){
        //qDebug() << absoluteX+i*facingX << absoluteY-i*facingY;
        if(absoluteX+i*facingX < 0 || absoluteX+i*facingX > 19 || absoluteY-i*facingY < 0 || absoluteY-i*facingY > 14)
            return false;
        if(target.x() == absoluteX+i*facingX && target.y() == absoluteY-i*facingY)
            return true;
        if(robotMapArray[absoluteY-i*facingY][absoluteX+i*facingX] == 2)
            return false;
    }

    return false;
}

QPoint *Sensor::getPossibleSensingPosition(QPoint target, int **robotMapArray)
{
    QPoint* pointArray = new QPoint[length*4];
    bool dirFlag[4] = {false, false, false, false};
    int xDirs[4] = {-1, 0, 1, 0};
    int yDirs[4] = {0, -1, 0, 1};
    //qDebug() << "Debugging";
    for(int i = 0; i < length*4; ++i){
        //0
        int possibleX = target.x() + (i/4)*xDirs[i%4];
        int possibleY = target.y() + (i/4)*yDirs[i%4];
        //qDebug() << QPoint(possibleX, possibleY);
        if(!dirFlag[i%4] && possibleY > -1 && possibleY < 15 && possibleX > -1 && possibleX < 20 && robotMapArray[possibleY][possibleX] != 2){
            pointArray[i] = QPoint(target.x() + offsetArray[i].x(), target.y() + offsetArray[i].y());

        }else{
            dirFlag[i%4] = true;
            pointArray[i] = QPoint(-1, -1);
        }

        //qDebug() << i << pointArray[i];
    }

    return pointArray;
}
