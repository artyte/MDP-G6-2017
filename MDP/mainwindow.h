#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

class QGraphicsScene;
class QGraphicsView;


class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

private slots:
    void adjustViewSize();

private:
    void initScene();
    void initMapGrid();

    QGraphicsScene *scene;
    QGraphicsView *view;

protected:
    //void paintEvent(QPaintEvent* );
};

#endif // MAINWINDOW_H
