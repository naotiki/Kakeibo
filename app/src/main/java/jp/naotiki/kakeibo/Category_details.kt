package jp.naotiki.kakeibo


import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import org.w3c.dom.Text

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Category_details.newInstance] factory method to
 * create an instance of this fragment.
 */
class Category_details : Fragment() {
    private val dbName: String = "KakeiboDB"
    private val tableName: String = "KakeiboTable"
    private val dbVersion: Int = 1

    private var arrayListId: ArrayList<String> = arrayListOf()
    private var arrayListDate: ArrayList<String> = arrayListOf()
    private var arrayListProduct: ArrayList<String> = arrayListOf()
    private var arrayListPrice: ArrayList<Int> = arrayListOf()
    var Year:String="";
    var Month:String="";
    var Day:String="";
    var Sort="year,month,day";
    var Turn="ASC"
   var ShowMode:Int=0;// 0＝一年でまとめて表示　1＝ひと月でまとめて表示 2＝今日の分をまとめて表示
    var Category:String="";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //  arrayListId = it.getStringArrayList("")as ArrayList<String>
            Category=it.getString("Category")?:""
            ShowMode= it.getInt("ShowMode")// 0=年 1=月 2=日
            Year= it.getString("Y")?:""
            Month= it.getString("M")?:""
            Day= it.getString("D")?:""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    // nav
val back_first=view.findViewById<TextView>(R.id.nav_all)
        back_first.setOnClickListener {


            val entry = activity?.supportFragmentManager?.getBackStackEntryAt(0)

            entry?.id?.let { _entry ->
                activity?.supportFragmentManager?.popBackStack(_entry, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
        if (Category=="日用品"||Category=="その他"){
            val category =view.findViewById<TextView>(R.id.category)
            category.text=Category
            val hide= arrayListOf<TextView>(view.findViewById(R.id.text3),view.findViewById(R.id.sub))
            hide[0].visibility=TextView.INVISIBLE
            hide[1].visibility=TextView.INVISIBLE
        }else{
            val category =view.findViewById<TextView>(R.id.category)
            category.text="食品"
            category.setOnClickListener {
                Log.i("my_log","Click:popbackstack")
                val entry = activity?.supportFragmentManager?.getBackStackEntryAt(1)

                entry?.id?.let { _entry ->
                    activity?.supportFragmentManager?.popBackStack(_entry, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
            val hide= arrayListOf<TextView>(view.findViewById(R.id.text3),view.findViewById(R.id.sub))
            hide[0].visibility=TextView.VISIBLE
            hide[1].visibility=TextView.VISIBLE
            hide[1].text=Category

        }
        //end:nav
        fun setview(first_time:Boolean=false){
            try {
                val sql = when(ShowMode){
                    0->{
                        "SELECT * FROM KakeiboTable WHERE year=\"$Year\" AND tag=\"$Category\" ORDER BY $Sort $Turn;"
                    }
                    1->{
                        "SELECT * FROM KakeiboTable WHERE year=\"$Year\" AND month=\"$Month\" AND tag=\"$Category\" ORDER BY $Sort $Turn;"
                    }
                    2->{
                        "SELECT * FROM KakeiboTable WHERE year=\"$Year\" AND month=\"$Month\" AND day=\"$Day\" AND tag=\"$Category\" ORDER BY $Sort $Turn;"
                    }
                    else -> {
                        ""
                    }
                }
                arrayListId.clear();arrayListDate.clear();arrayListProduct.clear();arrayListPrice.clear();

                val dbHelper = DBHelper(context!!.applicationContext, dbName, null, dbVersion)
                val database = dbHelper.readableDatabase



                val cursor = database.rawQuery(sql, null)
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    while (!cursor.isAfterLast) {

                        arrayListId.add(cursor.getString(0))
                        // YYYY/MM/DD
                        arrayListDate.add("${cursor.getString(1)}/${cursor.getString(2)}/${cursor.getString(3)}")
                        arrayListProduct.add(cursor.getString(4))
                        arrayListPrice.add(cursor.getInt(5))


                        cursor.moveToNext()
                    }
                }
            }catch(exception: Exception) {
                Log.e("selectData", exception.toString());
            }
            val list=view.findViewById<TableLayout>(R.id.table)

                    Log.i("my_rem","view_remove${list.childCount}")
            if (!first_time){list.removeViews(1,list.childCount-1)}



            val density = resources.displayMetrics.density

            for (i in 0 until arrayListId.size) {
                val product_name= arrayListProduct[i]
                val price =arrayListPrice[i].toString()
                val date=arrayListDate[i]
                val box= TableRow(context)// <TableRow>

                //box.setBackgroundResource(R.drawable.background_line)
                list.addView(box)
                val param0 = box.layoutParams as TableLayout.LayoutParams
                param0.width=  TableLayout.LayoutParams.MATCH_PARENT
                param0.height- TableLayout.LayoutParams.MATCH_PARENT
                param0.topMargin=(10f * density + 0.5f).toInt()
                box.layoutParams=param0
                val product= TextView(context)//  <CheckBox>
                box.addView(product)
                val param = product.layoutParams as TableRow.LayoutParams
                //param.setMargins((10f * density + 0.5f).toInt(),0,0,0)
                param.width=(160f * density + 0.5f).toInt();
                param.height= TableRow.LayoutParams.WRAP_CONTENT
                param.weight=0.8F

                product.layoutParams = param
                product.text=product_name
                product.textSize= 25F
                product.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
                product.gravity=Gravity.CENTER

                val priceText= TextView(context)
                box.addView(priceText)
                val param2 = priceText.layoutParams as  TableRow.LayoutParams
                param2.weight= 1F
                param2.width= TableRow.LayoutParams.WRAP_CONTENT
                param2.height= TableRow.LayoutParams.MATCH_PARENT

                priceText.layoutParams=param2
                priceText.setLines(1)
                priceText.text="￥$price"
                priceText.textSize=20F
                priceText.textAlignment= TextView.TEXT_ALIGNMENT_CENTER
                priceText.gravity= Gravity.CENTER
                priceText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                priceText.setAutoSizeTextTypeUniformWithConfiguration(10,20,1, TypedValue.COMPLEX_UNIT_SP)
                priceText.setLines(1)

                val dateText=TextView(context)
                box.addView(dateText)
                val param3 = dateText.layoutParams as  TableRow.LayoutParams
                param3.width= TableRow.LayoutParams.WRAP_CONTENT
                param3.height= TableRow.LayoutParams.MATCH_PARENT
                param3.weight=2F
                param3.leftMargin=(3f * density + 0.5f).toInt()
                dateText.layoutParams=param3
                dateText.text=date
                dateText.textSize=17F
                dateText.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
                dateText.gravity=Gravity.CENTER
                dateText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                dateText.setAutoSizeTextTypeUniformWithConfiguration(10,17,1, TypedValue.COMPLEX_UNIT_SP)
                dateText.setLines(1)

            }
            //END FOR
        }
        setview(true)//最初


        val sort=view.findViewById<Spinner>(R.id.sort1)
        sort.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        viw: View, position: Int, id: Long) {

               Sort= when(position){
                    0->{
                        "year,month,day"
                    }
                    1->{
                        "CAST(product AS CHAR)"
                    }
                    2->{
                        "price"
                    }
                   else -> {
                       ""
                   }
               }
                setview()
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        val sort2=view.findViewById<Spinner>(R.id.sort2)
        sort2.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        viw: View, position: Int, id: Long) {

                Turn= when(position){
                    0->{
                        "ASC"
                    }
                    1->{
                        "DESC"
                    }
                    else -> {
                        ""
                    }
                }
                setview()
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

    }
    companion object {

        @JvmStatic
        fun newInstance(Category_name:String,posision:Int, Y:String, M:String, D:String) =
                Category_details().apply {
                    arguments = Bundle().apply {
                        putString("Category",Category_name)
                        putInt("ShowMode",posision)// 0=年 1=月 2=日
                        putString("Y",Y)
                        putString("M",M)
                        putString("D",D)
                    }
                }
    }
}
