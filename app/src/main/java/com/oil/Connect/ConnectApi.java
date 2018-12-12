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
    Observable<ResponseBody> getInfo(@Field("dataType") String dataType, @Field("cardid") String cardid, @Field("daid") String daid);


    @FormUrlEncoded
    @POST("GRNRServer_xiaoshouData.do")
    Observable<ResponseBody> inputDataGZ(@Field("daid") String daid,
                                         @Field("dataType") String dataType, @Field("cardid") String cardid,
                                         @Field("name") String name, @Field("goumaizenhao") String goumaizenhao,
                                         @Field("qiyoubiaohao") String qiyoubiaohao, @Field("sulian") String sulian,
                                         @Field("goumairenzhaopian") String goumairenzhaopian, @Field("cardpic") String cardpic,
                                         @Field("xiaoshourenhao") String xiaoshourenhao, @Field("xiaoshouren") String xiaoshouren,
                                         @Field("zhiwen") String zhiwen,
                                         @Field("new") boolean news);


    @FormUrlEncoded
    @POST("/GRNRServer_renyuanData.do?")
    Observable<ResponseBody> renyuanData(@Field("new") boolean news,
                                         @Field("dataType") String dataType,
                                         @Field("daid") String daid,
                                         @Field("psonName") String psonName,
                                         @Field("psonSex") String psonSex,
                                         @Field("psonBirthday") String psonBirthday,
                                         @Field("psonMingzu") String psonMingzu,
                                         @Field("psonAddress") String psonAddress,
                                         @Field("psonNativeplace") String psonNativeplace,
                                         @Field("psonNativeaddress") String psonNativeaddress,
                                         @Field("psonIdcard") String psonIdcard,
                                         @Field("psonPhone") String psonPhone,
                                         @Field("wtype") String wtype,
                                         @Field("psonPhoto") String psonPhoto);

    @FormUrlEncoded
    @POST("/GRNRServer_renyuanData.do?")
    Observable<ResponseBody> renyuanData(@Field("dataType") String dataType,
                                         @Field("daid") String daid);

    @FormUrlEncoded
    @POST("/GRNRServer_renyuanData.do?")
    Observable<ResponseBody> renyuanData(@Field("dataType") String dataType,
                                         @Field("daid") String daid,
                                         @Field("psonId") String psonId);

    @FormUrlEncoded
    @POST("/GRNRServer_xsRecordData.do?")
    Observable<ResponseBody> xsRecordData(@Field("dataType") String dataType,
                                          @Field("daid") String daid,
                                          @Field("time1") String time1,
                                          @Field("time2") String time2);

    @FormUrlEncoded
    @POST("/GRNRServer_xsRecordData.do?")
    Observable<ResponseBody> xsRecordData(@Field("dataType") String dataType,
                                          @Field("daid") String daid,
                                          @Field("xsId") String xsId);

}

