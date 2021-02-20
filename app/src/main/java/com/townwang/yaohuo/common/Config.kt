package com.townwang.yaohuo.common

//app public field
const val THEME_KEY = "theme key"
const val HOME_LIST_THEME_SHOW = "home list theme show"
const val COOKIE_KEY = "cookie cache"
const val TROUSER_KEY = "trouser id"

/***********************************************异常信息*******************************************/
class ResponseNullPointerException : Exception()
class ApiErrorException(val code: Int, error: String) : Exception(error)
class NetworkFailureException(failure: String) : Exception(failure)
class UseVPNException(failure: String) : Exception(failure)
/**********************************************校验*********************************************/

val  signatureHash  =  arrayListOf<Long>().apply {
    add(-508714960)
    add(1375692864)
}
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
//list
const val NEW_LIST = "div.line1,div.line2"
const val NEW_LIST_TIME = "span.right"

//跳转携带参数
const val HOME_DETAILS_URL_KEY = "home details url key"
const val HOME_SEARCH_URL_KEY = "home search url key"
const val WEB_VIEW_URL_KEY = "web view url key"
const val WEB_VIEW_URL_TITLE = "web view url title"
const val HOME_DETAILS_READ_KEY = "home details read key"
const val HOME_DETAILS_BEAR_KEY = "home details bear key"
const val HOME_DETAILS_TITLE_KEY = "home details title key"
const val LIST_CLASS_ID_KEY = "list class id key"
const val LIST_ACTION_KEY = "list action key"
const val LIST_BBS_NAME_KEY = "list bbs name key"
const val SEND_CONTENT_KEY = "send content key"