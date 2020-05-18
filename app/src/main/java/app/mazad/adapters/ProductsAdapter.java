package app.mazad.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
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
import java.util.concurrent.TimeUnit;

import app.mazad.R;
import app.mazad.classes.FixControl;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.fragments.AuctionDetailsFragment;
import app.mazad.webservices.models.AuctionModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.viewHolder> {
    private Context context;
    private ArrayList<AuctionModel> auctionsList;
    private OnItemCLickListener listener;

    public ProductsAdapter(Context context, ArrayList<AuctionModel> auctionsList,OnItemCLickListener listener) {
        this.context = context;
        this.auctionsList = auctionsList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_product_riv_productImage)
        RoundedImageView productImage;
        @BindView(R.id.item_product_tv_highValue)
        TextView highValue;
        @BindView(R.id.item_product_tv_productName)
        TextView productName;
        @BindView(R.id.item_product_tv_currentPrice)
        TextView currentPrice;
        @BindView(R.id.item_product_tv_biddingCounter)
        TextView biddingCounter;
        @BindView(R.id.item_product_tv_date)
        TextView date;
        @BindView(R.id.item_product_v_details)
        View details;
        @BindView(R.id.loading)
        ProgressBar loading;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ProductsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_product, viewGroup, false);
        return new ProductsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.viewHolder viewHolder, final int position) {
        if (auctionsList.get(position).isHighValue)
            viewHolder.highValue.setVisibility(View.VISIBLE);
        else
            viewHolder.highValue.setVisibility(View.GONE);

        setImage(auctionsList.get(position).photoUrl, viewHolder);
        viewHolder.productName.setText(auctionsList.get(position).getTitle());

        int intValue = (int) auctionsList.get(position).lastBidPrice;
        double doubleValue = auctionsList.get(position).lastBidPrice;
        if (doubleValue - intValue > 0) {
            viewHolder.currentPrice.setText(doubleValue + " " + context.getString(R.string.kuwaitCurrency));
        } else {
            viewHolder.currentPrice.setText(intValue + " " + context.getString(R.string.kuwaitCurrency));
        }

        viewHolder.biddingCounter.setText(String.valueOf(auctionsList.get(position).userBids));
        viewHolder.date.setText(GlobalFunctions.formatDateAndTime(auctionsList.get(position).startDate));
        viewHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.detailsClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return auctionsList.size();
    }

    private void setImage(String imagePath, ProductsAdapter.viewHolder viewHolder) {
        if (imagePath != null) {
            try {
                int Width = FixControl.getImageWidth(context, R.drawable.products_list_noimg);
                int Height = FixControl.getImageHeight(context, R.drawable.products_list_noimg);
                viewHolder.productImage.getLayoutParams().height = Height;
                viewHolder.productImage.getLayoutParams().width = Width;
                Glide.with(context.getApplicationContext()).load(imagePath)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.products_list_noimg)
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

    public interface OnItemCLickListener{
        public void detailsClick(int position);
    }
}