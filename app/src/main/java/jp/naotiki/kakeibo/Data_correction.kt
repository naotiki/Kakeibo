package jp.naotiki.kakeibo

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.File

class Data_correction : AppCompatActivity() {
    private val dbName: String = "KakeiboDB"
    private val tableName: String = "KakeiboTable"
    private val dbVersion: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_correction)
        val productText=findViewById<EditText>(R.id.ProductEdit)
        val priceText =findViewById<EditText>(R.id.PriceEdit)
        val SQLID=intent.extras?.getInt("ID")?:-1// SQL のID
        val year=intent.extras?.getString("Y")?:""
        val month=intent.extras?.getString("M")?:""
        val day=intent.extras?.getString("D")?:""


        if (SQLID!=-1){
            val returnArray= mutableListOf<String>()
            getIDSQL(SQLID.toString(),returnArray)
            var product =returnArray[4]
            var price=returnArray[5]
            productText!!.setText(product, TextView.BufferType.NORMAL)
            priceText!!.setText(price, TextView.BufferType.NORMAL)
            val savebtn=findViewById<Button>(R.id.save)
            savebtn.setOnClickListener {

                if(productText.text.toString() != ""&& priceText.text.toString()!=""){
                    product = productText.text.toString()
                    price = priceText.text.toString()
                    try {
                        val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion);
                        val database = dbHelper.writableDatabase
                        val values = ContentValues()
                        values.put("product", product)
                        values.put("price", price)

                        val whereClauses = "_id = ${SQLID.toString()}"
                        database.update(tableName, values, whereClauses, null)
                    }catch(exception: Exception) {
                        Log.e("updateData", exception.toString())
                    }

                    getIDSQL(SQLID.toString(),returnArray)
                    Toast.makeText(applicationContext, "保存しました", Toast.LENGTH_LONG).show()
                    val intent = Intent()
                    intent.putExtra("ID",SQLID)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else{
                    Toast.makeText(applicationContext, "商品名と値段を入力してね", Toast.LENGTH_LONG).show()
                }

            }
        }
        val cancelBtn=findViewById<Button>(R.id.button2)
        cancelBtn.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }
    fun getIDSQL(SQLID:String, returnArray:MutableList<String> ){
        try {//これで取得できる


            val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion)
            val database = dbHelper.readableDatabase

            val sql = "SELECT * FROM KakeiboTable WHERE _id= ${SQLID.toInt()} ORDER BY _id;"

            val cursor = database.rawQuery(sql, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    Log.i("my_log","SELECT:${cursor.getString(0)}")
                    Log.i("my_log","SELECT:${cursor.getString(1)}")
                    Log.i("my_log","SELECT:${cursor.getString(2)}")
                    Log.i("my_log","SELECT:${cursor.getString(3)}")
                    Log.i("my_log","SELECT:${cursor.getString(4)}")
                    Log.i("my_log","SELECT:${cursor.getString(5)}")
                    Log.i("my_log","SELECT:${cursor.getString(6)}")
                    returnArray.add(cursor.getString(0))// ID
                    returnArray.add(cursor.getString(1))// YYYY
                    returnArray.add(cursor.getString(2))// MM
                    returnArray.add(cursor.getString(3))// DD
                    returnArray.add(cursor.getString(4))// product
                    returnArray.add(cursor.getString(5))// price
                    returnArray.add(cursor.getString(6))// tag



                    cursor.moveToNext()
                }
            }
        }catch(exception: Exception) {
            Log.e("selectData", exception.toString());
        }
    }
}
