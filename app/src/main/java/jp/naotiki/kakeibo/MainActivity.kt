package jp.naotiki.kakeibo

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import java.util.*


//public class MainActivity extends AppCompatActivity {
//@Suppress("CAST_NEVER_SUCCEEDS")
class MainActivity : Activity()  {
    private val dbName: String = "KakeiboDB"
    private val tableName: String = "KakeiboTable"
    private val dbVersion: Int = 1
    private var productText: EditText? =null
    private var priceText: EditText? =null
    private var lang = 0
    private var product = ""
    private var price = ""
    private var tag =""
    val year=MyVariable.getInstance().Y
    val month=MyVariable.getInstance().M
    val day=MyVariable.getInstance().D


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val datetext =findViewById<TextView>(R.id.Datetext)

        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)



        // 言語選択 0:日本語、1:英語、2:オフライン、その他:General
        Log.i("my_log","Y:$year M:$month D:$day")
        datetext.text="$year/$month/$day"
        lang = 0

        // 認識結果を表示させる
        productText = findViewById<EditText>(R.id.product_text) as EditText
        priceText = findViewById<EditText>(R.id.price_text) as EditText
        val buttonStart = findViewById<View>(R.id.button_start) as Button
        buttonStart.setOnClickListener {
            // 音声認識を開始
            speech("商品名と値段を入力")
            //speech("値段を入力");
        }
        val myApp = MyVariable.getInstance()
       val spinnerItems= myApp.spinnerItems
        val spinnerImages=myApp.spinnerImages
        val taglist =findViewById<Spinner>(R.id.spinner)
        val adapter = TestAdapter(this.applicationContext,
                R.layout.list, spinnerItems, spinnerImages)
        taglist.adapter=adapter
        taglist.setSelection(7)
        taglist.setOnItemSelectedListener(object : OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        viw: View, position: Int, id: Long) {
                tag= spinnerItems[position]
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
                tag=spinnerItems[taglist.selectedItemPosition]
      /*  taglist.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val spinner = parent as Spinner
                // 選択したアイテムを取得
                tag= spinner.selectedItem as String

                // ログで確認
                Log.v("my_log", tag)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) { // アイテムを選択しなかったとき
            }
        }*/
        val savebtn=findViewById<Button>(R.id.save_button)
        savebtn.setOnClickListener {
            if(productText!!.text.toString() != ""&&priceText!!.text.toString()!=""){
                // 取得したテキストを TextView に張り付ける

                product = productText!!.text.toString()
                price = priceText!!.text.toString()
                try {
                    val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion);
                    val database = dbHelper.writableDatabase

                    val values = ContentValues()
                    values.put("year",year)
                    values.put("month",month)
                    values.put("day",day)
                    values.put("product", product)
                    values.put("price", price)
                    values.put("tag",tag)

                    database.insertOrThrow(tableName, null, values)

                }catch(exception: Exception) {
                    Log.e("insertData", exception.toString())
                }
                Toast.makeText(applicationContext, "保存しました", Toast.LENGTH_LONG).show()
                productText!!.setText("",TextView.BufferType.NORMAL)
                priceText!!.setText("",TextView.BufferType.NORMAL)
            }else{
                Toast.makeText(applicationContext, "商品名と値段を入力してね", Toast.LENGTH_LONG).show()
            }
        }
        toolBar.setNavigationOnClickListener {//終了
            if (productText!!.text.toString() != ""||priceText!!.text.toString()!=""){
                AlertDialog.Builder(this)
                        .setTitle("確認")
                        .setMessage("終了してよろしいですか").setPositiveButton("OK") { dialog, which ->
                            // Yesが押された時の挙動
                            this.finish()
                        }
                        .setNegativeButton("No") { dialog, which ->
                            // Noが押された時の挙動
                            dialog.dismiss();
                        }.show()
            }else{
                this.finish()
            }

        }

    }
    override fun onBackPressed() {
        if (productText!!.text.toString() != ""||priceText!!.text.toString()!=""){
            AlertDialog.Builder(this)
                    .setTitle("確認")
                    .setMessage("編集を終了してよろしいですか").setPositiveButton("OK") { dialog, which ->
                        // Yesが押された時の挙動
                        this.finish()
                    }
                    .setNegativeButton("NO") { dialog, which ->
                        // Noが押された時の挙動
                        dialog.dismiss();
                    }.show()
        }else{
            this.finish()
        }
    }

    private fun speech(message: String) {
        // 音声認識の　Intent インスタンス
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        if (lang == 0) {
            // 日本語
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString())
        } else if (lang == 1) {
            // 英語
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString())
        } else if (lang == 2) {
            // Off line mode
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        } else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, message)
        try {
            // インテント発行
            startActivityForResult(intent, REQUEST_CODE) // 0　商品名　1　値段
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }catch (e:RuntimeException){
            e.printStackTrace()
        }
    }

    // 結果を受け取るために onActivityResult を設置
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)  {

            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                // 認識結果を ArrayList で取得
                val candidates = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (candidates!!.size   > 0) {
                    // 認識結果候補で一番有力なものを表示
                    var str = candidates.get(0) ?:""

                    str=str.replace("万","0000")
                    //  price = str.replace("([^0-9]+)[^円]".toRegex(),"")
                    //  product = str.replace("[0-9]+[円]".toRegex(),"").replaceFirst("円".toRegex(), "")
                    product=str.replace("[0-9]+[円]".toRegex(),"")
                    price = "[0-9]+[円]".toRegex().find(str)?.value.toString().replaceFirst("円".toRegex(), "")
                    Log.i("my_log","price:$price")
                    productText!!.setText(product,TextView.BufferType.NORMAL)
                    priceText!!.setText(price,TextView.BufferType.NORMAL)
                    if(priceText!!.text.toString()=="null"){
                        priceText!!.setText("",TextView.BufferType.NORMAL)

                    }
                }
            }



    }

    companion object {
        private const val REQUEST_CODE = 1000
    }

}




