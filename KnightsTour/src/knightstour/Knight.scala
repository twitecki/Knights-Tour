package knightstour

import java.awt.image.BufferedImage
import java.awt.Point
import java.awt.geom.Point2D

class Knight(val difficulty:Int, val img:BufferedImage, 
    var location:Point, val xBound:Int, val yBound:Int) extends GameCharacter {
  
  var killable:Boolean = false
  var moveable:Boolean = true
  var vel = 3
  
  val areaCovered = 5 
    
  def moveUp {
    if (location.y >= 0)
    location.y -= vel
    killable = true
  }
  def moveDown {
    if (location.y <= yBound)
    location.y += vel
    killable = true
  }
  def moveLeft {
    if (location.x >= 0)
    location.x -= vel
    killable = true
  }
  def moveRight {
    if (location.x <= xBound)
    location.x += vel
    killable = true
  }
}