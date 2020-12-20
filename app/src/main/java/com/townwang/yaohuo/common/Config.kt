package com.townwang.yaohuo.common

//app public field
const val THEME_KEY = "theme key"
const val HOME_LIST_THEME_SHOW = "home list theme show"
const val COOKIE_KEY = "cookie cache"

/***********************************************异常信息*******************************************/
class ResponseNullPointerException : Exception("服务器异常")

class ApiErrorException(val code: Int, error: String) : Exception(error)
class NetworkFailureException(failure: String) : Exception(failure)
class UseVPNException(failure: String) : Exception(failure)


/**********************************************校验*********************************************/

var isCrack = false


//解析所用字段
const val AK_FORM = "form"
const val AK_NAME = "name"
const val AK_VALUE = "value"
const val A_KEY = "a[href]"
const val A_HREF = "href"
const val A = "a"
const val IMG_GIF = "img[src\$=.gif]"
const val IMG_JPG = "img[src\$=.jpg],img[src\$=.gif],img[src\$=.png],img[src\$=.jpeg],img[src\$=.bmp]"
const val IMG_ALT = "alt"
//妖火字段
const val YH_USERNAME = "logname"
const val YH_PASSWORD = "logpass"
const val YH_SACESID = "savesid"
//list
const val NEW_LIST = "div.line1,div.line2"
const val NEW_LIST_TIME = "span.right"

//跳转携带参数
const val HOME_DETAILS_URL_KEY = "home details url key"
const val HOME_DETAILS_READ_KEY = "home details read key"
const val LIST_CLASS_ID_KEY = "list class id key"
const val LIST_BBS_NAME_KEY = "list bbs name key"