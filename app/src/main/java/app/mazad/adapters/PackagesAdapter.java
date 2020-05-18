package app.mazad.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.webservices.models.PackageModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.viewHolder> {
    private Context context;
    private ArrayList<PackageModel> packagesList;
    private OnItemClickListener listener;

    public PackagesAdapter(Context context, ArrayList<PackageModel> packagesList, OnItemClickListener listener) {
        this.context = context;
        this.packagesList = packagesList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_package_cv_container)
        CardView container;
        @BindView(R.id.item_package_tv_packageName)
        TextView packageName;
        @BindView(R.id.item_package_tv_packagePrice)
        TextView packagePrice;
        @BindView(R.id.item_package_tv_packageDescription)
        TextView packageDescription;
        @BindView(R.id.item_package_tv_submit)
        TextView submit;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public PackagesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_package, viewGroup, false);
        return new PackagesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull PackagesAdapter.viewHolder viewHolder, final int position) {
        viewHolder.packageName.setText(packagesList.get(position).getName());

        int intValue = (int) packagesList.get(position).price;
        double doubleValue = packagesList.get(position).price;
        if (doubleValue - intValue > 0) {
            viewHolder.packagePrice.setText(doubleValue + " " + context.getString(R.string.kuwaitCurrency));
        } else {
            viewHolder.packagePrice.setText(intValue + " " + context.getString(R.string.kuwaitCurrency));
        }

        viewHolder.packageDescription.setText(packagesList.get(position).getAbout());
        viewHolder.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.subscribeClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packagesList.size();
    }

    public interface OnItemClickListener {
        public void subscribeClick(int position);
    }
}