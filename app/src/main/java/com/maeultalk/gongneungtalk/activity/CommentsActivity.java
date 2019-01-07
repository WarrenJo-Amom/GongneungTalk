package com.maeultalk.gongneungtalk.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.contents.model.CommentModel;
import com.maeultalk.gongneungtalk.contents.model.SpotModel;
import com.maeultalk.gongneungtalk.contents.networking.AddComment;
import com.maeultalk.gongneungtalk.contents.networking.AddContent;
import com.maeultalk.gongneungtalk.contents.networking.LoadComments;
import com.maeultalk.gongneungtalk.util.SoftKeyboard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    LinearLayout layout;

    ArrayList<CommentModel> commentModels = new ArrayList<>();
    ListView listView;
    ArrayAdapter<String> adapter;
    EditText editText_comment;
    Button button_comment;

    String no;
    String content;

    SoftKeyboard softKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setTitle("댓글");

        // 게시물 번호
        Intent intent = getIntent();
        //Toast.makeText(getApplicationContext(), intent.getStringExtra("no"), Toast.LENGTH_SHORT).show();

        layout = (LinearLayout) findViewById(R.id.layout);

        /*InputMethodManager controlManager = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(layout, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged()
        {
            @Override
            public void onSoftKeyboardHide()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //키보드 내려왔을때
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //키보드 올라왔을때
                        listView.setSelection(adapter.getCount() - 1);
                    }
                });
            }
        });*/

        listView = (ListView) findViewById(R.id.listView_comments) ;

        no = intent.getStringExtra("no");
        content = intent.getStringExtra("content");
        setTitle(content);

        getComments(no);

        editText_comment = (EditText) findViewById(R.id.editText_comment);
        editText_comment.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if(hasFocus)
                {
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            listView.setSelection(adapter.getCount() - 1);
                        }
                    }, 500);
                }
            }
        });

        button_comment = (Button) findViewById(R.id.button_comment);
        button_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(editText_comment.getText().toString().replace(" ", "").equals(""))) {
                    // 댓글이 있을때,
                    button_comment.setEnabled(false);

                    // 댓글 등록
                    AddComment addComment = new AddComment("http://gongneungtalk.cafe24.com/version_code_1/", no, editText_comment.getText().toString());
                    addComment.start();
                    try{
                        addComment.join();
                        //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        //Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                    }

                    editText_comment.setText("");
                    button_comment.setEnabled(true);

                    getComments(no);

                } else {
                    // 댓글이 없을때,
                    Toast.makeText(getApplicationContext(), "댓글을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    void getComments(String no) {

        // 게시물 로드
        LoadComments loadComments = new LoadComments("http://gongneungtalk.cafe24.com/version_code_1/", no);
        loadComments.start();
        try {
            loadComments.join();
        } catch (InterruptedException e) {

        }

        // JSON -> GSON -> Object -> ArrayList
        try {
            //Toast.makeText(getApplicationContext(), loadSpots.getStr(), Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject(loadComments.getStr());
            JSONArray jsonArray = (JSONArray) jsonObject.get("results");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<CommentModel>>() {
            }.getType();
            commentModels = gson.fromJson(jsonArray.toString(), listType);
            //Toast.makeText(getApplicationContext(), contentModels.get(0).getSpot_code(), Toast.LENGTH_SHORT).show(); // 스팟코드 확인 테스트

            String[] array = new String[commentModels.size()];
            for(int i = 0; i< commentModels.size() ; i++) {
                array[i] = commentModels.get(i).getComment();
            }

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);

            listView.setAdapter(adapter);
            listView.setSelection(adapter.getCount() - 1);

        } catch (Exception e) {

        }

    }
}
