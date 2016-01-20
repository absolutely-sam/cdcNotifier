package com.cdc.developers.cdcnotifier;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.cdc.developers.cdcnotifier.internetchecker.ConnectionDetector;

public class LoginActivity extends AppCompatActivity
{

    private EditText email, password;
    private Button sign_in_register;
    private RequestQueue requestQueue;
    private static final String URL = "http://jayakrishnan1236-001-site1.1tempurl.com/sampath_login_test.php";
    private StringRequest request;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        sign_in_register = (Button) findViewById(R.id.sign_in_register);

        requestQueue = Volley.newRequestQueue(this);

        sign_in_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetPresent();

                if(isInternetPresent)
                {
                    if(email.getText().toString().equals("") && password.getText().toString().equals(""))
                    {
                        Toast.makeText(LoginActivity.this, "Fields Empty", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        checkLogin();
                    }
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        checkInternetPresent();
    }

    private void checkLogin()
    {
        loading = ProgressDialog.show(this,"Please wait :~) ...","Signing In...",false,false);

        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                loading.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if(jsonObject.names().get(0).equals("result"))
                    {
                        if(jsonObject.getString("result").equals("1"))
                        {
                            Toast.makeText(LoginActivity.this, "user name not valid", Toast.LENGTH_SHORT).show();
                        } else if(jsonObject.getString("result").equals("2"))
                        {
                            Toast.makeText(LoginActivity.this, "password incorrect", Toast.LENGTH_SHORT).show();
                        } else if(jsonObject.getString("result").equals("3"))
                        {
                            Toast.makeText(LoginActivity.this, "valid user", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                            finish();
                        } else if(jsonObject.getString("result").equals("4"))
                        {
                            Toast.makeText(LoginActivity.this, "valid admin user", Toast.LENGTH_SHORT).show();
                        } else if(jsonObject.getString("result").equals("5"))
                        {
                            Toast.makeText(LoginActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this,error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("user",email.getText().toString());
                hashMap.put("passkey",password.getText().toString());
                return hashMap;
            }
        };

        requestQueue.add(request);
    }

    public void checkInternetPresent()
    {
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent == false)
        {
            showAlertDialog(LoginActivity.this, "No Internet Connection",
                    "Check your Internet Connection.");
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }


}
