package org.studia.barterapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.studia.barterapplication.R;
import org.studia.barterapplication.models.StringWithKey;

import java.util.ArrayList;

/**
 * This is an adapter class for the recycler view in the add offer activity
 */
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder>{

    ArrayList<String> items;
    ArrayList<StringWithKey> chosenItemsWithKey;

    // constructor method
    public OfferAdapter(ArrayList<String> items, ArrayList<StringWithKey> chosenItemsWithKey) {
        this.items = items;
        this.chosenItemsWithKey = chosenItemsWithKey;
    }

    /**
     * This method creates and returns a FeedbackViewHolder object which holds the references to the visuals
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view from the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offer_item_editable, parent, false);
        OfferViewHolder holder = new OfferViewHolder(view);

        return holder;
    }

    /**
     * This method is called in the creation of each part (row) in the recycler view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        // set the item title for the text view
        holder.offerItemTextView.setText(items.get(position));

        // set the delete button to remove from chosen items list
        holder.deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.remove(position);
                notifyDataSetChanged();
                chosenItemsWithKey.remove(position);
            }
        });
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
    static class OfferViewHolder extends RecyclerView.ViewHolder {

        TextView offerItemTextView;
        Button deleteItemButton;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);

            offerItemTextView = itemView.findViewById(R.id.offerItemTitleTextView);
            deleteItemButton = itemView.findViewById(R.id.deleteItemButton);
        }
    }
}

