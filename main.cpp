#include "mainwindow.h"
#include <QApplication>
#include <QLabel>
#include "socket.h"
#include <QFile>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    socket s;
    s.doConnect();
    s.ok();
    //s.delay();
    //s.delay();
    //s.readyRead();
     s.writeto();
    //MainWindow w;
    //w.show();
    //qDebug() << QDir::currentPath();

    return a.exec();
}
