package com.maeultalk.gongneungtalk.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.contents.model.ContentModel;
import com.maeultalk.gongneungtalk.contents.networking.LoadContents;
import com.maeultalk.gongneungtalk.contents.setting.RecyclerViewAdapter;
import com.maeultalk.gongneungtalk.contents.setting.RecyclerViewDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SpotActivity extends AppCompatActivity {

    // 레이아웃 관련
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    LinearLayoutManager layoutManager;
    FloatingActionButton fab;

    // 게시글 리스트
    ArrayList<ContentModel> contentModels = new ArrayList<>();
    ArrayList<ContentModel> newContentModels = new ArrayList<>();

    // 한 번에 불러오는 컨텐츠 갯 수
    int THE_NUMBER_OF_INITIAL_CONTENTS = 5; // 처음 로딩되는 컨텐츠 갯 수
    int THE_NUMBER_OF_ADDITIONAL_CONTENTS = 5; // 추가로 로딩되는 컨텐츠 갯 수

    int contentNoOfNextLoad;

    // 어떤 액티비티에서 부르는지(from스팟액티비티)
    boolean callFromSpotActivity = true;

    boolean enableGetContents = true;

    String spot_name;
    String spot_code;

    // FAB 이동
    //float oldXvalue;
    //float oldYvalue;

    @Override
    public void finish() {
        super.finish();
        //activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // 액티비티 전환 애니메이션
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);

        // 액션바 타이틀 스팟이름으로 설정
        Intent intent = getIntent();
        spot_name = intent.getStringExtra("spot_name");
        spot_code = intent.getStringExtra("spot_code");
        setTitle(spot_name);

        // '글쓰기' FAB 셋팅
        //todo FAB 셋팅 및 클릭리스너 연결
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddContentActivity.class);
                intent.putExtra("spot_name", spot_name);
                intent.putExtra("spot_code", spot_code);
                startActivity(intent);
            }
        });
        /*fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int width = ((ViewGroup) view.getParent()).getWidth() - view.getWidth();
                int height = ((ViewGroup) view.getParent()).getHeight() - view.getHeight();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    oldXvalue = motionEvent.getX();
                    oldYvalue = motionEvent.getY();
                    //  Log.i("Tag1", "Action Down X" + event.getX() + "," + event.getY());
                    Log.i("Tag1", "Action Down rX " + motionEvent.getRawX() + "," + motionEvent.getRawY());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    view.setX(motionEvent.getRawX() - oldXvalue);
                    view.setY(motionEvent.getRawY() - (oldYvalue + view.getHeight()));
                    //  Log.i("Tag2", "Action Down " + me.getRawX() + "," + me.getRawY());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (view.getX() > width && view.getY() > height) {
                        view.setX(width);
                        view.setY(height);
                    } else if (view.getX() < 0 && view.getY() > height) {
                        view.setX(0);
                        view.setY(height);
                    } else if (view.getX() > width && view.getY() < 0) {
                        view.setX(width);
                        view.setY(0);
                    } else if (view.getX() < 0 && view.getY() < 0) {
                        view.setX(0);
                        view.setY(0);
                    } else if (view.getX() < 0 || view.getX() > width) {
                        if (view.getX() < 0) {
                            view.setX(0);
                            view.setY(motionEvent.getRawY() - oldYvalue - view.getHeight());
                        } else {
                            view.setX(width);
                            view.setY(motionEvent.getRawY() - oldYvalue - view.getHeight());
                        }
                    } else if (view.getY() < 0 || view.getY() > height) {
                        if (view.getY() < 0) {
                            view.setX(motionEvent.getRawX() - oldXvalue);
                            view.setY(0);
                        } else {
                            view.setX(motionEvent.getRawX() - oldXvalue);
                            view.setY(height);
                        }
                    }
                }
                return true;
            }
        });*/

        // 게시글 데이터 초기화
        initContentsData();

        // Swipe Refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // 게시글 데이터 초기화
                initContentsData();

                // 리사이클러뷰 갱신
                setRecyclerViewAdapterDataChanged();

            }
        });

        // 리사이클러뷰 셋팅
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerViewDecoration(getApplicationContext(), 12));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(enableGetContents==true) {

                    if (!recyclerView.canScrollVertically(1)) {

                        // 추가 게시물 정보 불러오기
                        getContents(THE_NUMBER_OF_ADDITIONAL_CONTENTS, contentNoOfNextLoad, spot_code);
                        if (newContentModels.size() == 0) {
                            //Toast.makeText(SpotActivity.this, "마지막 게시물입니다.", Toast.LENGTH_SHORT).show();
                            recyclerViewAdapter.setLast();
                        } else {
                            enableGetContents = false;
                            //Toast.makeText(SpotActivity.this, "다음 게시물을 불러옵니다.", Toast.LENGTH_SHORT).show();
                            recyclerViewAdapter.setNext();
                            Handler hd = new Handler();
                            hd.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    recyclerViewAdapter.addItem(newContentModels);
                                    enableGetContents = true;
                                }

                            }, 1000);
                            //recyclerViewAdapter.addItem(newContentModels);
                        }

                    }

                }

            }
        });

        // 리사이클러뷰 어댑터 셋팅
        //setRecyclerViewAdapter();

        recyclerViewAdapter = new RecyclerViewAdapter(SpotActivity.this, contentModels, swipeRefreshLayout, true, recyclerView, callFromSpotActivity);
        recyclerViewAdapter.setOnLoadFinishListner(new RecyclerViewAdapter.OnLoadFinishListner() {
            @Override
            public void onLoadFinish() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    // 게시글 데이터 초기화
    void initContentsData() {
        contentModels.clear();
        getContents(THE_NUMBER_OF_INITIAL_CONTENTS, 0, spot_code);
    }

    // 리사이클러뷰 갱신
    void setRecyclerViewAdapterDataChanged() {
        recyclerViewAdapter.setItem(contentModels);
        recyclerViewAdapter.notifyDataSetChanged();
        //recyclerViewAdapter.setStart();
    }

    // 게시물 데이터 로딩 및 자료화
    void getContents(int theNumberOfContents, int contentNoOfNextLoad, String spot_code) {

        // 게시물 로드
        LoadContents loadContents = new LoadContents("http://gongneungtalk.cafe24.com/version_code_1/", theNumberOfContents, contentNoOfNextLoad, spot_code);
        loadContents.start();
        try {
            loadContents.join();
        } catch (InterruptedException e) {

        }

        // JSON -> GSON -> Object -> ArrayList
        try {
            JSONObject jsonObject = new JSONObject(loadContents.getStr());
            JSONArray jsonArray = (JSONArray) jsonObject.get("results");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ContentModel>>() {
            }.getType();
            if (contentNoOfNextLoad == 0) {
                // 첫 로드일 때
                contentModels = gson.fromJson(jsonArray.toString(), listType);
                this.contentNoOfNextLoad = Integer.parseInt(contentModels.get(contentModels.size() - 1).getNo());
            } else {
                // 추가 로드일 때
                newContentModels.clear();
                newContentModels = gson.fromJson(jsonArray.toString(), listType);
                this.contentNoOfNextLoad = Integer.parseInt(newContentModels.get(newContentModels.size() - 1).getNo());
            }
        } catch (Exception e) {

        }

    }

}
