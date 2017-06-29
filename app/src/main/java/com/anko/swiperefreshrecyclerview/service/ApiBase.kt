package com.anko.swiperefreshrecyclerview.service

import com.anko.swiperefreshrecyclerview.common.JodaGsonAdapter
import com.bumptech.glide.load.model.LazyHeaders
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

//val apiBaseUrl = "http://304f7e16.ngrok.io/"
val apiBaseUrl = "http://192.168.2.208:8888/"
val AppId = "test"
val AppKey = "test"
val glideHeader: LazyHeaders = LazyHeaders.Builder().addHeader("Authorization", Credentials.basic(AppId, AppKey)).build()
val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

class HeaderInterceptor(val name: String = "Accept", val value: String = "application/json") : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().addHeader(name, value).build()
        return chain.proceed(request)
    }
}

val authenticator = Authenticator { _, response ->
    fun responseCount(response: Response): Int {
        var result = 1
        while ((response.priorResponse()) != null) result++
        return result
    }

    if (responseCount(response) >= 3) return@Authenticator null

    val credential = Credentials.basic(AppId, AppKey)
    response.request().newBuilder().header("Authorization", credential).build()
}

object ApiService {

    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setPrettyPrinting()
            .create()

    val okClient = OkHttpClient().newBuilder()
            .authenticator(authenticator)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(okClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
}
