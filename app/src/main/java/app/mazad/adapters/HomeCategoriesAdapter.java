package app.mazad.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
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

public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter.viewHolder> {
    private Context context;
    private ArrayList<CategoryModel> categoriesList;
    private OnItemClickListener listener;

    public HomeCategoriesAdapter(Context context, ArrayList<CategoryModel> categoriesList, OnItemClickListener listener) {
        this.context = context;
        this.categoriesList = categoriesList;
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_home_category_riv_categoryImage)
        RoundedImageView categoryImage;
        @BindView(R.id.item_home_category_tv_categoryName)
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
    public HomeCategoriesAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_home_category, viewGroup, false);
        return new HomeCategoriesAdapter.viewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCategoriesAdapter.viewHolder viewHolder, final int position) {
        setImage(categoriesList.get(position).imageUrl, viewHolder);
        String categoryName = categoriesList.get(position).getName();
        if (categoryName.length() > 10)
            viewHolder.categoryName.setText(categoryName.substring(0, 9) + "...");
        else
            viewHolder.categoryName.setText(categoryName);

        viewHolder.categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.categoryProductsClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    private void setImage(String imagePath, viewHolder viewHolder) {
        if (imagePath != null) {
            try {
                int Width = FixControl.getImageWidth(context, R.drawable.cate_small_noimg);
                int Height = FixControl.getImageHeight(context, R.drawable.cate_small_noimg);
                viewHolder.categoryImage.getLayoutParams().height = Height;
                viewHolder.categoryImage.getLayoutParams().width = Width;
                Glide.with(context.getApplicationContext()).load(imagePath)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.cate_small_noimg)
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
        public void categoryProductsClick(int position);
    }
}



