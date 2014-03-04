package knightstour

import java.awt.Point
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

abstract class GameCharacter {
	val difficulty:Int
	val img:BufferedImage 
	var location:Point
}