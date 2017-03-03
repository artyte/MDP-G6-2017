#ifndef SOCKET_H
#define SOCKET_H

#include <QObject>
#include <QtNetwork/QAbstractSocket>
#include <QtNetwork/QTcpSocket>
#include <QTime>

class socket : public QObject {
    Q_OBJECT

public:
    explicit socket(QObject *parent = 0);
    void doConnect();

public slots:
    void connected();
     void disconnected();
     void bytesWritten(qint64 bytes);
     void readyRead();
     void writeto();
     void delay();
     void ok();

 private:
     QTcpSocket *sock;

};

#endif // SOCKET_H
