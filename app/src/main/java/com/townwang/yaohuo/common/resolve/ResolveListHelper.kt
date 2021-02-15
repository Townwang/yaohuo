package com.townwang.yaohuo.common.resolve

import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.HomeData
import org.jsoup.nodes.Document


class ResolveListHelper(private val document: Document) {

    val homeListData: List<HomeData>
        get() {
            val list = document.select(NEW_LIST)
            val lists = arrayListOf<HomeData>()
            lists.clear()
            list.forEach {
                val hrefs = it.select(A_KEY).first()
                val a = hrefs.attr(A_HREF)
                val title = hrefs.text()
                val str = it
                str.select(A).first().remove()
                val auth = str.text().split("/")[0].split(" ").last()
                val reply = str.text().split("/")[1]
                val read = str.text().split("/")[2]
                val time = it.select(NEW_LIST_TIME).text()
                val smailImg = it.select(IMG_GIF)
                val smailImgs = arrayListOf<String>()
                smailImg.forEach { img ->
                    smailImgs.add(img.attr(IMG_ALT))
                }
                lists.add(HomeData(title, a, auth, reply, read, time, smailImgs))
            }
            return lists
        }


}