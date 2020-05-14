package jp.naotiki.kakeibo

import android.app.Application

/**
 * @see MyVariable グローバル変数
 */
class MyVariable:Application() {

    val spinnerItems = arrayOf(
            "野菜","肉、魚", "主食", "嗜好品、果物", "惣菜、カップ麺等","調味料、その他", "日用品","その他")
    val category=arrayOf(
             "food", "日用品","その他" )
    val foods= arrayOf("野菜","肉、魚", "主食", "嗜好品、果物", "惣菜、カップ麺等","調味料、その他")
    val spinnerImages = arrayOf(
            "food","food", "food", "food", "food", "food", "transparent","transparent")

    var Y:String=""
    var M:String=""
    var D:String=""
var SelectedDate= arrayListOf("","","")


    companion object {
        private var instance : MyVariable? = null

        /**
         * @return MyVariableの変数
         */
        fun  getInstance(): MyVariable {
            if (instance == null)
                instance = MyVariable()

            return instance!!
        }
    }
}