package com.cdc.developers.cdcnotifier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.*;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class WelcomeActivity extends AppCompatActivity
{

    RecyclerView recList;

    LinearLayoutManager llm;
    private Button logout, refresh;

    private ProgressDialog loading;

    public static int arrayDataBaseSize;

    public static String[] arrayDatabase_Id;
    public static String[] arrayDatabase_Subject;
    public static String[] arrayDatabase_Message;
    public static String[] arrayDatabase_Date;
    public static String[] arrayDatabase_Time;

    private RequestQueue requestQueue;
    private static final String URL = "http://jayakrishnan1236-001-site1.1tempurl.com/return_messages.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        requestQueue = Volley.newRequestQueue(this);

        refresh = (Button) findViewById(R.id.refreshBtn);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                finish();
            }
        });

        getData();

    }

    private void getData()
    {
        loading = ProgressDialog.show(this,"Please wait...","Fetching Updates...",false,false);

        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    loading.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray row = jsonObject.getJSONArray(JsonConfig.JSON_ARRAY);
                    arrayDataBaseSize = row.length();
                    if(arrayDataBaseSize >= 1)
                    {

                        arrayDatabase_Id = new String[arrayDataBaseSize];
                        arrayDatabase_Subject = new String[arrayDataBaseSize];
                        arrayDatabase_Message = new String[arrayDataBaseSize];
                        arrayDatabase_Date = new String[arrayDataBaseSize];
                        arrayDatabase_Time = new String[arrayDataBaseSize];

                        for (int i=0; i< arrayDataBaseSize; i++)
                        {
                            JSONObject collegeData = row.getJSONObject(i);
                            arrayDatabase_Id[i] = collegeData.getString(JsonConfig.KEY_ID).trim();
                            arrayDatabase_Subject[i] = collegeData.getString(JsonConfig.KEY_SUBJECT).trim();
                            arrayDatabase_Message[i] = collegeData.getString(JsonConfig.KEY_MESSAGE).trim();
                            arrayDatabase_Date[i] = collegeData.getString(JsonConfig.KEY_DATE).trim();
                        }

                        print();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(WelcomeActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("msg_id","0");
                return hashMap;
            }
        };

        requestQueue.add(request);

    }

    private void print()
    {

        ContactAdapter ca = new ContactAdapter(createList(arrayDataBaseSize));
        recList.setAdapter(ca);
    }


    private List<ContactInfo> createList(int size) {

        List<ContactInfo> result = new ArrayList<ContactInfo>();
        for (int i=0; i < size; i++) {
            ContactInfo ci = new ContactInfo();
            ci.subject = arrayDatabase_Subject[i];
            ci.message = arrayDatabase_Message[i];
            ci.date = arrayDatabase_Date[i];
            ci.time = arrayDatabase_Time[i];
            result.add(ci);
        }

        return result;
    }

}
