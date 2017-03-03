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
    connect(sock, SIGNAL(readyRead()),this, SLOT(ok()));

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


void socket::readyRead() //
{
    //qDebug() << "Reading...";
    //sock->waitForReadyRead();
    // read the data from the socket
   // qDebug() << sock->readAll();
}

void socket::writeto()
{
    if(sock->state() == QAbstractSocket::ConnectedState) {
        sock->write("af1");
        qDebug() << "Written...";}

}

void socket::delay()
{
    QTime dieTime = QTime::currentTime().addSecs(2); //Introduces a delay of 2s
    while (QTime::currentTime() < dieTime) {
        QApplication::processEvents(QEventLoop::AllEvents, 100);
    }
}

//Only start reading/writing after reading "k"
void socket::ok()
{

while (sock->canReadLine()){

    QString line;
    line = QString::fromUtf8(sock->readLine()).trimmed();
    qDebug() << "Received: " << line;
    if(line == "k"){

        writeto();
    }
}


}



