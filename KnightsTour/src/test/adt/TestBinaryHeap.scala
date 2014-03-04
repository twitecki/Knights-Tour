package test.adt

import knightstour.HeapPriorityQueue
import org.junit._
import org.junit.Assert._

class TestBinaryHeap {
  var pq: HeapPriorityQueue[Int] = null
  
  def compareLevels(l1: Int, l2: Int): Int = {
    l1 - l2
  }
  
  @Before def setup {
    pq = new HeapPriorityQueue[Int](compareLevels)
  }

  @Test def emptyToStart {
    assertTrue(pq.isEmpty)
  }

  @Test def addRemove1 {
    pq.enqueue(5)
    assertFalse(pq.isEmpty)
    assertEquals(5, pq.peek)
    assertEquals(5, pq.dequeue())
    assertTrue(pq.isEmpty)
  }

  @Test def addRemove3 {
    pq.enqueue(5)
    pq.enqueue(3)
    pq.enqueue(7)
    pq.enqueue(6)
    assertEquals(7, pq.peek)
    assertEquals(7, pq.dequeue())
    assertEquals(6, pq.peek)
    assertEquals(6, pq.dequeue())
    assertEquals(5, pq.peek)
    assertEquals(5, pq.dequeue())
    assertEquals(3, pq.peek())
  }

  @Test def addRemove300 {
    val nums = Array.fill(300)(util.Random.nextInt)
    for (num <- nums) {
      if (num > 0) pq.enqueue(num)
    }
    
    for (n <- nums.sorted.reverse) {
      if (n > 0) {
      assertEquals(n, pq.peek)
      assertEquals(n, pq.dequeue())
      }
    }
  }
}