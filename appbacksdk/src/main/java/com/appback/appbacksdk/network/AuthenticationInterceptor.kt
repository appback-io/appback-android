package com.appback.appbacksdk.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Class that will intercept all petitions to the server and will attach an authentication
 * token to the header of the request
 * @param token String containing the token that wants to be attached to the requests
 * @author - sapardo10
 */
internal class AuthenticationInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        return if (!token.isBlank()) {
            // Request customization: add request headers
            val requestBuilder: Request.Builder = original.newBuilder()
                .header("Authorization", "Bearer $token") // <-- this is the important line
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        } else {
            chain.proceed(original)
        }

    }

}