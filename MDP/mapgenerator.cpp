#include "mapgenerator.h"
#include<QFile>
#include<QDebug>

int** MapGenerator::readMapFile(QString filePath){
    //initialize and try to open map file
    QFile mapFile(filePath);
    int** mapArray = NULL;
    if(!mapFile.open(QIODevice::ReadOnly | QIODevice::Text)){
        qDebug() << "Open file failed";
        return mapArray;
    }

    //initialize 2d Array
    mapArray = new int*[15];
    for(int i = 0; i < 15; ++i){
        mapArray[i] = new int[20];
    }

    char* holder = new char;

    //read file data into array
    //0 - empty, 1 - obstacle
    for(int i =0; i < 15; ++i){
        for(int j = 0; j < 20; ++j){
            mapFile.read(holder, 1);
            if(*holder == '0')
                mapArray[i][j] = 0;
            else mapArray[i][j] = 1;
            //qDebug() << mapArray[i][j];
        }
        mapFile.read(holder, 1);
    }

    return mapArray;
}
