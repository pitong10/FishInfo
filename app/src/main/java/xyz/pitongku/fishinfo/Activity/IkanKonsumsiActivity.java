package xyz.pitongku.fishinfo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
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
import java.util.List;

import xyz.pitongku.fishinfo.R;
import xyz.pitongku.fishinfo.model.Ikan;
import xyz.pitongku.fishinfo.model.Kota;
import xyz.pitongku.fishinfo.model.Pasar;

public class IkanKonsumsiActivity extends AppCompatActivity {

    private List<Kota> listKota;
    private List<Pasar> listPasar;
    private List<Ikan> listIkan;

    private String jenis;

    List<String> arraySpinnerKota = new ArrayList<>();
    List<String> arraySpinnerPasar = new ArrayList<>();
    private List<String> arrayIkan = new ArrayList<>();

    private Spinner kotaSp, pasarSp;
    private AutoCompleteTextView ikanActv;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ikan_konsumsi);
        kotaSp = findViewById(R.id.namaKotaSp);
        pasarSp = findViewById(R.id.namaPasarSp);
        ikanActv = findViewById(R.id.ikanActv);
        jenis = getIntent().getStringExtra("JENIS");

        getSupportActionBar().setTitle("Filter Data");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listKota = new ArrayList<>();
        listPasar = new ArrayList<>();
        listIkan = new ArrayList<>();

        setSuggestion();
        setSpinnerItemKotaValue(kotaSp, "kota", "kota", "35");

        kotaSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (listKota != null && listKota.size() != 0)
                    setSpinnerItemPasarValue(pasarSp, "pasar", "data_pasar", listKota.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerItemKotaValue(final Spinner spinner, final String arrayName, String tableName, String filterValue) {
        listKota.clear();
        arraySpinnerKota.clear();
        listKota.add(new Kota("0", "Semua kota"));
        arraySpinnerKota.add("Semua kota");

        String URL_DATA = "http://mobile.fishinfojatim.net/rest_json/kota/kota?param=kode_provinsi&data=" + filterValue;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data " + tableName + "...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(arrayName);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                Kota kota = new Kota(
                                        data.getString("kode_kota"),
                                        data.getString("nama_kota")
                                );
                                arraySpinnerKota.add(data.getString("nama_kota"));
                                listKota.add(kota);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arraySpinnerKota);
                            spinner.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(IkanKonsumsiActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void setSpinnerItemPasarValue(final Spinner spinner, final String arrayName, String tableName, String filterValue) {
        listPasar.clear();
        arraySpinnerPasar.clear();
        listPasar.add(new Pasar("0", "Semua pasar"));
        arraySpinnerPasar.add("Semua pasar");
        String URL_DATA = "http://mobile.fishinfojatim.net/rest_json/pasar/data_pasar?param=kode_kota&data=" + filterValue;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data " + tableName + "...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(arrayName);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                Pasar pasar = new Pasar(
                                        data.getString("id_pasar"),
                                        data.getString("nama_pasar")
                                );
                                arraySpinnerPasar.add(data.getString("nama_pasar"));
                                listPasar.add(pasar);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arraySpinnerPasar);
                            spinner.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(IkanKonsumsiActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void setSuggestion() {
        listIkan.clear();
        arrayIkan.clear();
        String URL_DATA = "http://mobile.fishinfojatim.net/rest_json/ikan/ikan?param=&data=";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("ikan");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                Ikan ikan = new Ikan(

                                        data.getString("kode_ikan"),
                                        data.getString("nama_ikan")
                                );
                                arrayIkan.add(data.getString("nama_ikan"));
                                listIkan.add(ikan);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arrayIkan);
                            ikanActv.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(IkanKonsumsiActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void BtnCariClicked(View view) {
        startActivity(new Intent(this, KatalogActivity.class));
    }

}
