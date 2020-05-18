package app.mazad.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import app.mazad.R;
import app.mazad.classes.FixControl;
import app.mazad.webservices.models.CategoryModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.viewHolder> {
    private Context context;
    private ArrayList<CategoryModel> categoriesList;
    private OnItemClickListener listener;

    public CategoriesAdapter(Context context, ArrayList<CategoryModel> categoriesList, OnItemClickListener listener) {
        this.context = context;
        this.categoriesList = categoriesList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_category_riv_categoryImage)
        RoundedImageView categoryImage;
        @BindView(R.id.item_category_tv_categoryName)
        TextView categoryName;
        @BindView(R.id.loading)
        ProgressBar loading;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public CategoriesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_category, viewGroup, false);
        return new CategoriesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.viewHolder viewHolder, final int position) {
        setImage(categoriesList.get(position).imageUrl, viewHolder);
        viewHolder.categoryName.setText(categoriesList.get(position).getName());
        viewHolder.categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.categoryClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    private void setImage(String imagePath, CategoriesAdapter.viewHolder viewHolder) {
        if (imagePath != null) {
            try {
                int Width = FixControl.getImageWidth(context, R.drawable.cate_larg_noimg);
                int Height = FixControl.getImageHeight(context, R.drawable.cate_larg_noimg);
                viewHolder.categoryImage.getLayoutParams().height = Height;
                viewHolder.categoryImage.getLayoutParams().width = Width;
                Glide.with(context.getApplicationContext()).load(imagePath)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.cate_larg_noimg)
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
                        }).into(viewHolder.categoryImage);
                viewHolder.categoryImage.setCornerRadius(10, 10, 10, 10);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public interface OnItemClickListener {
        public void categoryClick(int position);
    }
}
