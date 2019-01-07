package com.maeultalk.gongneungtalk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.contents.networking.MarketVersionChecker;
import com.maeultalk.gongneungtalk.contents.setting.RecyclerViewDecoration;
import com.maeultalk.gongneungtalk.contents.model.ContentModel;
import com.maeultalk.gongneungtalk.contents.networking.LoadContents;
import com.maeultalk.gongneungtalk.contents.setting.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    AlertDialog.Builder alt_bld;
    String store_version;
    String device_version = null;

    String marketVersion, verSion;
    AlertDialog.Builder mDialog;

    // 레이아웃 관련
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    LinearLayoutManager layoutManager;
    FloatingActionButton fab;
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    // 게시글 리스트
    ArrayList<ContentModel> contentModels = new ArrayList<>();
    ArrayList<ContentModel> newContentModels = new ArrayList<>();

    // 한 번에 불러오는 컨텐츠 갯 수
    int THE_NUMBER_OF_INITIAL_CONTENTS = 5; // 처음 로딩되는 컨텐츠 갯 수
    int THE_NUMBER_OF_ADDITIONAL_CONTENTS = 5; // 추가로 로딩되는 컨텐츠 갯 수

    int contentNoOfNextLoad;

    boolean enableGetContents = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        mCollapsingToolbarLayout.setTitleEnabled(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.simbol_icon2);
        /*toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });*/

        View logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
                recyclerView.smoothScrollToPosition(0);
            }
        });

        //파싱호출
        mDialog = new AlertDialog.Builder(this);
        new MarketVersion().execute();

        /*alt_bld = new AlertDialog.Builder(this);
        new MarketVersion().execute();*/

        /*alt_bld = new AlertDialog.Builder(this);

        // 버전 정보 체크
        String store_version = MarketVersionChecker.getMarketVersion(getPackageName());
        String device_version = null;
        try {
            device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (store_version.compareTo(device_version) > 0) {
            // 업데이트 필요
            alt_bld.setMessage("새로운 업데이트가 있습니다.")
                    .setCancelable(false)
                    .setPositiveButton("업데이트",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                                    marketLaunch.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                                    startActivity(marketLaunch);
                                    finish();
                                }
                            })
                    .setNegativeButton("나중에",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
            AlertDialog alert = alt_bld.create();
            alert.setTitle("안내");
            alert.show();
        }*/

        // 인트로 띄우기
        //startActivity(new Intent(getApplicationContext(), IntroActivity.class));

        // '글쓰기' FAB 셋팅
        //todo FAB 셋팅 및 클릭리스너 연결

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
        layoutManager = new LinearLayoutManager(getApplicationContext())/* {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        }*/;
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerViewDecoration(getApplicationContext(), 12));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(enableGetContents==true) {

                    if (!recyclerView.canScrollVertically(1)) {

                        // 추가 게시물 정보 불러오기
                        getContents(THE_NUMBER_OF_ADDITIONAL_CONTENTS, contentNoOfNextLoad);
                        if (newContentModels.size() == 0) {
                            //Toast.makeText(MainActivity.this, "마지막 게시물입니다.", Toast.LENGTH_SHORT).show();
                            //recyclerView.smoothScrollToPosition(0);
                            recyclerViewAdapter.setLast();
                        } else {
                            enableGetContents = false;
                            //Toast.makeText(MainActivity.this, "다음 게시물을 불러옵니다.", Toast.LENGTH_SHORT).show();
                            recyclerViewAdapter.setNext();
                            //recyclerView.setNestedScrollingEnabled(false);
                            Handler hd = new Handler();
                            hd.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    recyclerViewAdapter.addItem(newContentModels);
                                    //recyclerView.setNestedScrollingEnabled(true);
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

        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, contentModels, swipeRefreshLayout, true, recyclerView, false);
        recyclerViewAdapter.setOnLoadFinishListner(new RecyclerViewAdapter.OnLoadFinishListner() {
            @Override
            public void onLoadFinish() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);

        // '글쓰기' FAB 셋팅
        //todo FAB 셋팅 및 클릭리스너 연결
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 공릉동 또는 장소 검색 후 글쓰기
                //Toast.makeText(getApplicationContext(), "전체글 쓰기는 준비중", Toast.LENGTH_SHORT).show();
                Intent intent_searchSpot = new Intent(getApplicationContext(), SearchSpotActivity.class);
                startActivity(intent_searchSpot);
            }
        });
        /*// '장소 생성' FAB 셋팅
        //todo FAB 셋팅 및 클릭리스너 연결
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddSpotActivity.class);
                startActivity(intent);
            }
        });*/

    }

    public static View getToolbarLogoIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    private class MarketVersion extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            /*store_version = MarketVersionChecker.getMarketVersionFast(getPackageName());

            try {
                device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }*/

            try {
                Document doc = Jsoup
                        .connect(
                                "https://play.google.com/store/apps/details?id=" + getPackageName() )
                        .get();
                Elements Version = doc.select(".htlgb");

                /*for (Element v : Version) {
                    if (v.attr("itemprop").equals("softwareVersion")) {
                        marketVersion = v.text();
                    }
                }
                return marketVersion;*/
                for (int i = 0; i < 5 ; i++) {
                    marketVersion = Version.get(i).text();
                    if (Pattern.matches("^[0-9]{1}.[0-9]{1}.[0-9]{1}$", marketVersion)) {
                        break;

                    }
                }
                return marketVersion;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            /*if (store_version.compareTo(device_version) > 0) {
                // 업데이트 필요
                alt_bld.setMessage("새로운 업데이트가 있습니다.")
                        .setCancelable(false)
                        .setPositiveButton("업데이트",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                                        marketLaunch.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                                        startActivity(marketLaunch);
                                        finish();
                                    }
                                })
                        .setNegativeButton("나중에",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                AlertDialog alert = alt_bld.create();
                alert.setTitle("안내");
                alert.show();
            }*/

            PackageInfo pi = null;
            try {
                pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            verSion = pi.versionName;
            marketVersion = result;

            if (!verSion.equals(marketVersion)) {
                mDialog.setMessage("새로운 업데이트가 있습니다.")
                        .setCancelable(false)
                        .setPositiveButton("업데이트",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Intent marketLaunch = new Intent(
                                                Intent.ACTION_VIEW);
                                        marketLaunch.setData(Uri
                                                .parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                                        startActivity(marketLaunch);
                                        finish();
                                    }
                                })
                        .setNegativeButton("나중에",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                AlertDialog alert = mDialog.create();
                alert.setTitle("안내");
                alert.show();
            }

            super.onPostExecute(result);
        }
    }

    // 게시글 데이터 초기화
    void initContentsData() {
        contentModels.clear();
        getContents(THE_NUMBER_OF_INITIAL_CONTENTS, 0);
    }

    // 리사이클러뷰 갱신
    void setRecyclerViewAdapterDataChanged() {
        recyclerViewAdapter.setItem(contentModels);
        recyclerViewAdapter.notifyDataSetChanged();
        //recyclerViewAdapter.setStart();
    }

    // 게시물 데이터 로딩 및 자료화
    void getContents(int theNumberOfContents, int contentNoOfNextLoad) {

        // 게시물 로드
        LoadContents loadContents = new LoadContents("http://gongneungtalk.cafe24.com/version_code_1/", theNumberOfContents, contentNoOfNextLoad);
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
                //Toast.makeText(getApplicationContext(), contentModels.get(0).getSpot_code(), Toast.LENGTH_SHORT).show(); // 스팟코드 확인 테스트
            } else {
                // 추가 로드일 때
                newContentModels.clear();
                newContentModels = gson.fromJson(jsonArray.toString(), listType);
                this.contentNoOfNextLoad = Integer.parseInt(newContentModels.get(newContentModels.size() - 1).getNo());
            }
        } catch (Exception e) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent_searchSpot = new Intent(getApplicationContext(), SearchSpotActivity.class);
                startActivity(intent_searchSpot);
                return true;
            case R.id.action_add_spot:
                Intent intent_addSpot = new Intent(getApplicationContext(), AddSpotActivity.class);
                startActivity(intent_addSpot);
                return true;
            case R.id.action_settings:
                // SharedPreferences 삭제
                SharedPreferences pref = getSharedPreferences("GongneungTalk_UserInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                // 로그인 액티비티로
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_spot:
                // 스팟액티비티2로 이동
                Intent intent_spot = new Intent(MainActivity.this, SpotActivity2.class);
                startActivity(intent_spot);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
