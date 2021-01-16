@file:Suppress("DEPRECATION")

package com.townwang.yaohuo.ui.fragment.pub
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.townwang.yaohuo.repo.data.TabBean
class TabListAdapter(
    fm: FragmentManager, state:Int,
    val  list: List<TabBean>
) : FragmentPagerAdapter(fm,state) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position].title
    }
}