#!/usr/bin/python

import xml.dom.minidom
import string

document = open("Downloads/ngbatoshow.kml")

kml = open('NGB_geojson.js', 'w')
kml.write('var walls = {"type": "Feature","geometry": { "type" : "MultiLineString", "coordinates" : [')

dom = xml.dom.minidom.parse(document)
walls = dom.getElementsByTagName("coordinates")

def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)


def wallToGeoJSON(wall):
    first2 = True

    points = wall.split(" ")
    for point in points:
        coordinates = point.split(",")
        kml.write('[' + coordinates[0] + "," + coordinates[1])
        if first2:
            kml.write('],')
            first2 = False
        else:
            kml.write(']')
        #kml.write(']},"type": "Feature","properties": {"popupContent": "This"},"id": 51}')

first = True

for wall in walls:
    if first:
        first = False
        kml.write("[")
    else:
        kml.write(",[")
    coordinates = getText(wall.childNodes)
    wallToGeoJSON(coordinates)
    kml.write("]")

kml.write(']},"properties": { "name": "Nottingam Geospatial Building - Ground Floor","style": {"color": "#004070","weight": 1,"opacity": 1}}};')
kml.close()
document.close()
