#include "socket.h"
#include <QApplication>

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

//First write, to request for first sensor reading from Arduino
void socket::writeok()
{
    if(sock->state() == QAbstractSocket::ConnectedState) {
        sock->write("as");
        qDebug() << "Written...";
        while (sock->canReadLine()) {
            QString line;
            line = QString::fromUtf8(sock->readLine()).trimmed();
            qDebug() << "Received: " << line;
            write("b" + line);
        }
    }
}

//Write to Android and Arduino
void socket::write(QString message)
{
    //Convert QString to const char *
    QByteArray inUtf8 = message.toUtf8();
    const char *data = inUtf8.constData();
    if (sock->state() == QAbstractSocket::ConnectedState) {
        sock->write(data);
        qDebug() << "Written...";
    }
}

//Only need to read sensor readings from Arduino
QString socket::readd()
{
    QString line;
    line = QString::fromUtf8(sock->readLine()).trimmed();
    qDebug() << "Received: " << line;
    line = line.replace(QString(","), QString("")); //Remove all commas (if present) for sensor reading
    qDebug() << "Message: " << line;
    return line;
}

void socket::delay()
{
    QTime dieTime = QTime::currentTime().addSecs(2); //Introduces a delay of 2s
    while (QTime::currentTime() < dieTime) {
        QApplication::processEvents(QEventLoop::AllEvents, 100);
    }
}

//Only start algo after reading "k"
void socket::ok()
{
while (sock->canReadLine()){

    QString line;
    line = QString::fromUtf8(sock->readLine()).trimmed();
    qDebug() << "Received: " << line;
    if(line == "k"){
        writeok();

    }

    else {
        qDebug() << line;
        line = line.replace(QString(","), QString("")); //Remove all commas (if present) for sensor reading

        int nValue = line.toInt();
        QString result = QString::number(nValue, 16);
        write("b{\"grid\":" + result + "}");
    }
}

}



