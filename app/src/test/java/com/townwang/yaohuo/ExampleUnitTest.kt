package com.townwang.yaohuo

import com.townwang.yaohuo.common.resolve.ResolveUserInfoHelper
import com.townwang.yaohuo.repo.enum.Level
import org.jsoup.Jsoup
import org.junit.Test
import java.util.*
import java.util.concurrent.Executors

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//      val string  = "这个是我一个同学介绍的不知道可靠不，想听听妖友们的意见！ 共有3个附件: 1.img_20191104_172450.jpg(3.3MB) 2.screenshot_2019-11-04-17-23-19-554_com.tencent.mm.jpg(440.7KB) 3.screenshot_2019-11-04-17-21-34-006_com.tencent.mm.jpg(416.4KB)"
//     print(string.substring(string.lastIndexOf("<!--listS-->"),string.indexOf("<!--listE-->")))
    }

    @Test
    fun test() {
        val service = Executors.newFixedThreadPool(10)
        for (i in 1..10) {
            service.submit(task(i))
        }
    }

    @Test
    fun testRegex() {
        val html = """
            <div class="line1">[椅子][<a href="/bbs/Book_re.aspx?siteid=1000&amp;classid=213&amp;lpage=1&amp;page=1&amp;reply=2&amp;id=897644&amp;touserid=49543&amp;ot=">回</a>]回复1楼：vx多少<br><a href="/bbs/userinfo.aspx?touserid=49543">知足(49543)</a> 01-15 23:00</div>
        """.trimIndent()
        val content = "((回</a>]).+(<br>))".toRegex().find(html)?.value?.removePrefix("回</a>]")
            ?.removeSuffix("<br>")
        println("str====== ${content}")
    }

    @Test
    fun commit() {
        val html = """
            <html><head>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
            <meta http-equiv="Cache-Control" content="no-cache">
            <meta name="viewport" content="width=device-width; initial-scale=1.0; minimum-scale=1.0; maximum-scale=1.0">
            <meta name="description" content=" 妖火网 - 分享你我 妖火网 - 分享你我 ">
            <meta name="author" content="妖火网 - 分享你我 ">
            <meta name="referrer" content="no-referrer">
            <link rel="apple-touch-icon" href="/NetImages/ico/APPS.png?v=2">
            <link href="/NetImages/ico/3x60.png" rel="apple-touch-icon?v=2" sizes="180x180">
            <link href="/NetImages/ico/2x60.png" rel="apple-touch-icon?v=2" sizes="120x120">
            <link href="/NetImages/ico/2x57.png" rel="apple-touch-icon?v=2" sizes="114x114">
            <link href="/NetImages/ico/1x57.png" rel="apple-touch-icon?v=2" sizes="57x57">
            <meta content="/NetImages/ico/APP.png?v=2" itemprop="image">
            <link rel="icon" href="/css/favicon.ico">
            <meta name="keywords" content="妖火,妖火网,妖火论坛">
            <meta name="apple-mobile-web-app-title" content="妖火网">
            <link rel="stylesheet" href="/Template/default/default.css?v=20210118" type="text/css">

            <title>个人资料</title>
            </head>
            <body>
            <a href="/bbs/messagelist.aspx?siteid=1000&amp;backurl=wapindex.aspx%3fsiteid%3d1000%26amp%3bclassid%3d0"><img src="/tupian/news.gif" alt=".">收到48封飞鸽传书</a><div class="subtitle">个人资料</div><div class="content">我的公众号：文科中的技术宅<br><img src="/bbs/head/62.gif" style="max-width:320;max-height:320;" alt="头像"><br>【用户ID】：24770<img src="/bbs/medal/t12.gif" alt="."><br>【昵称】：τ<br>【妖晶】：46712<br>【经验】：25129<br>【等级】：5级<br>【头衔】：呢喃的歌声<br>【身份】：普通会员<br>【权限】：普通<br>【勋章】：<img src="/xinzhang/images/hongxin.gif" alt="."><img src="/xinzhang/images/lanxin.gif" alt="."><br>【性别】：男<br>【年龄】：28岁<br>【状态】：<img src="/NetImages/on1.gif" alt="ONLINE"><br>【积时】：21天21小时43分41秒<br>【注册时间】：2014/8/1 0:51:00<br>【QQ号】：<br>【身高】：170<br>【体重】：55<br>【星座】：<br>【爱好】：技术<br>【婚否】：否<br>【职业】：android研发工程师<br>【城市】：冥界<br>【邮箱】：android@townwang.com<br></div><div class="btBox"><div class="bt2"><a href="/bbs/userinfo.aspx?siteid=1000&amp;touserid=24770&amp;backurl=myfile.aspx%3fsiteid%3d1000">返回上级</a> <a href="/wapindex.aspx?siteid=1000">返回首页</a>	</div></div><!-- Global site tag (gtag.js) - Google Analytics -->
            <script type="text/javascript" async="" src="https://www.google-analytics.com/analytics.js"></script><script async="" src="https://www.googletagmanager.com/gtag/js?id=UA-88858350-1"></script>
            <script>
              window.dataLayer = window.dataLayer || [];
              function gtag(){dataLayer.push(arguments);}
              gtag('js', new Date());
              gtag('config', 'UA-88858350-1');
            </script><div id="lastOne"></div>
            <script>
            ${'$'}(document).scroll(function () {
            	${'$'}("#lastOne").nextAll().find("iframe").remove();
            }); 
            </script>

            <div id="NR_CHROME_EXTENSIONS" style="display: none;"></div></body></html>
        """.trimIndent()

        val level = ResolveUserInfoHelper(Jsoup.parse(html)).grade
        val l = Level.getLevel(level)
        println("我在下载东西$l")
    }

}

class task(val a: Int) : Runnable {
    override fun run() {
        print("我在下载东西$a")
    }


}