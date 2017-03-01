#include "mainwindow.h"
#include "mapgenerator.h"
#include "robot.h"
#include "obstacle.h"

#include <QPainter>
#include <QGraphicsView>
#include <QTimer>
#include <QDebug>

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent)
{
    setWindowTitle(tr("MDP Simulator"));
    setFixedSize(1000, 800);

    scene = new QGraphicsScene(this);
    view = new QGraphicsView(scene, this);

    setCentralWidget(view);

    view->setRenderHint(QPainter::Antialiasing);
    scene->setSceneRect(-400, -300, 800, 600);

    //initialize pen
    QPen mPen = QPen(Qt::black);

    //Draw Arena
    QLineF tempLine;
    for(int i = 0; i < 16; ++i){
        tempLine.setP1(QPointF(-400, -300+40*i));
        tempLine.setP2(QPointF(400, -300+40*i));
        scene->addLine(tempLine);
    }

    for(int i = 0; i < 21; ++i){
        tempLine.setP1(QPointF(-400+40*i, -300));
        tempLine.setP2(QPointF(-400+40*i, 300));
        scene->addLine(tempLine);
    }

    //read map from external file
    MapGenerator mapGenerator;
    int** mapArray = mapGenerator.readMapFile("mazeMap.txt");

    //draw obstacle
     Obstacle *obstacle;
    for(int i = 0; i < 15; ++i){
        for (int j = 0; j <20; ++j){
            if(mapArray[i][j] == 1){
                obstacle = new Obstacle(j, i);
                scene->addItem(obstacle);
            }
        }
    }

    //add robot
    MyRobot *myRobot;
    myRobot = new MyRobot(mapArray);
    //MyRobot *myRobot = new MyRobot();
    scene->addItem(myRobot);

    //set timer
    QTimer *timer = new QTimer(this);
    connect(timer, SIGNAL(timeout()), scene, SLOT(advance()));
    timer->start(10);
}

void MainWindow::initScene()
{

}

void MainWindow::initMapGrid()
{

}

void MainWindow::adjustViewSize()
{

}


MainWindow::~MainWindow()
{
    //delete ui;
}
