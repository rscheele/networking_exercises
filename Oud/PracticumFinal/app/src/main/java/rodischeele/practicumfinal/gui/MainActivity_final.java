package rodischeele.practicumfinal.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gj_webdev.communicatie.R;
import com.gj_webdev.communicatie.practicum_final.enities.Globals;
import com.gj_webdev.communicatie.practicum_final.enities.StreamEntity;
import com.gj_webdev.communicatie.practicum_final.util.HttpApi;
import com.gj_webdev.communicatie.practicum_final.util.UtilIp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity_final extends Activity {

    private AsyncTask<String, String, Long> task;
    private ListView list;
    private ArrayList<StreamEntity> entitys = new ArrayList<StreamEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_final);
        list = (ListView) findViewById(R.id.listView);
        list.setAdapter(new ArrayAdapter<StreamEntity>(this, android.R.layout.simple_list_item_1, entitys));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StreamEntity entity = entitys.get((int) id);
                Intent intent = new Intent(MainActivity_final.this, ClientActivity.class);
                intent.putExtra("description", entity.getDescription());
                intent.putExtra("ip", entity.getIp());
                startActivity(intent);
            }
        });
        refreshData();
        TextView textView = (TextView) this.findViewById(R.id.ip_field);
        textView.setText(UtilIp.getIPAddress(true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_final, menu);
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
        if (id == R.id.action_refresh) {
            refreshData();
            return true;
        }
        if(id == R.id.action_start){
            startStream();
            return true;
        }
        if(id == R.id.direct_connect){
            directConnect();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void directConnect(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Direct connect");
        alert.setMessage("Ip address:");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value != null && !"".equals(value)) {
                    Intent intent = new Intent(MainActivity_final.this, ClientActivity.class);
                    intent.putExtra("description", value);
                    intent.putExtra("ip", value);
                    startActivity(intent);
                }
            }
        });

        alert.show();
    }

    private boolean refreshData(){
        System.out.println("Refresh Data");
        if(task == null){
            task = new AsyncTask<String, String, Long>() {
                @Override
                protected Long doInBackground(String[] params) {
                    try {
                        String response = HttpApi.get(Globals.URL_LIST);
                        JSONArray array = new JSONArray(response);

                        entitys.clear();
                        for(int i = 0; i < array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            StreamEntity entity = new StreamEntity();
                            entity.setId(object.getInt("id"));
                            entity.setIp(object.getString("ip"));
                            entity.setDescription(object.getString("description"));
                            entitys.add(entity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    task = null;
                    return null;
                }
                @Override
                protected void onPostExecute(Long result) {
                    list.invalidateViews();
                }
            };
            task.execute();
            return true;
        }else{
            System.out.println("already refreshing");
            return false;
        }
    }

    private void startStream(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Start new stream");
        alert.setMessage("Start your stream and share it with the World!");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value != null && !"".equals(value)) {
                    registerStream(value);
                } else {
                    Toast.makeText(MainActivity_final.this, "You must enter a short description", Toast.LENGTH_SHORT).show();
                    startStream();
                }
            }
        });

        alert.show();

    }

    private void registerStream(final String name){
        AsyncTask<String, String, Exception> registerTask = new AsyncTask<String, String, Exception>() {
            @Override
            protected Exception doInBackground(String[] params) {
                try {
                    Globals.SERVER_MANAGER.register(name);
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Exception result) {
                task = null;
                if(result == null){
                    Intent intent = new Intent(MainActivity_final.this, ServerActivity.class);
                    intent.putExtra("description", name);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity_final.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    startStream();
                }
            }
        };
        registerTask.execute();
    }
}
