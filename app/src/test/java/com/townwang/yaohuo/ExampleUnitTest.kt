package com.townwang.yaohuo

import com.townwang.yaohuo.common.A_HREF
import com.townwang.yaohuo.common.A_KEY
import com.townwang.yaohuo.common.resolve.ResolveDetailsHelper
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
    @Test
    fun testDing(): Unit {
        val html = """
            <html><head><style type="text/css">.swal-icon--error{border-color:#f27474;-webkit-animation:animateErrorIcon .5s;animation:animateErrorIcon .5s}.swal-icon--error__x-mark{position:relative;display:block;-webkit-animation:animateXMark .5s;animation:animateXMark .5s}.swal-icon--error__line{position:absolute;height:5px;width:47px;background-color:#f27474;display:block;top:37px;border-radius:2px}.swal-icon--error__line--left{-webkit-transform:rotate(45deg);transform:rotate(45deg);left:17px}.swal-icon--error__line--right{-webkit-transform:rotate(-45deg);transform:rotate(-45deg);right:16px}@-webkit-keyframes animateErrorIcon{0%{-webkit-transform:rotateX(100deg);transform:rotateX(100deg);opacity:0}to{-webkit-transform:rotateX(0deg);transform:rotateX(0deg);opacity:1}}@keyframes animateErrorIcon{0%{-webkit-transform:rotateX(100deg);transform:rotateX(100deg);opacity:0}to{-webkit-transform:rotateX(0deg);transform:rotateX(0deg);opacity:1}}@-webkit-keyframes animateXMark{0%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}50%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}80%{-webkit-transform:scale(1.15);transform:scale(1.15);margin-top:-6px}to{-webkit-transform:scale(1);transform:scale(1);margin-top:0;opacity:1}}@keyframes animateXMark{0%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}50%{-webkit-transform:scale(.4);transform:scale(.4);margin-top:26px;opacity:0}80%{-webkit-transform:scale(1.15);transform:scale(1.15);margin-top:-6px}to{-webkit-transform:scale(1);transform:scale(1);margin-top:0;opacity:1}}.swal-icon--warning{border-color:#f8bb86;-webkit-animation:pulseWarning .75s infinite alternate;animation:pulseWarning .75s infinite alternate}.swal-icon--warning__body{width:5px;height:47px;top:10px;border-radius:2px;margin-left:-2px}.swal-icon--warning__body,.swal-icon--warning__dot{position:absolute;left:50%;background-color:#f8bb86}.swal-icon--warning__dot{width:7px;height:7px;border-radius:50%;margin-left:-4px;bottom:-11px}@-webkit-keyframes pulseWarning{0%{border-color:#f8d486}to{border-color:#f8bb86}}@keyframes pulseWarning{0%{border-color:#f8d486}to{border-color:#f8bb86}}.swal-icon--success{border-color:#a5dc86}.swal-icon--success:after,.swal-icon--success:before{content:"";border-radius:50%;position:absolute;width:60px;height:120px;background:#fff;-webkit-transform:rotate(45deg);transform:rotate(45deg)}.swal-icon--success:before{border-radius:120px 0 0 120px;top:-7px;left:-33px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-transform-origin:60px 60px;transform-origin:60px 60px}.swal-icon--success:after{border-radius:0 120px 120px 0;top:-11px;left:30px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-transform-origin:0 60px;transform-origin:0 60px;-webkit-animation:rotatePlaceholder 4.25s ease-in;animation:rotatePlaceholder 4.25s ease-in}.swal-icon--success__ring{width:80px;height:80px;border:4px solid hsla(98,55%,69%,.2);border-radius:50%;box-sizing:content-box;position:absolute;left:-4px;top:-4px;z-index:2}.swal-icon--success__hide-corners{width:5px;height:90px;background-color:#fff;padding:1px;position:absolute;left:28px;top:8px;z-index:1;-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}.swal-icon--success__line{height:5px;background-color:#a5dc86;display:block;border-radius:2px;position:absolute;z-index:2}.swal-icon--success__line--tip{width:25px;left:14px;top:46px;-webkit-transform:rotate(45deg);transform:rotate(45deg);-webkit-animation:animateSuccessTip .75s;animation:animateSuccessTip .75s}.swal-icon--success__line--long{width:47px;right:8px;top:38px;-webkit-transform:rotate(-45deg);transform:rotate(-45deg);-webkit-animation:animateSuccessLong .75s;animation:animateSuccessLong .75s}@-webkit-keyframes rotatePlaceholder{0%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}5%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}12%{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}to{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}}@keyframes rotatePlaceholder{0%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}5%{-webkit-transform:rotate(-45deg);transform:rotate(-45deg)}12%{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}to{-webkit-transform:rotate(-405deg);transform:rotate(-405deg)}}@-webkit-keyframes animateSuccessTip{0%{width:0;left:1px;top:19px}54%{width:0;left:1px;top:19px}70%{width:50px;left:-8px;top:37px}84%{width:17px;left:21px;top:48px}to{width:25px;left:14px;top:45px}}@keyframes animateSuccessTip{0%{width:0;left:1px;top:19px}54%{width:0;left:1px;top:19px}70%{width:50px;left:-8px;top:37px}84%{width:17px;left:21px;top:48px}to{width:25px;left:14px;top:45px}}@-webkit-keyframes animateSuccessLong{0%{width:0;right:46px;top:54px}65%{width:0;right:46px;top:54px}84%{width:55px;right:0;top:35px}to{width:47px;right:8px;top:38px}}@keyframes animateSuccessLong{0%{width:0;right:46px;top:54px}65%{width:0;right:46px;top:54px}84%{width:55px;right:0;top:35px}to{width:47px;right:8px;top:38px}}.swal-icon--info{border-color:#c9dae1}.swal-icon--info:before{width:5px;height:29px;bottom:17px;border-radius:2px;margin-left:-2px}.swal-icon--info:after,.swal-icon--info:before{content:"";position:absolute;left:50%;background-color:#c9dae1}.swal-icon--info:after{width:7px;height:7px;border-radius:50%;margin-left:-3px;top:19px}.swal-icon{width:80px;height:80px;border-width:4px;border-style:solid;border-radius:50%;padding:0;position:relative;box-sizing:content-box;margin:20px auto}.swal-icon:first-child{margin-top:32px}.swal-icon--custom{width:auto;height:auto;max-width:100%;border:none;border-radius:0}.swal-icon img{max-width:100%;max-height:100%}.swal-title{color:rgba(0,0,0,.65);font-weight:600;text-transform:none;position:relative;display:block;padding:13px 16px;font-size:27px;line-height:normal;text-align:center;margin-bottom:0}.swal-title:first-child{margin-top:26px}.swal-title:not(:first-child){padding-bottom:0}.swal-title:not(:last-child){margin-bottom:13px}.swal-text{font-size:16px;position:relative;float:none;line-height:normal;vertical-align:top;text-align:left;display:inline-block;margin:0;padding:0 10px;font-weight:400;color:rgba(0,0,0,.64);max-width:calc(100% - 20px);overflow-wrap:break-word;box-sizing:border-box}.swal-text:first-child{margin-top:45px}.swal-text:last-child{margin-bottom:45px}.swal-footer{text-align:right;padding-top:13px;margin-top:13px;padding:13px 16px;border-radius:inherit;border-top-left-radius:0;border-top-right-radius:0}.swal-button-container{margin:5px;display:inline-block;position:relative}.swal-button{background-color:#7cd1f9;color:#fff;border:none;box-shadow:none;border-radius:5px;font-weight:600;font-size:14px;padding:10px 24px;margin:0;cursor:pointer}.swal-button:not([disabled]):hover{background-color:#78cbf2}.swal-button:active{background-color:#70bce0}.swal-button:focus{outline:none;box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(43,114,165,.29)}.swal-button[disabled]{opacity:.5;cursor:default}.swal-button::-moz-focus-inner{border:0}.swal-button--cancel{color:#555;background-color:#efefef}.swal-button--cancel:not([disabled]):hover{background-color:#e8e8e8}.swal-button--cancel:active{background-color:#d7d7d7}.swal-button--cancel:focus{box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(116,136,150,.29)}.swal-button--danger{background-color:#e64942}.swal-button--danger:not([disabled]):hover{background-color:#df4740}.swal-button--danger:active{background-color:#cf423b}.swal-button--danger:focus{box-shadow:0 0 0 1px #fff,0 0 0 3px rgba(165,43,43,.29)}.swal-content{padding:0 20px;margin-top:20px;font-size:medium}.swal-content:last-child{margin-bottom:20px}.swal-content__input,.swal-content__textarea{-webkit-appearance:none;background-color:#fff;border:none;font-size:14px;display:block;box-sizing:border-box;width:100%;border:1px solid rgba(0,0,0,.14);padding:10px 13px;border-radius:2px;transition:border-color .2s}.swal-content__input:focus,.swal-content__textarea:focus{outline:none;border-color:#6db8ff}.swal-content__textarea{resize:vertical}.swal-button--loading{color:transparent}.swal-button--loading~.swal-button__loader{opacity:1}.swal-button__loader{position:absolute;height:auto;width:43px;z-index:2;left:50%;top:50%;-webkit-transform:translateX(-50%) translateY(-50%);transform:translateX(-50%) translateY(-50%);text-align:center;pointer-events:none;opacity:0}.swal-button__loader div{display:inline-block;float:none;vertical-align:baseline;width:9px;height:9px;padding:0;border:none;margin:2px;opacity:.4;border-radius:7px;background-color:hsla(0,0%,100%,.9);transition:background .2s;-webkit-animation:swal-loading-anim 1s infinite;animation:swal-loading-anim 1s infinite}.swal-button__loader div:nth-child(3n+2){-webkit-animation-delay:.15s;animation-delay:.15s}.swal-button__loader div:nth-child(3n+3){-webkit-animation-delay:.3s;animation-delay:.3s}@-webkit-keyframes swal-loading-anim{0%{opacity:.4}20%{opacity:.4}50%{opacity:1}to{opacity:.4}}@keyframes swal-loading-anim{0%{opacity:.4}20%{opacity:.4}50%{opacity:1}to{opacity:.4}}.swal-overlay{position:fixed;top:0;bottom:0;left:0;right:0;text-align:center;font-size:0;overflow-y:auto;background-color:rgba(0,0,0,.4);z-index:10000;pointer-events:none;opacity:0;transition:opacity .3s}.swal-overlay:before{content:" ";display:inline-block;vertical-align:middle;height:100%}.swal-overlay--show-modal{opacity:1;pointer-events:auto}.swal-overlay--show-modal .swal-modal{opacity:1;pointer-events:auto;box-sizing:border-box;-webkit-animation:showSweetAlert .3s;animation:showSweetAlert .3s;will-change:transform}.swal-modal{width:478px;opacity:0;pointer-events:none;background-color:#fff;text-align:center;border-radius:5px;position:static;margin:20px auto;display:inline-block;vertical-align:middle;-webkit-transform:scale(1);transform:scale(1);-webkit-transform-origin:50% 50%;transform-origin:50% 50%;z-index:10001;transition:opacity .2s,-webkit-transform .3s;transition:transform .3s,opacity .2s;transition:transform .3s,opacity .2s,-webkit-transform .3s}@media (max-width:500px){.swal-modal{width:calc(100% - 20px)}}@-webkit-keyframes showSweetAlert{0%{-webkit-transform:scale(1);transform:scale(1)}1%{-webkit-transform:scale(.5);transform:scale(.5)}45%{-webkit-transform:scale(1.05);transform:scale(1.05)}80%{-webkit-transform:scale(.95);transform:scale(.95)}to{-webkit-transform:scale(1);transform:scale(1)}}@keyframes showSweetAlert{0%{-webkit-transform:scale(1);transform:scale(1)}1%{-webkit-transform:scale(.5);transform:scale(.5)}45%{-webkit-transform:scale(1.05);transform:scale(1.05)}80%{-webkit-transform:scale(.95);transform:scale(.95)}to{-webkit-transform:scale(1);transform:scale(1)}}</style>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
            <meta http-equiv="Cache-Control" content="no-cache">
            <meta name="viewport" content="width=device-width; initial-scale=1.0; minimum-scale=1.0; maximum-scale=1.0">
            <meta name="description" content="pdd退货问题 妖火茶馆 妖火网 - 分享你我 ">
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
            <link rel="stylesheet" href="/Template/default/default.css?v=20210131" type="text/css">

            <title>pdd退货问题_妖火茶馆_妖火网</title>
            <style type="text/css">
            :root .adsbygoogle,
            :root .footer > #box[style="width:100%;height:100%;position:fixed;top:0"],
            :root .content > a > .topline,
            :root .right > .content > .ads
            { display: none !important; }</style><style type="text/css">
            :root *[nscwakn][hidden] { display: none !important; }</style></head>
            <body>
            <a href="https://yaohuo.me/bbs/messagelist.aspx?siteid=1000&amp;backurl=wapindex.aspx?siteid=1000&amp;amp;classid=177"><img src="/tupian/news.gif" alt=".">收到53封飞鸽传书</a><div class="title"><a href="https://yaohuo.me/">首页</a>&gt;<a href="https://yaohuo.me/bbs/">论坛</a>&gt;<a href="https://yaohuo.me/bbs/book_list.aspx?action=class&amp;classid=177">妖火茶馆</a>&gt;帖子</div><!--web--><div class="subtitle"><a href="http://tu.yaohuo.me/">图床</a> <a href="https://yaohuo.me/wapindex.aspx?siteid=1000&amp;classid=206">发帖</a> <a href="https://yaohuo.me/bbs/book_list.aspx?action=new&amp;getTotal=2019">新帖</a> <a href="https://yaohuo.me/bbs/Book_View_admin.aspx?siteid=1000&amp;classid=177&amp;id=901868&amp;lpage=1">管理</a></div><div class="content">[标题] pdd退货问题 (阅64)<br>[时间] 2021/1/31 0:45:00<div class="dashed"></div><div class="bbscontent"><!--listS-->前几天在pdd上买了个蓝牙耳机，有毛病。现在商家同意退货，而且我还没有退货，已退款成功。但是只退了实付款，当时买的时候用了20元的无门槛券，商家不能退，怎么办？？？不退货会有什么后果？<!--listE--><span id="KL_show_next_list"></span></div></div><div class="margin-top"></div><div class="subtitle">[楼主] <a href="https://yaohuo.me/bbs/userinfo.aspx?touserid=37201">nomadichu(路旁的落叶)</a><img src="/NetImages/on1.gif" alt="ONLINE"><img src="/bbs/medal/t7.gif" alt="."><br>[荣誉] <br>[地区] <a href="https://yaohuo.me/search/book_search.aspx">交友</a><br>[操作] <a href="https://yaohuo.me/bbs/Book_View_admin.aspx?siteid=1000&amp;classid=177&amp;id=901868&amp;lpage=1">管理</a> <a href="https://yaohuo.me/bbs/Report_add.aspx?siteid=1000&amp;classid=177&amp;page=1&amp;id=901868">举报</a> <a href="https://yaohuo.me/bbs/share.aspx?siteid=1000&amp;classid=177&amp;id=901868">分享</a> <a href="https://yaohuo.me/bbs/Share.aspx?action=fav&amp;siteid=1000&amp;classid=177&amp;id=901868">收藏</a><br> [签名] <u></u><br><a href="https://yaohuo.me/bbs/book_view.aspx?siteid=1000&amp;classid=177&amp;state=1&amp;vpage=1&amp;lpage=1&amp;id=901868">顶<img src="/NetImages/flower.gif" alt="花"></a>(0) <a href="https://yaohuo.me/bbs/book_view.aspx?siteid=1000&amp;classid=177&amp;state=2&amp;vpage=1&amp;lpage=1&amp;id=901868">赞<img src="/NetImages/egg.gif" alt="蛋"></a>(0) <br></div><div class="content"><form name="f" action="/bbs/book_re.aspx" method="post"><select name="face"><option value="">表情</option><option value="踩.gif">踩</option><option value="淡定.gif">淡定</option><option value="囧.gif">囧</option><option value="不要.gif">不要不要</option><option value="砳砳.gif">砳砳君</option><option value="滑稽砳砳.gif">滑稽君</option><option value="沙发.gif">坐沙发</option><option value="汗.gif">汗</option><option value="亲亲.gif">么么哒</option><option value="太开心.gif">太开心</option><option value="酷.gif">酷</option><option value="思考.gif">思考</option><option value="发呆.gif">发呆</option><option value="得瑟.gif">嘚瑟</option><option value="哈哈.gif">开心</option><option value="超人.gif">超人</option><option value="泪流满面.gif">泪流满面</option><option value="放电.gif">放电</option><option value="困.gif">困</option><option value="害羞.gif">害羞</option><option value="呃.gif">无语</option><option value="哇哦.gif">哇哦</option><option value="要死了.gif">要死了</option><option value="谢谢.gif">谢谢</option><option value="抓狂.gif">抓狂</option><option value="无奈.gif">无奈</option><option value="不好笑.gif">不好笑</option><option value="呦呵.gif">哟呵</option><option value="感动.gif">感动</option><option value="喜欢.gif">喜欢</option><option value="疑问.gif">疑问</option><option value="委屈.gif">委屈</option><option value="你不行.gif">你不行</option><option value="流口水.gif">流口水</option><option value="潜水.gif">潜水</option><option value="咒骂.gif">咒骂</option><option value="耶耶.gif">耶耶</option><option value="被揍.gif">被揍</option><option value="抱走.gif">抱走</option></select><select name="sendmsg"><option value="0">通知楼主？</option><option value="0">否</option><option value="1">是</option></select><br><textarea name="content" minlength="1" required="required" placeholder="请不要乱打字回复，以免被加黑。" rows="2" style="width:98%;height:80px;"></textarea><br><input type="hidden" name="action" value="add"><input type="hidden" name="id" value="901868"><input type="hidden" name="siteid" value="1000"><input type="hidden" name="lpage" value="1"><input type="hidden" name="classid" value="177"><input type="hidden" name="sid" value="0C9F5256EE1FBF0_710_04771_22980_51001-2-0-0-0-0"><input type="submit" name="g" value="快速回复"> <a href="https://yaohuo.me/bbs/book_re_addfile.aspx?action=class&amp;siteid=1000&amp;classid=177&amp;id=901868&amp;lpage=1">文件回帖</a><br></form><div class="recontent"><div class="reline">[板凳][<a href="https://yaohuo.me/bbs/Book_re.aspx?siteid=1000&amp;classid=177&amp;lpage=1&amp;vpage=1&amp;reply=3&amp;id=901868&amp;touserid=24770">回</a>]<a href="https://yaohuo.me/bbs/userinfo.aspx?touserid=24770">τ</a>：这个………<br>蓝牙耳机的事，我买了好几个都放边上吃灰了😂      📱 (01-31 01:20)</div><div class="reline">[椅子][<a href="https://yaohuo.me/bbs/Book_re.aspx?siteid=1000&amp;classid=177&amp;lpage=1&amp;vpage=1&amp;reply=2&amp;id=901868&amp;touserid=4000">回</a>]<a href="https://yaohuo.me/bbs/userinfo.aspx?touserid=4000">翼城</a>：找客服 (01-31 01:15)</div><div class="reline">[沙发][<a href="https://yaohuo.me/bbs/Book_re.aspx?siteid=1000&amp;classid=177&amp;lpage=1&amp;vpage=1&amp;reply=1&amp;id=901868&amp;touserid=34976">回</a>]<a href="https://yaohuo.me/bbs/userinfo.aspx?touserid=34976"><font color="#FF0000">妖友好好记</font></a>：那个券肯定找平台啊<img src="/face/汗.gif" alt="."> (01-31 00:46)</div></div><div class="more"><a href="https://yaohuo.me/bbs/book_re.aspx?lpage=1&amp;getTotal=3&amp;id=901868&amp;classid=177&amp;siteid=1000&amp;page=2">更多回帖(3)</a></div></div><div class="title">【<a href="https://yaohuo.me/wapindex.aspx?classid=206">发表主题</a>】<a href="https://yaohuo.me/bbs/book_list.aspx?action=new&amp;getTotal=2020">最新</a>．<a href="https://yaohuo.me/wapindex.aspx?siteid=1000&amp;classid=300">搜索</a></div><div class="list">1.<a href="https://yaohuo.me/bbs-901872.html">任天堂switch 有问</a><br>2.<a href="https://yaohuo.me/bbs-901871.html">狗东运费券</a><br>3.<a href="https://yaohuo.me/bbs-901870.html">免费容器服务，代搭建V2</a><br>4.<a href="https://yaohuo.me/bbs-901869.html">我总觉得这个妹子喜欢我</a><br>5.<a href="https://yaohuo.me/bbs-901868.html">pdd退货问题</a><br>6.<a href="https://yaohuo.me/bbs-901867.html">京东函数&amp;&amp;联通函数一键</a><br>7.<a href="https://yaohuo.me/bbs-901866.html">【分享】联通签到流量</a><br>8.<a href="https://yaohuo.me/bbs-901865.html">此贴终结！</a><br>9.<a href="https://yaohuo.me/bbs-901864.html">广东腾讯王卡可以加什么包</a></div><div class="nexttitle">=<a href="https://yaohuo.me/">首页</a>-<a href="https://yaohuo.me/bbs/">论坛</a>-<a href="https://yaohuo.me/ka.html?e">神卡</a>-<a href="https://yaohuo.me/Games/gamesindex.aspx">游戏</a>-<a href="https://s.click.taobao.com/sv4OSsu">补贴</a>=</div><div class="footer">1月31日 星期日 <a href="https://yaohuo.me/bbs-901868.html#top"><img src="/tupian/top.png" alt=".">回顶部</a></div><div class="Google" style=" line-height: 0; text-align:center; ">
            <!-- 自适应 -->

            <script>
                 (adsbygoogle = window.adsbygoogle || []).push({});
            </script></div><!-- Global site tag (gtag.js) - Google Analytics -->
            <script async="" src="https://www.googletagmanager.com/gtag/js?id=UA-88858350-1"></script>
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

            <div id="returnToTop" style="display: none; font-size: 15px; padding: 10px; bottom: 5px; right: 5px; z-index: 1000; background-color: gray; position: fixed; border-radius: 25px; text-align: center; cursor: pointer; color: rgb(255, 255, 255);">回到顶部</div></body></html>
        """.trimIndent()


//        val level = ResolveDetailsHelper(Jsoup.parse(html)).getPraiseUrl
     val ddd =   Jsoup.parse(html)
         .select("div.subtitle").last()
         .getElementsContainingOwnText("顶").attr(A_HREF)
        println("我在下载东西$ddd")
    }

}

class task(val a: Int) : Runnable {
    override fun run() {
        print("我在下载东西$a")
    }


}