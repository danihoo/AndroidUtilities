package de.danihoo94.www.androidutilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class EmptyViewRecycler extends RecyclerView {
    private View view;

    private final AdapterDataObserver observer = new AdapterDataObserver() {

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkVisibility();
        }

        @Override
        public void onItemRangeRemoved(int a, int b) {
            checkVisibility();
        }
    };

    public EmptyViewRecycler(@NonNull Context context) {
        super(context);
    }

    public EmptyViewRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyViewRecycler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(View view) {
        this.view = view;
    }

    @SuppressWarnings("rawtypes")
    private void checkVisibility() {
        Adapter adapter = getAdapter();
        if (adapter != null && view != null) {
            if (adapter.getItemCount() == 0) {
                view.setVisibility(View.VISIBLE);
                EmptyViewRecycler.this.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.GONE);
                EmptyViewRecycler.this.setVisibility(View.VISIBLE);
            }
        }
    }
}