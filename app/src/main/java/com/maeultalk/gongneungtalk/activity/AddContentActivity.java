package com.maeultalk.gongneungtalk.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.contents.networking.AddContent;
import com.maeultalk.gongneungtalk.contents.networking.AddContentRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class AddContentActivity extends AppCompatActivity {

    String spot_name;
    String spot_code;

    Button button;
    EditText editText;

    CheckBox checkBox;

    private String mCurrentPhotoPath;
    private Uri mCurrentPhotoPathUri;

    String largeImagePath = "";


    // LOG
    private String TAGLOG = "LoG";

    // 서버로 업로드할 파일관련 변수
    public String uploadFilePath;
    public String uploadFileName;
    private int REQ_CODE_PICK_PICTURE = 1;

    // 파일을 업로드 하기 위한 변수 선언
    private int serverResponseCode = 0;


    EditText editText3;

    String url = "http://gongneungtalk.cafe24.com";

    String spot;
    String content;

    //SoftKeyboard softKeyboard;

    ImageView imageView6;
    ImageView imageView8;
    ImageView imageView_gallery;

    /*@Override
    public void onDestroy()
    {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }*/


    TextView textView_nick;
    TextView textView_identity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);

        // 액션바 타이틀 설정
        Intent intent = getIntent();
        spot_name = intent.getStringExtra("spot_name");
        spot_code = intent.getStringExtra("spot_code");
        setTitle("\'" + spot_name + "\'에 글쓰기");

        // UI
        //test_게시버튼
        /*editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setClickable(false);

                // 게시글 등록
                AddContent addContent = new AddContent("http://gongneungtalk.cafe24.com/version_code_1/", spot_name, spot_code, editText.getText().toString(), "test.png");
                addContent.start();
                try{
                    addContent.join();
                    //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    //Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                }

                // 액티비티 종료
                finish();

            }
        });*/


        textView_nick = (TextView) findViewById(R.id.textView_nick);
        textView_identity = (TextView) findViewById(R.id.textView_identity);
        SharedPreferences pref = getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);
        textView_nick.setText(pref.getString("nick", "null"));
        textView_identity.setText(pref.getString("identity", "null"));
        switch (pref.getString("identity", "null")) {
            case "resident":
                textView_identity.setText("주민");
                break;
            case "merchant":
                textView_identity.setText("상인");
                break;
            case "student":
                textView_identity.setText("대학생");
                break;
            case "worker":
                textView_identity.setText("직장인");
                break;
            case "visitor":
                textView_identity.setText("방문자");
                break;
        }

        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                checkBox.setChecked(false);
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        imageView6 = (ImageView) findViewById(R.id.imageView6);
        imageView8 = (ImageView) findViewById(R.id.imageView8);
        imageView_gallery = (ImageView) findViewById(R.id.imageView_gallery);

        editText3 = (EditText) findViewById(R.id.editText3);
        editText3.setHint("\'" + spot_name + "'엔 어떤 일이 있나요?");

        /*layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText3.requestFocus();
            }
        });
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editText3.performLongClick();
                return true;
            }
        });*/

        imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Toast.makeText(AddContentActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        goToCamera();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(AddContentActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.with(AddContentActivity.this)
                        .setPermissionListener(permissionlistener)
                        //.setRationaleTitle(R.string.rationale_title) // 권한 필요 이유
                        //.setRationaleMessage(R.string.rationale_message) // 권한 필요 이유
                        //.setDeniedTitle("Permission denied")
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        //.setGotoSettingButtonText("bla bla")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();
            }
        });

        imageView_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Toast.makeText(AddContentActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        goToGallery();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(AddContentActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.with(AddContentActivity.this)
                        .setPermissionListener(permissionlistener)
                        //.setRationaleTitle(R.string.rationale_title) // 권한 필요 이유
                        //.setRationaleMessage(R.string.rationale_message) // 권한 필요 이유
                        //.setDeniedTitle("Permission denied")
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        //.setGotoSettingButtonText("bla bla")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        });

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    /*case ACTION_MOVE :
                        editText3.
                        return false;*/
                    case ACTION_DOWN:
                        editText3.dispatchTouchEvent(event);
                        //Toast.makeText(getApplicationContext(), "다운", Toast.LENGTH_SHORT).show();
                        return true;
                    case ACTION_UP:
                        editText3.dispatchTouchEvent(event);
                        //Toast.makeText(getApplicationContext(), "업", Toast.LENGTH_SHORT).show();
                        return false;
                    default:
                        return false;
                }


            }
        });

        imageView6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // 진동
        final Vibrator mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final int vibeSec = 10;

        // 글자색 바꾸기
        /*String str = "하나의 텍스트뷰에서 스타일 다르게 적용하기";
        final SpannableStringBuilder sps = new SpannableStringBuilder(str);
        sps.setSpan(new ForegroundColorSpan(Color.parseColor("#0000ff")), 4, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText3.append(sps);*/

        final TextView textView18 = (TextView) findViewById(R.id.textView18);
        final TextView textView19 = (TextView) findViewById(R.id.textView19);
        final TextView textView20 = (TextView) findViewById(R.id.textView20);
        final TextView textView21 = (TextView) findViewById(R.id.textView21);

        textView18.setText("");
        textView19.setText("");
        textView20.setText("");
        textView21.setText("");

        final String text1 = "맛집";
        final String text2 = "돈까스";
        final String text3 = "공부하기_좋은_카페";
        final String text4 = "족발";
        final String text5 = "공릉동";
        final String text6 = "서울과학기술대";
        final String text7 = "봉춘이네";
        final String text8 = "공릉동_주민센터";

        final ToggleButton toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        final ToggleButton toggleButton3 = (ToggleButton) findViewById(R.id.toggleButton3);

        // 소프트 키보드
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
                        Toast.makeText(getApplication(),"키보드 내려감",Toast.LENGTH_SHORT).show();
                        toggleButton2.setVisibility(View.INVISIBLE);
                        toggleButton3.setVisibility(View.INVISIBLE);
                        textView18.setVisibility(View.INVISIBLE);
                        textView19.setVisibility(View.INVISIBLE);
                        textView20.setVisibility(View.INVISIBLE);
                        textView21.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(getApplication(),"키보드 올라감",Toast.LENGTH_SHORT).show();
                        toggleButton2.setVisibility(View.VISIBLE);
                        toggleButton3.setVisibility(View.VISIBLE);
                        textView18.setVisibility(View.VISIBLE);
                        textView19.setVisibility(View.VISIBLE);
                        textView20.setVisibility(View.VISIBLE);
                        textView21.setVisibility(View.VISIBLE);
                    }
                });
            }
        });*/

        toggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test_게시버튼 mVibe.vibrate(vibeSec);
                if (toggleButton2.isChecked()) {
                    toggleButton3.setVisibility(View.GONE);
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    StringBuffer sb = new StringBuffer(et);
                    //sb.insert(selS, selE, "#");
                    sb.replace(selS, selE, "#");
                    editText3.setText(sb.toString());
                    editText3.setSelection(selS + 1);
                    /*editText3.setText(editText3.getText().toString() + "#");
                    editText3.setSelection(editText3.length());*/
                    /*new Thread(new Runnable() {

                        public void run() {
                            new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_POUND);
                        }

                    }).start();*/
                    textView18.setText("#맛집");
                    textView19.setText("#돈까스");
                    textView20.setText("#공부하기_좋은_카페");
                    textView21.setText("#족발");
                } else {
                    toggleButton3.setVisibility(View.VISIBLE);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    if (etS.endsWith("#")) {
                        // '#' 삭제
                        editText3.setText(etS.substring(0, etS.length() - 1) + etE);
                        editText3.setSelection(etS.length() - 1);
                    } else {
                        editText3.setText(etS + " " + etE);
                        editText3.setSelection(etS.length() + 1);
                    }
                }
            }
        });
        toggleButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test_게시버튼 mVibe.vibrate(vibeSec);
                if (toggleButton3.isChecked()) {
                    //toggleButton2.setVisibility(View.GONE);
                    toggleButton2.setEnabled(false);
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    StringBuffer sb = new StringBuffer(et);
                    //sb.insert(selS, selE, "#");
                    sb.replace(selS, selE, "@");
                    editText3.setText(sb.toString());
                    editText3.setSelection(selS + 1);
                    textView18.setText("@공릉동");
                    textView19.setText("@서울과학기술대");
                    textView20.setText("@봉춘이네");
                    textView21.setText("@공릉동_주민센터");
                } else {
                    //toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton2.setEnabled(true);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    if (etS.endsWith("@")) {
                        // '#' 삭제
                        editText3.setText(etS.substring(0, etS.length() - 1) + etE);
                        editText3.setSelection(etS.length() - 1);
                    } else {
                        editText3.setText(etS + " " + etE);
                        editText3.setSelection(etS.length() + 1);
                    }
                }
            }
        });

        textView18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test_게시버튼 mVibe.vibrate(vibeSec);
                if (toggleButton2.isChecked()) {
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    editText3.setText(etS + text1 + " " + etE);
                    editText3.setSelection((etS + text1 + " ").length());
                    toggleButton2.setChecked(false);

                    toggleButton3.setVisibility(View.VISIBLE);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                } else {
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    editText3.setText(etS + text5 + " " + etE);
                    editText3.setSelection((etS + text5 + " ").length());
                    toggleButton3.setChecked(false);

                    //toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton2.setEnabled(true);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                }
            }
        });
        textView19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test_게시버튼 mVibe.vibrate(vibeSec);
                if (toggleButton2.isChecked()) {
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    editText3.setText(etS + text2 + " " + etE);
                    editText3.setSelection((etS + text2 + " ").length());
                    toggleButton2.setChecked(false);

                    toggleButton3.setVisibility(View.VISIBLE);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                } else {
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    editText3.setText(etS + text6 + " " + etE);
                    editText3.setSelection((etS + text6 + " ").length());
                    toggleButton3.setChecked(false);

                    //toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton2.setEnabled(true);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                }
            }
        });
        textView20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test_게시버튼 mVibe.vibrate(vibeSec);
                if (toggleButton2.isChecked()) {
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    editText3.setText(etS + text3 + " " + etE);
                    editText3.setSelection((etS + text3 + " ").length());
                    toggleButton2.setChecked(false);

                    toggleButton3.setVisibility(View.VISIBLE);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                } else {
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    editText3.setText(etS + text7 + " " + etE);
                    editText3.setSelection((etS + text7 + " ").length());
                    toggleButton3.setChecked(false);

                    //toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton2.setEnabled(true);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                }
            }
        });
        textView21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test_게시버튼 mVibe.vibrate(vibeSec);
                if (toggleButton2.isChecked()) {
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    editText3.setText(etS + text4 + " " + etE);
                    editText3.setSelection((etS + text4 + " ").length());
                    toggleButton2.setChecked(false);

                    toggleButton3.setVisibility(View.VISIBLE);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                } else {
                    int selS = editText3.getSelectionStart();
                    int selE = editText3.getSelectionEnd();
                    String et = editText3.getText().toString();
                    String etS = et.substring(0, selS);
                    String etE = et.substring(selS, et.length());
                    editText3.setText(etS + text8 + " " + etE);
                    editText3.setSelection((etS + text8 + " ").length());
                    toggleButton3.setChecked(false);

                    //toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton2.setEnabled(true);
                    textView18.setText("");
                    textView19.setText("");
                    textView20.setText("");
                    textView21.setText("");
                }
            }
        });

        //editText3.setMovementMethod (null);

        /*Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                content = editText3.getText().toString();

                if(TextUtils.isEmpty(content)) {
                    Toast.makeText(getApplicationContext(), "글 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    AddSpotContent addSpotContent = new AddSpotContent(url, spot, content);
                    addSpotContent.start();
                    try{
                        addSpotContent.join();
                        //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                    }


                    ArrayList<int[]> positionStartEnd = new ArrayList<int[]>();
                    positionStartEnd = getSpans(content, '#');
                    int numOfHashTags = positionStartEnd.size();

                    String[] hashTags = new String[numOfHashTags];
                    AddHashTag addHashTag;
                    for(int i=0; i<numOfHashTags; i++) {
                        hashTags[i] = content.subSequence(positionStartEnd.get(i)[0]+1, positionStartEnd.get(i)[1]).toString();

                        //Toast.makeText(getApplicationContext(), hashTags[i], Toast.LENGTH_SHORT).show();

                        addHashTag = new AddHashTag(url, spot, content, hashTags[i]);
                        addHashTag.start();
                        try{
                            addHashTag.join();
                            //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    }


                    finish();
                    Toast.makeText(getApplicationContext(), "게시물 등록 성공", Toast.LENGTH_SHORT).show();
                }

                *//*Intent intent = new Intent();
                intent.putExtra("result_msg", "결과가 넘어간다 얍!");
                setResult(RESULT_OK, intent);
                finish();*//*
            }
        });*/


    }


    void goToCamera() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            String BX1 = android.os.Build.MANUFACTURER;

            if (BX1.equalsIgnoreCase("samsung")) {
                //Toast.makeText(getApplicationContext(), "Device man"+BX1, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 5);

            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {

                    }
                    if (photoFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                        mCurrentPhotoPathUri = photoUri;
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, 0);
                    }
                }
            }


        }
    }

    void goToGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return /*"file://" + */cursor.getString(column_index);
    }

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/GongneungTalk/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        //File storageDir = new File(Environment.getExternalStorageDirectory() + "/path/");
        File image = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/GongneungTalk/" + imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = /*"file://" + */image.getAbsolutePath();
        return image;
    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Glide.with(getApplicationContext()).load(mCurrentPhotoPath).into(imageView6);
                //textView.setText(mCurrentPhotoPath);
                //String path = getPath(mCurrentPhotoPathUri);
                String name = getName(mCurrentPhotoPathUri);
                uploadFilePath = mCurrentPhotoPath;
                uploadFileName = name;
            } else if (requestCode == 1) {
                Glide.with(getApplicationContext()).load(getRealPathFromURI(data.getData())).into(imageView6);
                //textView.setText(getRealPathFromURI(data.getData()));
                Uri uri = data.getData();
                String path = getPath(uri);
                String name = getName(uri);
                uploadFilePath = path;
                uploadFileName = name;
            } else if (requestCode == 5) {
                Glide.with(getApplicationContext()).load(mCurrentPhotoPath).into(imageView6);
                //textView.setText(mCurrentPhotoPath);
                //String path = getPath(mCurrentPhotoPathUri);
                String name = getName(mCurrentPhotoPathUri);
                uploadFilePath = mCurrentPhotoPath;
                uploadFileName = name;
            }
        }

    }

    // 실제 경로 찾기

    private String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }


    // 파일명 찾기

    private String getName(Uri uri) {

        /*String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        return imgName;*/

        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;


        /*String[] projection = {MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA};

        String fileSort = MediaStore.Images.ImageColumns._ID + " DESC";

        //Cursor cursor = managedQuery(uri, projection, null, null, null);
        Cursor cursor = getContentResolver().query(uri, projection, null, null, fileSort);

        try {
            cursor.moveToFirst();
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            largeImagePath = cursor.getString(column_index);
        } finally {
            cursor.close();
        }

        return largeImagePath;*/

    }


    // uri 아이디 찾기

    private String getUriId(Uri uri) {

        String[] projection = {MediaStore.Images.ImageColumns._ID};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }


    // ============================== 사진을 서버에 전송하기 위한 스레드 ========================


    private class UploadImageToServer extends AsyncTask<String, String, String> {

        ProgressDialog mProgressDialog;

        String fileName = uploadFileName;

        HttpURLConnection conn = null;

        DataOutputStream dos = null;

        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 1 * 10240 * 10240;

        File sourceFile = new File(uploadFilePath);


        @Override

        protected void onPreExecute() {

            // Create a progressdialog

            mProgressDialog = new ProgressDialog(AddContentActivity.this);

            mProgressDialog.setTitle("Loading...");

            mProgressDialog.setMessage("Image uploading...");

            mProgressDialog.setCanceledOnTouchOutside(false);

            mProgressDialog.setIndeterminate(false);

            mProgressDialog.show();

        }


        @Override

        protected String doInBackground(String... serverUrl) {

            if (!sourceFile.isFile()) {

                runOnUiThread(new Runnable() {

                    public void run() {

                        Log.i(TAGLOG, "[UploadImageToServer] Source File not exist :" + uploadFilePath);

                    }

                });

                return null;

            } else {

                try {

                    // open a URL connection to the Servlet

                    FileInputStream fileInputStream = new FileInputStream(sourceFile);

                    URL url = new URL(serverUrl[0]);


                    // Open a HTTP  connection to  the URL

                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true); // Allow Inputs

                    conn.setDoOutput(true); // Allow Outputs

                    conn.setUseCaches(false); // Don't use a Cached Copy

                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Connection", "Keep-Alive");

                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    conn.setRequestProperty("uploaded_file", fileName);

                    Log.i(TAGLOG, "fileName: " + fileName);


                    dos = new DataOutputStream(conn.getOutputStream());


                    // 사용자 이름으로 폴더를 생성하기 위해 사용자 이름을 서버로 전송한다.

                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"data1\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    dos.writeBytes("images");

                    dos.writeBytes(lineEnd);


                    // 이미지 전송

                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);


                    // create a buffer of  maximum size

                    bytesAvailable = fileInputStream.available();


                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    buffer = new byte[bufferSize];


                    // read file and write it into form...

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);

                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);

                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }


                    // send multipart form data necesssary after file data...

                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                    // Responses from the server (code and message)

                    serverResponseCode = conn.getResponseCode();

                    String serverResponseMessage = conn.getResponseMessage();


                    Log.i(TAGLOG, "[UploadImageToServer] HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);


                    if (serverResponseCode == 200) {

                        runOnUiThread(new Runnable() {

                            public void run() {

                                //Toast.makeText(getApplicationContext(), "File Upload Completed", Toast.LENGTH_SHORT).show();

                            }

                        });

                    }

                    //close the streams //

                    fileInputStream.close();

                    dos.flush();

                    dos.close();


                } catch (MalformedURLException ex) {

                    ex.printStackTrace();

                    runOnUiThread(new Runnable() {

                        public void run() {

                            Toast.makeText(getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();

                        }

                    });

                    Log.i(TAGLOG, "[UploadImageToServer] error: " + ex.getMessage(), ex);

                } catch (Exception e) {

                    e.printStackTrace();

                    runOnUiThread(new Runnable() {

                        public void run() {

                            Toast.makeText(getApplicationContext(), "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();

                        }

                    });

                    Log.i(TAGLOG, "[UploadImageToServer] Upload file to server Exception Exception : " + e.getMessage(), e);

                }

                Log.i(TAGLOG, "[UploadImageToServer] Finish");

                return null;

            } // End else block

        }


        @Override
        protected void onPostExecute(String s) {

            SharedPreferences pref = getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);

            // 게시글 등록

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    try {
                        final JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success) {
                            Toast.makeText(getApplicationContext(), "게시물 등록 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "게시물 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {

                    }
                }
            };

            AddContentRequest addContentRequest = new AddContentRequest(spot_name, spot_code, content, uploadFileName, pref.getString("email", "null"), responseListener);
            RequestQueue queue = Volley.newRequestQueue(AddContentActivity.this);
            queue.add(addContentRequest);


            /*
            //AddContent addContent = new AddContent("http://gongneungtalk.cafe24.com/version_code_1/", spot_name, spot_code, content, uploadFileName, pref.getString("email", "null"));
            AddContent addContent = new AddContent("http://gongneungtalk.cafe24.com/version_code_1/", spot_name, spot_code, content, uploadFileName, pref.getString("email", "null"));
            addContent.start();
            try {
                addContent.join();
                //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                //Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
            }

            // 액티비티 종료
            //finish();

            mProgressDialog.dismiss();

            finish();
            Toast.makeText(getApplicationContext(), "게시물 등록 성공", Toast.LENGTH_SHORT).show();*/

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_content_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        item.setEnabled(false);

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            content = editText3.getText().toString();

            if (TextUtils.isEmpty(content)) {
                Toast.makeText(getApplicationContext(), "글 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            } else {

                if (null != imageView6.getDrawable()) {
                    //imageview have image
                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                    uploadimagetoserver.execute("http://gongneungtalk.cafe24.com/ImageUploadToServer.php");
                } else {
                    //imageview have no image
                    SharedPreferences pref = getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);


                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                final JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success) {
                                    Toast.makeText(getApplicationContext(), "게시물 등록 성공", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "게시물 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {

                            }
                        }
                    };

                    AddContentRequest addContentRequest = new AddContentRequest(spot_name, spot_code, content, "null", pref.getString("email", "null"), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(AddContentActivity.this);
                    queue.add(addContentRequest);


                    /*// 게시글 등록
                    AddContent addContent = new AddContent("http://gongneungtalk.cafe24.com/version_code_1/", spot_name, spot_code, content, "null", pref.getString("email", "null"));
                    addContent.start();
                    try {
                        addContent.join();
                        //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        //Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                    Toast.makeText(getApplicationContext(), "게시물 등록 성공", Toast.LENGTH_SHORT).show();*/
                }


                //test_게시버튼
                /*if (uploadFilePath != null) {


                    AddSpotContentWithImg addSpotContentWithImg = new AddSpotContentWithImg(url, spot, content, uploadFileName);
                    addSpotContentWithImg.start();
                    try{
                        addSpotContentWithImg.join();
                        //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                    }


                    ArrayList<int[]> positionStartEnd = new ArrayList<int[]>();
                    positionStartEnd = getSpans(content, '#');
                    int numOfHashTags = positionStartEnd.size();

                    String[] hashTags = new String[numOfHashTags];
                    AddHashTag addHashTag;
                    for(int i=0; i<numOfHashTags; i++) {
                        hashTags[i] = content.subSequence(positionStartEnd.get(i)[0]+1, positionStartEnd.get(i)[1]).toString();

                        //Toast.makeText(getApplicationContext(), hashTags[i], Toast.LENGTH_SHORT).show();

                        addHashTag = new AddHashTag(url, spot, content, hashTags[i]);
                        addHashTag.start();
                        try{
                            addHashTag.join();
                            //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    }



                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();

                    uploadimagetoserver.execute("http://gongneungtalk.cafe24.com/ImageUploadToServer.php");



                } else {


                    AddSpotContent addSpotContent = new AddSpotContent(url, spot, content);
                    addSpotContent.start();
                    try{
                        addSpotContent.join();
                        //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                    }


                    ArrayList<int[]> positionStartEnd = new ArrayList<int[]>();
                    positionStartEnd = getSpans(content, '#');
                    int numOfHashTags = positionStartEnd.size();

                    String[] hashTags = new String[numOfHashTags];
                    AddHashTag addHashTag;
                    for(int i=0; i<numOfHashTags; i++) {
                        hashTags[i] = content.subSequence(positionStartEnd.get(i)[0]+1, positionStartEnd.get(i)[1]).toString();

                        //Toast.makeText(getApplicationContext(), hashTags[i], Toast.LENGTH_SHORT).show();

                        addHashTag = new AddHashTag(url, spot, content, hashTags[i]);
                        addHashTag.start();
                        try{
                            addHashTag.join();
                            //Toast.makeText(getApplicationContext(), (urlConnector.check==0 ? "실패" : "성공"), Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            Toast.makeText(getApplicationContext(), "urlConnector.join(); 또는 check 값 가져오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    }



                    Toast.makeText(getApplicationContext(), "You didn't insert any image", Toast.LENGTH_SHORT).show();

                    finish();
                    Toast.makeText(getApplicationContext(), "게시물 등록 성공", Toast.LENGTH_SHORT).show();

                }*/


            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<int[]> getSpans(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<int[]>();
        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body); // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }
        return spans;
    }


}
