package jp.naotiki.kakeibo

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TagAdapter(fm: FragmentManager, private val context: Context) : FragmentStatePagerAdapter(fm) {
    private val pageTitle = arrayOf("年", "月", "日")


    override fun getItem(position: Int): Fragment {
        return  Categories_List.newInstance(position,MyVariable.getInstance().Y,MyVariable.getInstance().M,MyVariable.getInstance().D )
        // 要求時 新しい Fragment を生成して返す

    }
    // タブの名前
    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitle[position]
    }
    // タブの個数
    override fun getCount(): Int {
        return pageTitle.size
    }
    // タブの変更

}