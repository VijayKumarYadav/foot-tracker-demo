#!/usr/bin/python2

import xml.dom.minidom
import string
from pyspatialite import dbapi2 as db

####### GLOBAL VARIABLES ########

building_id = "-1"
rooms = []
doors = []

#################################

document = open("AFloorNGB.kml")

conn = db.connect('toto.db')
cur = conn.cursor()

#sql = open('build_database.sql', 'w')
cur.execute('SELECT InitSpatialMetadata()')
cur.execute("CREATE TABLE buildings (id INTEGER PRIMARY KEY AUTOINCREMENT, name STRING NOT NULL, floors_count INT);\n")
cur.execute("SELECT AddGeometryColumn('buildings', 'footprint', 4326, 'POLYGON', 'XY');\n");
cur.execute("CREATE TABLE rooms (id INTEGER PRIMARY KEY AUTOINCREMENT, name STRING NOT NULL, building_id INT, floor_lvl  INT, stairs BOOLEAN);\n")
cur.execute("SELECT AddGeometryColumn('rooms', 'geometry', 4326, 'POLYGON', 'XY');\n");
cur.execute("CREATE TABLE doors (id INTEGER PRIMARY KEY AUTOINCREMENT, building_id INT, floor_lvl INT);\n")
cur.execute("SELECT AddGeometryColumn('doors', 'position', 4326, 'POINT', 'XY');\n");
cur.execute("SELECT AddGeometryColumn('doors', 'wall', 4326, 'LINESTRING', 'XY');\n");
cur.execute("CREATE TABLE door_to_rooms (door_id INT, room1_id INT, room2_id INT, FOREIGN KEY(door_id) REFERENCES doors(id), FOREIGN KEY(room1_id) REFERENCES rooms(id), FOREIGN KEY(room2_id) REFERENCES rooms(id));\n")
dom = xml.dom.minidom.parse(document)



def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)

def handleDocument(document):
    placemarks = document.getElementsByTagName("Placemark")
    handlePlacemarks(placemarks)

def handlePlacemarks(placemarks):
    for placemark in placemarks:
        handlePlacemark(placemark)

def handlePlacemark(placemark):
    global building_id
    global rooms
    global doors
    style = handleStyleUrl(placemark.getElementsByTagName("styleUrl")[0])
    if style == "#rooms" or style == "#stairs" :
        room = Room(placemark)    
        cur.execute(room.toSQL())
        room.id = cur.lastrowid
        rooms.append(room)
        #room.debug()
    elif style == "#Door":
        door = Door(placemark)
        cur.execute(door.toSQL())
        door.id = cur.lastrowid
        doors.append(door)
        #door.debug()
    elif style == "#footprint":
        footprint = Footprint(placemark)
        cur.execute(footprint.toSQL())
        building_id = cur.lastrowid

def handleStyleUrl(styleUrl):
    return getText(styleUrl.childNodes)

class Footprint:
    def __init__(self, placemark):
        self.handleName(placemark.getElementsByTagName("name")[0])
        self.handlePolygon(placemark.getElementsByTagName("coordinates")[0])
        self.floors_count = "0"

    def handleName(self, name):
        self.name = getText(name.childNodes)

    def handlePolygon(self, polygon):
        self.polygon = []
        for points in polygon.childNodes:
            if points.nodeType == points.TEXT_NODE:
                coordinates = points.data
                coordinates = coordinates.split("\n")
                for coordinate in coordinates :
                    self.polygon.append(coordinate.strip())

    def geometryToSql(self):
        geomSQL = "GeomFromText('POLYGON(("
        for point in self.polygon:
            if len(point) > 0:
                coordinates = point.split(",")
                geomSQL += coordinates[0] + " " + coordinates[1] + ","
        geomSQL = geomSQL[:-1]
        geomSQL += "))',4326)"
        return geomSQL

    def toSQL(self):
        roomSQL = "INSERT INTO buildings (name, floors_count, footprint) VALUES ('%s', '%s', %s);\n" % (self.name, self.floors_count, self.geometryToSql())
        return roomSQL


class Room:
    def __init__(self, placemark):
        self.handleName(placemark.getElementsByTagName("name")[0])
        self.handleDescription(placemark.getElementsByTagName("description")[0])
        self.handlePolygon(placemark.getElementsByTagName("coordinates")[0])
        self.id = "-1"

    def handleName(self, name):
        self.name = getText(name.childNodes)

    def handleDescription(self, description):
        txt = getText(description.childNodes)
        txt = txt.split(",")

        #Handle Door link
        doors = txt[1]
        self.doors = doors[doors.find("{")+1:doors.find("}")]
        if len(self.doors) > 0: 
            self.doors = self.doors.split(";")

        #Handle Floor Level
        floor = txt[2]
        self.floor = floor[floor.find("{")+1:floor.find("}")]

        #Handle Stairs
        self.stairs = "0"
        try:
            stairs = txt[3]
            stairs = stairs[stairs.find("{")+1:stairs.find("}")]
            if stairs == str(1) :
                self.stairs = "1"
        except:
            print("Cannot extract Room Ele{}")

    def handlePolygon(self, polygon):
        self.polygon = []
        for points in polygon.childNodes:
            if points.nodeType == points.TEXT_NODE:
                coordinates = points.data
                coordinates = coordinates.split("\n")
                for coordinate in coordinates :
                    self.polygon.append(coordinate.strip())
    
    def debug(self):
        print(self.name + " " + self.floor + " " + self.stairs + " " )

    def geometryToSql(self):
        geomSQL = "GeomFromText('POLYGON(("
        for point in self.polygon:
            if len(point) > 0:
                coordinates = point.split(",")
                geomSQL += coordinates[0] + " " + coordinates[1] + ","
        geomSQL = geomSQL[:-1]
        geomSQL += "))',4326)"
        return geomSQL

    def toSQL(self):
        global building_id
        roomSQL = "INSERT INTO rooms (name, building_id, floor_lvl, stairs, geometry) VALUES ('%s', '%s', '%s', '%s', %s);\n" % (self.name, building_id, self.floor, self.stairs, self.geometryToSql())
        return roomSQL

class Door:
    def __init__(self, placemark):
        self.handleName(placemark.getElementsByTagName("name")[0])
        self.handlePosition(placemark.getElementsByTagName("coordinates")[0])
        self.floor_lvl = "0"
        self.id = "-1"

    def handleName(self, name):
        self.name = getText(name.childNodes)

    def handlePosition(self, position):
        self.position = getText(position.childNodes)

    def debug(self):
        print(self.name + " " + self.position)

    def positionToGeom(self):
        positionSQL = "GeomFromText('POINT("
        coordinates = self.position.split(",")
        positionSQL += coordinates[0] + " " + coordinates[1]
        positionSQL += ")',4326)"
        return positionSQL



    def toSQL(self):
        global building_id
        roomSQL = "INSERT INTO doors (building_id, floor_lvl, position) VALUES ('%s', '%s', %s);" % (building_id, self.floor_lvl, self.positionToGeom())
        return roomSQL

def linkDoorsToRooms():
    global building_id
    global rooms
    global doors
    walls = []

    for room in rooms:
        for door_id in room.doors:
            for door in doors:
                if door.name == door_id:
                    cur.execute("SELECT * FROM door_to_rooms WHERE door_id == %s;" % door.id)
                    rows = cur.fetchall()
                    if len(rows) == 0 :
                        cur.execute("INSERT INTO door_to_rooms VALUES ('%s', '%s', '%s');" % (door.id, room.id, 'NULL'))
                    else :
                        cur.execute("UPDATE door_to_rooms SET room2_id = ? WHERE door_id = ?;", (room.id, door.id))

    cur.execute("SELECT door_id, AsText(geometry) FROM rooms, door_to_rooms WHERE rooms.id = door_to_rooms.room1_id;")
    rows = cur.fetchall()
    for row in rows :
        
        min_dist = 1000000
        wall = None
        id = row[0]
        polygon = row[1]
        polygon = polygon[polygon.find("((")+2:polygon.find("))")]
        points = polygon.split(",")

        for i in range(len(points)):
            points[i] = points[i].strip()
        for i in range(len(points)-1):
            segment = "GeomFromText('LINESTRING(%s, %s)',4326)" % (points[i],points[i+1])
            cur.execute("SELECT Distance(%s, position) FROM doors WHERE doors.id = %s" % (segment, id))
            distance = cur.fetchone()
            distance = distance[0]
            if distance < min_dist:
                min_dist = distance
                wall = segment
        cur.execute("UPDATE doors SET wall = %s WHERE id = %s;" % (wall, id))

class Wall:
    def __init__(self, id, coordinates):
        self.id = str(id)
        self.coordinates = coordinates

    def debug(self):
        print "WALL : ID = " + self.id + " COORDINATES = " + self.coordinates 

handleDocument(dom)
linkDoorsToRooms()
conn.commit()
conn.close()
document.close()
