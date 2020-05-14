package jp.naotiki.kakeibo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class StartActivity : AppCompatActivity() {
    // SQLのデータ
    private val dbName: String = "KakeiboDB"
    private val tableName: String = "KakeiboTable"
    private val dbVersion: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val food = mutableListOf(
                mutableListOf<Int>(),//肉、魚　
                mutableListOf<Int>(),//主食
                mutableListOf<Int>(),//野菜
                mutableListOf<Int>(),//果物
                mutableListOf<Int>(),//日用品、消耗品
                mutableListOf<Int>(),//嗜好品
                mutableListOf<Int>())//その他


        val format = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        //val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val button = findViewById<Button>(R.id.button)
        val editButton =findViewById<Button>(R.id.button5)
        val viewButton =findViewById<Button>(R.id.button3)
        val file = getFileStreamPath("data.json")
        val isExists = file.exists() //data.jsonがあるか
        Log.i("my_log", "ファイル:$isExists")

        val calendarView =findViewById<MaterialCalendarView>(R.id.calendarView)

        var selectdate :CalendarDay = CalendarDay.today()
        fun selected_fun(date:CalendarDay){
            selectdate=date
            MyVariable.getInstance().Y=selectdate.date.toString().split("-")[0]
            MyVariable.getInstance().M=selectdate.date.toString().split("-")[1]
            MyVariable.getInstance().D=selectdate.date.toString().split("-")[2]
            val daydata=findViewById<TextView>(R.id.day_data)
            try {
              var arrayListPrice: ArrayList<Int> = arrayListOf()
                arrayListPrice.clear();

                val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion)
                val database = dbHelper.readableDatabase

                        val sql =
                        "SELECT * FROM KakeiboTable WHERE year=\"${selectdate.date.toString().split("-")[0]}\" AND month=\"${selectdate.date.toString().split("-")[1]}\" AND day=\"${selectdate.date.toString().split("-")[2]}\" ORDER BY year,month,day;"
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

                        arrayListPrice.add(cursor.getInt(5))


                        cursor.moveToNext()
                    }
                }
                var total=0;
                arrayListPrice.forEach {
                total+=it
                }
                daydata.text="${arrayListPrice.size}件のデータ\n${total}円"
            }catch(exception: Exception) {
                Log.e("selectData", exception.toString());
            }
                }
        selected_fun(CalendarDay.today())
        calendarView.selectedDate= CalendarDay.today()



        calendarView.addDecorator(HighlightSaturdayDecorator())
        calendarView.addDecorator(HighlightSundayDecorator())
        calendarView.setOnMonthChangedListener { _, date ->

            if (CalendarDay.today().year==date.year&& CalendarDay.today().month==date.month){
                selected_fun(CalendarDay.today())
                calendarView.selectedDate= CalendarDay.today()
                return@setOnMonthChangedListener
            }
            selected_fun(date)
            calendarView.selectedDate=date
        }
        calendarView.setOnDateChangedListener { widget, date, selected ->
            Log.i("my_","event_chenge")
            selected_fun(date)
        }
        MyVariable.getInstance().Y=selectdate.date.toString().split("-")[0]
        MyVariable.getInstance().M=selectdate.date.toString().split("-")[1]
        MyVariable.getInstance().D=selectdate.date.toString().split("-")[2]



            //calendarView.addDecorators(CurrentDayDecorator(this, mydate))


        try {
            val dates= arrayListOf<CalendarDay>()


            val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion)
            val database = dbHelper.readableDatabase


                   val sql= "SELECT * FROM KakeiboTable ORDER BY year,month,day ;"




            val cursor = database.rawQuery(sql, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {

                    val mydate= CalendarDay.from(cursor.getString(1).toInt(),  cursor.getString(2).toInt(), cursor.getString(3).toInt()) // year, month, date
                    dates.add(mydate)



                    cursor.moveToNext()
                }
            }




            calendarView.addDecorator(EventDecorator(Color.parseColor("#FF00FF00"),dates))
        }catch(exception: Exception) {
            Log.e("selectData", exception.toString());
        }
    /*    val defaultDate = format.format(calendarView.date)
        var date = defaultDate
        MyVariable.getInstance().Y=date.split("/")[0]
        MyVariable.getInstance().M=date.split("/")[1]
        MyVariable.getInstance().D=date.split("/")[2]
        if (date.split("/")[0].toString().length ==1){
            MyVariable.getInstance().M="0${MyVariable.getInstance().M}"
        }
        if (date.split("/")[0].toString().length==1) {
            MyVariable.getInstance().D ="0${MyVariable.getInstance().D}"
        }*/

     /*   calendarView.setOnDateChangeListener { view, s_year, s_month, s_dayOfMonth ->

            MyVariable.getInstance().Y=s_year.toString()
            MyVariable.getInstance().M=(s_month+1).toString()
            MyVariable.getInstance().D=s_dayOfMonth.toString()
            val year=MyVariable.getInstance().Y
            val month=MyVariable.getInstance().M
            val day=MyVariable.getInstance().D
            if (s_month.toString().length ==1){
                MyVariable.getInstance().M="0$month"
            }
            if (s_dayOfMonth.toString().length==1) {
                MyVariable.getInstance().D ="0$s_dayOfMonth"
            }
            date  = "$year/$month/$day"


        }*/
        button.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            //Log.i("my_log", "date:$date");
          //  intent.putExtra(EXTRA_MESSAGE, date);
            startActivity(intent)

        }
                editButton.setOnClickListener {
            val intent = Intent(applicationContext, Edit_Activity::class.java)

            startActivity(intent)

        }
        viewButton.setOnClickListener {
            val intent=Intent(applicationContext,DataView::class.java)
          //  intent.putExtra(EXTRA_MESSAGE,date)
            startActivity(intent)
        }
    }
    companion object {
        const val EXTRA_MESSAGE = "jp.naotiki.kakeibo.MESSAGE"
    }
    fun export_csv(){
        var jsonfile=""
        val pathUtf8 =  filesDir.absolutePath;
        jsonfile = File("$pathUtf8/data.json").readText(Charsets.UTF_8)
        val json =JSONObject(jsonfile)
    }
}
