package com.townwang.yaohuo.api

import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.Niece
import com.townwang.yaohuo.repo.data.YaoCdnReq
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.http.*
import java.io.File

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
     * 刷新论坛
     */
    @GET("wapindex.aspx?sid=-2")
    fun refresh(): Call<Document>

    /**
     * url 透传
     */
    @GET
    fun urlPenetrate(@Url url: String): Call<Document>

    /**
     * 登录
     */
    @Headers("loginCookie:town")
    @POST("waplogin.aspx")
    fun login(
        @QueryMap map: HashMap<String, String>,
        @Query("siteid") siteId: String? = "1000",
        @Query("action") action: String = "login",
        @Query("savesid") savesId: String = "1"
    ): Call<Document>

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
     * bbs/book_re.aspx?id=907477&classid=177
     */
    @GET("bbs/book_re.aspx")
    fun commentLists(
        @Query(BuildConfig.YH_REPLY_ID) id: String,
        @Query(BuildConfig.YH_SEND_BOOK_CLASSID) classId: String
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
    fun reply(
        @FieldMap map: HashMap<String, String>,
        @Field("siteid") siteId: String? = "1000",
        @Field("action") action: String? = "add",
        @Field("lpage") lpage: String? = "1"
    ): Call<Document>

    /**
     * 获取用户信息
     */
    @GET("bbs/userinfomore.aspx")
    fun getUserInfo(@Query("touserid") touserid: String): Call<Document>

    /**
     * 搜索?type=title&key=妖火客户端
     */
    @GET("bbs/book_list.aspx")
    fun queryListBBS(
        @Query("key") key: String,
        @Query("page") page: String,
        @Query(BuildConfig.YH_SEND_BOOK_CLASSID) classId: String? = "0",
        @Query("action") action: String? = "search",
        @Query("type") type: String? = "title",
        @Query("siteId") siteId: String? = "1000",
        @Query("getTotal") getTotal: String? = "2021"
    ): Call<Document>

    /**
     * 获取消息列表
     */
    @GET("bbs/messagelist.aspx")
    fun msg(): Call<Document>

    /**
     *删除消息
     */
    @GET("bbs/messagelist_del.aspx")
    fun deleteMsg(
        @Query("id") id: String,
        @Query("backurl") backUrl: String,
        @Query("action") action: String? = "godel"
    ): Call<Document>

    /**
     * 获取发帖参数
     */
    @GET("bbs/book_view_add.aspx")
    fun getSendBookUrl(
        @Query(BuildConfig.YH_SEND_BOOK_CLASSID) classId: String
    ): Call<Document>

    /**
     * 发帖（普通）
     */
    @FormUrlEncoded
    @POST("bbs/book_view_add.aspx")
    fun sendGeneral(
        @FieldMap map: HashMap<String, String>,
        @Field("sendmoney") sendmoney: String? = null//悬赏
    ): Call<Document>

    /**
     * 图床（上传文件）
     */
    @Headers("url_name:yao_cdn")
    @Multipart
    @POST("upload/localhost")
    fun upLoadFile(
        @Part file: MultipartBody.Part
    ): Call<YaoCdnReq>

    /**
     * 发帖（派币）
     */
    @FormUrlEncoded
    @POST("bbs/book_view_add.aspx")
    fun sendFreeMoney(
        @FieldMap map: HashMap<String, String>,
        @Field("sendmoney") sendmoney: String? = null,//悬赏
        @Field("freemoney") freemoney: String,//派币
        @Field("freerule1") freerule1: String//派币每人
    ): Call<Document>

    /**
     * 发帖（投票）
     */
    @FormUrlEncoded
    @POST("bbs/book_view_add.aspx")
    fun sendVote(
        @FieldMap map: HashMap<String, String>,
        @Field("sendmoney") sendmoney: String? = null,//悬赏
        @Field("vote") vote1: String? = null,//投票
        @Field("vote") vote2: String? = null,//投票
        @Field("vote") vote3: String? = null//投票
    ): Call<Document>
    /**
     * 发送消息
     */
    @FormUrlEncoded
    @POST("bbs/messagelist_add.aspx")
    fun sendMsg(
        @FieldMap map: Map<String, String>
    ): Call<Document>

}