package knightstour

import java.awt.image.BufferedImage

class Square(@transient var img: BufferedImage, val file: Char, val rank: Int, var sqType:String) extends Serializable {

  var minX = 0
  var minY = 0
  val originalType = sqType
  @transient var originalImage = img
  @transient var prevImg:BufferedImage = null
  
  //For load method
  def setOriginalImage(original:BufferedImage) {
    originalImage = original
  }
  
  def chgImg(newImg: BufferedImage, imgType:String) {
    prevImg = img
    img = newImg
    sqType = imgType
  }
  
  def getFile() = {
    file.toInt - 97
  }

  def getCoord(): (Char, Int) = {
    (file, rank)
  }
  
  def setLocation(x:Int,y:Int) {
    minX = x
    minY = y
  }
  
  def getX:Int = {
    minX
  } 
  
  def getY:Int = {
   minY 
  }
  
  def getHeight: Int = {
     img.getHeight
  }

  def getImage: BufferedImage = {
    img
  }
  
  override def toString():String = {
    file + " " + rank
  }
  
}