package com.anko.swiperefreshrecyclerview.service

import com.anko.swiperefreshrecyclerview.model.Media
import com.anko.swiperefreshrecyclerview.model.Sweet
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface SweetApi {
    @GET("/sweet/{id}") fun singleSweet(@Path("id") id: Int): Observable<Sweet>
    @GET("/top/{count}/{page}") fun topSweet(@Path("count") count: Int, @Path("page") page: Int): Observable<List<Sweet>>
    @GET("/latest/{count}/{page}") fun latestSweet(@Path("count") count: Int, @Path("page") page: Int): Observable<List<Sweet>>
    @GET("/sweet-replies/{id}") fun getReplies(@Path("id") id: Int): Observable<List<Sweet>>
    @GET("/sweet-medias/{refId}") fun getMedias(@Path("refId") refId: Int): Observable<List<Media>>

    @GET("/media/{name}/{type}") fun viewMedia(@Path("name") name: String, @Path("type") type: String): Observable<ResponseBody>
    @GET("/media/{id}") fun getMedia(@Path("id") id: Int): Observable<Media>
    @GET("/mediasBySweet/{refId}") fun getMediasBySweet(@Path("refId") refId: Int): Observable<List<Int>>


    companion object Factory {
        fun create(): SweetApi {
            val retrofit = ApiService.retrofit
            return retrofit.create(SweetApi::class.java)
        }
    }

}
