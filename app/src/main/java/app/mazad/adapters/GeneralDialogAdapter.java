package app.mazad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.webservices.models.CountryCodeModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GeneralDialogAdapter extends RecyclerView.Adapter<GeneralDialogAdapter.viewHolder> {
    Context context;
    ArrayList<CountryCodeModel> countriesCodesList;

    public GeneralDialogAdapter(Context context, ArrayList<CountryCodeModel> countriesCodesList) {
        this.context = context;
        this.countriesCodesList = countriesCodesList;
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_dialog_general_tv_name)
        TextView name;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public GeneralDialogAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_dialog_general, viewGroup, false);
        return new GeneralDialogAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralDialogAdapter.viewHolder viewHolder, final int position) {
        viewHolder.name.setText(countriesCodesList.get(position).countryCode);
    }

    @Override
    public int getItemCount() {
        return countriesCodesList.size();
    }

}

