package xyz.pitongku.fishinfo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import xyz.pitongku.fishinfo.R;
import xyz.pitongku.fishinfo.model.InputIkan;

public class InputIkanAdapter extends RecyclerView.Adapter<InputIkanAdapter.InputIkanViewHolder> {

    Context ctx;
    List<InputIkan> ikan;

    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private InputIkan editedValue;
    private View dialogView;
    private EditText hargaEt, volumeEt;

    public InputIkanAdapter(Context ctx){
        this.ctx = ctx;
    }

    public void setMyFishList(List<InputIkan> ikan){this.ikan = ikan;}

    @Override
    public InputIkanViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ikank, parent, false);
        return new InputIkanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InputIkanViewHolder viewHolder, final int i) {
        InputIkan ikanObj = ikan.get(i);
        viewHolder.tvNama.setText(ikanObj.getNamaIkan());
        viewHolder.tvHarga.setText(ikanObj.getValue());
        viewHolder.tvVolume.setText(ikanObj.getVolume());

        final InputIkan updatedIkan = ikanObj;

        viewHolder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIkanForm(updatedIkan, i);
            }
        });
        if (ikanObj.getUtama().equals("1")){
            viewHolder.hapusIcon.setVisibility(View.INVISIBLE);
        }
        viewHolder.hapusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ikan.remove(i);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ikan.size();
    }

    public class InputIkanViewHolder extends RecyclerView.ViewHolder{

        TextView tvNama, tvHarga, tvVolume;
        ImageView hapusIcon, editIcon;

        InputIkanViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.namaIkanTv);
            tvHarga = itemView.findViewById(R.id.hargaIkanTv);
            tvVolume = itemView.findViewById(R.id.volumeIkanTv);
            editIcon = itemView.findViewById(R.id.editImg);
            hapusIcon = itemView.findViewById(R.id.hapusImg);
        }
    }

    private void editIkanForm(final InputIkan ikanObj, final int index) {
        dialog = new AlertDialog.Builder(ctx);
        inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        dialogView = inflater.inflate(R.layout.edit_ikan_layout, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.input);
        dialog.setTitle("Edit Harga Ikan");

        hargaEt = dialogView.findViewById(R.id.hargaEt);
        volumeEt = dialogView.findViewById(R.id.volumeEt);

        hargaEt.setText(ikanObj.getValue());
        volumeEt.setText(ikanObj.getVolume());

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (hargaEt.getText().toString().matches("") || volumeEt.getText().toString().isEmpty()) {
                    editIkanForm(ikanObj, index);
                    Toast.makeText(ctx, "Harap mengisi semua inputan", Toast.LENGTH_SHORT).show();
                } else {
                    editedValue = new InputIkan(ikanObj.getId(), ikanObj.getNamaIkan(), hargaEt.getText().toString(), volumeEt.getText().toString(), ikanObj.getUtama());
                    dialog.dismiss();
                    ikan.set(index, editedValue);
                    notifyItemChanged(index);
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
}
