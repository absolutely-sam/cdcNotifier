package com.cdc.developers.cdcnotifier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cdc.developers.cdcnotifier.internetchecker.ConnectionDetector;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sampath.bad.mymaterial.MaterialLoginView;
import com.sampath.bad.mymaterial.MaterialLoginViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LoginActivity extends Activity
{

    protected static final String SECRET = "VkMdosI44OxclHXcHr4Ft.C5QG0cfte.EYPfwTntKByNYs0TbzPka";
    private static final String mailUserName = "cdcappmessage@gmail.com";
    private static final String mailPassword = "cdcadmin";
    private static final String URL = "http://cdcdeveloper2016-001-site1.1tempurl.com/login_mobile.php";
    private static String user, pass;
    private static TextInputLayout sampleLoginUser, samplePassword;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private RequestQueue requestQueue;
    private StringRequest request;
    private MaterialLoginView login;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_login_layout);

        requestQueue = Volley.newRequestQueue(this);

        ImageView aboutUsButton = (ImageView) findViewById(R.id.about_us_btn);

        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialStyledDialog(LoginActivity.this)
                        .setDescription("What can we improve? \n Contact CDC to give us your feedback")
                        .setIcon(R.drawable.linkedin_logo)
                        .withDialogAnimation(true, Duration.SLOW)
                        .setHeaderColor(R.color.dialog_1)
                        .setPositive("Sampath Kumar", new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://in.linkedin.com/in/sampath-kumar-08a96860")));
                            }
                        })
                        .setNegative("Jaya Krishnan Nair", new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://in.linkedin.com/in/jaya-krishnan-nair-071168102")));
                            }
                        })
                        .show();

            }
        });

        login = (MaterialLoginView) findViewById(R.id.login);
        login.setListener(new MaterialLoginViewListener() {
            @Override
            public void onRegister(TextInputLayout registerUser, TextInputLayout registerPass, TextInputLayout registerPassRep) {

                checkInternetPresent();


                if (isInternetPresent)
                {
                    Snackbar.make(login, settingStringColor("Provide valid Email") + "\n" + settingStringColor("Login details will be sent to that mail"), Snackbar.LENGTH_LONG).show();

                    String emailString = registerUser.getEditText().getText().toString();
                    if (emailString.isEmpty()) {
                        registerUser.setError("Email can't be empty");
                        return;
                    }
                    registerUser.setError("");

                    String fullNameString = registerPass.getEditText().getText().toString();
                    if (fullNameString.isEmpty()) {
                        registerPass.setError("Full name can't be empty");
                        return;
                    }
                    registerPass.setError("");

                    String rollNo = registerPassRep.getEditText().getText().toString();
                    if (rollNo.isEmpty()) {
                        registerPassRep.setError("RollNo can't be empty");
                        return;
                    }
                    registerPassRep.setError("");
                    if (rollNo.length() != 10)
                    {
                        registerPassRep.setError("RollNo is invalid");
                        return;
                    }
                    registerPassRep.setError("");

                    if (!emailString.isEmpty() && !fullNameString.isEmpty() && !rollNo.isEmpty())
                    {

                        String email = "cdcappdeveloper@gmail.com";
                        String subject = "Requesting Credentials";

                        String sampleFullName = fullNameString;
                        String sampleRollNo = rollNo;
                        String sampleEmail = emailString;
                        String message = "Full name: " + sampleFullName + "\n" + "Roll No: " + sampleRollNo + "\n" + "Email: " + sampleEmail + "\n";
                        sendMail(email, subject, message);

                    }

                }

            }

            @Override
            public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {


                sampleLoginUser = loginUser;
                samplePassword = loginPass;

                loginUser.getEditText().setHintTextColor(getResources().getColor(R.color.testing_color));

                checkInternetPresent();


                if (isInternetPresent) {


                    user = loginUser.getEditText().getText().toString();
                    if (user.isEmpty()) {
                        loginUser.setError("User name can't be empty");
                        return;
                    }
                    loginUser.setError("");

                    pass = loginPass.getEditText().getText().toString();
                    if (pass.isEmpty()) {
                        loginPass.setError("Password can't be empty");
                        return;
                    }
                    loginPass.setError("");

                    if (!user.isEmpty() && !pass.isEmpty())
                    {
                        new CheckLogin().execute();
                    }

                }

            }
        });

    }


    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("hitam.org", "CDC Android App"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUserName, mailPassword);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkInternetPresent();
    }

    public void checkInternetPresent()
    {
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == false)
        {

            Snackbar.make(login, settingStringColor("Check your Internet Connection"), Snackbar.LENGTH_LONG).show();
        }
    }

    private SpannableStringBuilder settingStringColor(String s) {
        SpannableStringBuilder builder1 = new SpannableStringBuilder();
        String red1 = s;
        SpannableString redSpannable1 = new SpannableString(red1);
        redSpannable1.setSpan(new ForegroundColorSpan(Color.WHITE), 0, red1.length(), 0);
        builder1.append(redSpannable1);

        return builder1;
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this, "", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Snackbar.make(login, settingStringColor("Contact CDC for Login Details"), Snackbar.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class CheckLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(LoginActivity.this, "", "", false, false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String userNameFinal = user;
            final String passWordFinal = pass;

            request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loading.dismiss();

                    try {
                        JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.names().get(0).equals("result")) {
                            if (jsonObject.getString("result").equals("1")) {
                                Snackbar.make(login, settingStringColor("User name does not exist"), Snackbar.LENGTH_LONG).show();
                                sampleLoginUser.setError("User name does not exist");
                            } else if (jsonObject.getString("result").equals("2")) {
                                Snackbar.make(login, settingStringColor("Password incorrect"), Snackbar.LENGTH_LONG).show();
                                samplePassword.setError("Password incorrect");
                            } else if (jsonObject.getString("result").equals("3")) {
                                Toast.makeText(getApplicationContext(), settingStringColor("Login Successful"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                                finish();
                            } else if (jsonObject.getString("result").equals("4")) {
                                Snackbar.make(login, settingStringColor("This app is not meant for ADMIN's"), Snackbar.LENGTH_LONG).show();
                            } else if (jsonObject.getString("result").equals("5")) {
                                Snackbar.make(login, settingStringColor("Network Error"), Snackbar.LENGTH_LONG).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(LoginActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("user", userNameFinal);
                    hashMap.put("passkey", passWordFinal);
                    hashMap.put("secret_key", SECRET);
                    return hashMap;
                }
            };

            requestQueue.add(request);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
