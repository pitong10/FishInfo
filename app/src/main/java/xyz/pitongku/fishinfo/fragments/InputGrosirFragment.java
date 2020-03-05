package xyz.pitongku.fishinfo.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import xyz.pitongku.fishinfo.R;
import xyz.pitongku.fishinfo.adapters.InputIkanAdapter;
import xyz.pitongku.fishinfo.model.Ikan;
import xyz.pitongku.fishinfo.model.InputIkan;

public class InputGrosirFragment extends Fragment {

    private List<InputIkan> listIkan;
    private List<String> arrayIkan = new ArrayList<>();

    private RecyclerView rvIkan;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    private EditText hargaEt, volumeEt;
    private Button tambahIkanBtn, simpanDataBtn;
    private ImageView datepickerBtn;
    private TextView dateTv;
    private String kodeKota, kodeSurveyor, jenis, tipePedagang;
    private InputIkanAdapter adapter;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private boolean isInsertSuccess;

    private AutoCompleteTextView namaIkanActv;

    public InputGrosirFragment() {
        // Required empty public constructor
    }

    public static InputEceranFragment newInstance(String regex) {
        InputEceranFragment fragment = new InputEceranFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInsertSuccess = true;
        listIkan = new ArrayList<>();
    }

    private void setListIkanUtama(){
        String URL_DATA = "http://mobile.fishinfojatim.net/rest_json/data_ikan/ikan?param=utama&data=1";
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Memuat data ikan utama...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data_ikan");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                getListIkan(data.getString("nama_ikan"), "0", "0", listIkan);
                                loadFishList(listIkan);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_grosir, container, false);

        if (getArguments() != null) {
            kodeKota = getArguments().getString("KODE_KOTA");
            kodeSurveyor = getArguments().getString("KODE_SURV");
            jenis = getArguments().getString("JENIS");
            if (jenis.equals("1"))
                tipePedagang = "Grosir";
            else if(jenis.equals("2"))
                tipePedagang = "Produsen";
        }

        tambahIkanBtn = view.findViewById(R.id.tambahIkanBtn);

        tambahIkanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputIkanForm();
            }
        });
        datepickerBtn = view.findViewById(R.id.datepicker_btn);
        datepickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
        dateTv = view.findViewById(R.id.tv_date);
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = df.format(c);
        dateTv.setText(formattedDate);

        simpanDataBtn = view.findViewById(R.id.simpanDataBtn);

        simpanDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveClicked();
            }
        });

        adapter = new InputIkanAdapter(getContext());
        rvIkan = view.findViewById(R.id.rv_ikank);
        rvIkan.setLayoutManager(new LinearLayoutManager(getContext()));
        loadFishList(listIkan);

        setSuggestion();
        setListIkanUtama();
        return view;
    }

    private void inputIkanForm() {
        dialog = new AlertDialog.Builder(getContext());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.input_ikan_layout, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.input);
        dialog.setTitle("Input Harga Ikan");

        namaIkanActv = dialogView.findViewById(R.id.namaIkanActv);
        hargaEt = dialogView.findViewById(R.id.hargaEt);
        volumeEt = dialogView.findViewById(R.id.volumeEt);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, arrayIkan);
        namaIkanActv.setAdapter(adapter);

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (hargaEt.getText().toString().matches("") || namaIkanActv.getText().toString().matches("") || volumeEt.getText().toString().isEmpty()) {
                    inputIkanForm();
                    Toast.makeText(getContext(), "Harap mengisi semua inputan", Toast.LENGTH_SHORT).show();
                } else {
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
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void getListIkan(final String value, final String harga, final String volume, final List<InputIkan> daftar) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
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
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void loadFishList(List<InputIkan> ikan) {
        InputIkanAdapter myAdapter = new InputIkanAdapter(getContext());
        myAdapter.setMyFishList(ikan);
        rvIkan.setAdapter(myAdapter);
    }

    public void btnSaveClicked() {
        if (listIkan.size() <= 0) {
            Toast.makeText(getContext(), "Harap menginputkan ikan terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Menyimpan data...");
            progressDialog.show();
            for (int i = 0; i < listIkan.size(); i++) {
                String idIkan = listIkan.get(i).getId();
                String hargaIkan = listIkan.get(i).getValue();
                String volumeIkan = listIkan.get(i).getVolume();
                insertDataIkan(idIkan, hargaIkan, volumeIkan, dateTv.getText().toString());

                if (i == listIkan.size() - 1) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
                    listIkan.clear();
                    loadFishList(listIkan);
                    setListIkanUtama();
                }

                if (!isInsertSuccess) {
                    isInsertSuccess = true;
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Terjadi Kesalahan saat menyimpan data", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }

    }

    private void insertDataIkan(final String kodeIkan, final String hargaIkan, final String volumeIkan, final String tanggal) {
        String URL_DATA = "http://mobile.fishinfojatim.net/harga_konsumsi/survey_harga_konsumsi";


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
                        isInsertSuccess = false;
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("kode_surveyor", kodeSurveyor);
                params.put("kode_kota", kodeKota);
                params.put("jenis", jenis);
                params.put("kode_responden_pasar", "");
                params.put("kode_ikan", kodeIkan);
                params.put("harga", hargaIkan);
                params.put("volume", volumeIkan);
                params.put("tanggal", tanggal);



                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(postRequest);
    }

    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                dateTv.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }
}
