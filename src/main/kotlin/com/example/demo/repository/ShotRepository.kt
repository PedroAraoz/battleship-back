package com.example.demo.repository

import com.example.demo.model.Shot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShotRepository : JpaRepository<Shot, Long>