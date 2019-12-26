package com.townwang.yaohuo.repo.data

import com.townwang.yaohuo.R

data class ThemeList(
    val id:Int,
    var icon:Int? = R.drawable.anim_vector_theme_icon,
    var color:Int,
    var title:String
)