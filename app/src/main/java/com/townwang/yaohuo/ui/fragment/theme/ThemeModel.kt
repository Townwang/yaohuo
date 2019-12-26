package com.townwang.yaohuo.ui.fragment.theme

import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.UIViewModel
import com.townwang.yaohuo.repo.data.ThemeList

class ThemeModel : UIViewModel() {
    val listDates = arrayListOf<ThemeList>().apply {
        add(ThemeList(R.style.DefaultAppTheme, null,R.color.colorPrimary, "默认色"))
        add(ThemeList(R.style.BlueAppTheme, null,R.color.md_blue_A700, "深蓝色"))
        add(ThemeList(R.style.RedAppTheme, null,R.color.md_red_A700, "姨妈红"))
        add(ThemeList(R.style.GreenAppTheme, null,R.color.md_green_A700, "出轨绿"))
        add(ThemeList(R.style.PurpleAppTheme, null,R.color.md_purple_A700, "基佬紫"))
        add(ThemeList(R.style.TealAppTheme, null,R.color.md_teal_A700, "水鸭青"))
        add(ThemeList(R.style.LimeAppTheme, null,R.color.md_lime_A700, "苹果绿"))
    }

}