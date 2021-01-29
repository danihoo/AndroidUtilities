package de.danihoo94.www.androidutilities.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.danihoo94.www.materialcomponents.ExpandableFloatingActionButton;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class HideFabOnScroll extends RecyclerView.OnScrollListener {

    private final ExpandableFloatingActionButton fab;
    private int oldState;

    public HideFabOnScroll(ExpandableFloatingActionButton fab) {
        this.fab = fab;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recycler, int state) {
        if (state == RecyclerView.SCROLL_STATE_DRAGGING && fab.getVisibility() == View.VISIBLE) {
            fab.animateOut(null);
        } else if (oldState == RecyclerView.SCROLL_STATE_DRAGGING) {
            fab.animateIn(null);
        }
        oldState = state;
    }
}
