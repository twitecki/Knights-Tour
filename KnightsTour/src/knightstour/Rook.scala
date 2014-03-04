package knightstour

import java.awt.Point
import java.awt.image.BufferedImage
import java.awt.geom.Point2D

class Rook(img: BufferedImage, location: Point, bounds:(Point, Point),
  startDown: Boolean, vel: Int, upDown: Boolean)
  extends Villain(img, location, bounds, startDown, vel) {

  def move() {
    if (upDown) {
      if (location == bounds._2) moveDown = false
      if (location == bounds._1) moveDown = true
      if (moveDown) location.y += vel
      else location.y -= vel
    } else {
      //Move Down here represents move to the right 
      if (location == bounds._2) moveDown = false
      if (location == bounds._1) moveDown = true
      if (moveDown) location.x += vel
      else location.x -= vel
    }
  }
}