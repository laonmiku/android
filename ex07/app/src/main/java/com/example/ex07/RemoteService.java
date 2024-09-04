package com.example.ex07;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RemoteService {
    public static final String BASE_URL="http://192.168.0.12:5000";

    @GET("/wine/list.json")
    Call<List<HashMap<String,Object>>> list();
}
