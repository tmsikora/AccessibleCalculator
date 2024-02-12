package com.example.accessibleCalculator.utils

object DataHolder {
    // List to store entered numbers
    //val enteredNumbers: MutableList<Int> = mutableListOf()

    // String to store the current equation
    var currentEquation: String = ""

    // Singleton pattern
    private var instance: DataHolder? = null

    fun getInstance(): DataHolder {
        if (instance == null) {
            instance = DataHolder
        }
        return instance!!
    }
}
