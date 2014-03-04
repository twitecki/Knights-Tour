package knightstour

import scala.collection.mutable.Stack
import scala.Mutable
import scala.Mutable
import scala.collection.mutable.Buffer

class MyStack[A] extends Serializable{
  
	class Node(var data:A, var next:Node)
	
	var head:Node = null
	
	def isEmpty():Boolean = {
	  head == null
	}
	
	def push(elem:A) {
	  if (isEmpty()) {
	    head = new Node(elem,null)
	  } else {
	    head = new Node(elem, head)
	  }
	}
	
	def pop:A = {
	  if (!isEmpty()) {
	     val rtnData = head.data
	     head = head.next
	     rtnData
	  } else {
	    throw new IndexOutOfBoundsException("Stack is empty")
	  }
	 
	}
	
	def peek():A = {
	  if (!isEmpty()) {
	     head.data 
	  } else {
	    throw new IndexOutOfBoundsException("Stack is empty")
	  }
	}
}