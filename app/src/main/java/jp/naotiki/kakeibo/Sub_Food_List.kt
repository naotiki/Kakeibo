package jp.naotiki.kakeibo


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

//  Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Sub_Food_List.newInstance] factory method to
 * create an instance of this fragment.
 */
class Sub_Food_List : Fragment() {
    private val dbName: String = "KakeiboDB"
    private val tableName: String = "KakeiboTable"
    private val dbVersion: Int = 1
    private var arrayListId: ArrayList<String> = arrayListOf()
    private var arrayListDate: ArrayList<String> = arrayListOf()
    private var arrayListProduct: ArrayList<String> = arrayListOf()
    private var arrayListPrice: ArrayList<Int> = arrayListOf()
    private var arrayListTag: ArrayList<String> = arrayListOf()
    var Year:String="";
    var Month:String="";
    var Day:String="";
    var ShowMode:Int=0;// 0＝一年でまとめて表示　1＝ひと月でまとめて表示 2＝今日の分をまとめて表示
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ShowMode= it.getInt("ShowMode")// 0=年 1=月 2=日
            Year= it.getString("Y")?:""
            Month= it.getString("M")?:""
            Day= it.getString("D")?:""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_food_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myApp = MyVariable.getInstance()
val backstack=view.findViewById<TextView>(R.id.nav_all2)
        backstack.setOnClickListener {
            Log.i("my_log","Click:popbackstack")
            val entry = activity?.supportFragmentManager?.getBackStackEntryAt(0)

            entry?.id?.let { _entry ->
                activity?.supportFragmentManager?.popBackStack(_entry, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
        val spinnerItems= myApp.foods
        for (its in spinnerItems) {
            val i:Int=spinnerItems.indexOf(its)
            arrayListId.clear();arrayListProduct.clear();arrayListPrice.clear();arrayListTag.clear();arrayListDate.clear()

                getDateSQL(ShowMode,its)


            val list= view.findViewById<TableLayout>(R.id.list_box).getChildAt(i)as TableRow?
            var allprice:Int=0
            arrayListPrice.forEach{
                allprice += it
            }
            if(list!=null){
                (list.getChildAt(1)as TextView).text="￥${allprice.toString()}"
                if (allprice>0){ list.setOnClickListener {
                    val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
                    fragmentTransaction.replace(R.id.flagment_box,Category_details.newInstance(its,ShowMode,Year,Month,Day))
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }}

            }


        }






    }
    fun getDateSQL(getMode:Int,Categories:String){
        try {


            val dbHelper = DBHelper(context!!.applicationContext, dbName, null, dbVersion)
            val database = dbHelper.readableDatabase

            val sql = when(getMode){
                0->{
                    "SELECT * FROM KakeiboTable WHERE year=\"$Year\" AND tag=\"$Categories\" ORDER BY year,month,day ;"
                }
                1->{
                    "SELECT * FROM KakeiboTable WHERE year=\"$Year\" AND month=\"$Month\" AND tag=\"$Categories\" ORDER BY year,month,day;"
                }
                2->{
                    "SELECT * FROM KakeiboTable WHERE year=\"$Year\" AND month=\"$Month\" AND day=\"$Day\" AND tag=\"$Categories\" ORDER BY year,month,day;"
                }
                else -> {
                    ""
                }
            }


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
                    arrayListDate.add("${cursor.getString(1)}/${cursor.getString(2)}/${cursor.getString(3)}")
                    arrayListProduct.add(cursor.getString(4))
                    arrayListPrice.add(cursor.getInt(5))
                    arrayListTag.add(cursor.getString(6))


                    cursor.moveToNext()
                }
            }
        }catch(exception: Exception) {
            Log.e("selectData", exception.toString());
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Sub_Food_List.
         */
        //  Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(position:Int, Y:String, M:String, D:String) =
                Sub_Food_List().apply {
                    arguments = Bundle().apply {
                        putInt("ShowMode",position)// 0=年 1=月 2=日
                        putString("Y",Y)
                        putString("M",M)
                        putString("D",D)
                    }
                }
    }
}
