package com.lautung.rxjavademo.ui.retrofit;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;

import com.lautung.rxjavademo.R;
import com.lautung.rxjavademo.rx.WanAndroidApi;
import com.lautung.rxjavademo.util.HttpUtil;

public class RetrofitLoginActivity extends FragmentActivity {

    Button btn1;
    Button btn2;
    Button btn3;

    WanAndroidApi wanAndroidApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        wanAndroidApi = HttpUtil.getOnlineCookieRetrofit().create(WanAndroidApi.class);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);



    }
}
