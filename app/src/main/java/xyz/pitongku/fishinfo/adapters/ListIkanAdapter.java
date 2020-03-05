package xyz.pitongku.fishinfo.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import xyz.pitongku.fishinfo.R;
import xyz.pitongku.fishinfo.interfaces.OnLoadMore;
import xyz.pitongku.fishinfo.model.HargaIkan;

public class ListIkanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    boolean isLoading;
    private List<HargaIkan> listIkan;
    OnLoadMore loadMore;
    Activity activity;
    int visibleTreshold = 5;
    int lastVisibleItem, totalItemCount;

    public ListIkanAdapter(RecyclerView recyclerView, Activity activity, List<HargaIkan> listIkan) {
        this.listIkan = listIkan;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem+visibleTreshold)){
                    if (loadMore != null)
                        loadMore.OnLoadMore();
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return listIkan.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setLoadMore(OnLoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_harga, parent, false);
            return new ItemViewHolder(view);
        }else if (i == VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder){
            HargaIkan ikan = listIkan.get(position);
            String namaPasar = ikan.getPasar();

            if (namaPasar.matches("")||namaPasar.equals("null")||namaPasar==null)
                namaPasar = "-";
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            holder.namaIkanTv.setText(": "+ikan.getId());
            holder.hargaTv.setText("Rp."+ String.format("%,d", Long.parseLong(ikan.getHarga())));
            holder.pasarTv.setText(": "+namaPasar);
            holder.volumeTv.setText(": "+String.format("%,d", Long.parseLong(ikan.getVolume()))+" Kg");
        }else if (viewHolder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }


    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public int getItemCount() {
        return listIkan.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView namaIkanTv, hargaTv, pasarTv, volumeTv;
        public ItemViewHolder(View itemView) {
            super(itemView);

            namaIkanTv = itemView.findViewById(R.id.ikanTv);
            hargaTv = itemView.findViewById(R.id.hargaTv);
            pasarTv = itemView.findViewById(R.id.pasarTv);
            volumeTv = itemView.findViewById(R.id.volumeTv);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder{

        public ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
