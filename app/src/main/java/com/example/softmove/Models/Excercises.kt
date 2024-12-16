package com.example.softmove.Models

data class Exercises(
    val Exercise_name: String = "",
    val Exercise_time: String = "",

    )
{
    // No-argument constructor required by Firebase Realtime Database
    constructor() : this("", "")
}
