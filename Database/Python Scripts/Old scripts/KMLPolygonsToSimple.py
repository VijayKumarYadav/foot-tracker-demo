#!/usr/bin/python

import xml.dom.minidom
import string

document = open("Documents/AFloorNGB.kml")

kml = open('polygons.txt', 'w')

dom = xml.dom.minidom.parse(document)
polygons = dom.getElementsByTagName("coordinates")

def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            polygon = ""
            txt = node.data
            coordinates = txt.split("\n")
            for coordinate in coordinates:
                polygon += coordinate.strip()
                polygon += " "
            rc.append(polygon)
    return ''.join(rc)


def wallToGeoJSON(wall):
    firstPoint = True

    points = wall.split(" ")
    for point in points:
        coordinates = point.split(",")
        kml.write(coordinates[0] + "," + coordinates[1])
        if firstPoint:
            kml.write('],')
            firstPoint = False
        else:
            kml.write(']')
        #kml.write(']},"type": "Feature","properties": {"popupContent": "This"},"id": 51}')

for polygon in polygons:
    coordinates = getText(polygon.childNodes)
    kml.write(coordinates + "\n")

kml.close()
document.close()
