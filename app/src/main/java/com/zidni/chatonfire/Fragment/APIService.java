package com.zidni.chatonfire.Fragment;

import com.zidni.chatonfire.Notifications.MyResponse;
import com.zidni.chatonfire.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAll9FKcM:APA91bHkPnV1LjWdQR9PnzJIPAdWiglPznEZ8jxOtKubNWrFsDcUr6llIoo7OmN-6sBaOBOION3SkgmzJHv-ZZ75Ev5Hoixp11UylLL0pHQ7Qw359yBvOWy2aOkXa_BY8x_Y4pGhRFCg"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> seenNotification(@Body Sender body);
}
