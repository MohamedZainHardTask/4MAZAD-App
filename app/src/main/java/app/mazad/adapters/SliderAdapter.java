package app.mazad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.classes.FixControl;
import app.mazad.classes.Navigator;
import app.mazad.fragments.ImagesGestureFragment;
import app.mazad.webservices.models.AuctionDetailsModel;

public class SliderAdapter extends PagerAdapter {
    Context context;
    ArrayList<AuctionDetailsModel.AttachmentModel> attachmentList;

    public SliderAdapter(Context context, ArrayList<AuctionDetailsModel.AttachmentModel> attachmentList) {
        this.context = context;
        this.attachmentList = attachmentList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View childView = LayoutInflater.from(context).inflate(R.layout.item_slider, container, false);
        ImageView sliderImage = (ImageView) childView.findViewById(R.id.item_slider_iv_sliderImg);

        if (attachmentList.get(position).fileUrl != null
                && !attachmentList.get(position).fileUrl.isEmpty()) {
            setImage(attachmentList.get(position).fileUrl, sliderImage);
        }

        sliderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> paths = new ArrayList<>();
                for (AuctionDetailsModel.AttachmentModel item : attachmentList) {
                    paths.add(item.fileUrl);
                }
                Navigator.loadFragment((FragmentActivity) context, ImagesGestureFragment.newInstance((FragmentActivity) context, paths, position), R.id.activity_main_fl_appContainer, true);
            }
        });

        container.addView(childView, 0);
        return childView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return attachmentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }

    private void setImage(String imagePath, ImageView image) {
        if (imagePath != null) {
            try {
                int Width = FixControl.getImageWidth(context, R.drawable.product_details);
                int Height = FixControl.getImageHeight(context, R.drawable.product_details);
                image.getLayoutParams().height = Height;
                image.getLayoutParams().width = Width;
                Glide.with(context.getApplicationContext()).load(imagePath)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.drawable.product_details))
                        .into(image);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}


