package com.townwang.yaohuo.di.factory;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class DocumentConverterFactory extends Converter.Factory {
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/html; charset=utf-8");

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            @NotNull Type type,
            @NotNull Annotation[] annotations,
            @NotNull Retrofit retrofit) {
        if (Document.class.equals(type)) {
            return (Converter<ResponseBody, Document>) value ->
                    Jsoup.parse(value.string());
        }
        return null;
    }

    @Override public Converter<?, RequestBody> requestBodyConverter(
            @NotNull Type type,
            @NotNull Annotation[] parameterAnnotations,
            @NotNull Annotation[] methodAnnotations,
            @NotNull Retrofit retrofit) {
        if (Document.class.equals(type)) {
            return (Converter<Document, RequestBody>) value ->
                    RequestBody.create(MEDIA_TYPE,value.toString());
        }
        return null;
    }
}