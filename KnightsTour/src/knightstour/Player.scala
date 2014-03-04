package knightstour

class Player(val name:String, val time:Int) {
	override def toString:String = {
	  name + "      " + time + " seconds"
	}
}