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
import app.mazad.webservices.models.FaqModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.viewHolder> {
    private Context context;
    private ArrayList<FaqModel> faqList;

    public FaqAdapter(Context context, ArrayList<FaqModel> faqList) {
        this.context = context;
        this.faqList = faqList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_faq_tv_question)
        TextView question;
        @BindView(R.id.item_faq_tv_answer)
        TextView answer;
        @BindView(R.id.item_faq_iv_arrow)
        ImageView arrow;
        @BindView(R.id.item_faq_v_details)
        View details;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public FaqAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_faq, viewGroup, false);
        return new FaqAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqAdapter.viewHolder viewHolder, final int position) {
        faqList.get(position).isShow = false;
        viewHolder.answer.setVisibility(View.GONE);
        viewHolder.arrow.setImageResource(R.drawable.down_arrow);
        viewHolder.question.setText(faqList.get(position).getQuestion());
        viewHolder.answer.setText(faqList.get(position).getAnswer());

        viewHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!faqList.get(position).isShow) {
                    viewHolder.arrow.setImageResource(R.drawable.up_arrow);
                    viewHolder.answer.setVisibility(View.VISIBLE);
                    faqList.get(position).isShow = true;
                } else {
                    viewHolder.arrow.setImageResource(R.drawable.down_arrow);
                    viewHolder.answer.setVisibility(View.GONE);
                    faqList.get(position).isShow = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

}

