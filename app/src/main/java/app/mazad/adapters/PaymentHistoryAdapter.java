package app.mazad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.webservices.models.PaymentHistoryModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.viewHolder> {
    private Context context;
    private ArrayList<PaymentHistoryModel> paymentHistoryList;

    public PaymentHistoryAdapter(Context context, ArrayList<PaymentHistoryModel> paymentHistoryList) {
        this.context = context;
        this.paymentHistoryList = paymentHistoryList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_payment_history_tv_date)
        TextView date;
        @BindView(R.id.item_payment_history_tv_title)
        TextView title;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public PaymentHistoryAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_payment_history, viewGroup, false);
        return new PaymentHistoryAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHistoryAdapter.viewHolder viewHolder, final int position) {
        viewHolder.date.setText(paymentHistoryList.get(position).getCreationDate());
        if (paymentHistoryList.get(position).flag == 1) {
            viewHolder.title.setText(context.getString(R.string.paid)
                    + " " + paymentHistoryList.get(position).amount
                    + " " + context.getString(R.string.kuwaitCurrency)
                    + " " + context.getString(R.string.subscribedOn)
                    + " " + paymentHistoryList.get(position).getService()
                    + " " + context.getString(R.string.Package));
        } else if (paymentHistoryList.get(position).flag == 2) {
            viewHolder.title.setText(context.getString(R.string.bid)
                    + " " + paymentHistoryList.get(position).getService()
                    + " " + context.getString(R.string.and)
                    + " " + context.getString(R.string.paid)
                    + " " + paymentHistoryList.get(position).amount
                    + " " + context.getString(R.string.kuwaitCurrency));
        } else if (paymentHistoryList.get(position).flag == 3) {
            viewHolder.title.setText(context.getString(R.string.paid)
                    + " " + paymentHistoryList.get(position).amount
                    + " " + context.getString(R.string.kuwaitCurrency)
                    + " " + context.getString(R.string.ForBuy)
                    + " " + paymentHistoryList.get(position).getService());
        }
    }

    @Override
    public int getItemCount() {
        return paymentHistoryList.size();
    }
}




