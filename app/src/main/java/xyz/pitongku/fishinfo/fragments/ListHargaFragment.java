package xyz.pitongku.fishinfo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import xyz.pitongku.fishinfo.adapters.ListIkanAdapter;
import xyz.pitongku.fishinfo.interfaces.OnLoadMore;
import xyz.pitongku.fishinfo.model.HargaIkan;

public class ListHargaFragment extends Fragment {

    private RecyclerView rvListHarga;
    private String jenis, GET_URL, kodeKota;
    private List<HargaIkan> listHarga = new ArrayList<>();
    private ListIkanAdapter adapter;
    int start = 0, end = 5;
    private JSONArray jsonArray;

    public ListHargaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_harga, container, false);

        rvListHarga = view.findViewById(R.id.rv_list_admin);
        rvListHarga.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListIkanAdapter(rvListHarga, getActivity(), listHarga);

        if (getArguments() != null) {
            jenis = getArguments().getString("JENIS");
            kodeKota = getArguments().getString("KODE_KOTA");
        }

        if (jenis.equals("0"))
            GET_URL = "http://mobile.fishinfojatim.net/harga_konsumsi/harga?jenis=0&kode_kota=" + kodeKota;
        else if (jenis.equals("1"))
            GET_URL = "http://mobile.fishinfojatim.net/harga_konsumsi/harga?jenis=1&kode_kota=" + kodeKota;
        else if (jenis.equals("2"))
            GET_URL = "http://mobile.fishinfojatim.net/harga_konsumsi/harga?jenis=2&kode_kota=" + kodeKota;

        getData(GET_URL,start, end);
        addToRecycler();


        return view;
    }

    private void getData(String url, final int start, final int end) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getJSONArray(response);
                        parseMoreJSONdata(start, end);

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

    private void parseMoreJSONdata(int start, int end) {
        try {
            for (int i = start; i < end; i++) {
                if (i==jsonArray.length()){
                    Toast.makeText(getContext(), "Loading data finished", Toast.LENGTH_SHORT).show();
                }else{
                    JSONObject data = jsonArray.getJSONObject(i);
                    String volume = "0";
                    if(!data.getString("volume").isEmpty() && !data.getString("volume").equals("null"))
                        volume = data.getString("volume");

                    HargaIkan harga = new HargaIkan(
                            data.getString("nama_ikan"),
                            data.getString("nama_pedagang_pasar"),
                            data.getString("harga"),
                            data.getString("nama_pasar"),
                            volume

                    );
                    listHarga.add(harga);
                    adapter.notifyDataSetChanged();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJSONArray(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonArray = jsonObject.getJSONArray("harga");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addToRecycler(){
        adapter = new ListIkanAdapter(rvListHarga, getActivity(), listHarga);
        rvListHarga.setAdapter(adapter);
        adapter.setLoadMore(new OnLoadMore() {
            @Override
            public void OnLoadMore() {
                if (listHarga.size() <= jsonArray.length()){
                    listHarga.add(null);
                    adapter.notifyItemInserted(listHarga.size()-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listHarga.remove(listHarga.size()-1);
                            adapter.notifyItemRemoved(listHarga.size());


                            start = listHarga.size()-1;
                            end = start + 5;
                            parseMoreJSONdata(start, end);

                            adapter.notifyDataSetChanged();
                            adapter.setLoaded();
                        }
                    }, 5000);
                }else if (listHarga.size() >= jsonArray.length()){
                    Toast.makeText(getContext(), "Semua data sudah dimuat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


