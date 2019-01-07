package com.maeultalk.gongneungtalk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.user.ExistRequest;
import com.maeultalk.gongneungtalk.user.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    Button button_register;
    TextView button_login;
    EditText editText_email;
    EditText editText_emailConfirm;
    EditText editText_password;
    EditText editText_passwordConfirm;
    EditText editText_nick;
    RadioGroup radioGroup_identity;

    String email;
    String emailConfirm;
    String password;
    String passwordConfirm;
    String nick;
    String identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("회원가입");

        editText_email = (EditText) findViewById(R.id.editText_email);
        editText_emailConfirm = (EditText) findViewById(R.id.editText_emailConfirm);
        editText_password = (EditText) findViewById(R.id.editText_password);
        editText_passwordConfirm = (EditText) findViewById(R.id.editText_passwordConfirm);
        editText_nick = (EditText) findViewById(R.id.editText_nick);
        radioGroup_identity = (RadioGroup) findViewById(R.id.radioGroup_identity);

        button_login = (TextView) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_register = (Button) findViewById(R.id.button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickConfirm();

            }
        });

    }

    void clickConfirm() {

        email = editText_email.getText().toString();
        emailConfirm = editText_emailConfirm.getText().toString();
        password = editText_password.getText().toString();
        passwordConfirm = editText_passwordConfirm.getText().toString();
        nick = editText_nick.getText().toString();
        //final String identity;
        switch (radioGroup_identity.getCheckedRadioButtonId()) {
            case R.id.radioButton_resident:
                identity = "resident";
                break;
            case R.id.radioButton_merchant:
                identity = "merchant";
                break;
            case R.id.radioButton_student:
                identity = "student";
                break;
            case R.id.radioButton_worker:
                identity = "worker";
                break;
            case R.id.radioButton_visitor:
                identity = "visitor";
                break;
        }

        if(!TextUtils.isEmpty(email)) {
            if(email.equals(emailConfirm)) {
                Response.Listener<String> existResponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject jsonResponse = new JSONObject(response);
                            boolean exist = jsonResponse.getBoolean("exist_user");
                            if(!exist) {
                                if(!TextUtils.isEmpty(password)) {
                                    if(password.equals(passwordConfirm)) {
                                        if(!TextUtils.isEmpty(nick)) {
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        final JSONObject jsonResponse = new JSONObject(response);
                                                        boolean success = jsonResponse.getBoolean("success");
                                                        if(success) {

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                            builder.setMessage("회원 등록에 성공했습니다.")
                                                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            SharedPreferences pref = getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);
                                                                            SharedPreferences.Editor editor = pref.edit();
                                                                            try {
                                                                                editor.putString("email", email);
                                                                                editor.putString("password", password);
                                                                                editor.putString("nick", nick);
                                                                                editor.putString("identity", identity);
                                                                            } catch (Exception e) {

                                                                            }
                                                                            editor.commit();
                                                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .create()
                                                                    .show();
                                                        } else {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                            builder.setMessage("회원 등록에 실패했습니다.")
                                                                    .setNegativeButton("다시 시도", null)
                                                                    .create()
                                                                    .show();
                                                        }
                                                    } catch (JSONException e) {

                                                    }

                                                }
                                            };

                                            RegisterRequest registerRequest = new RegisterRequest(email, password, nick, identity, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                                            queue.add(registerRequest);
                                        } else {
                                            // 닉네임을 입력해주세요.
                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                            builder.setMessage("닉네임을 입력해주세요.")
                                                    .setNegativeButton("재입력", null)
                                                    .create()
                                                    .show();
                                        }
                                    } else {
                                        // 비밀번호을 확인해주세요.
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setMessage("비밀번호을 확인해주세요.")
                                                .setNegativeButton("재입력", null)
                                                .create()
                                                .show();
                                    }
                                } else {
                                    // 비밀번호를 입력하세요.
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("비밀번호를 입력하세요.")
                                            .setNegativeButton("재입력", null)
                                            .create()
                                            .show();
                                }
                            } else {
                                // 이미 가입된 이메일입니다.
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("이미 가입된 이메일입니다.")
                                        .setNegativeButton("재입력", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {

                        }

                    }
                };

                ExistRequest existRequest = new ExistRequest(email, existResponseListener);
                RequestQueue existQueue = Volley.newRequestQueue(RegisterActivity.this);
                existQueue.add(existRequest);
            } else {
                // 이메일을 확인해주세요.
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage("이메일을 확인해주세요.")
                        .setNegativeButton("재입력", null)
                        .create()
                        .show();
            }
        } else {
            // 이메일을 입력하세요.
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("이메일을 입력하세요.")
                    .setNegativeButton("재입력", null)
                    .create()
                    .show();
        }

                /*if(TextUtils.isEmpty(email)) {
                    // 이메일을 입력하세요.
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("이메일을 입력하세요.")
                            .setNegativeButton("재입력", null)
                            .create()
                            .show();
                } else if(!email.equals(emailConfirm)) {
                    // 이메일을 확인해주세요.
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("이메일을 확인해주세요.")
                            .setNegativeButton("재입력", null)
                            .create()
                            .show();
                } else if(false) {
                    // 이미 가입된 이메일입니다.

                } else if(TextUtils.isEmpty(password)) {
                    // 비밀번호를 입력하세요.
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("비밀번호를 입력하세요.")
                            .setNegativeButton("재입력", null)
                            .create()
                            .show();
                } else if(!password.equals(passwordConfirm)) {
                    // 비밀번호을 확인해주세요.
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("비밀번호을 확인해주세요.")
                            .setNegativeButton("재입력", null)
                            .create()
                            .show();
                } else if(TextUtils.isEmpty(nick)) {
                    // 닉네임을 입력해주세요.
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("닉네임을 입력해주세요.")
                            .setNegativeButton("재입력", null)
                            .create()
                            .show();
                } else {

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                final JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("회원 등록에 성공했습니다.")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    SharedPreferences pref = getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    try {
                                                        editor.putString("email", email);
                                                        editor.putString("password", password);
                                                        editor.putString("nick", nick);
                                                        editor.putString("identity", identity);
                                                    } catch (Exception e) {

                                                    }
                                                    editor.commit();
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .create()
                                            .show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("회원 등록에 실패했습니다.")
                                            .setNegativeButton("다시 시도", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {

                            }

                        }
                    };

                    RegisterRequest registerRequest = new RegisterRequest(email, password, nick, identity, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);

                }*/

    }

}
