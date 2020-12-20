package com.townwang.yaohuo.api
import com.townwang.yaohuo.repo.data.Neice
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @Headers("url_name:town")
    @GET("app/json/yaohuo/neice.do")
    fun neice(): Call<Neice>
    /**
     * check bbs
     */
    @GET("/")
    fun checkNice(): Call<Document>
    /**
     * url 透传
     */
    @GET
    fun urlPenetrate(@Url url:String): Call<Document>
    /**
     * 检查cookie
     */
    @GET("wapindex.aspx")
    fun checkCookie(): Call<Document>

    /**
     * 获取登录参数
     */
    @GET("waplogin.aspx")
    fun getLoginParameter():Call<Document>


    /**
     * 登录
     */
    @Headers("loginCookie:town")
    @POST("waplogin.aspx")
    fun login(@QueryMap map:HashMap<String,String>):Call<Document>

    /**
     * 获取最新列表
     */
    @GET("bbs/book_list.aspx")
    fun getNewList(
        @Query("classId") classId: String,
        @Query("page") page: String,
        @Query("action") action: String? = "new",
        @Query("siteId") siteId: String? = "1000",
        @Query("getTotal") getTotal: String? = "2020"
    ): Call<Document>
    /**
     * 评论 [页码,帖子ID,栏目ID]
     */
    @GET("bbs/book_re.aspx")
    fun commentLists(
        @Query("page") page: String,
        @Query("id") id: String,
        @Query("classId") classId: String,
        @Query("ot") ot: String
    ): Call<Document>


}