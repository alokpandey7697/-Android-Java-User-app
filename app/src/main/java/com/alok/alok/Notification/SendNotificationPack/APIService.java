package com.alok.alok.Notification.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAtKCFzZs:APA91bFFVBLTkOTwoKT01kavwGukrN7qOgqWDAWMQBDqr4nyE1AOEIUuZdgy6TvV0QVnGg8B2Va5W3cRhPpb8akPOo7HqEOnQz0BulauVJw-S2eBJjVmBjHacbDFQMsGdrTMlfyfjz1m" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}

