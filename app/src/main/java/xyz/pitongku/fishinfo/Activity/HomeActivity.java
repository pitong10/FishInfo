package xyz.pitongku.fishinfo.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xyz.pitongku.fishinfo.MainActivity;
import xyz.pitongku.fishinfo.R;

public class HomeActivity extends AppCompatActivity {

    private String kodeKota, kodeSurveyor;
    private TextView kotaAdmin;
    private boolean isPasar;
    private Button goBtn;
    SharedPreferences mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        kotaAdmin = findViewById(R.id.kotaAdminTv);
        goBtn = findViewById(R.id.fishPriceBtn);
        View parentLayout = findViewById(android.R.id.content);
        mUser = getSharedPreferences("User", Context.MODE_PRIVATE);

        String namaSurveyor = getIntent().getStringExtra("NAMA_SURVEYOR");
        kodeKota = getIntent().getStringExtra("KODE_KOTA");
        kodeSurveyor = getIntent().getStringExtra("KODE_SURVEYOR");
        isPasar = getIntent().getBooleanExtra("IS_PASAR",true);

        if (isPasar)
            goBtn.setText(R.string.input_harga_ikan);
        else
            goBtn.setText(R.string.input_ketersediaan_ikan);

        Snackbar.make(parentLayout, "Selamat datang "+ namaSurveyor, Snackbar.LENGTH_SHORT).show();
        setNamaKota(kodeKota);
    }

    void setNamaKota(String kode){
        String URL_DATA = "http://mobile.fishinfojatim.net/rest_json/kota/kota?param=kode_kota&data="+kode;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("kota");
                            for(int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject data = jsonArray.getJSONObject(i);
                                kotaAdmin.setText("Admin "+data.getString("nama_kota"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.about){
            Toast.makeText(this, "Go to about activity", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.logout) {
            mUser.edit().clear().apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        return true;
    }

    public void inputBtnClicked(View view) {
        if (isPasar){
            Intent intent = new Intent(this, ListHargaActivity.class);
            intent.putExtra("KODE_KOTA", kodeKota);
            intent.putExtra("KODE_SURVEYOR", kodeSurveyor);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, InputJmlActivity.class);
            intent.putExtra("KODE_KOTA", kodeKota);
            intent.putExtra("KODE_SURVEYOR", kodeSurveyor);
            startActivity(intent);
        }

    }
}
