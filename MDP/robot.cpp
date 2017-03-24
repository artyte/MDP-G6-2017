#include "robot.h"
#include <pathnode.h>
#include <QtMath>
#include <queue>
#include <array>

#include <QtMath>
#include "QDebug"

using namespace std;

void MyRobot::paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget)
{
    QRectF rect = QRect(-40, -40, 80, 80);
    QBrush brush(QColor(255, 255, 190 ));//80 80 190
    painter->setBrush(brush);

    painter->drawEllipse(QPointF(0, 0), 40, 40);

    QPen pen(Qt::red);
    painter->setPen(pen);
    //to indicate facing
    painter->drawLine(QPointF(0, 0), QPointF(40, 0));
}

void MyRobot::advance(int phase)
{


    if(!phase) return;

    if(exploreMode == 4){
        //qDebug() << "reading";
        robotSocket->ok();
        QString sensorReading = robotSocket->sensorReading;
        if(!sensorReading.isEmpty()){
            //qDebug() << "receiving" << QString::number(QTime::currentTime().second()) << QString::number(QTime::currentTime().msec());

            qDebug() << "received: " << sensorReading;

            if(sensorReading == QString("k")){
                //after robot is ready, do self calibration

                startPointCalibration();
                robotSocket->sensorReading = "";

            }
            //check for android exploration start
            if(sensorReading != QString("se")){
                if(sensorReading == QString("e")){
                    //af the start of explore, ask robot to send the first sensor reading
                    qDebug() << "Entering explore mode";
                    strQue.push(QString("as"));
                    robotState = 2;
                    robotSocket->sensorReading = "";
                }else if(sensorReading == QString("f")){
                    robotState = 3;
                    //write fastest path command to robot
                    if(fastestStartRotation == 0){
                        strQue.push(QString("ag") + fastestPathStr0 + QString("g"));
                    }else if(fastestStartRotation == 90){
                        strQue.push(QString("ag") + fastestPathStr90 + QString("g"));
                    }

                    robotSocket->sensorReading = "";
                }else if(sensorReading == QString("s")){
                    notToExploreRest = true;
                }else if(robotState == 2){
                    //process qstring
                    processSignal(sensorReading);
                }
            }

            robotSocket->sensorReading = "";
        }
        //send string each frame
        sendString();

        if(steps == 0){
            //processSignal("9,9,9,9,9,9");
        }
    }

    time += 10; //ns
    emit OnFrameUpdate();

    if(exploreMode == 2){
        qDebug() << time;
        qDebug() << timeLimit;
        if(time >= timeLimit){
            emit stop();
        }
    }


    if(steps == 0 && exploreMode != 4){
        //check if goal or start
        //sense around, update robot map, determine strategy

        if(exploreMode!=5)
            updateRobotMap();

        int exploredGridCounter = 0;
        for(int i = 0; i < 15; ++i){
            for(int j = 0; j < 20; ++j){
                if(robotMapArray[i][j] != 0){
                    ++exploredGridCounter;
                }
            }
        }

        coverage = exploredGridCounter/3;
        emit OnUpdate();

        if(exploreMode == 3){
            if(coverage >= coverageLimit){
                emit stop();
            }
        }

        action = 1;
        qDebug() << "current loc" << pos;
        action = determineStrategy();

        updateRobotInfo();
        qDebug() << "next loc" << pos;
    }

    //draw movement
    if(exploreMode != 4 || (exploreMode == 4 && movingFlag)){
        if(action == 0){
            setRotation((360 + (90 + rotation)%360 - (steps*90/maxSteps))%360);
        }else if(action == 1){
            setRotation(((360 + rotation - 90)%360 + (steps*90/maxSteps))%360);
        }else if(action == 2){
            setPos(getCanvasPosition(QPointF(pos.x() - xDir +steps*xDir*1.0/maxSteps, pos.y() - yDir+steps*yDir*1.0/maxSteps)));
        }
        ++steps;
    }


    //update robot data at the end of each movement
    if(steps == maxSteps){
        //get into next cycle and update robot information
        if(exploreMode == 4)
            movingFlag = false;
        steps = 0;
        ++actionCounter;
        setPos(getCanvasPosition(pos));
        setRotation(rotation);
        qDebug() << "Action: " << actionCounter;
    }

}

MyRobot::MyRobot(int** mapArray, int mode, int timeLimit, int coverageLimit, int robotSpeed)
{
    //some initialization
    exploreMode = mode;
    this->timeLimit = timeLimit;
    this->coverageLimit = coverageLimit;
    this->robotSpeed = robotSpeed;

    //be careful, the order is (y, x)
    pos = QPoint(1, 1);
    rotation = 0; // clockwise by degree
    xDir = 1;
    yDir = 0;
    //setPos(40, 40);
    setPos(getCanvasPosition(pos));
    setRotation(0);

    actualMapArray = mapArray;

    //initialize robot map
    robotMapArray = new int*[15];
    for(int i = 0; i < 15; ++i){
        robotMapArray[i] = new int[20];
    }


    for(int i =0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            robotMapArray[i][j] = 0;
            robotMapConfirmArray[i][j] = false;
        }
    }

    //set PathFinder's map
    PathNode::setRobotMap(robotMapArray);

    //bind sensor with robot
    sensorArray[0] = new Sensor(1, -1, 3, 270);
    sensorArray[1] = new Sensor(1, 1, 3, 270);
    sensorArray[2] = new Sensor(1, -1, 3, 0);
    sensorArray[3] = new Sensor(1, 0, 3, 0);
    sensorArray[4] = new Sensor(1, 1, 3, 0);
    sensorArray[5] = new Sensor(1, 0, 5, 90);

    //hardcoded initial position
    for(int i = 0; i < 6; ++i){
        sensorArray[i]->update(rotation, pos.x(), pos.y());
    }

    exploreState = 1;
    movingByWallState = 0;

    steps = 0;
    if(robotSpeed != 0)
        maxSteps = 100/robotSpeed;
    else maxSteps = 50;
    if(exploreMode == 4){
        maxSteps = 50; // hardcoded robot speed
        rotation = 270;

        //exploreState = 3;
        robotSocket = new socket();
        robotSocket->doConnect();
        qDebug() << "connection finished";
        robotSocket->ok();
    }

    if(exploreMode == 5){
        for(int i =0; i < 15; ++i){
            for(int j = 0; j < 20; ++j){
                if(actualMapArray[i][j]==1)
                    robotMapArray[i][j] = 2;
                else{
                    robotMapArray[i][j] = 0;
                }
            }
        }
        exploreState = 4;
    }
}

MyRobot::~MyRobot()
{
    for(int i = 0; i < 15; ++i){
        delete [] robotMapArray[i];
    }
    delete [] robotMapArray;

    for(int i = 0; i < 6; ++i){
        delete sensorArray[i];
    }
}

void MyRobot::updateRobotMap(int* msg)
{

    for(int i = 0; i < 6; ++i){
        //update sensor information
        sensorArray[i]->update(rotation, pos.x(), pos.y());

        //sense to get map information
        sensorFeedback* feedbackArray = nullptr;
        if(exploreMode != 4)
            feedbackArray = sensorArray[i]->Sense(actualMapArray);
        else
            feedbackArray = sensorArray[i]->processSignal(msg[i]);

        //qDebug() << "sensor is working";
        for(int j = 1; j <= sensorArray[i]->getLength(); ++j){
            if(feedbackArray[j].type == -2) break;
            if(feedbackArray[j].type == 0 || feedbackArray[j].type == 1){
                //qDebug() << feedbackArray[j].pos.x() << feedbackArray[j].pos.y();
                if(!(robotMapConfirmArray[feedbackArray[j].pos.y()][feedbackArray[j].pos.x()]))
                    robotMapArray[feedbackArray[j].pos.y()][feedbackArray[j].pos.x()] = feedbackArray[j].type+1;
            }
        }
        delete [] feedbackArray;
    }

    QPoint absoluteConfirmGrid[6];

    for(int i = 0; i < 6; ++i){
        if(rotation==0){
            absoluteConfirmGrid[i].setX(pos.x() + confirmGrid[i].x());
            absoluteConfirmGrid[i].setY(pos.y() + confirmGrid[i].y());
        }else if(rotation == 90){
            absoluteConfirmGrid[i].setX(pos.x() -confirmGrid[i].y());
            absoluteConfirmGrid[i].setY(pos.y() + confirmGrid[i].x());
        }else if(rotation == 180){
            absoluteConfirmGrid[i].setX(pos.x() -confirmGrid[i].x());
            absoluteConfirmGrid[i].setY(pos.y()-confirmGrid[i].y());
        }else if(rotation == 270){
            absoluteConfirmGrid[i].setX(pos.x() + confirmGrid[i].y());
            absoluteConfirmGrid[i].setY(pos.y() -confirmGrid[i].x());
        }
    }

    for(int i = 0; i < 6; ++i){
        if(isInBound(absoluteConfirmGrid[i])){
            robotMapConfirmArray[absoluteConfirmGrid[i].y()][absoluteConfirmGrid[i].x()] = true;
        }

    }

    for(int i = -1; i < 2; ++i){
        for(int j = -1; j <2; ++j){
            robotMapArray[pos.y()+i][pos.x()+j] = 1;
            robotMapConfirmArray[pos.y()+i][pos.x()+j] = true;
        }
    }
}

void MyRobot::updateRobotInfo()
{
    if(action == 0){
        rotation = (360 + rotation - 90) % 360;
    }else if(action == 1){
        rotation = (rotation + 90) % 360;
    }else if(action == 2){
        pos.setX(pos.x() + xDir);
        pos.setY(pos.y() + yDir);
        if(pos.x() == 19){
            pos.setX(18);
        }else if(pos.x() == 0){
            pos.setX(1);
        }
        if(pos.y() == 14){
            pos.setY(13);
        }else if(pos.y() == 0){
            pos.setY(1);
        }
    }

    switch(rotation){
    case 0:
        xDir = 1;
        yDir = 0;
        break;
    case 90:
        xDir = 0;
        yDir = 1;
        break;
    case 180:
        xDir = -1;
        yDir = 0;
        break;
    case 270:
        xDir = 0;
        yDir = -1;
        break;
    default:
        xDir = 0;
        yDir = 0;
    }
}

int MyRobot::determineStrategy()
{
    int action;

    if(exploreState == 1 && actionCounter > 10){
        if(pos.x() == start.x() && pos.y() == start.y()){
            forwardExplore = false;
            if(notToExploreRest){
                exploreState = 3;
            }else{
                exploreState = 2;
            }
            //exploreState = 3; // hardcoded for fastest path
        }
    }

    if(exploreState == 2){
        //if run more than 250 steps, go back to goal zone
        if(actionCounter >= 250)
            exploreState = 3;
        else{
            action = ExploreRest();

            if(action == -2)
                exploreState = 3;
            else
                return action;
        }
    }

    if(exploreState == 3 && pos.x() == start.x() && pos.y() == start.y()){
        exploreState = 4;

        if(exploreMode == 4){
            //self calibration and wait
            robotState = 1;

            QString mapStr;
            for(int i = 0; i < 15; ++i){
                for(int j = 0; j < 20; ++j){
                    if(robotMapArray[i][j] == 0 || robotMapArray[i][j] == 3)
                        mapStr += "0"; // not explored
                    else mapStr += QString::number(robotMapArray[i][j]); // explored empty/obstacle
                }
            }

            int endRotation = rotation;
            int pathLength0, pathLength90;

            rotation = 0;
            this->action = -1;
            updateRobotInfo();
            //get fastest path string
            fastestPathStr0 = pathActionListString(pos, rotation, goal, &pathLength0);
            pos = start;
            rotation = 90;
            this->action = -1;
            updateRobotInfo();
            fastestPathStr90 = pathActionListString(pos, rotation, goal, &pathLength90);
            if(pathLength0 > pathLength90){
                fastestStartRotation = 90;
            }

            rotation = endRotation;
            pos = start;
            startPointCalibration();

            strQue.push(QString("b{\"robotPosition\":[" + QString::number(pos.y()) + "," + QString::number(pos.x()) + "," + QString::number(rotation) + "]," + "\"grid\":\"" + mapStr + "\"}"));
            return -1;
        }

        /*int endRotation = rotation;
        int pathLength0, pathLength90;

        rotation = 0;
        this->action = -1;
        updateRobotInfo();
        //get fastest path string
        fastestPathStr0 = pathActionListString(pos, rotation, goal, &pathLength0);
        pos = start;
        rotation = 90;
        this->action = -1;
        updateRobotInfo();
        fastestPathStr90 = pathActionListString(pos, rotation, goal, &pathLength90);
        if(pathLength0 > pathLength90){
            fastestStartRotation = 90;
        }

        qDebug() << pathLength0 << pathLength90;

        rotation = endRotation;
        pos = start;
        startPointCalibration();*/

    }

    if(exploreState == 4 && pos.x() == goal.x() && pos.y() == goal.y()){
        if(exploreMode != 4)
            emit stop();
        return -1;
    }

    if(exploreState == 1){
        action = moveByWall();

    }else if(exploreState == 3){
        //moving back to start zone
        action = PathNode::findPath(pos.x(), pos.y(), rotation/90, start.x(), start.y(), false);
    }else if(exploreState == 4){
        action = PathNode::findPath(pos.x(), pos.y(), rotation/90, goal.x(), goal.y(), false);
    }
    return action;
}

int MyRobot::moveByWall()
{
    //initialize absolute left side and front side position
    QPoint absoluteLeftSide[3];
    QPoint absoluteFrontSide[3];

    for(int i = 0; i < 3; ++i){
        if(rotation==0){
            absoluteLeftSide[i].setX(pos.x() + leftSide[i].x());
            absoluteLeftSide[i].setY(pos.y() + leftSide[i].y());
            absoluteFrontSide[i].setX(pos.x() + frontSide[i].x());
            absoluteFrontSide[i].setY(pos.y() + frontSide[i].y());
        }else if(rotation == 90){
            absoluteLeftSide[i].setX(pos.x() -leftSide[i].y());
            absoluteLeftSide[i].setY(pos.y() + leftSide[i].x());
            absoluteFrontSide[i].setX(pos.x() -frontSide[i].y());
            absoluteFrontSide[i].setY(pos.y() + frontSide[i].x());
        }else if(rotation == 180){
            absoluteLeftSide[i].setX(pos.x() -leftSide[i].x());
            absoluteLeftSide[i].setY(pos.y()-leftSide[i].y());
            absoluteFrontSide[i].setX(pos.x() -frontSide[i].x());
            absoluteFrontSide[i].setY(pos.y()-frontSide[i].y());
        }else if(rotation == 270){
            absoluteLeftSide[i].setX(pos.x() + leftSide[i].y());
            absoluteLeftSide[i].setY(pos.y() -leftSide[i].x());
            absoluteFrontSide[i].setX(pos.x() + frontSide[i].y());
            absoluteFrontSide[i].setY(pos.y() -frontSide[i].x());
        }
    }

    bool isLeftAttachWall, isFrontAttachWall;
    isLeftAttachWall = isFrontAttachWall = false;

    for(int i = 0; i < 3; ++i){
        if(isInBound(absoluteLeftSide[i]))
            isLeftAttachWall = isLeftAttachWall || robotMapArray[absoluteLeftSide[i].y()][absoluteLeftSide[i].x()] == 2;
        else
            isLeftAttachWall = isLeftAttachWall || true;

        if(isInBound(absoluteFrontSide[i]))
            isFrontAttachWall = isFrontAttachWall || robotMapArray[absoluteFrontSide[i].y()][absoluteFrontSide[i].x()] == 2;
        else
            isFrontAttachWall = isFrontAttachWall || true;
    }


    if(movingByWallState == 1){
        if(exploreMode == 4)
            forwardExplore = true;
        movingByWallState = 0;
        return 2;
    }

    if(isLeftAttachWall){
        if(!isFrontAttachWall){
            //if robot's left side has wall AND front side has no wall, moving forward
            if(exploreMode == 4){
                forwardExplore = true;
            }
            return 2;
        }else{
            //if robot's left side has wall AND front side has wall, turning right
            return 1;
        }
    }else{
        //if robot's left side does not have wall, turning left AND moving forward
        movingByWallState = 1;
        return 0;
    }
}

void MyRobot::processSignal(QString msg)
{
    //process msg string, divide to 6 piece
    if(msg.isEmpty()) return;

    //sending position to android
    //qDebug() << "processing signal" << msg;
    QStringList strList = msg.split(",");
    //qDebug() << "test" << strList.length();
    int signalMsg[6];
    signalMsg[0] = strList.at(4).toInt() +1;
    signalMsg[1] = strList.at(3).toInt() +1;
    signalMsg[2] = strList.at(2).toInt() +1;
    signalMsg[3] = strList.at(0).toInt() +1;
    signalMsg[4] = strList.at(1).toInt() +1;
    signalMsg[5] = strList.at(5).toInt() +1;

    if((signalMsg[0] != 1 && signalMsg[1] != 1 )|| signalMsg[2] == 1 || signalMsg[3] == 1 || signalMsg[4] == 1){
        forwardExplore = false;
    }

    qDebug() << "robot position" << pos;
    qDebug() << "robot rotation" << rotation;
    //pass to sensor to update robot map
    updateRobotMap(signalMsg);

    int exploredGridCounter = 0;
    for(int i = 0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            if(robotMapArray[i][j] != 0){
                ++exploredGridCounter;
            }
        }
    }

    coverage = exploredGridCounter/3;
    emit OnUpdate();

    //determine strategy and send back to arduino & android
    action = 2;

    QString actionStr = "";

    // if in follow the way state, robot send command l, r, e
    if(!forwardExplore){
        action = determineStrategy();
        if(action == 0){
            actionStr = "l";
        }else if(action == 1){
            actionStr = "r";
        }else if(action == 2){
            if(exploreState == 1)
                actionStr = "e";
            else
                actionStr = "f1";
        }
        if(!actionStr.isEmpty()){
            qDebug() << actionStr;
            strQue.push(QString("a") + actionStr);
            strQue.push(QString(""));
            strQue.push(QString(""));
        }
        else{
            qDebug() << "No command sending";
            return;
        }
    }


    //qDebug() << "action" << action;

    //update robot on PC
     steps = 0;
     updateRobotInfo();
     //if the last moving is not finished
     if(movingFlag){
         setPos(getCanvasPosition(pos));
         setRotation(rotation);
     }
     movingFlag = true;

    QString mapStr;
    for(int i = 0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            if(robotMapArray[i][j] == 0 || robotMapArray[i][j] == 3)
                mapStr += "0"; // not explored
            else mapStr += QString::number(robotMapArray[i][j]); // explored empty/obstacle
        }
    }
    strQue.push(QString("b{\"robotPosition\":[" + QString::number(pos.y()) + "," + QString::number(pos.x()) + "," + QString::number(rotation) + "]," + "\"grid\":\"" + mapStr + "\"}"));

}

int MyRobot::ExploreRest(){
    if(isMapComplete(robotMapArray)){
        return -2;
    }

    priority_queue<PosWDistance> pqD;
    priority_queue<PosWDistance> pqA;
    PosWDistance temp;
    int pointLimit = 0;

    //take nearest unexplored points into queue
    for(int i = 0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            if(robotMapArray[i][j] == 0){
                temp = {QPoint(j, i), qAbs(j - pos.x()) + qAbs(i - pos.y())};
                pqD.push(temp);
            }
        }
    }

    //take the nearest 10 points and find their path length
    while(!pqD.empty() && ++pointLimit < 11){
        temp = {QPoint(pqD.top().pos.x(), pqD.top().pos.y()), PathNode::pathLength(pos.x(), pos.y(), rotation/90, pqD.top().pos.x(), pqD.top().pos.y())};
        pqA.push(temp);
    }

    //get the point with smallest path length
    QPoint nDest = pqA.top().pos;

    //clear queue
    while(!pqA.empty()) pqA.pop();
    while(!pqD.empty()) pqD.pop();

    int action = -1;
    if(isAvailablePosition(nDest)){
        action = PathNode::findPath(pos.x(), pos.y(), rotation/90, nDest.x(), nDest.y(), true);
        if(action!=-1)
            return action;
    }

    //point is not available OR point is not reachable
    //try to rotate to sense this point
    for(int i = 1; i < 4; ++i){
        for(int j = 0; j < 6; ++j){
            //update sensor information
            sensorArray[j]->update((rotation + i*90) % 360, pos.x(), pos.y());
            //return rotation if can be sensed

            //qDebug() << "start";
            if(sensorArray[j]->isCovering(robotMapArray, nDest)){
                if(i == 3 || i == 2)// -1 - 90, -2 - 180, -3 - 270
                    return 0;
                else if(i == 1)
                    return 1;
            }
        }
    }

    //qDebug() << "target" << nDest;

    //current location cannot sense target location
    QPoint* pointArray;
    for(int i = 0; i < 6; ++i){
        //qDebug() << "sensor id" << i;
        pointArray = sensorArray[i]->getPossibleSensingPosition(nDest, robotMapArray);
        for(int j = 0; j < sensorArray[i]->getLength()*4; ++j){
            if(!isAvailablePosition(pointArray[j]))
                continue;
            //temp = {pointArray[j], qAbs(pointArray[j].x() - pos.x()) + qAbs(pointArray[j].y() - pos.y())};
            //debug
            if(nDest.x() == 19 && nDest.y() == 1){
                //qDebug() << "possible location" << pointArray[j];
            }
            temp = {pointArray[j], PathNode::pathLength(pos.x(), pos.y(), rotation/90, pointArray[j].x(), pointArray[j].y())};

            pqA.push(temp);
        }
        delete [] pointArray;
    }

    QPoint senseTarget;

    if(pqA.empty()){
        //if no possible access could be found, mark this position as an obstacle
        robotMapArray[nDest.y()][nDest.x()] = 3; // not detectable
        return ExploreRest();
    }else{
        senseTarget = pqA.top().pos;
        action = PathNode::findPath(pos.x(), pos.y(), rotation/90, senseTarget.x(), senseTarget.y(), true);

        //if cannot reach target location
        //try next target location
        while(action == -1 && !pqA.empty()){
            //qDebug() << "trying new sense location";
            pqA.pop();
            senseTarget = pqA.top().pos;
            action = PathNode::findPath(pos.x(), pos.y(), rotation/90, senseTarget.x(), senseTarget.y(), true);
        }

        //clear queue
        while(!pqA.empty()){
            pqA.pop();
        }

        //if no location can be reached
        //mark target location as not detectable
        if(action == -1){
            robotMapArray[nDest.y()][nDest.x()] = 3;
            return ExploreRest();
        }


        //qDebug() << pq.top().distance;
        return action;
    }
}

void MyRobot::startPointCalibration()
{
    qDebug() << "Calibration rotation" << rotation;
    qDebug() << "Calibration pos" << pos;
    qDebug() << "fasest rotation" << fastestStartRotation;
    if(rotation == 270){
        //calibrate and facing front
        if(fastestStartRotation == 0){
            strQue.push(QString("acrr"));
            strQue.push(QString(""));
            strQue.push(QString(""));
            strQue.push(QString(""));
        }else if(fastestStartRotation == 90){
            strQue.push(QString("acrrr"));
            strQue.push(QString(""));
            strQue.push(QString(""));
            strQue.push(QString(""));
        }
    }else if(rotation == 180){
        //turn right and calibrate and facing front;
        if(fastestStartRotation == 0){
            strQue.push(QString("acr"));
            strQue.push(QString(""));
            strQue.push(QString(""));
            strQue.push(QString(""));
        }else if(fastestStartRotation == 90){
            strQue.push(QString("acrr"));
            strQue.push(QString(""));
            strQue.push(QString(""));
            strQue.push(QString(""));
        }
    }
    this->action = -1;
    rotation = fastestStartRotation;
    updateRobotInfo();
    robotState = 0;
}

void MyRobot::sendString()
{
    if(!delay == 0){
        --delay;
        return;
    }
    if(strQue.empty()) return;
    robotSocket->write(strQue.front());
    //qDebug() << strQue.front();
    //qDebug() << "sending" << QString::number(QTime::currentTime().second()) << QString::number(QTime::currentTime().msec());
    strQue.pop();
    delay = 0;
}

QString MyRobot::pathActionListString(QPoint _start, int _rotation, QPoint _target, int* pathLength)
{
    int i_action;
    QString str_pathActionList = "";
    int i_moveForward = 0;
    bool b_isPreviousForward = false;
    int pathCounter = 0;
    while(!(pos.x() == _target.x() && pos.y() == _target.y())){


        i_action = PathNode::findPath(pos.x(), pos.y(), rotation/90, _target.x(), _target.y(), false);
        this->action = i_action;
        updateRobotInfo();

        pathCounter++;

        QString actionStr = "";
        if(i_action == 0){
            actionStr = "l";
            b_isPreviousForward = false;
        }else if(i_action == 1){
            actionStr = "r";
            b_isPreviousForward = false;
        }else if(i_action == 2){
            ++i_moveForward;
            b_isPreviousForward = true;
        }

        if(!b_isPreviousForward && i_moveForward != 0){
            if(i_moveForward >  9){
                str_pathActionList += "f9" + QString("f") + QString::number(i_moveForward - 9);
            }else{
                str_pathActionList += "f" + QString::number(i_moveForward);
            }
            /*
            while(i_moveForward > 9){
                str_pathActionList += "f9";
                i_moveForward -= 9;
            }
            str_pathActionList += "f" + QString::number(i_moveForward);*/
            i_moveForward = 0;
        }

        str_pathActionList +=  actionStr;
        *pathLength = pathCounter;
    }

    /*
    while(i_moveForward > 9){
        str_pathActionList += "f9";
        i_moveForward -= 9;
    }
    str_pathActionList += "f" + QString::number(i_moveForward);*/

    if(i_moveForward >  9){
        str_pathActionList += "f9" + QString("f") + QString::number(i_moveForward - 9);
    }else{
        str_pathActionList += "f" + QString::number(i_moveForward);
    }

    return str_pathActionList;
}

bool MyRobot::isInBound(QPoint pos){
    int x = pos.x();
    int y = pos.y();

    if(x < 0 || x > 19 || y < 0 || y > 14) return false;

    return true;
}

//check if position is available to move to
bool MyRobot::isAvailablePosition(QPoint pos)
{

    int x = pos.x();
    int y = pos.y();

    //position out of bound
    if(x < 1 || x > 18 || y < 1 || y > 13) return false;

    //position has obstacles
    for(int i = -1; i < 2; ++i){
        for(int j = -1; j < 2; ++j){
            if(robotMapArray[y+i][x+j] == 2)
                return false;
        }
    }
    return true;
}

bool MyRobot::isMapComplete(int** robotMap)
{
    for(int i = 0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            if(robotMap[i][j] == 0){
                return false;
            }
        }
    }
    return true;
}

bool MyRobot::isCovering(QPoint robotPos, int rotation, QPoint targetPos)
{
    for(int j = 0; j < 6; ++j){
        //update sensor information
        sensorArray[j]->update(rotation, robotPos.x(), robotPos.y());

        if(sensorArray[j]->isCovering(robotMapArray, targetPos)){
            return true; // -1 - 90, -2 - 180, -3 - 270
        }
    }
    return false;
}

QPointF MyRobot::getCanvasPosition(QPointF mapPosition)
{
    return QPointF(-400 +20 + mapPosition.x()*40, -300 +20 + mapPosition.y()*40);
}


//give the shortest path from current location to target location
//assuming all grids havent checked are not obstacle
//return the action that should be taken to have the shortest path

QRectF MyRobot::boundingRect() const
{
    return QRectF(-40, -40, 80, 80);
}
