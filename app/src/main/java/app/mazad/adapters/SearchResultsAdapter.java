package app.mazad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.webservices.models.AuctionModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.viewHolder> {
    private Context context;
    private ArrayList<AuctionModel> searchResultsList;
    private OnItemClickListener listener;

    public SearchResultsAdapter(Context context, ArrayList<AuctionModel> searchResultsList, OnItemClickListener listener) {
        this.context = context;
        this.searchResultsList = searchResultsList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_search_result_tv_name)
        TextView name;
        @BindView(R.id.item_search_result_v_details)
        View details;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public SearchResultsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_search_result, viewGroup, false);
        return new SearchResultsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsAdapter.viewHolder viewHolder, final int position) {
        viewHolder.name.setText(searchResultsList.get(position).getTitle());
        viewHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.detailsClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResultsList.size();
    }

    public interface OnItemClickListener {
        public void detailsClick(int position);
    }
}






