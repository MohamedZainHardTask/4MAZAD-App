package app.mazad.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.classes.Constants;
import app.mazad.fragments.FiltersFragment;
import app.mazad.webservices.models.FilterModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.viewHolder> {
    private Context context;
    private ArrayList<FilterModel> filterList;

    public FilterAdapter(Context context, ArrayList<FilterModel> filterList) {
        this.context = context;
        this.filterList = filterList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_filter_tv_name)
        TextView name;
        @BindView(R.id.item_filter_et_singleValue)
        TextInputEditText singleValue;
        @BindView(R.id.item_filter_rv_multiValues)
        RecyclerView multiValues;
        @BindView(R.id.item_filter_v_line)
        View line;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public FilterAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_filter, viewGroup, false);
        return new FilterAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.viewHolder viewHolder, final int position) {
        viewHolder.name.setText(filterList.get(position).getName());
        if (filterList.get(position).type.equals(Constants.LIST_BOX)) {
            viewHolder.multiValues.setVisibility(View.VISIBLE);
            viewHolder.singleValue.setVisibility(View.GONE);
            viewHolder.line.setVisibility(View.VISIBLE);
            viewHolder.multiValues.setLayoutManager(new LinearLayoutManager(context));
            viewHolder.multiValues.setAdapter(new FilterValuesAdapter(context,filterList.get(position).filterValues,position));
        } else if (filterList.get(position).type.equals(Constants.TEXT_BOX)) {
            viewHolder.multiValues.setVisibility(View.INVISIBLE);
            viewHolder.singleValue.setVisibility(View.VISIBLE);
            viewHolder.line.setVisibility(View.GONE);
        }
        viewHolder.singleValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FiltersFragment.requestFilterList.get(position).value = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }
}


