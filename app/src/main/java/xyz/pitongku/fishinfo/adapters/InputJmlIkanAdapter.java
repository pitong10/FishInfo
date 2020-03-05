package xyz.pitongku.fishinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xyz.pitongku.fishinfo.R;
import xyz.pitongku.fishinfo.model.JmlIkan;

public class InputJmlIkanAdapter extends RecyclerView.Adapter<InputJmlIkanAdapter.InputJmlIkanViewHolder> {

    Context ctx;
    List<JmlIkan> ikan;

    public InputJmlIkanAdapter(Context ctx){
        this.ctx = ctx;
    }

    public void setMyFishList(List<JmlIkan> ikan){this.ikan = ikan;}

    @Override
    public InputJmlIkanViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ikann, parent, false);
        return new InputJmlIkanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InputJmlIkanViewHolder viewHolder, final int position) {
        JmlIkan ikanObj = ikan.get(position);
        viewHolder.tvNama.setText(ikanObj.getNamaIkan());
        viewHolder.tvVolume.setText(ikanObj.getVolume());

        viewHolder.hapusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ikan.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ikan.size();
    }

    public class InputJmlIkanViewHolder extends RecyclerView.ViewHolder{

        TextView tvNama, tvVolume, tvJumlah;
        ImageView hapusIcon;

        InputJmlIkanViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.namaIkanTv);
            tvVolume = itemView.findViewById(R.id.volumeTv);
            hapusIcon = itemView.findViewById(R.id.hapusImg);
        }
    }
}
