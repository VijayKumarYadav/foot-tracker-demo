#!/usr/bin/python

import xml.dom.minidom
import string

document = open("Downloads/ngbatoshow.kml")

kml = open('NGB_geojson.js', 'w')

dom = xml.dom.minidom.parse(document)
walls = dom.getElementsByTagName("coordinates")

def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
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

for wall in walls:
    coordinates = getText(wall.childNodes)
    kml.write(coordinates + "\n")

kml.close()
document.close()
