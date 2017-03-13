#include <explorenode.h>
#include <sensor.h>
#include <queue>
#include <QDebug>

using namespace std;

int **ExploreNode::robotMap;


void ExploreNode::traceAction(ExploreNode* parent, int parentAction)
{
    actionTrace = new int [cost];
    for(int i = 0; i < cost - 1; ++i){
        actionTrace[i] = parent->actionTrace[i];
    }
    actionTrace[cost] = parentAction;
}

int ExploreNode::findPath(const int &xStart, const int &yStart, const int &dirStart, const int &firstPointX, const int &firstPointY, const int &secondPointX, const int &secondPointY, MyRobot& robot)
{
    priority_queue<ExploreNode> pq[2];
    int pqi = 0;
    ExploreNode *n0, *m0;

    int x, y, dir;
    int cx, cy, cdir;
    bool firstSense, secondSense;

    int openNodeMap [15][20][4];
    bool boolNodeMap [15][20][4];
    //int closedNodeMap [15][20][4];

    //actionMap records the action taken by the parent of current state
    //int actionMap [15][20][4];

    int* actionTraceArray;
    int action;


    //initialize node map
    for(int i = 0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            for(int k = 0; k < 4; ++k){
                openNodeMap[i][j][k] = 0;
                boolNodeMap[i][j][k] = false;
                //closedNodeMap[i][j][k] = 0;
            }
        }
    }

    //set the start point and push into queue
    n0 = new ExploreNode(xStart, yStart, dirStart, 0);
    n0->firstSenseFlag = robot.isCovering(QPoint(x, y), dir*90, QPoint(firstPointX, firstPointY));
    n0->secondSenseFlag = robot.isCovering(QPoint(x, y), dir*90, QPoint(secondPointX, secondPointY));
    n0->updatePriority(firstPointX, firstPointY, secondPointX, secondPointY);
    pq[0].push(*n0);
    openNodeMap[yStart][xStart][dirStart] = n0->getPriority();

    //A* search
    while(!pq[pqi].empty()){
        //get the current node with the highest priority
        //form priority queue
        n0 = new ExploreNode(pq[pqi].top().getX(), pq[pqi].top().getY(), pq[pqi].top().getDir(), pq[pqi].top().getCost());
        x = n0->getX();
        y = n0->getY();
        dir = n0->getDir();
        actionTraceArray = n0->actionTrace;


        firstSense = n0->firstSenseFlag;
        secondSense = n0->secondSenseFlag;

        n0->updatePriority(firstPointX, firstPointY, secondPointX, secondPointY);
        //qDebug() << "Step";
        //qDebug() << "Current node:";
        //qDebug() << x << y << dir;

        pq[pqi].pop();
        //openNodeMap[y][x][dir] = 0;
        //closedNodeMap[y][x][dir] = 1;


        //quit searching if goal is reached
        if(firstSense && secondSense){
            action = actionTraceArray[0];

            delete n0;
            while(!pq[0].empty()){
                pq[0].pop();
            }

            while(!pq[1].empty()){
                pq[1].pop();
            }

            return action;
        }


        //qDebug() << "Child node:";
        //generate child nodes by taking all possible actions
        for(int i = 0; i < 3; ++i){
            //update state of child node
            cx = x;
            cy = y;
            cdir = dir;
            if(i == 0){
                cdir = (4+dir-1)%4;
            }else if(i == 1){
                cdir = (dir+1)%4;
            }else if(i == 2){
                cx = x + n0->getXDir();
                cy = y + n0->getYDir();
                //if position out of range
                if(cx<1 || cy<1 || cx>18 || cy>13) continue;
                //if position has an obstacle
                if(robotMap[cy][cx] == 2 || robotMap[cy+1][cx+1] == 2 || robotMap[cy+1][cx] == 2 || robotMap[cy][cx+1] == 2 || robotMap[cy-1][cx-1] == 2 || robotMap[cy-1][cx] == 2 || robotMap[cy][cx-1] == 2 || robotMap[cy+1][cx-1] == 2 || robotMap[cy-1][cx+1] == 2) continue;
            }

            //if position has been explored
            //if(closedNodeMap[cy][cx][cdir] == 1) continue;

            //qDebug() << cx << cy << cdir;

            m0 = new ExploreNode(cx, cy, cdir, n0->getCost()+1);
            m0->firstSenseFlag = robot.isCovering(QPoint(x, y), dir*90, QPoint(firstPointX, firstPointY));
            m0->secondSenseFlag = robot.isCovering(QPoint(x, y), dir*90, QPoint(secondPointX, secondPointY));
            m0->updatePriority(firstPointX, firstPointY, secondPointX, secondPointY);
            m0->traceAction(n0, i);

            if(openNodeMap[cy][cx][cdir] == 0){
                openNodeMap[cy][cx][cdir] = m0->getPriority();
                boolNodeMap[cy][cx][cdir] = m0->firstSenseFlag;
                pq[pqi].push(*m0);
            }else if(boolNodeMap[cy][cx][cdir] == m0->firstSenseFlag && openNodeMap[cy][cx][cdir] > m0->getPriority()){
                while(!(pq[pqi].top().getX() == cx && pq[pqi].top().getY() == cy && pq[pqi].top().getDir() == cdir && pq[pqi].top().firstSenseFlag == m0->firstSenseFlag)){
                    pq[1-pqi].push(pq[pqi].top());
                    pq[pqi].pop();
                }
                pq[pqi].pop();
                if(pq[pqi].size() > pq[1-pqi].size()) pqi = 1 - pqi;
                while(!pq[pqi].empty()){
                    pq[1-pqi].push(pq[pqi].top());
                    pq[pqi].pop();
                }
                pqi = 1 - pqi;
                pq[pqi].push(*m0);
            }

            delete m0;

            //if not in openNodeMap, add it into
            /*if(openNodeMap[cy][cx][cdir] == 0){
                openNodeMap[cy][cx][cdir] = m0->getPriority();
                pq[pqi].push(*m0);
                actionMap[cy][cx][cdir] = i;
            }else if(openNodeMap[cy][cx][cdir] > m0->getPriority()){
                //update new priority
                openNodeMap[cy][cx][cdir] = m0->getPriority();
                //update new action
                actionMap[cy][cx][cdir] = i;

                //replace node
                //1. push nodes to another queue except the one to be replaced
                while(!(pq[pqi].top().getX() == cx && pq[pqi].top().getY() == cy)){
                    pq[1-pqi].push(pq[pqi].top());
                    pq[pqi].pop();
                }
                //2. remove the one to be replaced
                pq[pqi].pop();

                //3. empty the smaller size pq to the larger one
                if(pq[pqi].size() > pq[1-pqi].size()) pqi = 1 - pqi;
                while(!pq[pqi].empty()){
                    pq[1-pqi].push(pq[pqi].top());
                    pq[pqi].pop();
                }
                //4. change working pqi
                pqi = 1 - pqi;
                pq[pqi].push(*m0);
            }else delete m0;*/
        }
        delete n0;
    }
    return -1;
}
