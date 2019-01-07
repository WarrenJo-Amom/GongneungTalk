package com.maeultalk.gongneungtalk.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maeultalk.gongneungtalk.R;
import com.maeultalk.gongneungtalk.contents.model.SpotModel;
import com.maeultalk.gongneungtalk.contents.networking.LoadSpots;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SearchSpotActivity extends AppCompatActivity {

    ArrayList<SpotModel> spotModels = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_spot);

        listView = (ListView)findViewById(R.id.listView);

        setTitle("장소 검색");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search_spot, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("장소 검색");
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        //searchView.requestFocusFromTouch();
        getSpots("");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                getSpots(s);
                //todo setSpotsToListView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                getSpots(s);
                return false;
            }
        });

        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    // 게시물 데이터 로딩 및 자료화
    void getSpots(String spot_name) {

        // 게시물 로드
        LoadSpots loadSpots = new LoadSpots("http://gongneungtalk.cafe24.com/version_code_1/", spot_name);
        loadSpots.start();
        try {
            loadSpots.join();
        } catch (InterruptedException e) {

        }

        // JSON -> GSON -> Object -> ArrayList
        try {
            //Toast.makeText(getApplicationContext(), loadSpots.getStr(), Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject(loadSpots.getStr());
            JSONArray jsonArray = (JSONArray) jsonObject.get("results");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<SpotModel>>() {
            }.getType();
            spotModels = gson.fromJson(jsonArray.toString(), listType);
            //Toast.makeText(getApplicationContext(), contentModels.get(0).getSpot_code(), Toast.LENGTH_SHORT).show(); // 스팟코드 확인 테스트

            String[] array = new String[spotModels.size()];
            for(int i = 0; i< spotModels.size() ; i++) {
                array[i] = spotModels.get(i).getSpot();
            }

            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(SearchSpotActivity.this, SpotActivity.class);
                    intent.putExtra("spot_name", spotModels.get(i).getSpot());
                    intent.putExtra("spot_code", spotModels.get(i).getSpot_code());
                    startActivity(intent);
                }
            });

        } catch (Exception e) {

        }

    }

}
