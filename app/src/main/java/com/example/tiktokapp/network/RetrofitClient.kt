package com.example.tiktokapp.network

import android.util.Log
import okhttp3.Dns
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://young-wood-7919.bmg-laurent.workers.dev/"
    private const val TAG = "RetrofitClient"

    private class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            return try {
                val response = chain.proceed(request)
                Log.d(TAG, "Response message: ${response.message}")
                response
            } catch (e: Exception) {
                Log.e(TAG, "Request failed with exception", e)
                throw e
            }
        }
    }

    private object GoogleDns : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                val addresses = InetAddress.getAllByName(hostname).toList()
                Log.d(TAG, "DNS lookup for $hostname: ${addresses.size} addresses found")
                addresses.forEach {
                    Log.d(TAG, "  - ${it.hostAddress}")
                }
                addresses.sortedByDescending { it is Inet4Address }
            } catch (e: Exception) {
                Log.e(TAG, "DNS lookup failed for $hostname", e)
                throw e
            }
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .dns(GoogleDns)
        .addInterceptor(LoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    val api: WebService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WebService::class.java)
}
