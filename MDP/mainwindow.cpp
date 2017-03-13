#include "mainwindow.h"
#include "mapgenerator.h"

//#include "obstacle.h"


#include <QPainter>
#include <QGraphicsView>
#include <QPushButton>



#include <QDebug>

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent)
{
    // initialize window
    setWindowTitle(tr("MDP Simulator"));
    setFixedSize(1600, 900);

    //initialize graphics
    scene = new QGraphicsScene(this);
    view = new QGraphicsView(scene, this);

    // create button
    QPushButton* btn1 = new QPushButton();
    btn1->setText("Explore simulation");
    btn1->setGeometry(QRect(450, -300, 220, 40));
    scene->addWidget(btn1);
    connect(btn1, SIGNAL(clicked()), this, SLOT(exploreSimulation()));

    QPushButton* btn2 = new QPushButton();
    btn2->setText("Time-limit Simulation");
    btn2->setGeometry(QRect(450, -225, 220, 40));
    scene->addWidget(btn2);
    connect(btn2, SIGNAL(clicked()), this, SLOT(timeLimitSimulation()));

    QPushButton* btn3 = new QPushButton();
    btn3->setText("Coverage-limit simulation");
    btn3->setGeometry(QRect(450, -150, 220, 40));
    scene->addWidget(btn3);
    connect(btn3, SIGNAL(clicked()), this, SLOT(coverageLimitSimulation()));

    QLabel* label1 = new QLabel();
    label1->setText("Speed (n setps/s): ");
    label1->setAlignment(Qt::AlignCenter);
    label1->setGeometry(QRect(450, -75, 220, 40));
    scene->addWidget(label1);

    lineEdit1 = new QLineEdit();
    lineEdit1->setText("2");
    lineEdit1->setGeometry(QRect(450, -25, 220, 40));
    scene->addWidget(lineEdit1);

    QLabel* label2 = new QLabel();
    label2->setText("Time limitation: ");
    label2->setAlignment(Qt::AlignCenter);
    label2->setGeometry(QRect(450, 50, 220, 40));
    scene->addWidget(label2);

    lineEdit2 = new QLineEdit();
    lineEdit2->setText("0");
    lineEdit2->setGeometry(QRect(450, 100, 220, 40));
    scene->addWidget(lineEdit2);

    QLabel* label3 = new QLabel();
    label3->setText("Coverage limitation: ");
    label3->setAlignment(Qt::AlignCenter);
    label3->setGeometry(QRect(450, 175, 220, 40));
    scene->addWidget(label3);

    lineEdit3 = new QLineEdit();
    lineEdit3->setText("0");
    lineEdit3->setGeometry(QRect(450, 225, 220, 40));
    scene->addWidget(lineEdit3);

    QPushButton* btn4 = new QPushButton();
    btn4->setText("Run real maze");
    btn4->setGeometry(QRect(450, 300, 220, 40));
    scene->addWidget(btn4);
    connect(btn4, SIGNAL(clicked()), this, SLOT(realMaze()));

    label4 = new QLabel();
    label4->setText("Coverage:  00%");
    //label4->setAlignment(Qt::AlignCenter);
    label4->setMargin(10);
    label4->setGeometry(QRect(-300, -400, 220, 40));
    scene->addWidget(label4);

    label5 = new QLabel();
    label5->setText("Time:  0.0s");
    //label5->setAlignment(Qt::AlignLeft);
    label5->setMargin(10);
    label5->setGeometry(QRect(80, -400, 220, 40));
    scene->addWidget(label5);

    label6 = new QLabel();
    label6->setText("Simulation");
    label6->setMargin(10);
    label6->setAlignment(Qt::AlignCenter);
    label6->setGeometry(QRect(-670, -20, 220, 40));
    scene->addWidget(label6);


    setCentralWidget(view);

    view->setRenderHint(QPainter::Antialiasing);
    scene->setSceneRect(-400, -300, 800, 600);

    for(int i = 0; i < 15; ++i){
        for (int j = 0; j <20; ++j){
            gridMap[i][j] = nullptr;
        }
    }

    timer = new QTimer(this);
    connect(timer, SIGNAL(timeout()), scene, SLOT(advance()));

    //initialize pen
    QPen mPen = QPen(Qt::black);

    //Draw Arena
    /*QLineF tempLine;
    for(int i = 0; i < 16; ++i){
        tempLine.setP1(QPointF(-400, -300+40*i));
        tempLine.setP2(QPointF(400, -300+40*i));
        scene->addLine(tempLine);
    }



    for(int i = 0; i < 21; ++i){
        tempLine.setP1(QPointF(-400+40*i, -300));
        tempLine.setP2(QPointF(-400+40*i, 300));
        scene->addLine(tempLine);
    }*/

    //read map from external file
    /*MapGenerator mapGenerator;
    mapArray = mapGenerator.readMapFile("mazeMap.txt");

    //draw obstacle
    for(int i = 0; i < 15; ++i){
        for (int j = 0; j <20; ++j){
            gridMap[i][j] = new Obstacle(j, i, 0);
            if(mapArray[i][j] == 1){
                gridMap[i][j]->setColor(4);
            }
            scene->addItem(gridMap[i][j]);
        }
    }

    //add robot

    MyRobot* myRobot = new MyRobot(mapArray, this);
    //MyRobot *myRobot = new MyRobot();
    scene->addItem(myRobot);



    //set timer
    timer = new QTimer(this);
    connect(timer, SIGNAL(timeout()), scene, SLOT(advance()));
    timer->start(2);*/
}


void MainWindow::adjustViewSize()
{

}

void MainWindow::exploreSimulation()
{
    reset();
    MapGenerator mapGenerator;
    mapArray = mapGenerator.readMapFile("mazeMap.txt");

    //draw obstacle
    for(int i = 0; i < 15; ++i){
        for (int j = 0; j <20; ++j){
            gridMap[i][j] = new Obstacle(j, i, 0);
            if(mapArray[i][j] == 1){
                gridMap[i][j]->setColor(4);
            }
            scene->addItem(gridMap[i][j]);
        }
    }

    //add robot

    robot = new MyRobot(mapArray, 1, 0, 0, lineEdit1->text().toInt());
    connect(robot, SIGNAL(OnUpdate()), this, SLOT(update()));
    connect(robot, SIGNAL(OnFrameUpdate()), this, SLOT(frameUpdate()));
    connect(robot, SIGNAL(stop()), this, SLOT(stop()));
    //MyRobot *myRobot = new MyRobot();
    scene->addItem(robot);
    //set timer

    timer->stop();
    timer->start(10);
}

void MainWindow::timeLimitSimulation()
{
    reset();
    MapGenerator mapGenerator;
    mapArray = mapGenerator.readMapFile("mazeMap.txt");

    //draw obstacle
    for(int i = 0; i < 15; ++i){
        for (int j = 0; j <20; ++j){
            gridMap[i][j] = new Obstacle(j, i, 0);
            if(mapArray[i][j] == 1){
                gridMap[i][j]->setColor(4);
            }
            scene->addItem(gridMap[i][j]);
        }
    }

    //add robot

    robot = new MyRobot(mapArray, 2, lineEdit2->text().toInt() * 1000, 0, lineEdit1->text().toInt());
    connect(robot, SIGNAL(OnUpdate()), this, SLOT(update()));
    connect(robot, SIGNAL(OnFrameUpdate()), this, SLOT(frameUpdate()));
    connect(robot, SIGNAL(stop()), this, SLOT(stop()));
    //MyRobot *myRobot = new MyRobot();
    scene->addItem(robot);
    //set timer

    timer->stop();
    timer->start(10);
}

void MainWindow::coverageLimitSimulation()
{
    reset();
    MapGenerator mapGenerator;
    mapArray = mapGenerator.readMapFile("mazeMap.txt");

    //draw obstacle
    for(int i = 0; i < 15; ++i){
        for (int j = 0; j <20; ++j){
            gridMap[i][j] = new Obstacle(j, i, 0);
            if(mapArray[i][j] == 1){
                gridMap[i][j]->setColor(4);
            }
            scene->addItem(gridMap[i][j]);
        }
    }

    //add robot

    robot = new MyRobot(mapArray, 3, 0, lineEdit3->text().toInt(), lineEdit1->text().toInt());
    connect(robot, SIGNAL(OnUpdate()), this, SLOT(update()));
    connect(robot, SIGNAL(OnFrameUpdate()), this, SLOT(frameUpdate()));
    connect(robot, SIGNAL(stop()), this, SLOT(stop()));
    //MyRobot *myRobot = new MyRobot();
    scene->addItem(robot);
    //set timer

    timer->stop();
    timer->start(10);
}

void MainWindow::realMaze()
{
    reset();
    for(int i = 0; i < 15; ++i){
        for (int j = 0; j <20; ++j){
            gridMap[i][j] = new Obstacle(j, i, 0);
            scene->addItem(gridMap[i][j]);
        }
    }

    //add robot

    robot = new MyRobot(nullptr, 4, 0, 0, 0);
    connect(robot, SIGNAL(OnUpdate()), this, SLOT(update()));
    connect(robot, SIGNAL(OnFrameUpdate()), this, SLOT(frameUpdate()));
    connect(robot, SIGNAL(stop()), this, SLOT(stop()));
    //MyRobot *myRobot = new MyRobot();
    scene->addItem(robot);
    //set timer

    timer->stop();
    timer->start(10);
}

void MainWindow::reset()
{
    for(int i = 0; i <15; ++i){
        for(int j = 0; j < 20; ++j){
            //qDebug() << "haha" + QString::to(gridMap[i][j] != nullptr);
            if(gridMap[i][j] != nullptr){
                scene->removeItem(gridMap[i][j]);
                delete gridMap[i][j];
                gridMap[i][j] = nullptr;
            }
                //continue;
        }
    }

    if(robot != nullptr){
        scene->removeItem(robot);
        delete robot;
        robot = nullptr;
    }

}

void MainWindow::update()
{
    label4->setText(QString("Coveratge: ").append(QString::number(robot->coverage)).append("%"));

    drawGrid();
}

void MainWindow::frameUpdate()
{
    if(robot->robotState == 0){
        label6->setText("Waiting");
    }else if(robot->robotState == 2){
        label6->setText("Exploring");
    }else if(robot->robotState == 3){
        label6->setText("Fastest Path");
    }
    label5->setText(QString("Time: ").append(QString::number(robot->time/1000.0)).append("s"));
}

void MainWindow::stop()
{
    timer->stop();
}


MainWindow::~MainWindow()
{
    delete scene;
    delete view;
    for(int i = 0; i < 15; ++i){
        for (int j = 0; j <20; ++j){
            delete gridMap[i][j];
        }
    }
    delete timer;
    //delete ui;
}

void MainWindow::drawGrid()
{
    int** robotMapArray = robot->getRobotMapArray();
    if(robotMapArray == nullptr)
        return;
    for(int i = 0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            // if grid is detected
            if(robotMapArray[i][j] != 0){
                if(gridMap[i][j]->gridState != robotMapArray[i][j]){
                    gridMap[i][j]->flag = true;
                    gridMap[i][j]->setGridState(robotMapArray[i][j]);
                }
            }
        }
    }
}
