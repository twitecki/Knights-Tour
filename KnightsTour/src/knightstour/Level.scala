package knightstour

import java.awt.Point
import java.awt.image.BufferedImage
import scala.util.Random
import java.awt.geom.Point2D

class Level(val level: Int, val board: Board, val difficulty: Int,
  val charMap: BSTMap[String, BufferedImage]) {
  
  var villains = List[Villain]()
  val rand = new Random
  
  def resetBadGuys() {
    villains = List[Villain]()
    generateBadGuys
  }
  
  def generateBadGuys() {
    var startDown = true
    //Create the vertical rooks and add them to the villain list
    for (i <- 1 to difficulty / 3) {
      var startLocation = new Point((i * (240 / ((difficulty / 3) + 1))), board.boardY)
      if (!startDown) startLocation.y = board.boardY + 240
      val lower = new Point(startLocation.x, 0)
      val upper = new Point(startLocation.x, 240)
      val badGuy = new Rook(charMap.get("rook").get, startLocation,
        (lower, upper),
        startDown, 1 + rand.nextInt(3), true)
      startDown = !startDown
      villains = badGuy :: villains
    }
    //Create the horizontal rooks and add them to the villain list
    for (i <- 1 to difficulty / 3) {
      var startLocation = new Point(0,(i * (250 / ((difficulty / 3 + 1)))))
      if (!startDown) startLocation.x = 240
      val lower = new Point(0, startLocation.y)
      val upper = new Point(240, startLocation.y)
      val badGuy = new Rook(charMap.get("rook").get, startLocation,
        (lower, upper),
        startDown, 1 + rand.nextInt(2), false)
      startDown = !startDown
      villains = badGuy :: villains
    }
    //Create the top left to bot right bishops and them to the villain list
    startDown = true 
    var bishopNum = 1
    if (difficulty >= 5) bishopNum = 2
    for (i <- 1 to bishopNum) {
      //was 0,0 and 240,240
      var startLocation = new Point(0, 0)
      if (!startDown) startLocation = new Point(240, 240)
      val lower = new Point(0, 0)
      val upper = new Point(240, 240)
      val badGuy = new Bishop(charMap.get("bishop").get, startLocation,
        (lower, upper), startDown, 1, true)
      startDown = !startDown
      villains = badGuy :: villains
    }
    board.villains = villains
    
    //Create the top left to bot right bishops and them to the villain list
     startDown = true 
     if (difficulty >= 15) bishopNum = 2
     else if (difficulty >= 10) bishopNum = 1
     else bishopNum = 0
    for (i <- 1 to bishopNum) {
      var startLocation = new Point(240, 0)
      if (!startDown) startLocation = new Point(0, 240)
      val lower = new Point(240,0)
      val upper = new Point(0, 240)
      val badGuy = new Bishop(charMap.get("bishop").get, startLocation,
        (lower, upper), startDown, 1, false)
      startDown = !startDown
      villains = badGuy :: villains
    }
    board.villains = villains
  }
}