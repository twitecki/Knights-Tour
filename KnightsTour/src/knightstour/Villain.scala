package knightstour

import java.awt.image.BufferedImage
import java.awt.Point
import java.awt.geom.Point2D

abstract class Villain(val img:BufferedImage, var location:Point, val bounds:(Point,Point), 
    val startDown:Boolean, val vel:Int) extends GameCharacter  {
	val difficulty = 1
	def getLocation():Point = {
	  new Point(img.getMinX(), img.getMinY())
	}
	var isAlive = true
	
	var moveDown:Boolean = startDown
	def move()
}