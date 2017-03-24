#ifndef NEWROBOT_H
#define NEWROBOT_H

#include <QPainter>
#include <QGraphicsItem>
#include <QGraphicsScene>

#include "sensor.h"


class NewRobot : public QObject, public QGraphicsItem
{

    Q_OBJECT

public:
    NewRobot(int** mapArray);
    ~NewRobot();

    QRectF boundingRect() const;
    void paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget);
    //bool isCovering(QPoint robotPos, int rotation, QPoint targetPos);
    //bool isMapComplete(int** robotMap);
    //int** getRobotMapArray() {return robotMapArray;}

protected:
    void advance(int phase);

signals:
    void OnUpdate();

private:
    /*MainWindow* canvasWindow;

    //canvas to update
    Sensor * sensorArray[6];

    //robot mode info
    // 1 - explore and fastest path simulation
    // 2 - explore with coverage limitation simulation
    // 3 - explore with time limitation simulation
    // 4 - explore with real robot
    int exploreMode;
    int timeLimit;
    int time;
    int coverageLimit;
    int coverage;
    int robotSpeed; // n steps per second

    //******robot info******
    QPoint pos;
    int rotation;
    int ** robotMapArray;
    //0 - havent checked, 1 - checked, no obstacle
    //2 - checked, obstacle, 3 - not accessable
    int ** actualMapArray;

    //*****update info*****

    //program runs at around 100fps
    //each action takes 50frames
    //step 0 sense around and determine strategy
    //step 0 - 49, perform action
    int steps;
    int maxSteps;
    int actionCounter = 0;


    //*****Exploration info*****
    QPoint start = QPoint(1, 1);
    QPoint goal = QPoint(18, 13);

    QPoint dirs[4] = {QPoint(1,0), QPoint(-1,0), QPoint(0,1), QPoint(0,-1)};

    QPoint leftSide[3] = {QPoint(-1, -2), QPoint(0, -2), QPoint(1, -2)};

    QPoint frontSide[3] = {QPoint(2, -1), QPoint(2, 0), QPoint(2, 1)};
    //0 - normal, 1 - move forward and reset to 0
    int movingByWallState;
    //1 - moving back to start zone, 2 - explore rest area, 3 - moving to start zone, 4 - fastest path to goal zone
    int exploreState;

    int action = -1;
    int xDir = 0;
    int yDir = 0;

    void updateRobotMap();
    int determineStrategy();
    int moveByWall();
    bool isAvailablePosition(QPoint pos);
    QPointF getCanvasPosition(QPointF mapPosition);
    bool isInBound(QPoint pos);
    int ExploreRest();*/
};

#endif // NEWROBOT_H
