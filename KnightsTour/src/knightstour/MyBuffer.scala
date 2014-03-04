package knightstour

import scala.collection.mutable.Buffer

//Chapter 25
class MyBuffer[A] extends Buffer[A] {

  class Node(var data: A, var next: Node)

  var h: Node = null
  var t: Node = null
  var lngth: Int = 0

  def +=(elem: A) = {
    if (h == null) {
      h = new Node(elem, null)
      t = h
      lngth += 1
      this
    } else {
      t.next = new Node(elem, null)
      t = t.next
      lngth += 1
      this
    }

  }

  def +=:(elem: A) = {
    if (h == null) {
      h = new Node(elem, null)
      t = h
      lngth += 1
      this
    } else {
      h = new Node(elem, h)
      lngth += 1
      this
    }

  }

  def apply(n: Int): A = {
    var cur = h
    var i = 0
    while (i < n) {
      if (cur == null) {
        throw new ArrayIndexOutOfBoundsException()
      } else {
        cur = cur.next
        i += 1
      }
    }
    cur.data
  }

  def iterator = new Iterator[A] {
    var cur = h
    def next: A = {
      val rtnValue = cur.data
      cur = cur.next
      rtnValue

    }
    def hasNext: Boolean = {
      cur != null
    }

  }

  def insertAll(n: Int, elems: collection.Traversable[A]) {
    var i = 0
    var cur = h
    lngth += elems.size
    if (n == 0) {
      elems.toList.reverse.foreach(elem => {
    	  this.+=(elem)
      })
    } else {
      while (i < n - 1) {
        if (cur != null) {
          cur = cur.next
          i += 1
        } else throw new IndexOutOfBoundsException()
      }
     elems.toList.reverse.foreach(elem => {
         cur.next = new Node(elem, cur.next)
      })
    }
  }

  def clear() {
    h = null
    t = h
    lngth = 0
  }

  def length: Int = {
    lngth
  }

  def remove(n: Int): A = {
    var cur = h
    var count = 0
    lngth -= 1
    if (h != null) {
      if (n == 0) { //If its the first one remove it
        val rtnValue = h.data
        h = h.next
        rtnValue
      } else { //otherwise navigate to one behind it
        while (count < n - 1) {
          if (cur != null) {
            cur = cur.next
            count += 1
          } else throw new IndexOutOfBoundsException()
        }
        if (cur.next != null) {
          val rtnValue = cur.next.data
          cur.next = cur.next.next
          rtnValue
        } else throw new IndexOutOfBoundsException()
      }
    } else throw new IndexOutOfBoundsException("Buffer is empty")
  }

  def update(n: Int, elem: A) {
    var cur = h
    var count = 0
    while (count < n && cur != null) {
      cur = cur.next
    }
    if (cur != null) {
      cur.data = elem
    } else {
      throw new IndexOutOfBoundsException()
    }
  }
}