package com.triplerock.tictactoe.utils

import android.util.Log

class Logger {
    companion object {
        private const val TAG = "TicTacToe_Logger"

        /**
         * Set to high log levels to avoid printing low priority logs
         */
        private const val ALLOWED_LOG_LEVEL = Log.VERBOSE
        private fun logControl(logLevel: Int, message: String) {
            if (logLevel >= ALLOWED_LOG_LEVEL) Log.println(logLevel, TAG, message)
        }

        private val methodName: String
            private get() {
                val element = Thread.currentThread().stackTrace[4]
                var className = element.className
                className = className.substring(className.lastIndexOf(".") + 1)
                val methodName = element.methodName
                return "$className:$methodName> "
            }

        fun entry() {
            logControl(Log.VERBOSE, methodName + "entry >>")
        }

        fun exit() {
            logControl(Log.VERBOSE, methodName + "entry >>")
        }

        fun info(message: String) {
            logControl(Log.INFO, methodName + message)
        }

        fun warnNoTag(message: String) {
            logControl(Log.WARN, message)
        }

        fun debugNoTag(message: String) {
            logControl(Log.DEBUG, message)
        }

        fun debug(message: String) {
            logControl(Log.DEBUG, methodName + message)
        }

        fun verbose(message: String) {
            logControl(Log.VERBOSE, methodName + message)
        }

        fun warn(message: String) {
            logControl(Log.WARN, methodName + message)
        }

        fun error(message: String) {
            logControl(Log.ERROR, methodName + message)
        }
    }
}