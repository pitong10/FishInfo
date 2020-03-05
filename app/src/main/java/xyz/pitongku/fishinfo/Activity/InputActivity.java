package xyz.pitongku.fishinfo.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.pitongku.fishinfo.R;
import xyz.pitongku.fishinfo.adapters.InputIkanAdapter;
import xyz.pitongku.fishinfo.model.Ikan;
import xyz.pitongku.fishinfo.model.InputIkan;
import xyz.pitongku.fishinfo.model.Pasar;

public class InputActivity extends AppCompatActivity {


    private List<InputIkan> listIkan;
    private List<String> arrayIkan = new ArrayList<>();

    private RecyclerView rvIkan;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    private Spinner namaPasarSp, namaPedagangSp;
    private EditText hargaEt, volumeEt;
    private String kodeKota, kodeSurveyor;
    private InputIkanAdapter adapter;
    private boolean isInsertSuccess;

    private AutoCompleteTextView namaIkanActv;

    private List<Pasar> listPasar;
    private List<String> arraySpinnerPasar = new ArrayList<>();


    private String idPasar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        namaPasarSp = findViewById(R.id.namaPasarSp);
        namaPedagangSp = findViewById(R.id.namaPedagangSp);
        kodeKota = getIntent().getStringExtra("KODE_KOTA");
        kodeSurveyor = getIntent().getStringExtra("KODE_SURVEYOR");

        isInsertSuccess = true;

        listPasar = new ArrayList<>();
        listIkan = new ArrayList<>();

        adapter = new InputIkanAdapter(this);
        rvIkan = findViewById(R.id.rv_ikank);
        rvIkan.setLayoutManager(new LinearLayoutManager(this));
        loadFishList(listIkan);

        setSpinnerItemPasarValue(kodeKota);
        setSuggestion();

        namaPasarSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listPasar != null && listPasar.size() != 0) {
                    idPasar = listPasar.get(position).getId();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setSpinnerItemPasarValue(String filterValue) {
        listPasar.clear();
        arraySpinnerPasar.clear();
        String URL_DATA = "http://mobile.fishinfojatim.net/rest_json/pasar/data_pasar?param=kode_kota&data=" + filterValue;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data pasar...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("pasar");
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
                            namaPasarSp.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(InputActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void inputIkanBtnCLicked(View view) {
        inputIkanForm();
    }

    private void inputIkanForm() {
        dialog = new AlertDialog.Builder(InputActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.input_ikan_layout, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.input);
        dialog.setTitle("Input Harga Ikan");

        namaIkanActv = dialogView.findViewById(R.id.namaIkanActv);
        hargaEt = dialogView.findViewById(R.id.hargaEt);
        volumeEt = dialogView.findViewById(R.id.volumeEt);


        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arrayIkan);
        namaIkanActv.setAdapter(adapter);

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(hargaEt.getText().toString().matches("")||namaIkanActv.getText().toString().matches("") || volumeEt.getText().toString().isEmpty()){
                    inputIkanForm();
                    Toast.makeText(InputActivity.this, "Harap mengisi semua inputan", Toast.LENGTH_SHORT).show();
                }else{
                    getListIkan(namaIkanActv.getText().toString().trim(), hargaEt.getText().toString().trim(), volumeEt.getText().toString().trim(), listIkan);
                    loadFishList(listIkan);
                    dialog.dismiss();
                }

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setSuggestion() {
        arrayIkan.clear();
        String URL_DATA = "";
        URL_DATA = "http://mobile.fishinfojatim.net/rest_json/ikan/ikan?param=&data=";

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


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InputActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getListIkan(final String value, final String harga, final String volume, final List<InputIkan> daftar) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan data...");
        progressDialog.show();
        String URL_DATA = "";
        String valueForLink = value.replaceAll("\\s", "+");
        URL_DATA = "http://mobile.fishinfojatim.net/rest_json/ikan/ikan?param=nama_ikan&data=" + valueForLink;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("ikan");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                InputIkan ikan = new InputIkan(
                                        data.getString("kode_ikan"),
                                        value,
                                        harga,
                                        volume,
                                        data.getString("utama")
                                );
                                daftar.add(ikan);
                                loadFishList(daftar);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InputActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadFishList(List<InputIkan> ikan) {
        InputIkanAdapter myAdapter = new InputIkanAdapter(this);
        myAdapter.setMyFishList(ikan);
        rvIkan.setAdapter(myAdapter);
    }

    public void btnSaveClicked(View view) {
        Log.d("hasil list",""+listIkan.size());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan data...");
        progressDialog.show();
        for(int i = 0 ; i  <listIkan.size() ; i++){
            String idIkan = listIkan.get(i).getId();
            String hargaIkan = listIkan.get(i).getValue();
            insertDataIkan(idIkan, hargaIkan);

            if(i == listIkan.size()-1){
                progressDialog.dismiss();
                Toast.makeText(InputActivity.this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
            }

            if (!isInsertSuccess){
                isInsertSuccess = true;
                Toast.makeText(this, "Terjadi Kesalahan saat menyimpan data", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private void insertDataIkan(final String kodeIkan, final String hargaIkan){
        String URL_DATA = "http://mobile.fishinfojatim.net/harga_konsumsi/survey_harga_konsumsi";



        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_DATA,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        isInsertSuccess = true;
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("kode_surveyor", kodeSurveyor);
                params.put("kode_kota", kodeKota);
                params.put("id_pasar", idPasar);
                params.put("jenis", "0");
                params.put("kode_responden_pasar", null);
                params.put("kode_ikan", kodeIkan);
                params.put("harga", hargaIkan);


                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }
}
