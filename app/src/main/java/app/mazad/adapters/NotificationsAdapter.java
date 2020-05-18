package app.mazad.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.webservices.models.NotificationModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.viewHolder> {
    private Context context;
    private ArrayList<NotificationModel> notificationsList;
    private OnItemClickListener listener;

    public NotificationsAdapter(Context context, ArrayList<NotificationModel> notificationsList,OnItemClickListener listener) {
        this.context = context;
        this.notificationsList = notificationsList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_notification_cv_container)
        CardView container;
        @BindView(R.id.item_notification_tv_date)
        TextView date;
        @BindView(R.id.item_notification_tv_message)
        TextView message;
        @BindView(R.id.item_notification_iv_indicator)
        ImageView indicator;
        @BindView(R.id.item_notification_v_details)
        View details;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public NotificationsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_notification, viewGroup, false);
        return new NotificationsAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.viewHolder viewHolder, final int position) {
        viewHolder.date.setText(notificationsList.get(position).getNotificationDate());
        String messageTxt = notificationsList.get(position).getMessage();
        if (messageTxt.length() > 100)
            viewHolder.message.setText(messageTxt.substring(0, 99) + "...");
        else
            viewHolder.message.setText(messageTxt);

        if (notificationsList.get(position).isRead) {
            viewHolder.container.setCardElevation(0);
            viewHolder.indicator.setVisibility(View.INVISIBLE);
            viewHolder.date.setTextColor(Color.parseColor("#AAAAAA"));
            viewHolder.message.setTextColor(Color.parseColor("#777777"));
        } else {
            viewHolder.container.setCardElevation(7);
            viewHolder.indicator.setVisibility(View.VISIBLE);
            viewHolder.date.setTextColor(Color.parseColor("#777777"));
            viewHolder.message.setTextColor(Color.parseColor("#000000"));
        }

        viewHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.notificationClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public interface OnItemClickListener{
        public void notificationClick(int position);
    }
}




