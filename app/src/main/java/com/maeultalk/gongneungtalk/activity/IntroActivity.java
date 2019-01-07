package com.maeultalk.gongneungtalk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maeultalk.gongneungtalk.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 1초 후 인트로 액티비티 종료
        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {

            @Override
            public void run() {

                SharedPreferences pref = getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);
                if(pref.getString("email", "null").equals("null")) {
                    // 로그인 정보가 없으면
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 페이드-아웃 애니메이션
                } else {
                    // 로그인 정보가 있으면
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 페이드-아웃 애니메이션
                }

            }

        }, 1000);

    }
}
