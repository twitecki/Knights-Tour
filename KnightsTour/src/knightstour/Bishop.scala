package knightstour

import java.awt.image.BufferedImage
import java.awt.Point
import java.awt.geom.Point2D

class Bishop(img: BufferedImage, location: Point, bounds: (Point, Point),
  startDown: Boolean, vel: Int, topLeftBotRight: Boolean)
  extends Villain(img, location, bounds, startDown, vel) {

  def move() {
    if (topLeftBotRight) {
      if (location == bounds._2) moveDown = false
      else if (location == bounds._1) moveDown = true
      if (moveDown) {
        location.y += vel
        location.x += vel
      } else {
        location.y -= vel
        location.x -= vel
      }
    } else {
      if (location == bounds._2) moveDown = false
      else if (location == bounds._1) moveDown = true
      if (moveDown) {
        location.y += vel
        location.x -= vel
      } else {
        location.y -= vel
        location.x += vel
      }
    }
  }
}