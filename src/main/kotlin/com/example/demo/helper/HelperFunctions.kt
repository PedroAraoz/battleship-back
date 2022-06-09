package com.example.demo.helper

import java.util.*

fun <T> Optional<T>.unwrap(): T? = orElse(null)

const val cyan = "\u001B[36m"
const val green = "\u001b[32m"
const val reset = "\u001b[0m"
fun printlnCyan(message: Any?) {
  println(cyan + message + reset)
}

fun printlnGreen(message: Any?) {
  println(green + message + reset)
}