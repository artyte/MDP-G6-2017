#include "socket.h"
#include <QApplication>
#include <string>

socket::socket(QObject *parent) :
    QObject(parent)
{
}

void socket::doConnect()
{
    sock = new QTcpSocket(this);

    connect(sock, SIGNAL(connected()),this, SLOT(connected()));
    connect(sock, SIGNAL(disconnected()),this, SLOT(disconnected()));
    connect(sock, SIGNAL(bytesWritten(qint64)),this, SLOT(bytesWritten(qint64)));
    connect(sock, SIGNAL(readyRead()),this, SLOT(readyRead()));

    qDebug() << "Connecting...";

    sock->connectToHost("192.168.6.6", 8089);

    if(!sock->waitForConnected(15000))
    {
        qDebug() << "Error: " << sock->errorString();
    }
}

void socket::connected()
{
    qDebug() << "Connected...";
}

void socket::disconnected()
{
    qDebug() << "Disconnected...";
}

void socket::bytesWritten(qint64 bytes)
{
    qDebug() << bytes << " Bytes written...";
}


void socket::readyRead() //working
{
    //qDebug() << "Reading...";
    //sock->waitForReadyRead();
    // read the data from the socket
    qDebug() << sock->readAll();
}

void socket::writeto() //working
{
    if(sock->state() == QAbstractSocket::ConnectedState) {
        sock->write("bF1");
        qDebug() << "Written...";}

}

void socket::delay() //working
{
    QTime dieTime = QTime::currentTime().addSecs(2); //Introduces a delay of 2s
    while (QTime::currentTime() < dieTime) {
        QApplication::processEvents(QEventLoop::AllEvents, 100);
    }
}

//Only start reading/writing after reading "k"
void socket::ok()
{
    QByteArray x = sock->readAll(); //sock->readAll() should return QByteArray
    qDebug << sock->readAll();
    qDebug << sock->readAll().value_type;
    while (x.toStdString() != "k") { //convert QByteArray to string - k\r\n?
        delay();
        x = sock->readAll();
    }
}
