package knightstour

import javax.imageio.ImageIO
import java.io.File
import scala.collection.mutable.ArrayStack
import scala.swing.MainFrame
import scala.swing.event.MouseReleased
import scala.swing.TextArea
import javax.swing.SwingUtilities
import sun.swing.SwingUtilities2
import scala.swing.event.MouseWheelMoved
import scala.swing.event.KeyReleased
import scala.swing.event.KeyPressed
import scala.swing.event.Key
import scala.swing.event.KeyTyped
import scala.swing.Dialog
import java.awt.image.BufferedImage
import scala.collection.mutable.Buffer
import java.awt.Color
import java.net.ServerSocket
import scala.swing.Button
import scala.swing.event.ButtonClicked
import javax.swing.Timer
import scala.swing.Swing
import scala.swing.Frame
import scala.swing.FlowPanel
import scala.swing.BorderPanel
import scala.swing.Label
import java.awt.Font
import scala.swing.Menu
import scala.swing.MenuBar
import scala.swing.ScrollPane
import java.awt.Dimension
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import scala.io.Source

//class Game(val board: Board, @transient var frame: Frame, @transient var txtArea: TextArea, @transient var knight: BufferedImage,
//  @transient var grnsq: BufferedImage, @transient var rdsq: BufferedImage, val blueSq: BufferedImage, logUsers: Buffer[LogUser],
//  val textTime: TextArea, val hintBttn: Button, newGame:Boolean) extends Serializable {
class NormalGame(var board: Board, newGame: Boolean, hFrame: Frame, scoreFile: String) extends Game(hFrame) with Serializable {

  //Normal Layout Components
  val txtArea = new TextArea() {
    background = Color.LIGHT_GRAY
    font = new Font("Impact", 0, 16)
    editable = false
  }
  val timeText = new TextArea("00") {
    editable = false
    background = Color.LIGHT_GRAY
    font = new Font("Impact", 0, 16)
  }
  val scrllPane = new ScrollPane(txtArea) {
    background = Color.LIGHT_GRAY
    preferredSize = new Dimension(200, 300)
  }

  val hintBttn = new Button("Hint") { enabled = false }

  var frame = new Frame {
    title = "Knights Tour"
    menuBar = menu
    resizable = false
    txtArea.text = ""
    contents = new FlowPanel {
      background = Color.LIGHT_GRAY
      contents += new BorderPanel {
        background = Color.LIGHT_GRAY
        layout += new FlowPanel {
          background = Color.LIGHT_GRAY
          contents += new Label("Time: ") { font = new Font("Impact", 0, 16) }
          contents += timeText
        } -> BorderPanel.Position.North
        layout += board -> BorderPanel.Position.South
      }

      contents += new BorderPanel {
        layout += scrllPane -> BorderPanel.Position.North
        layout += hintBttn -> BorderPanel.Position.South
      }
    }
    visible = true
    override def closeOperation {
      System.exit(0)
    }
    centerOnScreen
  }

  var curTime = 0
  if (newGame) {
    curTime = 0
    moveStack = new ArrayStack[Square]
    redoMoveStack = new ArrayStack[Square]
  }
  var hintSquare: Square = null
  val timer = new Timer(1000, Swing.ActionListener(e => {
    curTime += 1
    timeText.text = curTime.toString
  }))
  //Stores the moves played by the player
  var moveStack = new ArrayStack[Square]
  var redoMoveStack = new ArrayStack[Square]

  //PlayGame is the main function in a game. It listens to the players actions and reacts accordingly
  def playGame() {
    //Listen to mouse actions 
    board.requestFocus
    frame.listenTo(board.mouse.clicks, board.keys, board.mouse.wheel, hintBttn)
    frame.reactions += {
      case e: MouseReleased =>
        val x = e.point.x
        val y = e.point.y
        val sqr = squareClicked(x, y)
        if (sqr != null) {
          if (hintSquare != null) {
            hintSquare.chgImg(hintSquare.prevImg, hintSquare.sqType)
            hintSquare = null
          }
          if (isFirstMove) {
            redoMoveStack = new ArrayStack[Square]
            timer.start
            makeMove(sqr)
          } else if (isLegalMove(sqr)) {
            redoMoveStack = new ArrayStack[Square]
            val tempSq = moveStack.pop
            if (!moveStack.isEmpty) {
              moveStack.top.chgImg(redCir, "red")
            }
            tempSq.chgImg(greenCir, "green")
            moveStack.push(tempSq)
            makeMove(sqr)

          }
          if (isGameOver) {
            timer.stop
            Dialog.showMessage(board, s"You Won in $curTime seconds!", "Congratulations", Dialog.Message.Info, null)
            val name = Dialog.showInput[String](board, "Enter Your Name", "Save Time", Dialog.Message.Plain, null, Nil, null).getOrElse(null)
            val lines = Source.fromFile(new File(scoreFile)).getLines.toArray
            if (name != null && name != "" && name != " +") {
              val os = new BufferedOutputStream(new FileOutputStream(new File(scoreFile)))
              for (line <- lines) {
                os.write((line + "\n").getBytes)
                os.flush
              }
              os.write((name + " " + curTime).getBytes)
              os.flush()
            }
            frame.close()
            running = false
          }

        }
      case e: MouseWheelMoved =>
        if (e.rotation == 1) {
          undoMove()
          if (moveStack.size < 32) hintBttn.enabled = false
        } else if (e.rotation == -1) {
          redoMove()
        }
      case e: KeyTyped =>
        if (e.char == 'u') {
          undoMove()
          if (moveStack.size < 32) hintBttn.enabled = false
        } else if (e.char == 'r') {
          redoMove()
        }
      case e: ButtonClicked => {
        if (e.source == hintBttn) {
          var intGrid = Array.ofDim[Int](8, 8)
          for (r <- 0 until board.boardArray.length) {
            for (c <- 0 until board.boardArray(r).length) {
              if (moveStack.exists(sqr => sqr == board.boardArray(r)(c))) {
                intGrid(c)(r) = 1
              } else intGrid(c)(r) = 0
            }
          }
          intGrid(7 - (moveStack.top.rank - 1))(moveStack.top.getFile) = -1
          val (possible, hintMove) = isPossible(intGrid, moveStack.top.getFile, 7 - (moveStack.top.rank - 1), 64 - moveStack.size)
          if (!possible) {
            Dialog.showMessage(null, "No more winning combinations", "No More", scala.swing.Dialog.Message.Info, null)
          } else {
            val hintSquareRank = hintMove._2
            val hintSquareFile = hintMove._1
            hintSquare = board.boardArray(hintSquareFile)(hintSquareRank)
            hintSquare.chgImg(blueSq, hintSquare.sqType)
            board.repaint
          }
        }
      }
    }
  }

  //PrintGrid is used to help in debugging the hint option of the game
  //It prints integer grid of squares so they are easier to visualize
  def printGrid(intGrid: Array[Array[Int]]) {
    for (r <- 0 until 8) {
      for (c <- 0 until 8) {
        print(intGrid(r)(c))
      }
      println()
    }
    println()
  }
  //isPossible Is used to create the hint option. It checks if it is possible to complete the game 
  //with a specified current board layout. If it is possible it returns a tuple of true with a 
  //hint move, if it is not possible, it returns a tuple of false (0,0)
  def isPossible(boardArr: Array[Array[Int]], tF: Int, tR: Int, count: Int): (Boolean, (Int, Int)) = {
    if (tF > 7 || tF < 0 || tR > 7 || tR < 0 || boardArr(tR)(tF) > 0) return (false, (0, 0))
    else if (count == 0) return (true, (0, 0))
    else {
      boardArr(tR)(tF) = 2
      if (isPossible(boardArr, tF + 2, tR + 1, count - 1)._1) return (true, (tF + 2, tR + 1))
      if (isPossible(boardArr, tF + 2, tR - 1, count - 1)._1) return (true, (tF + 2, tR - 1))
      if (isPossible(boardArr, tF + 1, tR + 2, count - 1)._1) return (true, (tF + 1, tR + 2))
      if (isPossible(boardArr, tF + 1, tR - 2, count - 1)._1) return (true, (tF + 1, tR - 2))
      if (isPossible(boardArr, tF - 1, tR + 2, count - 1)._1) return (true, (tF - 1, tR + 2))
      if (isPossible(boardArr, tF - 1, tR - 2, count - 1)._1) return (true, (tF - 1, tR - 2))
      if (isPossible(boardArr, tF - 2, tR + 1, count - 1)._1) return (true, (tF - 2, tR + 1))
      if (isPossible(boardArr, tF - 2, tR - 1, count - 1)._1) return (true, (tF - 2, tR - 1))
      boardArr(tR)(tF) = 0
      (false, (0, 0))
    }
  }

  def makeMove(sqr: Square) {
    sqr.chgImg(knightImage, "knight")
    moveStack.push(sqr)
    txtArea.text = ""
    displayMoveStack()
    if (moveStack.size >= 32) hintBttn.enabled = true
    board.repaint
  }

  def isFirstMove: Boolean = { moveStack.isEmpty }

  def redoMove() {
    if (!redoMoveStack.isEmpty) {
      val nextMove = redoMoveStack.pop
      if (!moveStack.isEmpty) {
        val tempSq = moveStack.pop
        if (!moveStack.isEmpty) moveStack.top.chgImg(redCir, "red")
        tempSq.chgImg(greenCir, "green")
        moveStack.push(tempSq)
      }
      makeMove(nextMove)
    }
  }

  def undoMove() {
    if (hintSquare != null) {
      hintSquare.chgImg(hintSquare.prevImg, hintSquare.sqType)
      hintSquare = null
    }
    if (!moveStack.isEmpty) {
      if (moveStack.top.prevImg != null) {
        moveStack.top.chgImg(moveStack.top.originalImage, moveStack.top.sqType)
        //
        val undoneMove = moveStack.pop
        if (!redoMoveStack.exists(sq => sq == undoneMove)) redoMoveStack.push(undoneMove)
        //
        if (!moveStack.isEmpty) {
          moveStack.top.chgImg(moveStack.top.originalImage, moveStack.top.sqType)
          moveStack.top.chgImg(knightImage, "knight")
          val knightSq = moveStack.pop
          if (!moveStack.isEmpty) moveStack.top.chgImg(greenCir, "green")
          moveStack.push(knightSq)
        }

        txtArea.text = ""
        displayMoveStack()
        board.repaint()
      }
    }
  }

  def isGameOver(): Boolean = {
    board.squareCount == moveStack.size
  }

  def displayMoveStack() {
    val revMoves = moveStack.reverse
    revMoves.foreach(move =>
      txtArea.text += revMoves.indexOf(move) + 1 + ". " + move.file + move.rank + "\n")
  }
}

object NormalGame {
  def apply(board: Board, hFrame: Frame, scoreFile: String): NormalGame = {
    new NormalGame(board, true, hFrame, scoreFile)
  }
}
