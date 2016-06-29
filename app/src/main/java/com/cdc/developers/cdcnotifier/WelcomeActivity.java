package com.cdc.developers.cdcnotifier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WelcomeActivity extends Activity
{

    protected static final String SECRET = "VkMdosI44OxclHXcHr4Ft.C5QG0cfte.EYPfwTntKByNYs0TbzPka";
    private static final String URL = "http://cdcdeveloper2016-001-site1.1tempurl.com/return_messages.php";
    public static int arrayDataBaseSize;
    public static String[] arrayDatabase_Id;
    public static String[] arrayDatabase_Subject;
    public static String[] arrayDatabase_Message;
    public static String[] arrayDatabase_Date_Time;
    RecyclerView recList;
    LinearLayoutManager llm;
    private ImageButton refresh;
    private ProgressDialog loading;
    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        recList.getBackground().setAlpha(210);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        requestQueue = Volley.newRequestQueue(this);

        refresh = (ImageButton) findViewById(R.id.refreshBtn);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        getData();

    }

    private void getData()
    {
        new LoadingData().execute();
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
            ci.date = arrayDatabase_Date_Time[i];
            result.add(ci);
        }

        return result;
    }

    private class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(WelcomeActivity.this, "", "refreshing...", false, false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(WelcomeActivity.this, "Successfully refreshed", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loading.dismiss();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray row = jsonObject.getJSONArray(JsonConfig.JSON_ARRAY);
                        arrayDataBaseSize = row.length();
                        if (arrayDataBaseSize >= 1) {

                            arrayDatabase_Id = new String[arrayDataBaseSize];
                            arrayDatabase_Subject = new String[arrayDataBaseSize];
                            arrayDatabase_Message = new String[arrayDataBaseSize];
                            arrayDatabase_Date_Time = new String[arrayDataBaseSize];

                            for (int i = 0; i < arrayDataBaseSize; i++) {
                                JSONObject collegeData = row.getJSONObject(i);
                                arrayDatabase_Id[i] = collegeData.getString(JsonConfig.KEY_ID).trim();
                                arrayDatabase_Subject[i] = collegeData.getString(JsonConfig.KEY_SUBJECT).trim();
                                arrayDatabase_Message[i] = collegeData.getString(JsonConfig.KEY_MESSAGE).trim();
                                arrayDatabase_Date_Time[i] = collegeData.getString(JsonConfig.KEY_DATE_TIME);
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
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("msg_id", "0");
                    hashMap.put("secret_key", SECRET);
                    return hashMap;
                }
            };

            requestQueue.add(request);
            return null;
        }
    }

}
