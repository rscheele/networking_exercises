package com.groepc.opdracht_6_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.groepc.opdracht_6_client.http.Http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected EditText editText;
    protected Button button;
    protected ListView list;
    private ArrayAdapter<Movies> adapter;
    private ArrayList<Movies> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.editText);
        editText.setText("http://192.168.1.100:3000/api/streams/");
        button = (Button) findViewById(R.id.button);
        arrayList = new ArrayList<Movies>();
        Http.getInstance(this.getApplicationContext());

        adapter = new ListViewAdapter(this, arrayList);
        // Here, you set the data in your ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newActivity = new Intent(view.getContext(), Main2Activity.class);
                newActivity.putExtra("movie",arrayList.get(position));
                startActivity(newActivity);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Http http = Http.getInstance();
                    http.getJson(editText.getText().toString(), new Http.JsonResponseListener() {
                        @Override
                        public void getResult(JSONArray json) {
                            try {
                                adapter.clear();

                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject movie = json.getJSONObject(i);


                                    // this line adds the data of your EditText and puts in your array
                                    arrayList.add(new Movies(movie.getString("name"), movie.getString("ip"), movie.getInt("port"), movie.getString("movie")));
                                    // next thing you have to do is check if your adapter has changed
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
