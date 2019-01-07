package com.maeultalk.gongneungtalk.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.contents.networking.AddContent;
import com.maeultalk.gongneungtalk.contents.networking.AddSpot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class AddSpotActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    EditText editText2;
    ImageView imageView4;

    String spot_name;
    String spot_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);

        setTitle("공릉동에 장소 만들기");

        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        editText = (EditText) findViewById(R.id.editText);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(textView.getId()==R.id.editText && i== EditorInfo.IME_ACTION_DONE) {
                    //doConfirm();
                }

                return false;
            }
        });
        editText2 = (EditText) findViewById(R.id.editText2);
        //editText2.setKeyListener(null);
        editText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doConfirm();
            }
        });
    }

    void doConfirm() {
        button.setEnabled(false);

        spot_name = editText.getText().toString();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        spot_code = "spot_" + simpleDateFormat.format(date) + "_" + new Random().nextInt(10000);

        // 게시글 등록
        AddSpot addSpot = new AddSpot("http://gongneungtalk.cafe24.com/version_code_1/", spot_name, spot_code);
        addSpot.start();
        try{
            addSpot.join();
            //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            //Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(AddSpotActivity.this, AddContentActivity.class);
        intent.putExtra("spot_name", spot_name);
        intent.putExtra("spot_code", spot_code);
        startActivity(intent);

        finish();
    }

}
