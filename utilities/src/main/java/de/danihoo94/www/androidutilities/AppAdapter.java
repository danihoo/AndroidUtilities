package de.danihoo94.www.androidutilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class AppAdapter<T extends Comparable<T>, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    @NonNull
    private final FragmentActivity activity;
    @NonNull
    private final List<T> objects;
    private final boolean reverseOrder;

    @Nullable
    private RecyclerView recycler;

    public AppAdapter(@NonNull FragmentActivity activity) {
        this(activity, false);
    }

    public AppAdapter(@NonNull FragmentActivity activity, boolean reverseOrder) {
        this.activity = activity;
        this.objects = new ArrayList<>();
        this.reverseOrder = reverseOrder;
        activity.runOnUiThread(this::notifyDataSetChanged);
    }

    @Nullable
    public RecyclerView getRecycler() {
        return recycler;
    }

    public T getItem(int i) {
        return objects.get(i);
    }

    @NonNull
    public FragmentActivity getActivity() {
        return activity;
    }

    public void add(final T o) {
        for (int i = 0; i < objects.size(); i++) {
            if (!reverseOrder && o.compareTo(objects.get(i)) > 0 || reverseOrder && o.compareTo(objects.get(i)) < 0) {
                objects.add(i, o);
                int finalI = i;
                activity.runOnUiThread(() -> notifyItemInserted(finalI));
                return;
            }
        }
        objects.add(o);
        activity.runOnUiThread(() -> {
            if (objects.size() > 1) {
                notifyItemChanged(objects.size() - 2);
            }
            notifyItemInserted(objects.size() - 1);
        });
    }

    public void addAll(T[] newObjects) {
        for (T o : newObjects) {
            boolean inserted = false;

            for (int i = 0; i < objects.size(); i++) {
                if (o.compareTo(objects.get(i)) > 0) {
                    objects.add(i, o);
                    inserted = true;
                }
            }

            if (!inserted) {
                objects.add(o);
            }
        }

        activity.runOnUiThread(() -> {
                    notifyDataSetChanged();

                    // Cause recycler empty view to show up
                    if (getItemCount() == 0) {
                        notifyItemRemoved(0);
                    }
                }
        );
    }

    public void clear() {
        activity.runOnUiThread(() -> {
                    objects.clear();
                    notifyDataSetChanged();
                }
        );
    }

    public void remove(final int position) {
        objects.remove(position);
        activity.runOnUiThread(() -> {
            if (position == objects.size()) {
                notifyItemChanged(position - 1);
            }
            notifyItemRemoved(position);
        });
    }

    public void refresh() {
        clear();
        populate();
    }

    public abstract void populate();

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recycler) {
        super.onAttachedToRecyclerView(recycler);

        this.recycler = recycler;
    }
}