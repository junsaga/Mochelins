package com.musthave0145.mochelins.api;

import com.musthave0145.mochelins.model.User;
import com.musthave0145.mochelins.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    // 회원가입 API
    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    // 로그인 API
    @POST("/user/login")
    Call<UserRes> login(@Body User user);

}
