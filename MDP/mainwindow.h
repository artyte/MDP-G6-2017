#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include "obstacle.h"
#include <QTimer>
#include <QlineEdit>
#include <robot.h>
#include <QLabel>

class QGraphicsScene;
class QGraphicsView;


class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();


    Obstacle* gridMap[15][20];

private slots:
    void adjustViewSize();
    void update();
    void frameUpdate();
    void stop();


    void exploreSimulation();
    void timeLimitSimulation();
    void coverageLimitSimulation();
    void realMaze();

private:
    int** mapArray;
    //MyRobot* myRobot;
    QTimer* timer;
    QGraphicsScene *scene;
    QGraphicsView *view;

    QLineEdit* lineEdit1;
    QLineEdit* lineEdit2;
    QLineEdit* lineEdit3;

    QLabel* label4;
    QLabel* label5;
    QLabel* label6;

    MyRobot* robot = nullptr;

    void reset();
    void drawGrid();


protected:
    //void paintEvent(QPaintEvent* );
};

#endif // MAINWINDOW_H
