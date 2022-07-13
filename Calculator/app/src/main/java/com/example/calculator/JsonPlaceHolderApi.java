package com.example.calculator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET("convert") //format example:'https://api.exchangerate.host/convert?from=USD&to=EUR'
    Call<Post> getRate(@Query("from")String from,@Query("to") String to);
}
