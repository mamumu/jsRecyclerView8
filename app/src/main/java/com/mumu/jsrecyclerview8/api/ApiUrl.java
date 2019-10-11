package com.mumu.jsrecyclerview8.api;

import com.mumu.jsrecyclerview8.BaseResponse;
import com.mumu.jsrecyclerview8.MainEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author : zlf
 * date    : 2019/10/10
 * github  : https://github.com/mamumu
 * blog    : https://www.jianshu.com/u/281e9668a5a6
 * desc    :
 */
public interface ApiUrl {

    @GET("data/福利/{num}/{page}")
    Call<BaseResponse<MainEntity.ResultsBean>> getPic(@Path("num")int num, @Path("page")int page);
}
