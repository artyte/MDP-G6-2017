#ifndef ROBOT_H
#define ROBOT_H

#include <QPainter>
#include <QGraphicsItem>
#include <QGraphicsScene>

#include "sensor.h"

struct PosWDistance{
    QPoint pos;
    int distance;

    bool operator<(const PosWDistance& rhs) const{
        return distance > rhs.distance;
    }
};

class MyRobot : public QGraphicsItem
{
public:
    MyRobot(int** mapArray);

    QRectF boundingRect() const;
    void paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget);

protected:
    void advance(int phase);

private:
    Sensor * sensorArray[6];
    QPoint pos;
    int rotation;

    //program runs at around 100fps
    //each action takes 50frames
    //step 0 sense around and determine strategy
    //step 0 - 49, perform action
    int steps;
    int maxSteps;
    int actionCounter = 0;
    int ** actualMapArray;

    //0 - havent checked, 1 - checked, no obstacle
    //2 - checked, obstacle
    int ** robotMapArray;
    QPoint destination;

    //route is defined as row route
    //robot will follow this route in exploration mode
    //what about exploration with time limit?
    int currentRoute;
    int direction;

    QPoint dirs[4] = {QPoint(1,0), QPoint(-1,0), QPoint(0,1), QPoint(0,-1)};

    int action = -1;
    int xDir = 0;
    int yDir = 0;

    void updateRobotMap();
    int determineStrategy();
    QPoint NextDestination();
    QPoint moveByRow();
    bool isAvailablePosition(QPoint pos);
    QPointF getCanvasPosition(QPointF mapPosition);
};


#endif // ROBOT_H
