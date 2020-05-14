package jp.naotiki.kakeibo


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


// TODO: Rename parameter arguments, choose names that match


/**
 * A simple [Fragment] subclass.
 * Use the [RootFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public class RootFragment : Fragment() {
    // TODO: Rename and change types of parameters

    var Year:String="";
    var Month:String="";
    var Day:String="";
    var ShowMode:Int=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //  arrayListId = it.getStringArrayList("")as ArrayList<String>
            ShowMode= it.getInt("ShowMode")// 0=年 1=月 2=日


        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Year=MyVariable.getInstance().SelectedDate[0]
        Month =MyVariable.getInstance().SelectedDate[1]
        Day= MyVariable.getInstance().SelectedDate[2]
        Log.i("putdata","root_pos:$ShowMode\n$Year/$Month/$Day")
        val view: View = inflater.inflate(R.layout.fragment_root, container, false)
        val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flagment_box, Categories_List.newInstance(ShowMode,Year,Month,Day))
        fragmentTransaction.commit()

        return view
    }

    companion object {


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(posision:Int) =
                RootFragment().apply {
                    arguments = Bundle().apply {
                        putInt("ShowMode",posision)// 0=年 1=月 2=日

                    }
                };
    }

}
