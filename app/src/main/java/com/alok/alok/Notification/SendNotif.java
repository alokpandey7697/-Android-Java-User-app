package com.alok.alok.Notification;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alok.alok.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.alok.alok.Notification.SendNotificationPack.APIService;
import com.alok.alok.Notification.SendNotificationPack.Client;
import com.alok.alok.Notification.SendNotificationPack.Data;
import com.alok.alok.Notification.SendNotificationPack.MyResponse;
import com.alok.alok.Notification.SendNotificationPack.NotificationSender;
import com.alok.alok.Notification.SendNotificationPack.Token;




import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotif extends AppCompatActivity {
    EditText UserTB,Title,Message;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notif);
        UserTB=findViewById(R.id.UserID);
        Title=findViewById(R.id.Title);
        Message=findViewById(R.id.Message);
        send=findViewById(R.id.button);
       /* send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
        UpdateToken();
    }
    private  void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    public static void sendNotification(String TokenString, String UserID, String Title, String Message, String sender, Context context) {
        Log.d("token",TokenString + "   " + UserID + " " + Title + " " + Message + " " + sender );
        FirebaseDatabase.getInstance().getReference().child(TokenString).child(UserID).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usertoken=dataSnapshot.getValue(String.class);
                sendNotifications(usertoken, Title,Message,sender,context);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void sendNotifications(String usertoken, String title, String message, String sndr, Context context) {
        Data data = new Data(title, message, sndr);
        NotificationSender sender = new NotificationSender(data, usertoken);

        APIService apiService;

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        //Toast.makeText(context, " Notification Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

}






/*

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotif extends AppCompatActivity {
    EditText UserTB,Title,Message;
    Button send;
    private APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notif);
        UserTB=findViewById(R.id.UserID);
        Title=findViewById(R.id.Title);
        Message=findViewById(R.id.Message);
        send=findViewById(R.id.button);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Tokens").child(UserTB.getText().toString().trim()).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String usertoken=dataSnapshot.getValue(String.class);
                        sendNotifications(usertoken, Title.getText().toString().trim(),Message.getText().toString().trim());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        UpdateToken();
    }
    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(SendNotif.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

}
*/
