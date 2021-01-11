package com.townwang.yaohuo.di.factory

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class DocumentConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (Document::class.java == type) {
            Converter{ value: ResponseBody ->
                Jsoup.parse(
                    value.string()
                )
            }
        } else null
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody?>? {
        return if (Document::class.java == type) {
            Converter { value: Document ->
                RequestBody.create(
                    MEDIA_TYPE,
                    value.toString()
                )
            }
        } else null
    }

    companion object {
        private val MEDIA_TYPE =
            "text/html; charset=utf-8".toMediaTypeOrNull()
    }
}