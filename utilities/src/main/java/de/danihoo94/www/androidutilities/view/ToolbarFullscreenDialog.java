package de.danihoo94.www.androidutilities.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.danihoo94.www.androidutilities.R;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class ToolbarFullscreenDialog extends DialogFragment {
    public static final String ARG_PARENT = "parent";

    protected Toolbar toolbar;
    protected String parentTag;

    public ToolbarFullscreenDialog() {
        // required empty constructor
    }

    @Nullable
    public Fragment getParent() {
        if (parentTag != null) {
            return requireActivity().getSupportFragmentManager().findFragmentByTag(parentTag);
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.dialog_frame, container, false);
        inflater.inflate(getLayout(), view, true);

        // setup toolbar
        this.toolbar = view.findViewById(R.id.dialog_toolbar);
        setupToolbar();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void show(@NonNull FragmentManager fm) {
        super.show(fm, getTagString());
    }

    protected String getTagString() {
        return "fullscreenDialog";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

        if (getArguments() != null) {
            this.parentTag = getArguments().getString(ARG_PARENT);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (parentTag != null) {
            outState.putString(ARG_PARENT, parentTag);
        }
    }

    private void setupToolbar() {

        // menu
        onCreateOptionsMenu(toolbar.getMenu(), requireActivity().getMenuInflater());

        // change title
        changeToolbarText(getTitle());

        // navigation icon
        toolbar.setNavigationOnClickListener(v -> dismiss());

        // find and animate navigation view
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v instanceof ImageButton) {
                RotateAnimation rotate = new RotateAnimation(0f, 180f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setInterpolator(new AccelerateDecelerateInterpolator());
                rotate.setDuration(2 * getResources().getInteger(R.integer.dialog_anim_time));
                v.startAnimation(rotate);
                break;
            }
        }
    }

    private void changeToolbarText(final String title) {

        TextView view = null;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof TextView) {
                view = (TextView) toolbar.getChildAt(i);
                break;
            }
        }

        if (view != null) {
            AlphaAnimation fade = new AlphaAnimation(0f, 1f);
            fade.setInterpolator(new LinearInterpolator());
            fade.setDuration(getResources().getInteger(R.integer.dialog_anim_time));
            view.setText(title);
            view.startAnimation(fade);
        }
    }

    protected abstract String getTitle();

    protected abstract int getLayout();
}
