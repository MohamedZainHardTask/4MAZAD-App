package app.mazad.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.util.concurrent.TimeUnit;

import app.mazad.R;
import app.mazad.classes.Constants;
import app.mazad.classes.FixControl;
import app.mazad.classes.SessionManager;
import app.mazad.webservices.models.WinningAuctionModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WinningAuctionsAdapter extends RecyclerView.Adapter<WinningAuctionsAdapter.viewHolder> {
    private Context context;
    private ArrayList<WinningAuctionModel> winningAuctionsList;
    private WinningAuctionsAdapter.OnItemCLickListener listener;

    public WinningAuctionsAdapter(Context context, ArrayList<WinningAuctionModel> winningAuctionsList, WinningAuctionsAdapter.OnItemCLickListener listener) {
        this.context = context;
        this.winningAuctionsList = winningAuctionsList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_winning_auctions_tv_highValue)
        TextView highValue;
        @BindView(R.id.item_winning_auctions_riv_productImage)
        RoundedImageView productImage;
        @BindView(R.id.item_winning_auctions_tv_price)
        TextView price;
        @BindView(R.id.item_winning_auctions_tv_productName)
        TextView productName;
        @BindView(R.id.item_winning_auctions_tv_pay)
        TextView pay;
        @BindView(R.id.loading)
        ProgressBar loading;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public WinningAuctionsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_winning_auctions, viewGroup, false);
        return new WinningAuctionsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull WinningAuctionsAdapter.viewHolder viewHolder, final int position) {

        int intValue = (int) winningAuctionsList.get(position).lastBidPrice;
        double doubleValue = winningAuctionsList.get(position).lastBidPrice;
        if (doubleValue - intValue > 0) {
            viewHolder.price.setText(doubleValue + " " + context.getString(R.string.kuwaitCurrency));
        } else {
            viewHolder.price.setText(intValue + " " + context.getString(R.string.kuwaitCurrency));
        }

        if (winningAuctionsList.get(position).isHighValue)
            viewHolder.highValue.setVisibility(View.VISIBLE);
        else
            viewHolder.highValue.setVisibility(View.GONE);

        setImage(winningAuctionsList.get(position).photoUrl, viewHolder);
        viewHolder.productName.setText(winningAuctionsList.get(position).getTitle());

        if (!winningAuctionsList.get(position).isPayed && winningAuctionsList.get(position).isPayOnline) {
            viewHolder.pay.setVisibility(View.VISIBLE);
        } else {
            viewHolder.pay.setVisibility(View.GONE);
        }

        viewHolder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.detailsClick(position);
            }
        });

        viewHolder.pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.payClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return winningAuctionsList.size();
    }

    private void setImage(String imagePath, WinningAuctionsAdapter.viewHolder viewHolder) {
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

    public interface OnItemCLickListener {
        public void detailsClick(int position);

        public void payClick(int position);
    }
}





