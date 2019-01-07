package com.maeultalk.gongneungtalk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.user.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button button_login;
    TextView button_register;
    EditText editText_email;
    EditText editText_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("로그인");

        editText_email = (EditText) findViewById(R.id.editText_email);
        editText_password = (EditText) findViewById(R.id.editText_password);
        editText_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(textView.getId()==R.id.editText_password && i== EditorInfo.IME_ACTION_DONE) {
                    clickConfirm();
                }

                return false;
            }
        });

        button_register = (TextView) findViewById(R.id.button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickConfirm();

            }
        });

    }

    void clickConfirm() {

        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("로그인에 성공했습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences pref = getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        try {
                                            editor.putString("email", jsonResponse.getString("email"));
                                            editor.putString("password", jsonResponse.getString("password"));
                                            editor.putString("nick", jsonResponse.getString("nick"));
                                            editor.putString("identity", jsonResponse.getString("identity"));
                                        } catch (Exception e) {

                                        }
                                        editor.commit();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("로그인에 실패했습니다.")
                                .setNegativeButton("다시 시도", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {

                }

            }
        };

        LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);

    }

}
