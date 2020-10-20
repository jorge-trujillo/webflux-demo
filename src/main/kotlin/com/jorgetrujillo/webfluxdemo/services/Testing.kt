package com.jorgetrujillo.webfluxdemo.services

import java.util.Stack
import java.util.concurrent.LinkedBlockingQueue

fun main() {
  val root = Node(
    "root",
    Node("a", Node("a1"), Node("a2")),
    Node("b", Node("b1"))
  )

  preOrder(root)
}

fun preOrder(root: Node) {
  val queue = LinkedBlockingQueue<Node>()

  queue.add(root)
  while (!queue.isEmpty()) {
    val current = queue.poll()
    println(current.id)

    if (current.left != null) {
      queue.add(current.left)
    }
    if (current.right != null) {
      queue.add(current.right)
    }
  }
}

fun inOrder(root: Node) {
  val stack = Stack<Node>()
  var current: Node? = root

  while (current != null || !stack.isEmpty()) {

    while (current != null) {
      stack.push(current)
      current = current.left
    }

    current = stack.pop()
    println(current.id)

    while (current?.right == null) {
      current = stack.pop()
      println(current.id)
    }

    current = current.right
    if (current?.right != null) {
      current = current?.right
    }
  }

}

data class Node(
  val id: String,
  val left: Node? = null,
  val right: Node? = null
)

