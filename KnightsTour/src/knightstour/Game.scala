package knightstour

import javax.imageio.ImageIO
import java.io.File
import scala.swing.MenuBar
import scala.swing.MenuItem
import scala.swing.Action
import scala.swing.Menu
import scala.swing.Button
import scala.io.Source
import scala.swing.Frame
import scala.swing.BorderPanel
import scala.swing.event.MouseReleased
import java.awt.Color
import scala.swing.TextArea
import java.awt.Font
import scala.swing.FlowPanel
import scala.collection.mutable.ArrayStack

abstract class Game(helpFrame: Frame) {

  var moveStack: ArrayStack[Square]
  var board: Board
  var frame: Frame

  val heart = ImageIO.read(new File("heart.png"))
  val flag = ImageIO.read(new File("flag.png"))
  val greenFlag = ImageIO.read(new File("greenWavyflag.png"))
  val bishop = ImageIO.read(new File("bishop.png"))
  val rook = ImageIO.read(new File("rook.png"))
  val knightImage = ImageIO.read(new File("knight.png"))
  val greenCir = ImageIO.read(new File("green_circle.png"))
  val blueSq = ImageIO.read(new File("blueSquare.png"))
  val redCir = ImageIO.read(new File("red_circle.png"))

  var running = true

  val menu = new MenuBar() {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Exit")(exit(0)))
      contents += new MenuItem(Action("New Game")(startNewGame))
    }
    contents += new Menu("Help") {
      contents += new MenuItem(Action("Instructions")(helpFrame.visible = true))
    }
  }

  def startNewGame {
    frame.dispose
    running = false
  }

  def isLegalMove(sqr: Square): Boolean = {
    val lastSquare = moveStack.top
    val pF = lastSquare.getFile
    val tF = sqr.getFile
    val pR = lastSquare.rank
    val tR = sqr.rank
    if (!moveStack.contains(sqr) &&
      (((tF == pF + 1) && tR == pR + 2) ||
        ((tF == pF + 1) && tR == pR - 2) ||
        ((tF == pF + 2) && tR == pR + 1) ||
        ((tF == pF + 2) && tR == pR - 1) ||
        ((tF == pF - 1) && tR == pR + 2) ||
        ((tF == pF - 1) && tR == pR - 2) ||
        ((tF == pF - 2) && tR == pR + 1) ||
        ((tF == pF - 2) && tR == pR - 1))) {
      true
    } else {
      false
    }
  }

  def squareClicked(x: Double, y: Double): Square = {
    var returnSq: Square = null
    for (a <- 0 to board.boardArray.length - 1) {
      for (b <- 0 to board.boardArray(a).length - 1) {
        val curSq = board.boardArray(a)(b)
        if (x < (curSq.getX + curSq.getHeight) && x > curSq.getX &&
          y < (curSq.getY + curSq.getHeight) && y > curSq.getY) {
          returnSq = curSq
        }
      }
    }
    returnSq
  }
}