package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.CommentData
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import java.util.regex.Pattern.DOTALL


class DetailsModel(private val repo: Repo) : UIViewModel() {
    var url: String = ""
    var docData: Document? = null
    private val _content = MutableLiveData<String>()
    private val _commentLists = MutableLiveData<List<CommentData>>()
    private val _image = MutableLiveData<String>()
    private val _download = MutableLiveData<List<String>>()
    private val _title = MutableLiveData<String>()
    private val _online = MutableLiveData<Boolean>()
    private val _time = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()
    private val _giftMoney = MutableLiveData<String>()
    private val _reward = MutableLiveData<String>()
    private val _commentPraise = MutableLiveData<String>()
    private val _commentSize = MutableLiveData<String>()
    private val _avatar = MutableLiveData<String>()
    private val _commentSuccess = MutableLiveData<Boolean>()
    val commentSuccess = _commentSuccess.asLiveData()
    val title = _title.asLiveData()
    val avatar = _avatar.asLiveData()
    val commentLists = _commentLists.asLiveData()
    val commentPraise = _commentPraise.asLiveData()
    val commentSize = _commentSize.asLiveData()
    val download = _download.asLiveData()
    val online = _online.asLiveData()
    val giftMoney = _giftMoney.asLiveData()
    val reward = _reward.asLiveData()
    val time = _time.asLiveData()
    val name = _name.asLiveData()
    val content = _content.asLiveData()
    val image = _image.asLiveData()
    fun getDetails(url: String) = launchTask {
        val doc = repo.getNewListDetails(url)
        docData = doc
        articleHeader(doc)
        articleAuth(doc)
        articleContent(doc)
        commentDetails(1, 0)
    }


    fun commentDetails(page: Int, ot: Int) = launchTask {
        docData?.run {
            val hand = this.select("div.subtitle").select(A_KEY)
            val management = hand.last().attr(A_HREF)
            Log.d("解析", "评论 ： $management")
            val doc = repo.comment(
                page,
                getParam(management, "id"),
                getParam(management, "classid").toInt(),
                ot
            )
            Log.d("解析", "评论 ： ${doc.body()}")
            articleComment(doc)
        }
    }


    fun praise() = launchTask {
        if (url.isNotEmpty()) {
            repo.praise(url)
        }
    }

    private fun articleHeader(doc: Document) {
        val hand = doc.select("div.content").eachText()
        val hands = hand.first().split(" ")
        hands.forEach {
            when {
                it.contains("[标题]") -> {
                    _title.value = hands[hands.indexOf("[标题]") + 1]
                }
                it.contains("[悬赏]") -> {
                    _reward.value =
                        "总额：${hands[hands.indexOf("[悬赏]") + 1] + hands[hands.indexOf("[悬赏]") + 2]}"
                }
                it.contains("礼金") -> {
                    _giftMoney.value = it
                }
                it.contains("[时间]") -> {
                    _time.value =
                        "发布时间：${hands[hands.indexOf("[时间]") + 1]} ${hands[hands.indexOf("[时间]") + 2]}"
                }
            }
        }
    }

    private fun articleContent(doc: Document) {
        val content = doc.select("div.bbscontent")
        val str = content.toString()
            .substring(
                content.toString().indexOf("<!--listS-->"),
                content.toString().indexOf("<!--listE-->")
            )
        _content.value = str
        val annex = content.select("div.line")
        val img = annex.select(IMG_JPG).after("src")
        img.forEach {
            _image.value = it.attr("src")
        }
        val urls = annex.select("a.urlbtn")
        annex.select(A_KEY).forEach {
            urls.forEach { i ->
                if (it.attr(A_HREF) == i.attr(A_HREF)) {
                    val annexName = it.parentNode().childNodes().first().outerHtml()
                    val annexUrl = i.attr(A_HREF)
                    val array = arrayListOf<String>()
                    array.add(annexName)
                    array.add(annexUrl)
                    _download.value = array
                }
            }
        }
    }

    private fun articleAuth(doc: Document) {
        val user = doc.select("div.subtitle").last()
        val userName = user.select(A_KEY).first()
        _name.value = userName.ownText()
        val handUrl = userName.attr(A_HREF)
        getAvatar(handUrl)
        val online = user.select(IMG_GIF).after(IMG_ALT)
        _online.value = online.first().attr(IMG_ALT) == "ONLINE"
        _commentPraise.value = Regex("([()])").split(user.ownText().split(" ").last())[1]
        url = user.select(A_KEY).last().attr(A_HREF)
    }

    private fun getAvatar(handUrl: String) = launchTask {
        val doc = repo.praise(handUrl)
        _avatar.value = doc.select("div.content").select(IMG_JPG).first().attr("src")
    }

    @SuppressLint("SimpleDateFormat")
    private fun articleComment(doc: Document) {
        val select = doc.select("div.line1 ,div.line2")
        val value = select.first()
        if (value != null) {
            _commentSize.value =
                convertText(Regex("([\\[\\]])").split(value.text())[1].replace("楼", "")).toString()
        }
        val array = arrayListOf<CommentData>()
        select.forEach {
            val url = it.select(A_KEY).first().attr(A_HREF)
            val auth = it.select(A_KEY).last()
            val floor = Regex("([\\[\\]])").split(it.text())[1].replace("楼", "")
            var b = ""
            val bEts = it.select("b")
            if (bEts.hasText()) {
                b = bEts.text()
            }
            auth.select(IMG_GIF).remove()
            val authString = auth.html()
            val avatar = auth.attr(A_HREF)
            val time = it.text().split(" ").last()
            val date = it.text().split(" ")[it.text().split(" ").lastIndex - 1]

            val format = SimpleDateFormat("yyyy-MM-ddHH:m")
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val dateObj = format.parse("$year-$date$time")
            var content = ""
            val matcher = Pattern.compile("回</a>].+<br>", DOTALL).matcher(it.toString())
            if (matcher.find()) {
                content = matcher.group().replace("回</a>]", "").replace("<br>", "")
            }
            val content2 = Jsoup.parse(content)
            content2.select(IMG_JPG).forEach { gif ->
                val src = gif.attr("src")//将所有的相对地址换为绝对地址;
                val regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\??(([A-Za-z0-9-~]+\\=?)([A-Za-z0-9-~]*)\\&?)*)$"
                if (!Pattern.compile(regex).matcher(src).matches()) {
                    gif.attr("src", "https://yaohuo.me/$src")
                }
            }
            content2.select(A_KEY).forEach { gif ->
                val src = gif.attr(A_HREF)//将所有的相对地址换为绝对地址;
                val regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\??(([A-Za-z0-9-~]+\\=?)([A-Za-z0-9-~]*)\\&?)*)$"
                if (!Pattern.compile(regex).matcher(src).matches()) {
                    gif.attr(A_HREF, "https://yaohuo.me/$src")
                }
            }
            array.add(
                CommentData(
                    convertText(floor),
                    url,
                    authString,
                    avatar,
                    RelativeDateFormat.format(dateObj!!),
                    content2.body().html(), b
                )
            )
        }
        _commentLists.value = array
    }

    fun reply(content: String, url: String, floor: String? = null, touserid: String? = null,sendmsg:String?="1") =
        launchTask {
            try {
                val doc = repo.reply(url, content, floor, touserid,sendmsg=sendmsg)
                doc.body()
                _commentSuccess.value = true
            } catch (e: Exception) {
                _commentSuccess.value = false
            }
        }

    private fun convertText(floor: String): Int {
        return try {
            when (floor) {
                "沙发" -> 1
                "椅子" -> 2
                "板凳" -> 3
                else -> floor.toInt()
            }
        } catch (e: NumberFormatException) {
            0
        }
    }


}