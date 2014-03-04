package knightstour

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.Color
import java.util.Scanner
import scala.swing.Panel
import javax.swing.ImageIcon
import java.io.File
import scala.collection.mutable.Buffer
import scala.collection.mutable.ListBuffer
import java.awt.Rectangle
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import javax.imageio.ImageIO
import scala.io.Source
import java.awt.Point

//Chapter 19, Polymorphism is used here by extending panel, this allows the board object created from this 
//class to be used where a componenet is needed. Since a panel is a component, and board extends panel, board
//acts as a componentand much more
class Board(@transient var sq1: BufferedImage, @transient var sq2: BufferedImage,
  val knight: Knight, val dim: Dimension, gameType: String) extends Panel with Serializable {

  var villains = List[Villain]()

  preferredSize = dim
  var boardArray = Array.ofDim[Square](8, 8)
  @transient var graphics: Graphics2D = null
  var squareCount = 0
  var startingSquare: Square = null
  var endingSquare: Square = null
  var boardX = 0
  var boardY = 0
  var boardStartX = 10
  var boardStartY = 10

  //Paints the current board array defined
  override def paint(g: Graphics2D) {
    g.setPaint(Color.LIGHT_GRAY)
    g.fillRect(0, 0, this.preferredSize.getWidth().toInt, this.preferredSize.getHeight().toInt)
    graphics = g

    if (gameType == "normal") {
      boardStartX = size.width / 4
      boardStartY = size.height / 2 - 120
    }

    val baseRect = new Rectangle(boardStartX - 10, boardStartY - 10, (20 + 30 * 8), (20 + 30 * 8))
    g.setColor(Color.DARK_GRAY)
    g.fill(baseRect)
    for (i <- 0 until boardArray.length) {
      for (i2 <- 0 until boardArray(i).length) {
        val sq = boardArray(i)(i2)
        if (sq != null) {
          g.drawImage(sq.getImage,
            null, (30 * i) + boardStartX, (30 * i2) + boardStartY)
          sq.setLocation((30 * i) + boardStartX, (30 * i2) + boardStartY)
        }
      }
    }
    if (knight != null) {
      g.drawImage(knight.img, null, knight.location.x, knight.location.y)
    }
    if (!villains.isEmpty) {
      for (villain <- villains) {
        g.drawImage(villain.img, null, villain.location.x, villain.location.y)
      }
    }
  }

  def getLayout(lineArray: Array[String]) {
    var curFile = 0
    var count = 0
    for (line <- lineArray) {
      val characters = line.toCharArray()
      for (i <- 0 until characters.length) {
        val squareType = characters(i)
        if (squareType == 'W') {
          boardArray(curFile)(i) = new Square(sq1, (97 + curFile).toChar, characters.length - i, "light")
          count += 1
        } else if (squareType == 'B') {
          boardArray(curFile)(i) = new Square(sq2, (97 + curFile).toChar, characters.length - i, "dark")
          count += 1
        } else if (squareType == 'X') {
          val img = ImageIO.read(new File("nullSquare.jpg"))
          val sqr = new Square(img, (97 + curFile).toChar, characters.length - i, "silver")
          boardArray(curFile)(i) = sqr
          endingSquare = sqr
          count += 1
        } else if (squareType == 'S') {
          val flag = ImageIO.read(new File("flag.png"))
          val sqr = new Square(flag, (97 + curFile).toChar, characters.length - i, "flag")
          startingSquare = sqr
          boardArray(curFile)(i) = sqr
          count += 1
        } else if (squareType == 'H') {
          val heart = ImageIO.read(new File("heart.png"))
          val sqr = new Square(heart, (97 + curFile).toChar, characters.length - i, "heart")
          boardArray(curFile)(i) = sqr
          count += 1
        }
      }
      curFile += 1
    }
    squareCount = count
  }
}

object Board {
  def apply(square1: BufferedImage, square2: BufferedImage, lineArray: Array[String],
    knight: Knight, dim: Dimension, gameType: String): Board = {
    val board = new Board(square1, square2, knight, dim, gameType)
    board.getLayout(lineArray)
    board
  }
}
