package app.mazad.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.duolingo.open.rtlviewpager.RtlViewPager;

import java.util.ArrayList;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.adapters.ImagesGestureAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesGestureFragment extends Fragment {
    static FragmentActivity activity;
    static ImagesGestureFragment fragment;
    private ArrayList<String> paintings=new ArrayList<>();
    int position;

    @BindView(R.id.fragment_images_gesture_rtl_viewPager)
    RtlViewPager viewPager;
    String photoUrl;
    public static ImagesGestureFragment newInstance(FragmentActivity activity,ArrayList<String> paintings,int position) {
        fragment = new ImagesGestureFragment();
        ImagesGestureFragment.activity = activity;
        fragment.paintings = paintings;
        fragment.position = position;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.fragment_images_gesture, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        //hidden MainAppBarLayout and Ads for making the image fullScreen
        MainActivity.bottomAppbar.setVisibility(View.GONE);
        MainActivity.topAppbar.setVisibility(View.GONE);
        viewPager.setAdapter(new ImagesGestureAdapter(activity, paintings));
        viewPager.setCurrentItem(position);

    }
}

