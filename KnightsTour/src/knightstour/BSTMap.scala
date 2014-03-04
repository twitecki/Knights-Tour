package knightstour

//Chapter 29
/*I am planning to add an adventure mode to my game where it is interactive and in 
real time as opposed to move based. The knight will be able to move all over the map 
with the arrow keys, but will be required to place flags in a path to a target to reach the 
next level, the flags can only be placed in the same fashion as the knight would normally move.
This BST will be used to keep track of different types of levels that the knight will advance on to 
once it has completed a level. **Note this is the implementation of BSTMap We made in class
*/

import collection.mutable
import scala.collection.mutable.ArrayStack

class BSTMap[K, V](comp: (K, K) => Int) extends mutable.Map[K, V] {
  class Node(var key: K, var value: V, var left: Node = null, var right: Node = null)

  private var root: Node = null

  def +=(kv: (K, V)) = {
    if (root == null) {
      root = new Node(kv._1, kv._2)
    } else {
      var rover = root
      var prev: Node = null
      var c = 0
      while (rover != null) {
        c = comp(kv._1, rover.key)
        prev = rover
        if (c == 0) {
          rover.value = kv._2
          rover = null
        } else if (c < 0) {
          rover = rover.left
        } else {
          rover = rover.right
        }
      }
      if (c < 0) {
        prev.left = new Node(kv._1, kv._2)
      } else if (c > 0) {
        prev.right = new Node(kv._1, kv._2)
      }
    }
    this
  }

  def -=(key: K) = this

  def get(key: K): Option[V] = {
    var rover = root
    while (rover != null) {
      val c = comp(key, rover.key)
      if (c == 0) {
        return Some(rover.value)
      } else if (c < 0) {
        rover = rover.left
      } else {
        rover = rover.right
      }
    }
    None
  }

  def iterator = new Iterator[(K, V)] {
    val stack = new ArrayStack[Node]
    pushAllLeft(root)
    def next():(K,V) = {
      val n = stack.pop()
      pushAllLeft(n.right)
      (n.key,n.value)
    }
    def hasNext:Boolean = !stack.isEmpty
    def pushAllLeft(n:Node) {
      if(n!=null) {
        stack.push(n)
        pushAllLeft(n.left)
      }
    }
  }
}