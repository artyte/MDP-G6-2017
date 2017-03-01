#include "robot.h"
#include <pathnode.h>
#include <QtMath>
#include <queue>
#include <array>
#include "QDebug"

using namespace std;

void MyRobot::paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget)
{
    QRectF rect = QRect(-40, -40, 80, 80);

    QBrush brush(QColor(80, 80, 190 ));
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
    //setPos(mapToParent(0, 10));

    if(steps == 0){
        //check if goal or start
        //sense around, update robot map, determine strategy
        updateRobotMap();
        action = determineStrategy();
        //qDebug() << action;
        //qDebug() << xDir << yDir;
    }

    //draw movement
    if(action == 0){
        setRotation((360 + rotation - (steps*90/maxSteps))%360);
    }else if(action == 1){
        setRotation((rotation + (steps*90/maxSteps))%360);
    }else if(action == 2){
        setPos(getCanvasPosition(QPointF(pos.x()+steps*xDir*1.0/maxSteps, pos.y()+steps*yDir*1.0/maxSteps)));
    }

    //update robot data at the end of each movement
    if(++steps == maxSteps){
        //get into next cycle and update robot information
        steps = 0;
        ++actionCounter;
        qDebug() << "Action: " << actionCounter;
        if(action == 0){
            rotation = (360 + rotation - 90) % 360;
        }else if(action == 1){
            rotation = (rotation + 90) % 360;
        }else if(action == 2){
            pos.setX(pos.x() + xDir);
            pos.setY(pos.y() + yDir);
            setPos(getCanvasPosition(pos));
        }

        if(action == 0 || action == 1){
            setRotation(rotation);
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
    }
}

MyRobot::MyRobot(int** mapArray)
{
    //some initialization
    //be careful, the order is (y, x)
    pos = QPoint(1, 1);
    rotation = 0; // clockwise by degree
    xDir = 1;
    yDir = 0;
    //setPos(40, 40);
    setPos(getCanvasPosition(pos));

    actualMapArray = mapArray;

    //initialize robot map
    robotMapArray = new int*[15];
    for(int i = 0; i < 15; ++i){
        robotMapArray[i] = new int[20];
    }

    for(int i =0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            robotMapArray[i][j] = 0;
        }
    }

    //set PathFinder's map
    PathNode::setRobotMap(robotMapArray);

    //bind sensor with robot
    sensorArray[0] = new Sensor(1, -1, 2, 270);
    sensorArray[1] = new Sensor(1, 1, 2, 270);
    sensorArray[2] = new Sensor(1, -1, 2, 0);
    sensorArray[3] = new Sensor(1, 0, 2, 0);
    sensorArray[4] = new Sensor(1, 1, 2, 0);
    sensorArray[5] = new Sensor(1, 0, 4, 90);

    //hardcoded initial position
    for(int i = 0; i < 6; ++i){
        sensorArray[i]->update(rotation, pos.x(), pos.y());
    }

    //set the first destination as robot's initial location
    currentRoute = 1;
    destination = QPoint(pos.x(), pos.y());
    direction = 1;

    steps = 0;
    maxSteps = 50;
}

void MyRobot::updateRobotMap()
{

    for(int i = 0; i < 6; ++i){
        //update sensor information
        sensorArray[i]->update(rotation, pos.x(), pos.y());

        //sense to get map information
        sensorFeedback* feedbackArray = sensorArray[i]->Sense(actualMapArray);
        //qDebug() << "sensor is working";
        for(int j = 1; j <= sensorArray[i]->getLength(); ++j){
            if(feedbackArray[j].type == -2) break;
            if(feedbackArray[j].type == 0 || feedbackArray[j].type == 1){
                robotMapArray[feedbackArray[j].pos.y()][feedbackArray[j].pos.x()] = feedbackArray[j].type+1;
            }
        }
        delete feedbackArray;
    }

    for(int i = -1; i < 2; ++i){
        for(int j = -1; j <2; ++j){
            robotMapArray[pos.y()+i][pos.x()+j] = 1;
        }
    }

    for(int i = 0 ; i < 15; ++i){
        QString str = "";
        for(int j = 0; j < 20; ++j){
            str = str + QString::number(robotMapArray[i][j]);
        }
        qDebug() << str;
    }
}

int MyRobot::determineStrategy()
{
    qDebug() << "Current position" << pos;
    //determine the next target location
    destination = NextDestination();
    qDebug() << "Target position" << destination;
    //determine a way to go there
    int action;

    if(!(destination.x() < 0)) // returned destination is a position, not a rotation commend
        action = PathNode::findPath(pos.x(), pos.y(), rotation/90, destination.x(), destination.y());
    else if(destination.x() == -1){
        action = 1; // turn right
    }else if(destination.x() == -3 || destination.x() == -2){
        action = 0; // trun left
    }
    //return action type

    //qDebug() << action;

    return action;
}

QPoint MyRobot::NextDestination()
{




    //find next destination
    QPoint nDest = moveByRow();


    //return next destination if it is an available point
    if(isAvailablePosition(nDest)){
        return nDest;
    }
    qDebug() << nDest;

    //if is not accessable, try to rotate to sense it
    for(int i = 1; i < 4; ++i){
        for(int j = 0; j < 6; ++j){
            //update sensor information
            sensorArray[j]->update((rotation + i*90) % 360, pos.x(), pos.y());
            //return rotation if can be sensed

            //qDebug() << "start";
            if(sensorArray[j]->isCovering(robotMapArray, nDest)){
                return QPoint(-i, 0); // -1 - 90, -2 - 180, -3 - 270
            }
        }
    }

    //qDebug() << nDest;
    //if not available point can be reached, find closest available point by back-tracing sensor
    priority_queue<PosWDistance> pq;
    QPoint* pointArray;
    PosWDistance temp;
    for(int i = 0; i < 6; ++i){
        //qDebug() << "sensor id" << i;
        pointArray = sensorArray[i]->getPossibleSensingPosition(nDest, robotMapArray);
        for(int j = 0; j < sensorArray[i]->getLength()*4; ++j){
            if(!isAvailablePosition(pointArray[j]))
                continue;
            temp = {pointArray[j], qAbs(pointArray[j].x() - pos.x()) + qAbs(pointArray[j].y() - pos.y())};

            pq.push(temp);
        }
        delete pointArray;
    }

    if(pq.empty()){
        //if no possible access could be found, mark this position as an obstacle
        robotMapArray[nDest.y()][nDest.x()] = 2;
        return NextDestination();
    }else{
        nDest = pq.top().pos;
        while(!pq.empty()){
            pq.pop();
        }
        //qDebug() << pq.top().distance;
        return nDest;
    }
}

QPoint MyRobot::moveByRow()
{
    //if map is explored completely, move to goal zone
    if(currentRoute > 15)
        return QPoint(18, 13);

    QPoint nDest = QPoint(0, 0);
    if(direction ==1){
        for(int i = 0; i < 20; ++i){
            if(robotMapArray[currentRoute][i] ==0){
                nDest = QPoint(i , currentRoute);
                break;
            }
            if(robotMapArray[currentRoute-1][i] == 0){
                nDest = QPoint(i , currentRoute-1);
                break;
            }
            if(robotMapArray[currentRoute+1][i] == 0){
                nDest = QPoint(i , currentRoute+1);
                break;
            }
        }
    }else{
        for(int i = 19; i > -1; --i){
            if(robotMapArray[currentRoute][i] ==0){
                nDest = QPoint(i , currentRoute);
                break;
            }
            if(robotMapArray[currentRoute-1][i] == 0){
                nDest = QPoint(i , currentRoute-1);
                break;
            }
            if(robotMapArray[currentRoute+1][i] == 0){
                nDest = QPoint(i , currentRoute+1);
                break;
            }
        }
    }


    //if current row is completely explored, move to the next row
    if(nDest.x() == 0 && nDest.y() == 0){
        currentRoute += 3;

        //move to goal area
        if(currentRoute > 15)
            return QPoint(18, 13);
        direction *= -1;
        return moveByRow();
    }

    return nDest;
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
