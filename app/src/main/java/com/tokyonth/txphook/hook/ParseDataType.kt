package com.tokyonth.txphook.hook

object ParseDataType {

    fun pares(index: Int, value: String): Any {
        var optValue: Any = ""
        when (index) {
            0 -> {
                optValue = value.toInt()
            }
            1 -> {
                optValue = value.toLong()
            }
            2 -> {
                optValue = value.toBooleanStrict()
            }
            3 -> {
                optValue = value.toFloat()
            }
            4 -> {
                optValue = value.toDouble()
            }
            5 -> {
                optValue = value
            }
            6 -> {
                optValue = "XP_NOT"
            }
        }
        return optValue
    }

}
