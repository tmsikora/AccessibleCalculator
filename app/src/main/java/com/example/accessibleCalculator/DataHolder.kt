package com.example.accessibleCalculator

object DataHolder {
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
