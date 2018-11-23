package com.oil.Connect;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ConnectApi {
    @FormUrlEncoded
    @POST("GRNRServer_getInfo.do")
    Observable<ResponseBody> getInfo(@Field("dataType") String dataType, @Field("cardid") String cardid,@Field("daid") String daid);


    @FormUrlEncoded
    @POST("GRNRServer_inputData.do")
    Observable<ResponseBody> inputDataGZ(@Field("daid") String daid,
            @Field("dataType") String dataType, @Field("cardid") String cardid,
            @Field("name") String name, @Field("goumaizenhao") String goumaizenhao,
            @Field("qiyoubiaohao") String qiyoubiaohao, @Field("sulian") String sulian,
            @Field("goumairenzhaopian") String goumairenzhaopian, @Field("cardpic") String cardpic,
            @Field("xiaoshourenhao") String xiaoshourenhao, @Field("xiaoshouren") String xiaoshouren,
            @Field("new") boolean news);

}

