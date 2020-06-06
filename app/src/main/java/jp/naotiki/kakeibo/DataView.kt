package jp.naotiki.kakeibo

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.nio.charset.Charset


class DataView : AppCompatActivity(),ViewPager.OnPageChangeListener{
    private val WRITE_REQUEST_CODE: Int = 43
    private val REQUEST_PERMISSION = 1000
    private val dbName: String = "KakeiboDB"
    private val tableName: String = "KakeiboTable"
    private val dbVersion: Int = 1
    var year=""
    var month=""
    var day=""
    private var arrayListId: ArrayList<String> = arrayListOf()
    private var arrayListDate: ArrayList<String> = arrayListOf()
    private var arrayListProduct: ArrayList<String> = arrayListOf()
    private var arrayListPrice: ArrayList<Int> = arrayListOf()
    private var arrayListTag: ArrayList<String> = arrayListOf()
    var position:Int = 1
    private val mPager: ViewPager? = null
    private val mTitles = arrayOf("年", "月", "日")
    var WHERE=""
    // 選択中のタブ番号を保持
    private var selected = 0
var isDateChenge=false
    @TargetApi(Build.VERSION_CODES.N)
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_view)
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar4)
        toolBar.setNavigationOnClickListener {this.finish()}
val editdata=findViewById<EditText>(R.id.EditDate)
        val date: Calendar = Calendar.getInstance()
        year=MyVariable.getInstance().Y
       month=MyVariable.getInstance().M
       day=MyVariable.getInstance().D
        MyVariable.getInstance().SelectedDate[0]=year
        MyVariable.getInstance().SelectedDate[1]=month
        MyVariable.getInstance().SelectedDate[2]=day

        val tab =findViewById<TabLayout>(R.id.tabLayout)

tab.getTabAt(1)!!.select()
         Log.i("my_log","date:$date")


        date.set(year.toInt(),(if(month.indexOf("0")==0){ month.substring(1) }else{month}).toInt()-1,
                (if (day.indexOf("0")==0) {
                    day.substring(1)
                }else{
                    day
                }).toInt())
        editdata.setText("$year/$month/$day")
        editdata.setOnClickListener {


            //DatePickerDialogインスタンスを取得

            //DatePickerDialogインスタンスを取得
            val datePickerDialog = DatePickerDialog(
                    this,
                    OnDateSetListener { view,  Edityear, Editmonth, dayOfMonth -> //setした日付を取得して表示
                        editdata.setText(String.format("%d/%02d/%02d", Edityear, Editmonth + 1, dayOfMonth))
                       year=String.format("%d/%02d/%02d",  Edityear,Editmonth + 1, dayOfMonth).split("/")[0];
                        month=String.format("%d/%02d/%02d",  Edityear, Editmonth + 1, dayOfMonth).split("/")[1];
                       day=String.format("%d/%02d/%02d",  Edityear, Editmonth + 1, dayOfMonth).split("/")[2];
                        date.set(year.toInt(),(if(month.indexOf("0")==0){ month.substring(1) }else{month}).toInt()-1,
                                (if (day.indexOf("0")==0) {
                                    day.substring(1)
                                }else{
                                    day
                                }).toInt())
                        MyVariable.getInstance().SelectedDate[0]=year
                        MyVariable.getInstance().SelectedDate[1]=month
                        MyVariable.getInstance().SelectedDate[2]=day
                        if (savedInstanceState == null) {
                            if (supportFragmentManager.backStackEntryCount!=0){
                                val entry = supportFragmentManager?.getBackStackEntryAt(0)

                                entry?.id?.let { _entry ->
                                    supportFragmentManager?.popBackStack(_entry, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                }
                            }
                            val transaction = supportFragmentManager.beginTransaction()

                            transaction.replace(R.id.flagment_box, Categories_List.newInstance(position,year,month,day) )
                            transaction.commit()
                        }

                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DATE)
            )

            datePickerDialog.show()
        }



        tab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            // "onTabSelected","onTabUnselected","onTabReselected"の3つを実装しないとエラー らしい
            // コーラグミうまい
            //　部屋の電球チカチカしてる 2020/4/8  PM 9:12
            override fun onTabSelected(tab: TabLayout.Tab) {
                arrayListTag.clear()
                arrayListPrice.clear()
                arrayListProduct.clear()
                arrayListId.clear()
                Log.i("myTag", "onTabSelebted")
               position = tab.position
                if (savedInstanceState == null) {
                    if (supportFragmentManager.backStackEntryCount!=0){
                        val entry = supportFragmentManager?.getBackStackEntryAt(0)

                        entry?.id?.let { _entry ->
                            supportFragmentManager?.popBackStack(_entry, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }
                    }
                    val transaction = supportFragmentManager.beginTransaction()

                    transaction.replace(R.id.flagment_box, Categories_List.newInstance(position,year,month,day) )
                    transaction.commit()
                }
        }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                Log.i("myTag", "onTabUnselected")
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                Log.i("myTag", "onTabReselected")
            }
        })

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            Log.i("my_log", "add_fragment")
            transaction.add(R.id.flagment_box, Categories_List.newInstance(position,year,month,day) )
            transaction.commit()
        }
        val saveCSV_btn =findViewById<ImageButton>(R.id.SaveCSV)
        saveCSV_btn.setOnClickListener {
        val intent =Intent(this,CSV_export::class.java)
            intent.putExtra("Y",year)
            intent.putExtra("M",month)
            intent.putExtra("D",day)
            startActivityForResult(intent,45)
            //confirmOutput()
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        // 選択中のタブのRootFragmentにバックスタックがあれば戻る処理
        Log.i("my_log","イベント発火：onBackPressed(DataView.kt:239)")
        val fm: FragmentManager = supportFragmentManager
        if (fm.backStackEntryCount>0){
            fm.popBackStack()
        }else{
            this.finish()
        }


    }

    private fun confirmOutput() {
        AlertDialog.Builder(this)
                .setTitle("CSV出力")
                .setMessage("CSVを出力します" )
                .setPositiveButton("OK") { _, _ -> checkPermission() }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun exportDatabase(): Boolean {

            // 外部ストレージがマウントされている事を確認
            val state = Environment.getExternalStorageState()
            return if (Environment.MEDIA_MOUNTED != state) {
                false
            } else {
                createFile("text/csv","Kakeibo_${year}_${month}_${day}.csv")
             true
            }


    }
    fun checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            exportDatabase()
        } else {
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            val toast = Toast.makeText(this, "アプリ実行に許可が必要です", Toast.LENGTH_SHORT)
            toast.show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION)
        }
    }
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportDatabase()
            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(this, "何もできません", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }
    private fun createFile(mimeType: String, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as
            // a file (as opposed to a list of contacts or timezones).
            addCategory(Intent.CATEGORY_OPENABLE)

            // Create a file with the requested MIME type.
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==45&&resultCode==Activity.RESULT_OK){
            WHERE = data!!.getStringExtra("where")!!
            confirmOutput()
        }
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {  uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    var printWriter: PrintWriter? = null
                   printWriter= PrintWriter(outputStream!!.writer(charset = Charset.forName("Shift-JIS")))
                    try {

                        //printWriter = PrintWriter(OutputStreamWriter(FileOutputStream("$uri/Kakeibo_${year}_${month}_${day}.csv",true), "Shift-JIS"))
                        val dbHelper = DBHelper(applicationContext, dbName, null, dbVersion)
                        val database = dbHelper.readableDatabase
                        val curCSV = database.rawQuery("select * from $tableName "+WHERE+" ORDER BY year,month,day;" , null)
                        // CSVファイルのヘッダーを書き出し
                        printWriter.println("date,year,month,day,product,price,tag")
                        // データの行数分CSV形式でデータを書き出し
                        while (curCSV.moveToNext()) {
                            val year = curCSV.getString(curCSV.getColumnIndex("year"))
                            val month = curCSV.getString(curCSV.getColumnIndex("month"))
                            val day = curCSV.getString(curCSV.getColumnIndex("day"))
                            val product = curCSV.getString(curCSV.getColumnIndex("product"))
                            val price = curCSV.getString(curCSV.getColumnIndex("price"))
                            val tag = curCSV.getString(curCSV.getColumnIndex("tag"))
                            val record = "$year/$month/$day,$year,$month,$day,$product,$price,$tag"
                            printWriter.println(record)

                        }
                        curCSV.close()
                        database.close()
                    } catch (exc: FileNotFoundException) {
                        // フォルダへのアクセス権限がない場合の表示
                        val ts = Toast.makeText(this, "アクセス権限がありません", Toast.LENGTH_SHORT)
                        ts.show()
                        exc.printStackTrace()

                    } catch (exc: Exception) {
                        val ts = Toast.makeText(this, "CSV出力が失敗しました", Toast.LENGTH_SHORT)
                        ts.show()
                        exc.printStackTrace()

                    } finally {
                        printWriter.close()
                    }
                    val ts = Toast.makeText(this, "CSVに出力しました", Toast.LENGTH_SHORT)
                    ts.show()
                }


            }
        }
    }

   override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        Log.d("MainActivity", "onPageSelected() position=$position")
        selected = position;
    }

    override fun onPageScrollStateChanged(state: Int) {}


}



