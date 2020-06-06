package jp.naotiki.kakeibo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.MonthDisplayHelper
import android.view.View
import android.widget.*
import androidx.core.view.children

class CSV_export : AppCompatActivity() {
var Category_flag= arrayListOf(true,true,true,true,true,true,true,true)
    var Year=""
    var Month=""
    var Day=""
    var date_SQL="year=\"$Year\""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_csv_export)
        Year=intent.extras?.getString("Y")?:""
        Month=intent.extras?.getString("M")?:""
        Day=intent.extras?.getString("D")?:""
        val spinner=findViewById<Spinner>(R.id.spinner2)
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        viw: View, position: Int, id: Long) {
                when(position){
                    0->{
                        date_SQL="year=\"$Year\""
                    }
                    1->{
                        date_SQL="year=\"$Year\" AND month=\"$Month\""
                    }
                    2->{
                        date_SQL="year=\"$Year\" AND month=\"$Month\" AND day=\"$Day\""
                    }

                }
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}})
        //spinner.setOnItemClickListener { parent, view, position, id ->}
        val tableLayout=findViewById<TableLayout>(R.id.tableLayout2)
        val categorys= arrayListOf<CheckBox>()
        (tableLayout.getChildAt(0)as TableRow).children.forEach {
            categorys.add((it as CheckBox))
        }
        (tableLayout.getChildAt(1)as TableRow).children.forEach {
            categorys.add((it as CheckBox))
        }
        (tableLayout.getChildAt(2)as TableRow).children.forEach {
            categorys.add((it as CheckBox))
        }

        Category_flag= arrayListOf(true,true,true,true,true,true,true,true)
        categorys.forEach {
            it.isChecked=true
            it.setOnCheckedChangeListener { _, isChecked ->
                val no=categorys.indexOf(it)
                if (isChecked){
                    Category_flag[no]=isChecked
                }else{
                    Category_flag[no]=isChecked
                }

            }
        }


val save=findViewById<Button>(R.id.save2)
        save.setOnClickListener {
            var Category_SQL=""
            if (Category_flag!= arrayListOf(true,true,true,true,true,true,true,true)){
                var i=0
               Category_flag.forEach {
                   Category_SQL+=" AND tag=\"${MyVariable.getInstance().spinnerItems[i]}\""
               }
                   i++
               }

            val Where= "WHERE $date_SQL$Category_SQL"
        Log.i("my_List","野菜:${Category_flag[0]}\n 肉、魚:${Category_flag[1]}\n 主食:${Category_flag[2]}\n 嗜好品、果物:${Category_flag[3]}\n 惣菜、カップ麺:${Category_flag[4]}\n 調味料、その他:${Category_flag[5]}\n 日用品:${Category_flag[6]}\n その他:${Category_flag[7]}")
            val intent = Intent()
            intent.putExtra("where",Where)
            Log.e("my_log","Where=$Where")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        findViewById<Button>(R.id.cansel2).setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
        }
    }

