#ifndef OBSTACLE_H
#define OBSTACLE_H

#include <QPainter>
#include <QGraphicsItem>
#include <QGraphicsScene>

class Obstacle : public QGraphicsItem
{
public:
    Obstacle(int x, int y, int gridState);

    QRectF boundingRect() const;
    void paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget);
    void setGridState(int gridState) {this->gridState = gridState;}
    void setColor(int gridState);

    int gridState;
    bool flag = true;

protected:
    void advance(int phase);

private:
    //white - not explored grid
    //black - not explored obstacle
    //light blue - explored grid
    //dark blue - explored obstacle
    //red - not detectable
    QColor color;
    int x, y;
    bool moving = false;
};


#endif // OBSTACLE_H


