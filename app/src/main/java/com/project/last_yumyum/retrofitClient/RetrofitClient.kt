package com.project.last_yumyum.retrofitClient

import com.project.last_yumyum.api.InterfaceRetrofit
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy

object RetrofitClient {
    private const val BASE_URL = "https://r2-r.com/"

    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookieManager))
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())////
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: InterfaceRetrofit by lazy {
        retrofit.create(InterfaceRetrofit::class.java)
    }


}