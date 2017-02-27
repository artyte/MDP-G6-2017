#ifndef ROBOT_H
#define ROBOT_H

#include <QPainter>
#include <QGraphicsItem>
#include <QGraphicsScene>

#include "sensor.h"

struct PathNode{
    PathNode* previousPath;
    QPoint location;
    int pastCost;
    int hueristic;
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

    void updateRobotMap();
    int determineStrategy();
    QPoint NextDestination();
    int AStarExplorePath();
    int Hueristics(QPoint currentPos, QPoint targetPos);
};


#endif // ROBOT_H
