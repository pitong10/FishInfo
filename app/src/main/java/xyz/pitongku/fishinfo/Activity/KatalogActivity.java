package xyz.pitongku.fishinfo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import java.util.ArrayList;

import xyz.pitongku.fishinfo.MainActivity;
import xyz.pitongku.fishinfo.R;
import xyz.pitongku.fishinfo.adapters.HargaIkanAdapter;
import xyz.pitongku.fishinfo.model.HargaIkan;

public class KatalogActivity extends AppCompatActivity {

    public static final String array = "harga";
    private static final String URL = "http://mobile.fishinfojatim.net/harga_konsumsi/harga?id_pasar=371&kode_ikan=292";
    private RecyclerView recyclerIkan;
    private RecyclerView.Adapter adapter;
    private ArrayList<HargaIkan> listIkan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);

        recyclerIkan = findViewById(R.id.rvDataIkan);
        recyclerIkan.setHasFixedSize(true);
        recyclerIkan.setLayoutManager(new LinearLayoutManager(this));

        listIkan = new ArrayList<>();

        loadRecyclerViewData();
        Toast.makeText(this, ""+listIkan.size(), Toast.LENGTH_SHORT).show();


    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(array);

                            for(int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject data = jsonArray.getJSONObject(i);
                                String volume = "0";
                                if(!data.getString("volume").isEmpty() && !data.getString("volume").equals("null"))
                                    volume = data.getString("volume");

                                HargaIkan ikan = new HargaIkan(
                                    data.getString("nama_ikan"),
                                    data.getString("nama_pedagang_pasar"),
                                    data.getString("harga"),
                                    data.getString("nama_pasar"),
                                    volume
                                );

                                listIkan.add(ikan);
                            }
                            adapter = new HargaIkanAdapter(listIkan, getApplicationContext());
                            recyclerIkan.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(KatalogActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void btnBackClicked(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
