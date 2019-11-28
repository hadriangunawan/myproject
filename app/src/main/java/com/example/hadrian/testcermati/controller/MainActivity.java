package com.example.hadrian.testcermati.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.example.hadrian.testcermati.R;
import java.util.ArrayList;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    RequestQueue queue;
    ListView listView;
    String before = "a";

    ArrayList<Integer> dataId = new ArrayList<>();
    ArrayList<String> dataLogin = new ArrayList<>();
    ArrayList<String> dataAvatarUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);    //remove label name from Activity

        listView = findViewById(R.id.listview);
        queue = Volley.newRequestQueue(this);

        loadJSON("a");
    }

    private void loadJSON(String text){
        if(text.equals("")){    //if empty text
            text = before;
        }
        String url = "https://api.github.com/search/users?q=" + text;
        //always remove list if you want to search new text
        dataId.clear();
        dataLogin.clear();
        dataAvatarUrl.clear();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("items");

                    //loop data
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject items = jsonArray.getJSONObject(i);

                        int id = items.getInt("id");
                        String login = items.getString("login");
                        String avatarUrl = items.getString("avatar_url");

                        dataId.add(id);
                        dataLogin.add(login);
                        dataAvatarUrl.add(avatarUrl);

                        CustomAdapter ca = new CustomAdapter();
                        listView.setAdapter(ca);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_user);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search Github users");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                loadJSON(s);
                before = s;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                loadJSON(s);
                before = s;
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataId.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.row_user, null);

            TextView loginName = view.findViewById(R.id.loginName);
            ImageView avatar = view.findViewById(R.id.cover);

            loginName.setText(dataLogin.get(i).toString());
            Picasso.with(getApplicationContext()).load(dataAvatarUrl.get(i).toString()).into(avatar);

            return view;
        }
    }
}
