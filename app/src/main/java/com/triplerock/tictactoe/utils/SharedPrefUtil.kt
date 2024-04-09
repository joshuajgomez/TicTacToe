package com.triplerock.tictactoe.utils

import android.content.Context

class SharedPrefUtil(context: Context) {

    private val sharedPrefName = "tic_data_store"
    private val keyName = "name"

    private val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

    fun setName(name: String) {
        Logger.debug("user = [$name]")
        val editor = sharedPref.edit()
        editor.putString(keyName, name)
        editor.apply()
    }

    fun getName(): String {
        val name = sharedPref.getString(keyName, "")!!
        Logger.debug("name=$name")
        return name
    }

    fun isNameSet() = sharedPref.contains(keyName)
}
