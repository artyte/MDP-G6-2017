#include "robot.h"
#include <QtMath>
#include "QDebug"

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
        int action = determineStrategy();
    }

    //follow strategy

    if(++steps == maxSteps){
        steps = 0;
    }
}

MyRobot::MyRobot(int** mapArray)
{
    //some initialization
    //be careful, the order is (y, x)
    pos = QPoint(1, 1);
    rotation = 0; // clockwise by degree
    setPos(-400 +20 + pos.x()*40, -300 +20 + pos.y()*40);

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
    //determine the next target location
    destination = NextDestination();
    qDebug() << destination;
    //determine a way to go there
    //return action type
    return 0;
}

QPoint MyRobot::NextDestination()
{
    if(robotMapArray[destination.y()][destination.x()] == 0)
        return destination;

    if(direction ==1){
        for(int i = 0; i < 20; ++i){
            if(robotMapArray[currentRoute][i] ==0)
                return QPoint(i , currentRoute);
            if(robotMapArray[currentRoute-1][i] == 0 && currentRoute > 1)
                return QPoint(i , currentRoute-1);
            if(robotMapArray[currentRoute+1][i] == 0 && currentRoute < 13)
                return QPoint(i , currentRoute+1);
        }
    }else{
        for(int i = 19; i > -1; --i){
            if(robotMapArray[currentRoute][i] ==0)
                return QPoint(i , currentRoute);
            if(robotMapArray[currentRoute-1][i] == 0 && currentRoute > 1)
                return QPoint(i , currentRoute-1);
            if(robotMapArray[currentRoute+1][i] == 0 && currentRoute < 13)
                return QPoint(i , currentRoute+1);
        }
    }

    currentRoute += 3;
    direction *= -1;
    return NextDestination();

}

//give the shortest path from current location to target location
//assuming all grids havent checked are not obstacle
//return the action that should be taken to have the shortest path
int MyRobot::AStarExplorePath()
{
    QQueue<QPoint> *nodeQueue;

    //initialize all possible nodes
    PathNode** nodeArray = new PathNode* [15];
    for(int i = 0; i < 15; ++i){
        nodeArray[i] = new PathNode [20];
    }

    for(int i = 0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            nodeArray[i][j].previousPath = NULL;
            nodeArray[i][j].location = pos;
            nodeArray[i][j].pastCost = -1;
            nodeArray[i][j].hueristic = -1;
        }
    }

    //initialize root
    nodeArray[pos.y()][pos.x()].pastCost = 0;
    nodeArray[pos.y()][pos.x()].hueristic = Hueristics(pos, destination);

    //expandNode()


    //release all memeory used by this function
    for(int i = 0; i < 15; ++i){
        delete nodeArray[i];
    }
    delete nodeArray;
}

int MyRobot::Hueristics(QPoint currentPos, QPoint targetPos)
{
    return qAbs(currentPos.x() - targetPos.x()) + qAbs(currentPos.y() - targetPos.y());
}

QRectF MyRobot::boundingRect() const
{
    return QRectF(-40, -40, 80, 80);
}
