package de.danihoo94.www.androidutilities.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class AppAdapter<T extends Comparable<T>, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
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
                    break;
                }
            }

            if (!inserted) {
                objects.add(o);
            }
        }

        activity.runOnUiThread(() -> {
                    // Cause recycler empty view to show up
                    if (getItemCount() == 0) {
                        notifyItemRemoved(0);
                    } else {
                        notifyDataSetChanged();
                    }
                }
        );
    }

    public void clear() {
        objects.clear();
        activity.runOnUiThread(this::notifyDataSetChanged);
    }

    public void remove(final int position) {
        objects.remove(position);
        activity.runOnUiThread(() -> {
            notifyItemRemoved(position);

            // Cause recycler empty view to show up
            if (position == objects.size()) {
                notifyItemChanged(position - 1);
            }
        });
    }

    public void refresh() {
        objects.clear();
        populate();
    }

    public int getItemPosition(T o) {
        return objects.indexOf(o);
    }

    public void replaceItem(int index, T o) {
        objects.set(index, o);
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