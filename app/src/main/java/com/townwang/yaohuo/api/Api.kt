package com.townwang.yaohuo.api
import com.townwang.yaohuo.repo.data.Niece
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @Headers("url_name:cn")
    @GET("neice.do")
    fun niece(): Call<Niece>
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
     * 获取帖子列表
     */
    @GET("bbs/book_list.aspx")
    fun getNewList(
        @Query("classId") classId: String,
        @Query("page") page: String,
        @Query("action") action: String,
        @Query("siteId") siteId: String? = "1000",
        @Query("getTotal") getTotal: String? = "2021"
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

    /**
     * 我的
     */
    @GET("myfile.aspx")
    fun getMe(): Call<Document>

    /**
     * 回帖
     */
    @FormUrlEncoded
    @POST("bbs/book_re.aspx")
    fun reply(@FieldMap map: HashMap<String, String>): Call<Document>

    /**
     * 获取用户信息
     */
    @GET("bbs/userinfomore.aspx")
    fun getUserInfo(@Query("touserid") touserid:String): Call<Document>

    /**
     * 搜索?type=title&key=妖火客户端
     */
    @GET("bbs/book_list.aspx")
    fun queryListBBS(
        @Query("key") key:String,
        @Query("page") page: String,
        @Query("classId") classId:String?="0",
        @Query("action") action:String?="search",
        @Query("type") type:String?="title",
        @Query("siteId") siteId: String? = "1000",
        @Query("getTotal") getTotal: String? = "2021"
    ): Call<Document>

    /**
     * 获取发帖参数
     */
    @GET("bbs/book_view_add.aspx")
    fun getSendBookUrl(
        @Query("classId") classId:String
    ): Call<Document>

    /**
     * 发帖
     */
    @FormUrlEncoded
    @POST("bbs/book_view_add.aspx")
    fun sendGeneral(
        @FieldMap map: HashMap<String, String>
    ): Call<Document>

}