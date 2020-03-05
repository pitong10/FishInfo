package xyz.pitongku.fishinfo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.pitongku.fishinfo.R;
import xyz.pitongku.fishinfo.model.HargaIkan;

public class HargaIkanAdapter extends RecyclerView.Adapter<HargaIkanAdapter.ViewHolder> {

    private List<HargaIkan> listIkan;
    private Context context;

    public HargaIkanAdapter(List<HargaIkan> listIkan, Context context) {
        this.listIkan = listIkan;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_harga_ikan_kons, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        HargaIkan ikan = listIkan.get(position);

        viewHolder.namaPenjualTv.setText(ikan.getResponden());
        viewHolder.namaIkanTv.setText(ikan.getId());
        viewHolder.hargaTv.setText("Rp."+ String.format("%,d", Long.parseLong(ikan.getHarga())));


    }

    @Override
    public int getItemCount() {
        return listIkan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView namaPenjualTv, namaIkanTv, hargaTv, pasarTv;
        public ViewHolder(View itemView) {
            super(itemView);

            namaIkanTv = itemView.findViewById(R.id.ikanTv);
            namaPenjualTv = itemView.findViewById(R.id.pedagangTv);
            hargaTv = itemView.findViewById(R.id.hargaTv);

        }
    }
}
