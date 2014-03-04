package knightstour

//Chapter 32 Binary Heap
class HeapPriorityQueue[A: Manifest](comp: (A, A) => Int) {

  var heapArray = new Array[A](10)

  //Marks the index of the next place to put a value in the heap
  var mark = 1
  var size = 0

  def enqueue(elem: A) {
    size += 1
    if (mark >= heapArray.length) {
      val tmp = new Array[A](heapArray.length * 2)
      for (i <- 1 until heapArray.length) {
        tmp(i) = heapArray(i)
      }
      heapArray = tmp
    }
    var bubble = mark
    while (bubble > 1 && comp(elem, heapArray(bubble / 2)) > 0) {
      heapArray(bubble) = heapArray(bubble / 2)
      bubble /= 2
    }
    heapArray(bubble) = elem
    mark += 1
  }

  def dequeue(): A = {
    size -= 1
    if (!isEmpty) {
      val ret = heapArray(1)
      mark -= 1
      val temp = heapArray(mark)
      heapArray(mark) = heapArray(0)
      var stone = 1
      var flag = true
      while (flag && stone * 2 < mark) {
        var greaterChild = if (stone * 2 + 1 < mark && comp(heapArray(stone * 2 + 1), heapArray(stone * 2)) > 0) {
          stone * 2 + 1
        } else stone * 2
        if (comp(heapArray(greaterChild), temp) > 0) {
          heapArray(stone) = heapArray(greaterChild)
          stone = greaterChild
        } else {
          flag = false
        }
      }
      heapArray(stone) = temp
      ret
    } else {
      throw new IndexOutOfBoundsException("There are no elements to dequeue")
    }
  }

  def peek(): A = heapArray(1)
  def isEmpty(): Boolean = mark == 1

}