package com.townwang.yaohuo
import org.junit.Test
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
      val  content =  "((回</a>]).+(<br>))".toRegex().find(html)?.value?.removePrefix("回</a>]")
          ?.removeSuffix("<br>")
        println("str====== ${content}")
    }

}

class task(val a: Int) : Runnable {
    override fun run() {
        print("我在下载东西$a")
    }


}