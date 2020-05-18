package app.mazad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.fragments.FiltersFragment;
import app.mazad.webservices.models.FilterValueModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterValuesAdapter extends RecyclerView.Adapter<FilterValuesAdapter.viewHolder> {
    private Context context;
    private ArrayList<FilterValueModel> filterValuesList;
    private int index;

    public FilterValuesAdapter(Context context, ArrayList<FilterValueModel> filterValuesList, int index) {
        this.context = context;
        this.filterValuesList = filterValuesList;
        this.index = index;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_filter_value_tv_name)
        TextView name;
        @BindView(R.id.item_filter_value_cb_checkBox)
        CheckBox checkBox;
        @BindView(R.id.item_filter_value_v_select)
        View select;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public FilterValuesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_filter_value, viewGroup, false);
        return new FilterValuesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterValuesAdapter.viewHolder viewHolder, final int position) {
        viewHolder.name.setText(filterValuesList.get(position).getName());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (FiltersFragment.requestFilterList.get(index).value == null || FiltersFragment.requestFilterList.get(index).value.isEmpty()) {
                        FiltersFragment.requestFilterList.get(index).value = filterValuesList.get(position).id + "";
                    } else {
                        FiltersFragment.requestFilterList.get(index).value = FiltersFragment.requestFilterList.get(index).value + "," + filterValuesList.get(position).id;
                    }
                } else {
                    FiltersFragment.requestFilterList.get(index).value = FiltersFragment.requestFilterList.get(index).value.replace(filterValuesList.get(position).id + "", "");
                    if (FiltersFragment.requestFilterList.get(index).value.contains(",,")) {
                        FiltersFragment.requestFilterList.get(index).value = FiltersFragment.requestFilterList.get(index).value.replace(",,", ",");
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterValuesList.size();
    }
}


