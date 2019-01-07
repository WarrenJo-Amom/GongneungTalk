package com.maeultalk.gongneungtalk.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

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

public class SpotActivity2 extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static String spot_name;
    static String spot_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot2);

        // 액션바 타이틀 스팟이름으로 설정
        Intent intent = getIntent();
        spot_name = intent.getStringExtra("spot_name");
        spot_code = intent.getStringExtra("spot_code");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(spot_name);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spot_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_spot_activity2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public static class TimeLineFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

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

        public TimeLineFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TimeLineFragment newInstance(int sectionNumber) {
            TimeLineFragment fragment = new TimeLineFragment();
            Bundle args = new Bundle();
            /*args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);*/
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

            // '글쓰기' FAB 셋팅
            //todo FAB 셋팅 및 클릭리스너 연결
            fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AddContentActivity.class);
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
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
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
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
            layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new RecyclerViewDecoration(getActivity(), 12));
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

            recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), contentModels, swipeRefreshLayout, true, recyclerView, callFromSpotActivity);
            recyclerViewAdapter.setOnLoadFinishListner(new RecyclerViewAdapter.OnLoadFinishListner() {
                @Override
                public void onLoadFinish() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            recyclerView.setAdapter(recyclerViewAdapter);

            return rootView;
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

    public static class StoreFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public StoreFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static StoreFragment newInstance(int sectionNumber) {
            StoreFragment fragment = new StoreFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_store, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public static class InfoFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public InfoFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static InfoFragment newInstance(int sectionNumber) {
            InfoFragment fragment = new InfoFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);

            switch (position) {
                case 0:
                    return TimeLineFragment.newInstance(position + 1);
                case 1:
                    return StoreFragment.newInstance(position + 1);
                case 2:
                    return InfoFragment.newInstance(position + 1);
                default:
                    return TimeLineFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
