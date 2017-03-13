#ifndef EXPLORENODE_H
#define EXPLORENODE_H

#include <QtMath>
#include <robot.h>

class ExploreNode{
public:
    ExploreNode(int x_p, int y_p, int dir_p, int cost_p){
        x = x_p;
        y = y_p;
        dir = dir_p;
        cost = cost_p;

        switch(dir){
        case 0:
            xDir = 1;
            yDir = 0;
            break;
        case 1:
            xDir = 0;
            yDir = 1;
            break;
        case 2:
            xDir = -1;
            yDir = 0;
            break;
        case 3:
            xDir = 0;
            yDir = -1;
            break;
        default:
            xDir = 0;
            yDir = 0;
        }
    }

    ~ExploreNode(){
        if(actionTrace != nullptr)
            delete actionTrace;
    }



    int getX() const {return x;}

    int getY() const {return y;}

    int getDir() const {return dir;}

    int getXDir() const {return xDir;}

    int getYDir() const {return yDir;}

    int getCost() const {return cost;}

    int getPriority() const {return priority;}

    int updatePriority(const int& firstPointX, const int& firstPointY, const int& secondPointX, const int& secondPointY){
        priority = cost + heuristicEstimation(firstPointX, firstPointY, secondPointX, secondPointY);
        return priority;
    }

    int heuristicEstimation(const int& firstPointX, const int& firstPointY, const int& secondPointX, const int& secondPointY) const{

        int xDest = firstPointX;
        int yDest = firstPointY;

        if(firstSenseFlag){
            xDest = secondPointX;
            yDest = secondPointY;
        }
        int xDiff = xDest - x;
        int yDiff = yDest - y;

        int heuristic = qAbs(xDiff) + qAbs(yDiff);

        //node is on destination
        if(xDiff == 0 && yDiff == 0){
            return heuristic;
        }

        int xFacing = xDiff*xDir;
        int yFacing = yDiff*yDir;

        //if node is on the same line of destination and facing opposite direction
        if((xDiff == 0 && yFacing < 0)||(yDiff == 0 && xFacing < 0)){
            heuristic += 2;
            return heuristic;
        }

        //if node is on the same line of destination and facing perpendicular direction
        if((xDiff == 0 && yFacing == 0)||(yDiff ==0 && xFacing ==0)){
            heuristic += 1;
            return heuristic;
        }

        //if node is not on the same line of destination and not facing correct direction
        if(xFacing < 0 || yFacing < 0){
            heuristic += 1;
            return heuristic;
        }

        return heuristic;
    }

    void traceAction(ExploreNode* parent, int parentAction);

    friend bool operator<(const ExploreNode &a, const ExploreNode &b){
        return a.getPriority() > b.getPriority();
    }

    //return a int as movement commend
    //0 - turn left, 1 - turn right, 2 - forward
    static int findPath(const int& xStart, const int& yStart, const int& dirStart, const int& firstPointX , const int& firstPointY, const int& secondPointX, const int& secondPointY, MyRobot& robot);

    static void setRobotMap(int** robotMap_p) {robotMap = robotMap_p;}

private:
    //node position
    int x;
    int y;
    //node direction 1,0,-1
    int dir;
    int xDir;
    int yDir;
    int cost;
    int priority;

    bool firstSenseFlag = false;
    bool secondSenseFlag = false;

    //to store action tracing from start
    int* actionTrace = nullptr;

    static int** robotMap;
};

#endif // EXPLORENODE_H
