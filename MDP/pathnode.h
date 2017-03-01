#ifndef PATHNODE_H
#define PATHNODE_H

#include <QtMath>

class PathNode{
public:
    PathNode(int x_p, int y_p, int dir_p, int cost_p){
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

    int getX() const {return x;}

    int getY() const {return y;}

    int getDir() const {return dir;}

    int getXDir() const {return xDir;}

    int getYDir() const {return yDir;}

    int getCost() const {return cost;}

    int getPriority() const {return priority;}

    int updatePriority(const int& xDest, const int& yDest){
        priority = cost + heuristicEstimation(xDest, yDest);
        return priority;
    }

    int heuristicEstimation(const int& xDest, const int& yDest) const{
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

    friend bool operator<(const PathNode &a, const PathNode &b){
        return a.getPriority() > b.getPriority();
    }

    //return a int as movement commend
    //0 - turn left, 1 - turn right, 2 - forward
    static int findPath(const int& xStart, const int& yStart, const int& dirStart, const int& xFinish, const int& yFinish);

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

    static int** robotMap;

};

#endif // PATHNODE_H
