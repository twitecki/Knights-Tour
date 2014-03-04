package knightstour

import java.awt.image.BufferedImage
import scala.collection.mutable.ArrayStack
import scala.swing.MainFrame
import scala.swing.event.Key
import scala.swing.event.KeyPressed
import java.awt.Color
import javax.swing.Timer
import java.awt.event.ActionEvent
import scala.swing.Swing
import scala.swing.event.KeyReleased
import scala.swing.Dialog
import java.awt.Point
import scala.swing.BorderPanel
import scala.swing.FlowPanel
import scala.swing.Label
import javax.imageio.ImageIO
import java.io.File
import scala.swing.Panel
import java.awt.Graphics2D
import java.awt.Dimension
import scala.swing.GridPanel
import java.awt.Font
import scala.util.Random
import java.awt.geom.Point2D
import scala.swing.Frame
import java.io.BufferedOutputStream
import scala.io.Source
import java.io.FileOutputStream
import scala.swing.TextArea

class Adventure(var levels: HeapPriorityQueue[Level], val knight: Knight, hFrame: Frame, scoreFile: String,
  var lives: Int = 5) extends Game(hFrame) {

  final val TOLERANCE = 15 //Tolerance for collision detection
  var curTime = 0.0
  val totalLevels = levels.size
  val timeText = new TextArea("00") {
    editable = false
    background = Color.WHITE
    font = new Font("Arial", Font.BOLD, 13)
  }
  var curLevel: Level = null
  var board: Board = null
  var frame: Frame = new Frame {
    title = "Adventure Mode"
    menuBar = menu
    resizable = false
    override def closeOperation {
      System.exit(0)
    }
    centerOnScreen
  }
  //Updates the contents of the frame to include relevant game information
  //The boolean parameter represents whether or not a new level is being started
  updateInformationDisplay(true)
  val timer = new Timer(20, null)
  def startAdventure() {
    startListening()
    timer.addActionListener(Swing.ActionListener(e => {
      if (knight.moveable) {
        if (leftPressed) knight.moveLeft
        if (rightPressed) knight.moveRight
        if (upPressed) knight.moveUp
        if (downPressed) knight.moveDown
      }
      for (villain <- board.villains) {
        if (knight.killable && (villain.location.x >= knight.location.x - TOLERANCE && villain.location.x <= knight.location.x + TOLERANCE) &&
          villain.location.y >= knight.location.y - TOLERANCE && villain.location.y <= knight.location.y + TOLERANCE) {
          lives -= 1
          println("death location " + board.startingSquare.getX + " " + board.startingSquare.getY)
          updateInformationDisplay(false)
          //Spawns the knight on the flag square when he dies
          knight.location.x = moveStack.top.getX
          knight.location.y = moveStack.top.getY
          knight.killable = false
          knight.moveable = false
          println("Starting square 2 " + board.startingSquare.getX + " " + board.startingSquare.getY)
          board.repaint
        }
        villain.move
      }
      board.repaint
      if (levelComplete) {
        updateInformationDisplay(true)
        println("new Location " + board.startingSquare.getX + " " + board.startingSquare.getY)
        knight.killable = false
        knight.moveable = false
        board.repaint
      }
      curTime += 0.02
      timeText.text = curTime.toInt.toString
    }))
    timer.start
  }

  if (curLevel == null) println("Curlevel is null")

  var moveStack = new ArrayStack[Square]
  if (board.startingSquare != null) moveStack.push(board.startingSquare)

  //MOVEMENT HANDLING
  var leftPressed = false
  var rightPressed = false
  var upPressed = false
  var downPressed = false

  //StartListening is used when you get to a new level. The frame needs to 
  //listen to its new contents  
  def startListening() {
    board.requestFocus
    frame.listenTo(board.keys)
    frame.reactions += {
      case e: KeyPressed =>
        val x = knight.location.x + knight.img.getWidth() / 2
        val y = knight.location.y + knight.img.getHeight() / 2
        if (e.key == Key.D) {
          val sqr = squareClicked(x, y)
          if (sqr != null && isLegalMove(sqr)) {
            if (!moveStack.isEmpty) {
              moveStack.top.chgImg(flag, "redFlag")
            }
            moveStack.push(sqr)
            if (sqr.sqType == "heart") {
              println("You gained a life")
              if (lives < 8) lives += 1
              updateInformationDisplay(false)
            }
            sqr.chgImg(greenFlag, "greenFlag")
          }
        }
        if (e.key == Key.S) {
          val sqr = squareClicked(x, y)
          val lastMove = moveStack.top
          if (moveStack.size > 1 && sqr == lastMove && lastMove.originalImage != null) {
            lastMove.chgImg(lastMove.originalImage, lastMove.sqType)
            moveStack.pop
            if (!moveStack.isEmpty) moveStack.top.chgImg(greenFlag, "greenFlag")
          }
        }
        if (e.key == Key.Up) upPressed = true
        if (e.key == Key.Down) downPressed = true
        if (e.key == Key.Left) leftPressed = true
        if (e.key == Key.Right) rightPressed = true
      case kr: KeyReleased =>
        if (kr.key == Key.Space) knight.moveable = true
        if (kr.key == Key.Up) upPressed = false
        if (kr.key == Key.Down) downPressed = false
        if (kr.key == Key.Left) leftPressed = false
        if (kr.key == Key.Right) rightPressed = false
    }
  }

  def levelComplete: Boolean = {
    moveStack.top == board.endingSquare
  }
  //This method is used to update the screen when you beat the game,
  //lose the game, lose a life, gain a life, or beat the current level
  def updateInformationDisplay(newLevel: Boolean) {
    if (levels != null && levels.isEmpty && newLevel && lives > 0) {
      //you win
      timer.stop
      Dialog.showMessage(curLevel.board, "You have beaten all levels! You win", "You Win", Dialog.Message.Info, null)
      val name = Dialog.showInput[String](board, "Enter Your Name", "Save Time", Dialog.Message.Plain, null, Nil, null).getOrElse(null)
      val lines = Source.fromFile(new File(scoreFile)).getLines.toArray
      if (name != null && name != "" && name != " +") {
        val os = new BufferedOutputStream(new FileOutputStream(new File(scoreFile)))
        for (line <- lines) {
          os.write((line + "\n").getBytes)
          os.flush
        }
        os.write((name + " " + curTime.toInt).getBytes)
        os.flush()
      }
      frame.close
      running = false
    } else if (lives > 0 && newLevel) {
      //create a new level and print it
      curLevel = levels.dequeue
      board = curLevel.board
      curLevel.generateBadGuys
      moveStack = new ArrayStack[Square]
      if (board.startingSquare != null) moveStack.push(board.startingSquare)
      updateLayout
    } else if (lives <= 0) {
      //You lose
      timer.stop
      Dialog.showMessage(curLevel.board, "YOU RAN OUT OF LIVES! GAME OVER!",
        "GAME OVER", Dialog.Message.Info, null)
      frame.close
      running = false
    } else if (lives > 0 && !newLevel) {
      //Just update
      updateLayout()
    }
  }
  //UpdateLayout is used to update the lives, as well as the current time of the game
  def updateLayout() {
    frame.contents = new BorderPanel {
      background = Color.WHITE
      layout += board -> BorderPanel.Position.North
      layout += new Panel {
        preferredSize = new Dimension(0, 30)
        override def paint(g: Graphics2D) {
          for (i <- 0 until lives) {
            g.drawImage(heart, null, i * 30, 0)
          }
        }
      } -> BorderPanel.Position.Center
      layout += new GridPanel(1, 2) {
        background = Color.WHITE
        contents += new Label("LEVEL " + curLevel.level + "/" + totalLevels) 
        contents += new FlowPanel {
          background = Color.WHITE
          contents += new Label("TIME ")
          contents += timeText
        }
      } -> BorderPanel.Position.South
    }
    frame.visible = true
    startListening
    board.repaint
  }

}

