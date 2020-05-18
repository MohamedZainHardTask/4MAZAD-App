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
import app.mazad.classes.GlobalFunctions;
import app.mazad.webservices.models.BidderModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BiddersAdapter extends RecyclerView.Adapter<BiddersAdapter.viewHolder> {
    private Context context;
    private ArrayList<BidderModel> biddersList;

    public BiddersAdapter(Context context, ArrayList<BidderModel> biddersList) {
        this.context = context;
        this.biddersList = biddersList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_bidder_tv_name)
        TextView name;
        @BindView(R.id.item_bidder_tv_date)
        TextView date;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public BiddersAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_bidder, viewGroup, false);
        return new BiddersAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull BiddersAdapter.viewHolder viewHolder, final int position) {
            viewHolder.name.setText(biddersList.get(position).user);
            viewHolder.date.setText(GlobalFunctions.formatDateAndTime(biddersList.get(position).bidDate));
    }

    @Override
    public int getItemCount() {
        return biddersList.size();
    }
}
