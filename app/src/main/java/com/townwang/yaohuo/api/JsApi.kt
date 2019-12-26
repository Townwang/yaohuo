package com.townwang.yaohuo.api

class JsApi {
    val BASE_URL = "https://yaohuo.me"
    /**
     * 检测Cookie
     */
    fun checkCookie(): String {
        return "$BASE_URL/wapindex.aspx?classid=206"
    }
    /**
     * 登录
     */
    fun login(): String {
        return "$BASE_URL/waplogin.aspx"
    }
    /**
     * 回复
     */
    fun reply(): String {
        return "$BASE_URL/bbs/book_re.aspx"
    }

    /**
     * 帖子列表 [栏目ID,页码]
     */
    fun newLists(classId:Int,page: Int): String {
        return "$BASE_URL/bbs/book_list.aspx?classid=$classId&action=new&page=$page"
    }

    /**
     * 帖子详情
     */
    fun listDetails(url: String): String {
        return "$BASE_URL$url"
    }
    /**
     * 评论 [页码,帖子ID,栏目ID]
     */
    fun commentLists(page:Int,id:String,classId: Int,ot:Int): String {
      return "$BASE_URL/bbs/book_re.aspx?page=$page&id=$id&classid=$classId&ot=$ot"
    }
}