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
import app.mazad.webservices.models.AttributeModel;
import app.mazad.webservices.models.AuctionDetailsModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.viewHolder> {
    private Context context;
    private ArrayList<AttributeModel> attributesList;

    public AttributesAdapter(Context context, ArrayList<AttributeModel> attributesList) {
        this.context = context;
        this.attributesList = attributesList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_attribute_tv_name)
        TextView name;
        @BindView(R.id.item_attribute_tv_value)
        TextView value;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public AttributesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_attributes, viewGroup, false);
        return new AttributesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull AttributesAdapter.viewHolder viewHolder, final int position) {
        if (attributesList.get(position).carBrand != null && !attributesList.get(position).carBrand.isEmpty()) {
            viewHolder.name.setText(context.getString(R.string.brand));
            viewHolder.value.setText(attributesList.get(position).carBrand);
        } else if (attributesList.get(position).carModel != null && !attributesList.get(position).carModel.isEmpty()) {
            viewHolder.name.setText(context.getString(R.string.model));
            viewHolder.value.setText(attributesList.get(position).carModel);
        } else {
            viewHolder.name.setText(attributesList.get(position).getName());
            viewHolder.value.setText(attributesList.get(position).getValue());
        }
    }

    @Override
    public int getItemCount() {
        return attributesList.size();
    }
}