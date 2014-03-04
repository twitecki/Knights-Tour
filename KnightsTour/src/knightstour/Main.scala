package knightstour

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Runnable
import java.net.ServerSocket
import java.util.concurrent.Executors
import scala.swing.Action
import scala.swing.BorderPanel
import scala.swing.BorderPanel.Position.North
import scala.swing.BorderPanel.Position.South
import scala.swing.Button
import scala.swing.Dialog
import scala.swing.FileChooser
import scala.swing.FlowPanel
import scala.swing.GridPanel
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Menu
import scala.swing.MenuBar
import scala.swing.MenuItem
import scala.swing.Panel
import scala.swing.ScrollPane
import scala.swing.TextArea
import scala.swing.event.MouseReleased
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import scala.io.Source
import java.awt.geom.Point2D
import scala.swing.Frame
import scala.swing.Separator
import scala.actors.Actor
import scala.swing.ListView

object Main {

  var curGame: Game = null
  val bishop = ImageIO.read(new File("bishop.png"))
  val rook = ImageIO.read(new File("rook.png"))
  val knight = ImageIO.read(new File("knight.png"))
  val wtSqSmall = ImageIO.read(new File("whitesquare.jpg"))
  val blckSqSmall = ImageIO.read(new File("blcksq.jpg"))
  val normalScoreFile = "normalTimeFile.txt"
  val advScoreFile = "advTimeFile.txt"

  def comp(str1: String, str2: String): Int = { str1.compareTo(str2) }
  val gameCharMap = new BSTMap[String, BufferedImage](comp)
  gameCharMap += (("bishop", bishop), ("knight", knight), ("rook", rook))

  def comp(c1: GameCharacter, c2: GameCharacter): Int = c1.difficulty - c2.difficulty
  var villains = new HeapPriorityQueue[GameCharacter](comp)

  //Chapter 20, The typemap here is used to map square types in string format so that
  //when a file is loaded in, the proper bufferImage can be selected. The typemap will eventually
  //hold more square types when more options are added to change colors and sizes 
  val typeMap = Map("light" -> wtSqSmall, "dark" -> blckSqSmall)

  //Main Frame Buttons
  val bttnFont = new Font("Impact", 0, 16)
  val bttn1 = new Button("Normal Mode") { font = bttnFont }
  val bttn2 = new Button("Adventure Mode") { font = bttnFont }
  val bttn3 = new Button("Fastest Times Normal Mode") { font = bttnFont }
  val bttn5 = new Button("Fastest Times Adventure Mode") { font = bttnFont }
  val bttn4 = new Button("Help") { font = bttnFont }
  val startbttn = new Button("Start")

  var mainFrame = new MainFrame {
    contents = new BorderPanel {
      background = Color.LIGHT_GRAY
      layout += new Label("Welcome to the Knights Tour!") {
        font = new Font("Impact", 0, 18)
      } -> North
      layout += new GridPanel(1, 2) {
        background = Color.LIGHT_GRAY
        contents += new Label() {
          background = Color.LIGHT_GRAY
          icon = new ImageIcon("kT.png")
        }
        contents += new GridPanel(5, 1) {
          background = Color.LIGHT_GRAY
          contents += bttn1
          contents += bttn2
          contents += bttn3
          contents += bttn5
          contents += bttn4
        }
      } -> South
    }
    centerOnScreen
    resizable = false
    override def closeOperation {
      System.exit(0)
    }
    title = "Knights Tour"
    resizable = false
    centerOnScreen
  }

  val hFrame = new Frame {
    title = "Help"
    background = Color.LIGHT_GRAY
    val str = Source.fromFile(new File("HelpFile.txt")).mkString
    contents = new BorderPanel {
      layout += new ScrollPane(new TextArea(str) {
        editable = false
        font = new Font("Impact", 0, 16)
        background = Color.LIGHT_GRAY
      }) -> BorderPanel.Position.North
    }
    centerOnScreen
    resizable = false
  }

  def readInScores(fileName: String): List[Player] = {
    val lines = Source.fromFile(new File(fileName)).getLines.toArray
    val playerScores = for (line <- lines) yield {
      val playerArr = line.split(" +").toArray
      val playerNames = playerArr.take(playerArr.size - 1)
      var name = ""
      for (player <- playerNames) {name += " " + player}
      new Player(name, playerArr.last.toInt)
    }
    def lt(a: Player, b: Player): Boolean = (a.time - b.time < 0)
    playerScores.toList.sortWith(lt)
  }

  def displayScores(fileName:String, titleName:String) {
    val playerScores = readInScores(fileName)
    val scoreFrame = new Frame {
      title = titleName
      preferredSize = new Dimension(300, 400)
      resizable = false
      background = Color.LIGHT_GRAY
      contents = new ScrollPane(new ListView(playerScores) {
        font = new Font("Impact", 0, 16)
        background = Color.LIGHT_GRAY
      })
      centerOnScreen
      visible = true
    }
  }

  def main(args: Array[String]) {
    mainFrame.visible = true
    mainFrame.listenTo(bttn1.mouse.clicks, bttn2.mouse.clicks, bttn3.mouse.clicks, bttn5.mouse.clicks, bttn4.mouse.clicks)
    mainFrame.reactions += {
      case e: MouseReleased =>
        if (e.source == bttn1) {
          mainFrame.visible = false
          if (curGame != null) curGame.frame.close
          useNormalLayout
          if (!curGame.running) mainFrame.visible = true
          loop
        } else if (e.source == bttn2) {
          if (curGame != null) curGame.frame.close
          mainFrame.visible = false
          useAdventureLayout
          loop
        } else if (e.source == bttn3) {
          displayScores(normalScoreFile, "Fastest Times Normal Mode")
        } else if (e.source == bttn5) {
          displayScores(advScoreFile, "Fastest Times Adventure Mode")
        } else if (e.source == bttn4) {
          hFrame.visible = true
        }
    }
  }
//This is the main program loop, if a game isn't running, the welcome screen is displayed
  def loop() {
    Actor.actor({
      while (curGame.running) {
        mainFrame.visible = false
      }
      curGame = null
      mainFrame.visible = true
    })

  }
//Use normal layout starts up a new Normal Game
  def useNormalLayout() {
    val gameBoard = readInLevels("kTNormalLayout.txt", true).dequeue.board
    val game = NormalGame(gameBoard, hFrame, normalScoreFile)
    curGame = game
    val es = Executors.newCachedThreadPool
    es.execute(new Runnable() {
      def run() = {
        game.playGame()
      }
    })
    es.shutdown()
  }

  def compareLevels(l1: Level, l2: Level): Int = {
    l2.difficulty - l1.difficulty
  }
  def readInLevels(fileName: String, isNormal: Boolean): HeapPriorityQueue[Level] = {
    val dim = new Dimension(260, 260)
    var knightCharacter: Knight = null
    if (!isNormal) knightCharacter = new Knight(0, knight, new Point(0, 150), dim.getWidth().toInt - 30, dim.getHeight().toInt - 30)
    val src = Source.fromFile(new File(fileName))
    val str = src.mkString
    //Chapter 30 I use regular expressions to parse the file containing the levels of the game
    val difficultyRegEx = """(\d+)""".r
    val boardRegEx = """([WBHXS]{8})([WBHXS]{8})([WBHXS]{8})([WBHXS]{8})([WBHXS]{8})([WBHXS]{8})([WBHXS]{8})([WBHXS]{8})""".r
    val diffDataArray = difficultyRegEx.findAllIn(str).matchData.toArray
    val boardDataArray = boardRegEx.findAllIn(str).matchData.toArray
    //Chapter 32 I use my own implementation of a Priority Queue using a Binary Heap
    val levels = new HeapPriorityQueue[Level](compareLevels)
    var lineArray = new Array[String](8)
    for (i <- 0 until diffDataArray.size) {
      for (j <- 1 to boardDataArray(i).groupCount) {
        lineArray(j - 1) = boardDataArray(i).group(j).mkString
      }
      val levelNum = diffDataArray(i).group(1).toInt
      val tmpBoard = Board(wtSqSmall, blckSqSmall, lineArray, knightCharacter, dim, "adventure")
      levels.enqueue(new Level(levelNum, tmpBoard, levelNum, gameCharMap))
    }
    levels
  }

  def useAdventureLayout() {
    val levels = readInLevels("kTLevels.txt", false)
    val adventure = new Adventure(levels, levels.peek.board.knight, hFrame, advScoreFile)
    curGame = adventure
    val es = Executors.newCachedThreadPool
    es.execute(new Runnable() {
      def run() = {
        adventure.startAdventure()
      }
    })
    es.shutdown()
  }
}
