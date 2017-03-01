#include "serialport.h"
#include "QDebug.h"

void serialport::serial() {
    //Initialization
   /* QSerialPort serial;
    serial.setPortName("?");//Name of serial port - COMXX
    serial.open(QIODevice::ReadWrite);
    serial.setBaudRate(QSerialPort::Baud9600);
    serial.setDataBits(QSerialPort::Data8);
    serial.setParity(QSerialPort::NoParity);
    serial.setStopBits(QSerialPort::OneStop);
    serial.setFlowControl(QSerialPort::NoFlowControl);

    if (serial.isOpen() && serial.isWritable()) {
        {
            //Try to do some tests
            //Send
            //F - Move Front
            qDebug() << "Woohoo" << endl;
            QByteArray test("F"); //Move front
            serial.write(test);
            qDebug() << "Data sent" << endl;
            serial.flush();

            //Receive
            serial.waitForReadyRead(1000);
            QByteArray input = serial.readAll();
            qDebug() << input << endl;

        }
    }*/
}

void serialport::getport() {
    QList<QSerialPortInfo> com_ports = QSerialPortInfo::availablePorts();
    qDebug() << com_ports << endl;
}
