package app.mazad.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import app.mazad.R;
import app.mazad.activities.MainActivity;
import app.mazad.classes.Navigator;
import app.mazad.classes.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TutorialsFragment extends Fragment {
    public static FragmentActivity activity;
    public static TutorialsFragment fragment;
    private View childView;
    private SessionManager sessionManager;

    @BindView(R.id.fragment_tutorial_iv_tutorialImg)
    ImageView tutorialImg;
    @BindView(R.id.fragment_tutorial_iv_next)
    ImageView next;

    public static TutorialsFragment newInstance(FragmentActivity activity) {
        fragment = new TutorialsFragment();
        TutorialsFragment.activity = activity;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_tutorials, container, false);
        ButterKnife.bind(this, childView);
        return childView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        MainActivity.setupAppbar(false, false, false, false, null);
        sessionManager = new SessionManager(activity);
    }

    @OnClick(R.id.fragment_tutorial_iv_next)
    public void nextCLick() {
        if (tutorialImg.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.tutorial1).getConstantState()) {
            tutorialImg.setImageResource(R.drawable.tutorial2);
        } else if (tutorialImg.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.tutorial2).getConstantState()) {
            tutorialImg.setImageResource(R.drawable.tutorial3);
        } else if (tutorialImg.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.tutorial3).getConstantState()) {
            tutorialImg.setImageResource(R.drawable.tutorial4);
        } else if (tutorialImg.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.tutorial4).getConstantState()) {
            tutorialImg.setImageResource(R.drawable.tutorial5);
            next.setImageResource(R.drawable.done);
        } else if (tutorialImg.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.tutorial5).getConstantState()) {
            Navigator.loadFragment(activity, LoginFragment.newInstance(activity, ""), R.id.activity_main_fl_appContainer, true);
            sessionManager.setShowTutorial(true);
        }

    }
}






