#ifndef ROBOT_H
#define ROBOT_H

#include <QPainter>
#include <QGraphicsItem>
#include <QGraphicsScene>
#include <socket.h>
#include <queue>

#include "sensor.h"

struct PosWDistance{
    QPoint pos;
    int distance;

    bool operator<(const PosWDistance& rhs) const{
        return distance > rhs.distance;
    }
};

class MyRobot : public QObject, public QGraphicsItem
{
    Q_OBJECT


public:
    MyRobot(int** mapArray, int mode, int timeLimit, int coverageLimit, int robotSpeed);
    ~MyRobot();

    QRectF boundingRect() const;
    void paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget);
    bool isCovering(QPoint robotPos, int rotation, QPoint targetPos);
    bool isMapComplete(int** robotMap);
    int** getRobotMapArray() {return robotMapArray;}

    int time = 0;
    int coverage = 0;

    // 0 - waiting, 1 = startPointCalibration, 2 = explore, 3 = fastest path
    int robotState = 1;

public slots:
    void processSignal(QString msg);



protected:
    void advance(int phase);

signals:
    void OnUpdate();
    void OnFrameUpdate();
    void stop();

private:

    //canvas to update
    Sensor * sensorArray[6];

    //robot mode info
    // 1 - explore and fastest path simulation
    // 2 - explore with coverage limitation simulation
    // 3 - explore with time limitation simulation
    // 4 - explore with real robot
    // 5 - fastest path with known map
    int exploreMode;
    int timeLimit;
    int coverageLimit;
    int robotSpeed; // n steps per second

    //******robot info******
    QPoint pos;
    int rotation;
    int ** robotMapArray;
    bool robotMapConfirmArray [15][20];
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
    bool movingFlag = false;
    socket* robotSocket;
    std::queue<QString> strQue;
    int delay = 0;


    //*****Exploration info*****
    QPoint start = QPoint(1, 1);
    QPoint goal = QPoint(18, 13);

    QPoint dirs[4] = {QPoint(1,0), QPoint(0, 1), QPoint(-1, 0), QPoint(0,-1)};

    QPoint leftSide[3] = {QPoint(-1, -2), QPoint(0, -2), QPoint(1, -2)};

    QPoint frontSide[3] = {QPoint(2, -1), QPoint(2, 0), QPoint(2, 1)};

    QPoint confirmGrid[6] = {QPoint(-1, -2), QPoint(1, -2), QPoint(2, -1), QPoint(2, 0), QPoint(2, 1), QPoint(0, 2)};
    //0 - normal, 1 - next step e and reset to 0, 2 - explore
    int movingByWallState;
    //1 - moving back to start zone, 2 - explore rest area, 3 - moving to start zone, 4 - fastest path to goal zone
    bool forwardExplore =false;
    int exploreState;
    bool notToExploreRest = false;
    int calibrateCounter = 0;
    int calibrateThreshold = 5;
    int fastestStartRotation = 0;
    QString fastestPathStr0= "";
    QString fastestPathStr90= "";

    int action = -1;
    int xDir = 0;
    int yDir = 0;

    void updateRobotMap(int* msg = nullptr);
    void updateRobotInfo();
    int determineStrategy();
    int moveByWall();
    bool isAvailablePosition(QPoint pos);
    QPointF getCanvasPosition(QPointF mapPosition);
    bool isInBound(QPoint pos);
    int ExploreRest();
    void startPointCalibration();
    void sendString();
    QString pathActionListString(QPoint _start, int _rotation, QPoint _target, int* pathLength);
};


#endif // ROBOT_H
