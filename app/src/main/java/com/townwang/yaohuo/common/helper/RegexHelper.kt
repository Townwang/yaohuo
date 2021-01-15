package com.townwang.yaohuo.common.helper

import java.util.regex.Pattern


fun matchValue(content: String, startPrefix: String, endPrefix: String, isEscape: Boolean): String {
    val pattern = if (isEscape) {
        Pattern.compile("\\$startPrefix.+\\$endPrefix", Pattern.DOTALL).matcher(content)
    } else {
        Pattern.compile("$startPrefix.+$endPrefix", Pattern.DOTALL).matcher(content)
    }
    if (pattern.find()) {
       return pattern.group()
    }
    return ""
}