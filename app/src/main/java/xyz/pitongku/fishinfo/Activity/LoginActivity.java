package xyz.pitongku.fishinfo.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import xyz.pitongku.fishinfo.MainActivity;
import xyz.pitongku.fishinfo.R;

public class LoginActivity extends AppCompatActivity {

    private EditText username, passwd;
    SharedPreferences mUser;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUser = getSharedPreferences("User", Context.MODE_PRIVATE);
        editor = mUser.edit();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Login Admin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.inputUsernameEt);
        passwd = findViewById(R.id.inputPasswdEt);
    }

    public void loginBtnCLicked(View view) {
        final String usernameTxt = username.getText().toString().trim();
        final String passwdTxt = passwd.getText().toString().trim();
        String URL_DATA = "http://mobile.fishinfojatim.net/login";

        final String md5Pass = convertToMd5(passwdTxt);
        if (usernameTxt.matches("") || passwdTxt.matches("")) {
            Toast.makeText(this, "Harap mengisi semua inputan untuk melanjutkan", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sedang masuk...");
            progressDialog.show();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL_DATA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            Log.d("response", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String nama = jsonObject.getJSONObject("login").getString("nama_surveyor");
                                String kodeKota = jsonObject.getJSONObject("login").getString("kode_kota");
                                String tipe = jsonObject.getJSONObject("login").getString("tipe_surveyor");
                                boolean logged = Boolean.valueOf(jsonObject.getJSONObject("login").getString("is_logged_in"));

                                if (logged) {
                                    if (tipe.equals("pasar")) {
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.putExtra("NAMA_SURVEYOR", nama);
                                        intent.putExtra("KODE_SURVEYOR", usernameTxt);
                                        intent.putExtra("KODE_KOTA", kodeKota);
                                        intent.putExtra("IS_PASAR", true);
                                        startActivity(intent);

                                        editor.putString("username", usernameTxt);
                                        editor.putString("passwd", passwdTxt);
                                        editor.putString("nama", nama);
                                        editor.putString("kodeKota", kodeKota);
                                        editor.apply();
                                        finish();
                                    } else if (tipe.equals("non konsumsi")) {
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.putExtra("NAMA_SURVEYOR", nama);
                                        intent.putExtra("KODE_SURVEYOR", usernameTxt);
                                        intent.putExtra("KODE_KOTA", kodeKota);
                                        intent.putExtra("IS_PASAR", false);
                                        startActivity(intent);

                                        editor.putString("username", usernameTxt);
                                        editor.putString("passwd", passwdTxt);
                                        editor.putString("nama", nama);
                                        editor.putString("kodeKota", kodeKota);
                                        editor.apply();
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Anda bukan admin pasar atau logistik", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Username dan password tidak cocok", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Username & password tidak terdaftar", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", usernameTxt);
                    params.put("password", passwdTxt);

                    return params;
                }
            };
            Volley.newRequestQueue(this).add(postRequest);
        }


    }

    private String convertToMd5(String passwdTxt) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(passwdTxt.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
        return true;
    }
}
