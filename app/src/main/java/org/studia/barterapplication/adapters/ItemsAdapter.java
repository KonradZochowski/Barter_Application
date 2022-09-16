package org.studia.barterapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.studia.barterapplication.R;

import java.util.ArrayList;

/**
 * This is an adapter class for the recycler view in the received and sent offer adapters
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {

    ArrayList<String> items;

    // constructor method
    public ItemsAdapter(ArrayList<String> itemsTitle) {
        this.items = itemsTitle;
    }


    /**
     * This method creates and returns a FeedbackViewHolder object which holds the references to the visuals
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view from the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offer_item, parent, false);
        ItemsAdapter.ItemsViewHolder holder = new ItemsAdapter.ItemsViewHolder(view);

        return holder;
    }

    /**
     * This method is called in the creation of each part (row) in the recycler view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {

        // set the item title for text view
        holder.offerItemTitleTextView.setText(items.get(position));
    }

    /**
     * Returns the number of rows
     * @return
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * This class helps to hold the references of the gui
     */
    static class ItemsViewHolder extends RecyclerView.ViewHolder {
        TextView offerItemTitleTextView;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            offerItemTitleTextView = itemView.findViewById(R.id.offerItemTitleTextView);
        }
    }
}
