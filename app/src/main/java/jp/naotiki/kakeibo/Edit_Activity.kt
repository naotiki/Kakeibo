package jp.naotiki.kakeibo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.File
import jp.naotiki.kakeibo.MainActivity


class Edit_Activity : AppCompatActivity() {
    private val dbName: String = "KakeiboDB"
    private val tableName: String = "KakeiboTable"
    private val dbVersion: Int = 1
    val year=MyVariable.getInstance().Y
    val month=MyVariable.getInstance().M
    val day=MyVariable.getInstance().D
    private var arrayListId: ArrayList<String> = arrayListOf()
    private var arrayListProduct: ArrayList<String> = arrayListOf()
    private var arrayListPrice: ArrayList<Int> = arrayListOf()
    private var arrayListTag: ArrayList<String> = arrayListOf()
    val boxs = mutableListOf<LinearLayout>()
    var selects = mutableListOf<String>()
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        val density = resources.displayMetrics.density
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar3)
        toolBar.setNavigationOnClickListener {this.finish()}

        val list =findViewById<TableLayout>(R.id.list)// <TableLayout>
        var IsWork=false
        try {
            arrayListId.clear();arrayListProduct.clear();arrayListPrice.clear();arrayListTag.clear()

            val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion)
            val database = dbHelper.readableDatabase

            val sql = "SELECT * FROM KakeiboTable WHERE year=\"$year\" AND month=\"$month\" AND day=\"$day\" ORDER BY _id;"

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
                    arrayListId.add(cursor.getString(0))
                    arrayListProduct.add(cursor.getString(4))
                    arrayListPrice.add(cursor.getInt(5))
                    arrayListTag.add(cursor.getString(6))


                    cursor.moveToNext()
                }
            }
        }catch(exception: Exception) {
            Log.e("selectData", exception.toString());
        }
        for (i in 0 until arrayListId.size) {

            val product_name= arrayListProduct[i]
            val price =arrayListPrice[i].toString()
            val tag:String =arrayListTag[i] ?: "なし"
            val box=TableRow(this)// <TableRow>
            //box.setBackgroundResource(R.drawable.background_line)
            list.addView(box)
            val param0 = box.layoutParams as TableLayout.LayoutParams
            param0.width=  TableLayout.LayoutParams.MATCH_PARENT
            param0.height- TableLayout.LayoutParams.MATCH_PARENT
            param0.topMargin=(10f * density + 0.5f).toInt()
            box.layoutParams=param0

            val checkBox=CheckBox(this)//  <CheckBox>
            checkBox.text=product_name
            checkBox.textSize= 25F
            box.addView(checkBox)
            box.setOnLongClickListener{
                NextActivity(arrayListId[boxs.indexOf(it)].toInt())
                true
            }



            val param = checkBox.layoutParams as TableRow.LayoutParams
            //param.setMargins((10f * density + 0.5f).toInt(),0,0,0)
            param.width=(175f * density + 0.5f).toInt();//LinearLayout.LayoutParams.WRAP_CONTENT //(200f * density + 0.5f).toInt();
            param.height= TableRow.LayoutParams.WRAP_CONTENT
            param.weight=1F
            checkBox.layoutParams = param
            checkBox.setOnLongClickListener{
                NextActivity(arrayListId[boxs.indexOf(it.parent)].toInt())
                true
            }
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                val parentView = buttonView.parent// チェックボックスの親を取得
                val SQLID= arrayListId[boxs.indexOf(parentView)] //日にちの項目一覧から親の番号を取得
                Log.i("my_log","BOXID:$SQLID")
                //


                Log.i("my_log","List:$selects")
                if (isChecked){
                    if(selects.indexOf(SQLID)==-1) {
                        selects.add(SQLID)
                        toolBar.setBackgroundColor(Color.rgb(147, 147, 147));
                        toolBar.title = "${selects.size}個選択中"
                    }
                    Log.i("my_log","List ADD=>$selects")
                }else{
                    if (!IsWork){
                        selects.removeAt(selects.indexOf(SQLID))

                        if (selects.size<=0){
                            toolBar.setBackgroundColor(Color.rgb(0,133,119));
                            toolBar.title="家計簿"
                        }else{
                            toolBar.title="${selects.size}個選択中"
                        }
                        Log.i("my_log","List REMOVE=>$selects")
                    }

                }
            }


            val priceText=TextView(this)
            priceText.text="￥$price"
            priceText.textSize=20F
            priceText.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
            priceText.gravity=Gravity.CENTER
            priceText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            priceText.setAutoSizeTextTypeUniformWithConfiguration(10,20,1, TypedValue.COMPLEX_UNIT_SP)
            priceText.setLines(1)
            box.addView(priceText)
            val param2 = priceText.layoutParams as  TableRow.LayoutParams
            param2.weight= 1F
            param2.width= TableRow.LayoutParams.WRAP_CONTENT
            param2.height= TableRow.LayoutParams.MATCH_PARENT
            priceText.layoutParams=param2

            val tagText=TextView(this)
            tagText.text=tag
            tagText.textSize=17F
            tagText.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
            tagText.gravity=Gravity.CENTER
            tagText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            tagText.setAutoSizeTextTypeUniformWithConfiguration(10,17,1, TypedValue.COMPLEX_UNIT_SP)
            tagText.setLines(1)
            box.addView(tagText)
            val param3 = tagText.layoutParams as  TableRow.LayoutParams
            param3.width= TableRow.LayoutParams.WRAP_CONTENT
            param3.height= TableRow.LayoutParams.MATCH_PARENT
            param3.leftMargin=(5f * density + 0.5f).toInt()

            tagText.layoutParams=param3
            boxs.add(box)

        }
        val myApp = MyVariable.getInstance()
        val spinnerItems= myApp.spinnerItems
        val spinnerImages=myApp.spinnerImages
        val taglist =findViewById<Spinner>(R.id.Categories)
        val adapter = TestAdapter(this.applicationContext,
                R.layout.list, spinnerItems, spinnerImages)
        taglist.adapter=adapter
        taglist.setSelection(7)
        var SelectTag=""
        taglist.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        viw: View, position: Int, id: Long) {
                SelectTag= spinnerItems[position]
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
         SelectTag =spinnerItems[taglist.selectedItemPosition]

        val categoryButton=findViewById<Button>(R.id.CategoriesButton)
        categoryButton.setOnClickListener {
            IsWork=true
            val returnArray= mutableListOf<String>()
            selects.forEach {
                val checnge_item=it
                try {
                    val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion);
                    val database = dbHelper.writableDatabase
                    val values = ContentValues()
                    values.put("tag", SelectTag)

                    val whereClauses = "_id = $it"
                    database.update(tableName, values, whereClauses, null)
                }catch(exception: Exception) {
                    Log.e("updateData", exception.toString())
                }

                getIDSQL(it,returnArray)
                val tagtext :TextView= boxs[arrayListId.indexOf(it)].getChildAt(2)as TextView
                tagtext.text=returnArray[6]
                val checkbox:CheckBox=boxs[arrayListId.indexOf(it)].getChildAt(0)as CheckBox
                checkbox.isChecked=false
            }

            selects = mutableListOf<String>()
            IsWork=false
            toolBar.setBackgroundColor(Color.rgb(0,133,119));
            toolBar.title="家計簿"
        }
        val del_btn=findViewById<Button>(R.id.removeButton)
        del_btn.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("確認")
                    .setMessage("選択したものを削除してもよろしいでしょうか？").setPositiveButton("OK") { dialog, which ->
                        // Yesが押された時の挙動
                        IsWork=true

                        selects.forEach {


                            try {
                                val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion);
                                val database = dbHelper.writableDatabase

                                val whereClauses = "_id = $it"
                                database.delete(tableName, whereClauses, null)
                            }catch(exception: Exception) {
                                Log.e("deleteData", exception.toString())
                            }
                            //it=5 id
                            //select[0]==5
                            //select 1
                            list.removeView(boxs[arrayListId.indexOf(it)])
                            arrayListProduct.removeAt(arrayListId.indexOf(it))
                            arrayListPrice.removeAt(arrayListId.indexOf(it))
                            arrayListTag.removeAt(arrayListId.indexOf(it))
                            arrayListId.removeAt(arrayListId.indexOf(it))


                        }

                        selects = mutableListOf<String>()
                        IsWork=false
                        toolBar.setBackgroundColor(Color.rgb(0,133,119));
                        toolBar.title="家計簿"
                    }
                    .setNegativeButton("NO") { dialog, which ->
                        // Noが押された時の挙動
                        dialog.dismiss();
                    }.show()

        }
    }
    fun NextActivity(id: Int){
        val intent=Intent(this,Data_correction::class.java)

        intent.putExtra("ID",id)

        val requestCode = 1000
        startActivityForResult(intent, requestCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK &&
                requestCode == 1000 && intent != null) {
//　返り値で　idを受け取り　boxs[id]が変更対象
            val ID:Int = intent.extras?.getInt("ID")?:-1

            if (ID!=-1){
                val returnArray = mutableListOf<String>()
                getIDSQL(ID.toString(),returnArray)
                val checkbox:CheckBox= boxs[arrayListId.indexOf(ID.toString())].getChildAt(0)as CheckBox
                checkbox.text=returnArray[4]
                val pricetext:TextView=  boxs[arrayListId.indexOf(ID.toString())].getChildAt(1)as TextView
                pricetext.text= "￥${returnArray[5]}"
            }

        }
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
