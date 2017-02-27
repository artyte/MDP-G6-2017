#ifndef SENSOR_H
#define SENSOR_H

#include <QPainter>

struct sensorFeedback{
    QPoint pos;
    //0 - no obstacle, 1 - obstacle, -1 - not detected, -2 - out bound
    int type;
};

class Sensor{
public:
    Sensor(int attachX, int attachY, int length, int relativeRotation);
    void update(int parentRotation, int parentX, int parentY);
    sensorFeedback* Sense(int** mapArray);
    int getLength();
private:
    int attachX;
    int attachY;
    //rotations is clockwise
    int relativeRotation;
    int absoluteRotation;
    int facingX;
    int facingY;
    int absoluteX;
    int absoluteY;
    int length;
};

#endif // SENSOR_H
