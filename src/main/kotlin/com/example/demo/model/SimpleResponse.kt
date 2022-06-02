package com.example.demo.model

data class SimpleResponse(val res: String) {
  constructor(res: Boolean) : this(res.toString())
}
