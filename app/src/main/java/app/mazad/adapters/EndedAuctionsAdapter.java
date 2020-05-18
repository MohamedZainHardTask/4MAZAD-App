package app.mazad.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.classes.Constants;
import app.mazad.classes.FixControl;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.models.AuctionModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EndedAuctionsAdapter extends RecyclerView.Adapter<EndedAuctionsAdapter.viewHolder> {
    private Context context;
    private ArrayList<AuctionModel> endedAuctionsList;
    private SessionManager sessionManager;

    public EndedAuctionsAdapter(Context context, ArrayList<AuctionModel> endedAuctionsList) {
        this.context = context;
        this.endedAuctionsList = endedAuctionsList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_ended_auctions_tv_highValue)
        TextView highValue;
        @BindView(R.id.item_ended_auctions_riv_productImage)
        RoundedImageView productImage;
        @BindView(R.id.item_ended_auctions_tv_biddingCounter)
        TextView biddingCounter;
        @BindView(R.id.item_ended_auctions_tv_productName)
        TextView productName;
        @BindView(R.id.item_ended_auctions_tv_closedPrice)
        TextView closedPrice;
        @BindView(R.id.item_ended_auctions_tv_closedDate)
        TextView closedDate;
        @BindView(R.id.item_ended_auctions_tv_winner)
        TextView winner;

        @BindView(R.id.loading)
        ProgressBar loading;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sessionManager = new SessionManager(context);
        }
    }

    @NonNull
    @Override
    public EndedAuctionsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_ended_auction, viewGroup, false);
        return new EndedAuctionsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull EndedAuctionsAdapter.viewHolder viewHolder, final int position) {


        int intValue = (int) endedAuctionsList.get(position).closedPrice;
        double doubleValue = endedAuctionsList.get(position).closedPrice;
        if (doubleValue - intValue > 0) {
            viewHolder.closedPrice.setText(doubleValue + " " + context.getString(R.string.kuwaitCurrency));
        } else {
            viewHolder.closedPrice.setText(intValue + " " + context.getString(R.string.kuwaitCurrency));
        }

        viewHolder.closedDate.setText(context.getString(R.string.closedDate) + " " + endedAuctionsList.get(position).getCloseDate());
        if (endedAuctionsList.get(position).userId == sessionManager.getUserId())
            viewHolder.winner.setText(context.getString(R.string.winnerBy) + " " + context.getString(R.string.me));
        else
            viewHolder.winner.setText(context.getString(R.string.winnerBy) + " " + endedAuctionsList.get(position).user);


        if (endedAuctionsList.get(position).isHighValue)
            viewHolder.highValue.setVisibility(View.VISIBLE);
        else
            viewHolder.highValue.setVisibility(View.GONE);

        setImage(endedAuctionsList.get(position).photoUrl, viewHolder);
        viewHolder.productName.setText(endedAuctionsList.get(position).getTitle());
        viewHolder.biddingCounter.setText(String.valueOf(endedAuctionsList.get(position).userBids));

    }

    @Override
    public int getItemCount() {
        return endedAuctionsList.size();
    }

    private void setImage(String imagePath, EndedAuctionsAdapter.viewHolder viewHolder) {
        if (imagePath != null) {
            try {
                int Width = FixControl.getImageWidth(context, R.drawable.home_list_noimg);
                int Height = FixControl.getImageHeight(context, R.drawable.home_list_noimg);
                viewHolder.productImage.getLayoutParams().height = Height;
                viewHolder.productImage.getLayoutParams().width = Width;
                Glide.with(context.getApplicationContext()).load(imagePath)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.home_list_noimg)
                                .centerCrop())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                viewHolder.loading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                viewHolder.loading.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(viewHolder.productImage);
                viewHolder.productImage.setCornerRadius(10, 10, 10, 10);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

}





