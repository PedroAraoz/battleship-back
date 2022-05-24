package com.example.demo.helper

import java.util.*

fun <T> Optional<T>.unwrap(): T? = orElse(null)
