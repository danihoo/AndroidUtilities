package de.danihoo94.www.androidutilities.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import de.danihoo94.www.androidutilities.R;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class LoadOverlay extends DialogFragment {
    public static final String TAG = "load";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(false);
        Objects.requireNonNull(requireDialog().getWindow()).getAttributes().windowAnimations = R.style.LoadAnimation;
    }

    public void show(FragmentManager fm) {
        show(fm, TAG);
    }
}
