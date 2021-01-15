package de.danihoo94.www.androidutilities.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import de.danihoo94.www.androidutilities.R;
import de.danihoo94.www.materialcomponents.MaterialSpinner;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class EditDialog extends ToolbarFullscreenDialog implements TextWatcher, MaterialSpinner.OnSelectionChangedListener {

    public static final String ARG_NEW = "new";
    public static final String ARG_ID = "id";

    private MenuItem saveAction;
    private MenuItem deleteAction;

    public EditDialog() {
        // required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // setup toolbar
        saveAction = toolbar.getMenu().findItem(R.id.action_save);
        deleteAction = toolbar.getMenu().findItem(R.id.action_delete);

        this.saveAction.setOnMenuItemClickListener(item -> {
            performSave();
            return true;
        });

        this.deleteAction.setOnMenuItemClickListener(item -> {
            performDelete();
            return true;
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // enable/disable actions
        setDeleteEnabled(!isNew);
        setSaveEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dialog, menu);
    }

    private void setSaveEnabled(final boolean enabled) {
        getActivity().runOnUiThread(() -> {
            AnimatedVectorDrawableCompat drawable = null;
            if (enabled && !saveAction.isEnabled()) {
                drawable = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.save_enable);
            } else if (!enabled && saveAction.isEnabled()) {
                drawable = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.save_disable);
            }

            saveAction.setEnabled(enabled);
            if (drawable != null) {
                saveAction.setIcon(drawable);
                drawable.start();
            }
        });
    }

    private void setDeleteEnabled(final boolean enabled) {
        getActivity().runOnUiThread(() -> {
            AnimatedVectorDrawableCompat drawable = null;
            if (enabled && !deleteAction.isEnabled()) {
                drawable = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.delete_enable);
            } else if (!enabled && deleteAction.isEnabled()) {
                drawable = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.delete_disable);
            }

            deleteAction.setEnabled(enabled);
            if (drawable != null) {
                deleteAction.setIcon(drawable);
                drawable.start();
            }
        });
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //empty
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //empty
    }

    public void afterTextChanged(Editable s) {
        setSaveEnabled(validate());
    }

    @Override
    public void onSelectionChanged(MaterialSpinner spinner, int index) {
        setSaveEnabled(validate());
    }

    protected void afterInsert() {
        isNew = false;
        setDeleteEnabled(true);
        setSaveEnabled(false);
    }

    protected void afterUpdate() {
        setDeleteEnabled(true);
        setSaveEnabled(false);
    }

    protected void afterDelete() {
        dismiss();
    }

    protected abstract boolean validate();

    protected abstract void performDelete();

    protected abstract void performSave();
}
