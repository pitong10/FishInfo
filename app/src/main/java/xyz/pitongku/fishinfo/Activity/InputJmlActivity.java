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
import xyz.pitongku.fishinfo.adapters.InputJmlIkanAdapter;
import xyz.pitongku.fishinfo.model.Ikan;
import xyz.pitongku.fishinfo.model.JmlIkan;
import xyz.pitongku.fishinfo.model.Pasar;
import xyz.pitongku.fishinfo.model.Pedagang;

public class InputJmlActivity extends AppCompatActivity {

    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    private AutoCompleteTextView ikanActv;
    private Spinner namaPedagangSp, namaPasarSp;
    private EditText volume;
    private RecyclerView rvIkan;
    private List<Pasar> listPasar = new ArrayList<>();
    private List<String> arraySpinnerPasar = new ArrayList<>();
    private List<Pedagang> listPedagang = new ArrayList<>();
    private List<String> arraySpinnerPedagang = new ArrayList<>();
    private List<JmlIkan> listIkan = new ArrayList<>();
    private List<String> arrayIkan = new ArrayList<>();
    private InputJmlIkanAdapter adapter;

    private String idSurveyor, kodeKota, idPasar, idPedagang;
    private boolean isInsertSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_jml);

        getSupportActionBar().setTitle("Input Ketersediaan");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        namaPasarSp = findViewById(R.id.namaPasarSp);
        namaPedagangSp = findViewById(R.id.namaPedagangSp);
        rvIkan = findViewById(R.id.rv_ikank);

        idSurveyor = getIntent().getStringExtra("KODE_SURVEYOR");
        kodeKota = getIntent().getStringExtra("KODE_KOTA");
        isInsertSuccess = true;

        adapter = new InputJmlIkanAdapter(this);
        rvIkan = findViewById(R.id.rv_ikank);
        rvIkan.setLayoutManager(new LinearLayoutManager(this));
        loadFishList(listIkan);

        setSpinnerItemPasarValue(kodeKota);

        namaPasarSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listPasar != null && listPasar.size() != 0) {
                    setSpinnerItemPedagangValue(listPasar.get(position).getId());
                    idPasar = listPasar.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        namaPedagangSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listPedagang != null && listPedagang.size() != 0) {
                    idPedagang = listPedagang.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setSuggestion();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadFishList(List<JmlIkan> ikan) {
        InputJmlIkanAdapter myAdapter = new InputJmlIkanAdapter(this);
        myAdapter.setMyFishList(ikan);
        rvIkan.setAdapter(myAdapter);
    }

    public void inputJmlIkanBtnCLicked(View view) {
        inputJmlIkanForm();
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void setSpinnerItemPedagangValue(String filterValue) {
        listPedagang.clear();
        arraySpinnerPedagang.clear();
        String URL_DATA = "http://mobile.fishinfojatim.net/pedagang_pasar/pedagang/data_pedagang_pasar?kode_kota=id_pasar&data_kota=" + filterValue + "&kode_jenis=jenis_pedagang&data_jenis=Pasar";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data pedagang...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("pedagang");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                Pedagang pedagang = new Pedagang(
                                        data.getString("kode_responden_pasar"),
                                        data.getString("nama_pedagang_pasar")
                                );
                                arraySpinnerPedagang.add(data.getString("nama_pedagang_pasar"));
                                listPedagang.add(pedagang);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arraySpinnerPedagang);
                            namaPedagangSp.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void inputJmlIkanForm() {
        dialog = new AlertDialog.Builder(InputJmlActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.input_jml_ikan_layout, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.input);
        dialog.setTitle("Input Jumlah Ikan");

        ikanActv = dialogView.findViewById(R.id.namaIkanActv);
        volume = dialogView.findViewById(R.id.volumeEt);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, arrayIkan);
        ikanActv.setAdapter(adapter);

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (volume.getText().toString().matches("") || ikanActv.getText().toString().matches("")) {
                    inputJmlIkanForm();
                    Toast.makeText(getApplicationContext(), "Harap mengisi semua inputan", Toast.LENGTH_SHORT).show();
                } else {
                    getListIkan(ikanActv.getText().toString().trim(), volume.getText().toString().trim(), listIkan);
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

    private void getListIkan(final String nama, final String harga,  final List<JmlIkan> daftar) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan data...");
        progressDialog.show();
        String URL_DATA = "";
        String valueForLink = nama.replaceAll("\\s", "+");
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
                                JmlIkan ikan = new JmlIkan(
                                        data.getString("kode_ikan"),
                                        nama,
                                        harga,
                                        "0"
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InputJmlActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void btnExFishClicked(View view) {
        if (listIkan.size() <= 0) {
            Toast.makeText(this, "Harap menginputkan ikan terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Menyimpan data...");
            progressDialog.show();
            for (int i = 0; i < listIkan.size(); i++) {
                String idIkan = listIkan.get(i).getId();
                String volumeIkan = listIkan.get(i).getVolume();
                String jumlah = listIkan.get(i).getJumlah();
                insertDataIkan(idIkan, volumeIkan, jumlah);

                if (i == listIkan.size() - 1) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
                    listIkan.clear();
                    loadFishList(listIkan);
                }

                if (!isInsertSuccess) {
                    isInsertSuccess = true;
                    Toast.makeText(getApplicationContext(), "Terjadi Kesalahan saat menyimpan data", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    private void insertDataIkan(final String kodeIkan, final String volumeIkan, final String jumlahIkan) {
        String URL_DATA = "http://mobile.fishinfojatim.net/volume_pasar/volume_pasar";


        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isInsertSuccess = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        isInsertSuccess = false;
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("kode_surveyor", idSurveyor);
                params.put("kode_kota", kodeKota);
                params.put("id_pasar", idPasar);
                params.put("kode_responden_pasar", idPedagang);
                params.put("kode_ikan", kodeIkan);
                params.put("volume", volumeIkan);
                params.put("jumlah", jumlahIkan);

                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }
}
