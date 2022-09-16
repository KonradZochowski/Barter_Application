package org.studia.barterapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.studia.barterapplication.activities.AddOffer;
import org.studia.barterapplication.Barter;
import org.studia.barterapplication.activities.OtherUsersProfile;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.Offer;
import org.studia.barterapplication.models.StringWithKey;
import org.studia.barterapplication.models.User;

import java.util.ArrayList;


/**
 * This is an adapter class for the recycler view in the received fragment
 */
public class ReceivedOffersAdapter extends RecyclerView.Adapter<ReceivedOffersAdapter.ReceivedOffersViewHolder> {

    private ArrayList<Offer> receivedOffers;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private RecyclerView.RecycledViewPool otherViewPool = new RecyclerView.RecycledViewPool();
    private Context context;
    private FirebaseFirestore db;
    private User currentSeller;

    // constructor method
    public ReceivedOffersAdapter(ArrayList<Offer> receivedOffers, Context context){
        this.receivedOffers = receivedOffers;
        this.context = context;
    }

    /**
     * This method creates and returns a FeedbackViewHolder object which holds the references to the visuals
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ReceivedOffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view from the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offer_proposition, parent, false);
        ReceivedOffersAdapter.ReceivedOffersViewHolder holder = new ReceivedOffersViewHolder(view);

        db = FirebaseFirestore.getInstance();

        return holder;
    }

    /**
     * This method is called in the creation of each part (row) in the recycler view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ReceivedOffersViewHolder holder, int position) {
        // set the offer status for the text view
        holder.propositionOfferStatusTextView.setText("Status ofery: " + receivedOffers.get(position).getOfferStatus());

        // initialize the items with cash arrays
        ArrayList<String> itemsWithCash = new ArrayList<>();
        ArrayList<String> otherItemsWithCash = new ArrayList<>();

        // add all the items and cash to the other user items array
        for (StringWithKey receiverItem : receivedOffers.get(position).getReceiverItems()) {
            itemsWithCash.add(receiverItem.getString());
        }
        if (receivedOffers.get(position).getReceiverCash()!=0){
            itemsWithCash.add("Gotówka " + receivedOffers.get(position).getReceiverCash() + " zł");
        }

        // add all the items and cash to the other user items array
        for (StringWithKey senderItem : receivedOffers.get(position).getSenderItems()) {
            otherItemsWithCash.add(senderItem.getString());
        }
        if (receivedOffers.get(position).getSenderCash()!=0){
            otherItemsWithCash.add("Gotówka " + receivedOffers.get(position).getSenderCash() + " zł");
        }

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.propositionMyItemsOfferRecyclerView.getContext(), LinearLayoutManager.VERTICAL,false);
        layoutManager.setInitialPrefetchItemCount(itemsWithCash.size());
        LinearLayoutManager otherLayoutManager = new LinearLayoutManager(holder.propositionOtherUserItemsOfferRecyclerView.getContext(), LinearLayoutManager.VERTICAL,false);
        otherLayoutManager.setInitialPrefetchItemCount(otherItemsWithCash.size());

        // Create sub item view adapter
        ItemsAdapter subItemAdapter = new ItemsAdapter(itemsWithCash);
        ItemsAdapter otherSubItemAdapter = new ItemsAdapter(otherItemsWithCash);

        // set the recycler views
        holder.propositionMyItemsOfferRecyclerView.setLayoutManager(layoutManager);
        holder.propositionMyItemsOfferRecyclerView.setAdapter(subItemAdapter);
        holder.propositionMyItemsOfferRecyclerView.setRecycledViewPool(viewPool);

        holder.propositionOtherUserItemsOfferRecyclerView.setLayoutManager(otherLayoutManager);
        holder.propositionOtherUserItemsOfferRecyclerView.setAdapter(otherSubItemAdapter);
        holder.propositionOtherUserItemsOfferRecyclerView.setRecycledViewPool(otherViewPool);

        // change offer status to accepted
        holder.acceptOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barter.updateOfferStatusInDatabase(receivedOffers.get(position).getId(),"Zaakceptowano");
                receivedOffers.get(position).setOfferStatus("Zaakceptowano");
                notifyDataSetChanged();
            }
        });

        // decline the offer and remove from database
        holder.declineOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barter.deleteOffer(receivedOffers.get(position).getId());
                receivedOffers.remove(position);
                notifyDataSetChanged();
            }
        });

        //get current seller name and set it to text view
        db.collection("usersObj").whereEqualTo(FieldPath.documentId(), receivedOffers.get(position).getSenderId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult() ) {
                    currentSeller = documentSnapshot.toObject(User.class);
                    holder.propositionOtherUserItemsOfferTextView.setText("Przedmioty użytkownika: " + currentSeller.getUserName());
                }
            }
        });

        // set on click listener to profile page
        holder.propositionOtherUserItemsOfferTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barter.setCurrentSeller(currentSeller);
                Intent sellerPage = new Intent(context, OtherUsersProfile.class);
                context.startActivity(sellerPage);
            }
        });

        // make counter offer (Add offer activity)
        holder.counterOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barter.setCurrentSeller(currentSeller);
                Intent intent = new Intent(context, AddOffer.class);
                intent.putExtra("documentId", receivedOffers.get(position).getId());
                context.startActivity(intent);
            }
        });


    }

    /**
     * Returns the number of rows
     * @return
     */
    @Override
    public int getItemCount() {
        return receivedOffers.size();
    }

    /**
     * This class helps to hold the references of the gui
     */
    static class ReceivedOffersViewHolder extends RecyclerView.ViewHolder {
        TextView propositionOfferStatusTextView;
        TextView propositionOtherUserItemsOfferTextView;
        Button acceptOfferButton;
        Button declineOfferButton;
        Button counterOfferButton;
        RecyclerView propositionMyItemsOfferRecyclerView;
        RecyclerView propositionOtherUserItemsOfferRecyclerView;

        public ReceivedOffersViewHolder(@NonNull View itemView) {
            super(itemView);
            propositionOfferStatusTextView = itemView.findViewById(R.id.propositionOfferStatusTextView);
            propositionOtherUserItemsOfferTextView = itemView.findViewById(R.id.propositionOtherUserItemsOfferTextView);
            acceptOfferButton = itemView.findViewById(R.id.acceptOfferButton);
            declineOfferButton = itemView.findViewById(R.id.declineOfferButton);
            counterOfferButton = itemView.findViewById(R.id.counterOfferButton);
            propositionMyItemsOfferRecyclerView = itemView.findViewById(R.id.propositionMyItemsOfferRecyclerView);
            propositionOtherUserItemsOfferRecyclerView = itemView.findViewById(R.id.propositionOtherUserItemsOfferRecyclerView);
        }
    }

}
