package app.mazad.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.microsoft.signalr.HubConnection;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import app.mazad.R;
import app.mazad.classes.Constants;
import app.mazad.classes.FixControl;
import app.mazad.classes.GlobalFunctions;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import app.mazad.fragments.HomeFragment;
import app.mazad.webservices.models.AuctionModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AuctionsAdapter extends RecyclerView.Adapter<AuctionsAdapter.viewHolder> {
    private Context context;
    private ArrayList<AuctionModel> auctionsList;
    private String comingFrom;
    private SessionManager sessionManager;
    private OnItemCLickListener listener;
    private CountDownTimer countDownTimer;

    public AuctionsAdapter(Context context, ArrayList<AuctionModel> auctionsList, String comingFrom, OnItemCLickListener listener) {
        this.context = context;
        this.auctionsList = auctionsList;
        this.comingFrom = comingFrom;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_auctions_tv_highValue)
        TextView highValue;
        @BindView(R.id.item_auctions_riv_productImage)
        RoundedImageView productImage;
        @BindView(R.id.item_auctions_ll_remaining)
        LinearLayout remaining;
        @BindView(R.id.item_auctions_tv_remainingDays)
        TextView remainingDays;
        @BindView(R.id.item_auctions_tv_remainingHours)
        TextView remainingHours;
        @BindView(R.id.item_auctions_tv_remainingMinutes)
        TextView remainingMinutes;
        @BindView(R.id.item_auctions_tv_remainingSeconds)
        TextView remainingSeconds;
        @BindView(R.id.item_auctions_tv_biddingCounter)
        TextView biddingCounter;
        @BindView(R.id.item_auctions_tv_currentPrice)
        TextView currentPrice;
        @BindView(R.id.item_auctions_tv_lastBid)
        TextView lastBid;
        @BindView(R.id.item_auctions_tv_productName)
        TextView productName;
        @BindView(R.id.item_auctions_tv_date)
        TextView date;
        @BindView(R.id.item_auctions_v_details)
        View details;
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
    public AuctionsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_auctions, viewGroup, false);
        return new AuctionsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull AuctionsAdapter.viewHolder viewHolder, final int position) {

        remainTimeCounter(auctionsList.get(position).remainTime, viewHolder);
        viewHolder.date.setText(GlobalFunctions.formatDateAndTime(auctionsList.get(position).startDate));

        int intValue = (int) auctionsList.get(position).lastBidPrice;
        double doubleValue = auctionsList.get(position).lastBidPrice;
        if (doubleValue - intValue > 0) {
            viewHolder.currentPrice.setText(doubleValue + " " + context.getString(R.string.kuwaitCurrency));
        } else {
            viewHolder.currentPrice.setText(intValue + " " + context.getString(R.string.kuwaitCurrency));
        }
        if (comingFrom.equals(Constants.HOME)) {
            viewHolder.lastBid.setVisibility(View.GONE);
        } else if (comingFrom.equals(Constants.LIVE)) {
            viewHolder.lastBid.setVisibility(View.VISIBLE);
            if (auctionsList.get(position).lastBidUserId == sessionManager.getUserId())
                viewHolder.lastBid.setText(context.getString(R.string.lastBid) + " " + context.getString(R.string.me));
            else
                viewHolder.lastBid.setText(context.getString(R.string.lastBid) + " " + auctionsList.get(position).user);

        }

        if (auctionsList.get(position).isHighValue)
            viewHolder.highValue.setVisibility(View.VISIBLE);
        else
            viewHolder.highValue.setVisibility(View.GONE);

        setImage(auctionsList.get(position).photoUrl, viewHolder);
        viewHolder.productName.setText(auctionsList.get(position).getTitle());
        viewHolder.biddingCounter.setText(String.valueOf(auctionsList.get(position).userBids));

        viewHolder.biddingCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.biddersClick(position);
            }
        });

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

    private void setImage(String imagePath, AuctionsAdapter.viewHolder viewHolder) {
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

    private void remainTimeCounter(Double remainTime, AuctionsAdapter.viewHolder viewHolder) {
        long remainTimeLong = Math.round(remainTime);
        setTime(remainTimeLong, viewHolder);
        countDownTimer = new CountDownTimer(remainTimeLong, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setTime(millisUntilFinished, viewHolder);
            }

            @Override
            public void onFinish() {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
//                getFragmentManager().popBackStack();
                Navigator.loadFragment((FragmentActivity) context, HomeFragment.newInstance((FragmentActivity) context), R.id.activity_main_fl_appContainer, true);
            }
        }.start();
    }

    private void setTime(long remainTime, AuctionsAdapter.viewHolder viewHolder) {
        long remainDays = TimeUnit.MILLISECONDS.toDays(remainTime);
        long remainHours = TimeUnit.MILLISECONDS.toHours(remainTime)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(remainTime));
        long remainMinutes = TimeUnit.MILLISECONDS.toMinutes(remainTime)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime));
        long remainSeconds = TimeUnit.MILLISECONDS.toSeconds(remainTime)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime));

        viewHolder.remainingDays.setText(String.valueOf(remainDays));
        viewHolder.remainingHours.setText(String.valueOf(remainHours));
        viewHolder.remainingMinutes.setText(String.valueOf(remainMinutes));
        viewHolder.remainingSeconds.setText(String.valueOf(remainSeconds));

    }


    public interface OnItemCLickListener {
        public void detailsClick(int position);

        public void biddersClick(int position);
    }
}





