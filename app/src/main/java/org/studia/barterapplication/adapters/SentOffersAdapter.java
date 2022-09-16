package org.studia.barterapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.activities.OtherUsersProfile;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.Offer;
import org.studia.barterapplication.models.StringWithKey;
import org.studia.barterapplication.models.User;

import java.util.ArrayList;

/**
 * This is an adapter class for the recycler view in the sent fragment
 */
public class SentOffersAdapter extends RecyclerView.Adapter<SentOffersAdapter.SentOffersViewHolder> {

    private ArrayList<Offer> sentOffers;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private RecyclerView.RecycledViewPool otherViewPool = new RecyclerView.RecycledViewPool();
    private Context context;
    private FirebaseFirestore db;
    private User currentSeller;

    // constructor method
    public SentOffersAdapter(ArrayList<Offer> sentOffers, Context context) {
        this.sentOffers = sentOffers;
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
    public SentOffersAdapter.SentOffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view from the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offer_my_proposition, parent, false);
        SentOffersAdapter.SentOffersViewHolder holder = new SentOffersAdapter.SentOffersViewHolder(view);

        db = FirebaseFirestore.getInstance();

        return holder;
    }

    /**
     * This method is called in the creation of each part (row) in the recycler view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull SentOffersViewHolder holder, int position) {

        // set the offer status for the text view
        holder.myPropositionOfferStatusTextView.setText("Status ofery: " + sentOffers.get(position).getOfferStatus());

        // initialize the items with cash arrays
        ArrayList<String> itemsWithCash = new ArrayList<>();
        ArrayList<String> otherItemsWithCash = new ArrayList<>();

        // add all the items and cash to the other user items array
        for (StringWithKey receiverItem : sentOffers.get(position).getReceiverItems()) {
            otherItemsWithCash.add(receiverItem.getString());

        }
        if (sentOffers.get(position).getReceiverCash()!=0){
            otherItemsWithCash.add("Gotówka " + sentOffers.get(position).getReceiverCash() + " zł");

        }

        // add all the items and cash to my items the array
        for (StringWithKey senderItem : sentOffers.get(position).getSenderItems()) {
            itemsWithCash.add(senderItem.getString());
        }
        if (sentOffers.get(position).getSenderCash()!=0){
            itemsWithCash.add("Gotówka " + sentOffers.get(position).getSenderCash() + " zł");
        }

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.myPropositionMyItemsOfferRecyclerView.getContext(), LinearLayoutManager.VERTICAL,false);
        layoutManager.setInitialPrefetchItemCount(itemsWithCash.size());
        LinearLayoutManager otherLayoutManager = new LinearLayoutManager(holder.myPropositionOtherUserItemsOfferRecyclerView.getContext(), LinearLayoutManager.VERTICAL,false);
        otherLayoutManager.setInitialPrefetchItemCount(otherItemsWithCash.size());

        // Create sub item view adapter
        ItemsAdapter subItemAdapter = new ItemsAdapter(itemsWithCash);
        ItemsAdapter otherSubItemAdapter = new ItemsAdapter(otherItemsWithCash);

        // set the recycler views
        holder.myPropositionMyItemsOfferRecyclerView.setLayoutManager(layoutManager);
        holder.myPropositionMyItemsOfferRecyclerView.setAdapter(subItemAdapter);
        holder.myPropositionMyItemsOfferRecyclerView.setRecycledViewPool(viewPool);

        holder.myPropositionOtherUserItemsOfferRecyclerView.setLayoutManager(otherLayoutManager);
        holder.myPropositionOtherUserItemsOfferRecyclerView.setAdapter(otherSubItemAdapter);
        holder.myPropositionOtherUserItemsOfferRecyclerView.setRecycledViewPool(otherViewPool);

        //get current seller name and set it to text view
        db.collection("usersObj").whereEqualTo(FieldPath.documentId(), sentOffers.get(position).getReceiverId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult() ) {
                    currentSeller = documentSnapshot.toObject(User.class);
                    holder.myPropositionOtherUserItemsOfferTextView.setText("Przedmioty użytkownika: " + currentSeller.getUserName());
                }
            }
        });

        // set on click listener to profile page
        holder.myPropositionOtherUserItemsOfferTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barter.setCurrentSeller(currentSeller);
                Intent sellerPage = new Intent(context, OtherUsersProfile.class);
                context.startActivity(sellerPage);
            }
        });

    }

    /**
     * Returns the number of rows
     * @return
     */
    @Override
    public int getItemCount() {
        return sentOffers.size();
    }

    /**
     * This class helps to hold the references of the gui
     */
    static class SentOffersViewHolder extends RecyclerView.ViewHolder {
        TextView myPropositionOfferStatusTextView;
        TextView myPropositionOtherUserItemsOfferTextView;
        RecyclerView myPropositionMyItemsOfferRecyclerView;
        RecyclerView myPropositionOtherUserItemsOfferRecyclerView;

        public SentOffersViewHolder(@NonNull View itemView) {
            super(itemView);
            myPropositionOfferStatusTextView = itemView.findViewById(R.id.myPropositionOfferStatusTextView);
            myPropositionOtherUserItemsOfferTextView = itemView.findViewById(R.id.myPropositionOtherUserItemsOfferTextView);
            myPropositionMyItemsOfferRecyclerView = itemView.findViewById(R.id.myPropositionMyItemsOfferRecyclerView);
            myPropositionOtherUserItemsOfferRecyclerView = itemView.findViewById(R.id.myPropositionOtherUserItemsOfferRecyclerView);
        }
    }

}
