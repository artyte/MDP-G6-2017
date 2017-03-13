#include "obstacle.h"
#include <QDebug>


Obstacle::Obstacle(int x, int y, int gridState)
{
    this->x = x;
    this->y = y;
    setPos(-400+40*x, -300+40*y);
    setColor(gridState);
}

QRectF Obstacle::boundingRect() const
{
    return QRectF(0, 0, 40, 40);
}

void Obstacle::paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget)
{
    QRectF rect = boundingRect();
    QBrush brush(color);

    painter->drawRect(rect);
    painter->fillRect(rect, brush);
}

void Obstacle::setColor(int gridState)
{
    this->gridState = gridState;
    if(gridState == 0){
        color = Qt::white;
    }else if(gridState == 1){
        color = QColor(144, 202, 249);
    }else if(gridState == 2){
        color = QColor(30, 136, 229);
    }else if(gridState == 3){
        color = QColor(244, 67, 54);
    }else if(gridState == 4){
        color = Qt::black;
    }
}

void Obstacle::advance(int phase)
{
    if(!phase) return;

    if(flag && gridState != 0 && gridState != 4){
        setPos(mapToParent(1, 1));
        setColor(gridState);
        flag = false;
        moving = true;
    }

    if(moving){
        setPos(mapToParent(-1, -1));
        moving = false;
    }



    //qDebug() << this->boundingRect();

}
