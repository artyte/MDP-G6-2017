#ifndef OBSTACLE_H
#define OBSTACLE_H

#include <QPainter>
#include <QGraphicsItem>
#include <QGraphicsScene>

class Obstacle : public QGraphicsItem
{
public:
    Obstacle(int x, int y);

    QRectF boundingRect() const;
    void paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget);

protected:
    void advance(int phase);
};


#endif // OBSTACLE_H

Obstacle::Obstacle(int x, int y)
{
    setPos(-400+40*x, -300+40*y);
}

QRectF Obstacle::boundingRect() const
{
    return QRectF(0, 0, 40, 40);
}

void Obstacle::paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget)
{
    QRectF rect = boundingRect();
    QBrush brush = Qt::black;

    painter->drawRect(rect);
    painter->fillRect(rect, brush);
}

void Obstacle::advance(int phase)
{
    if(!phase) return;
}
