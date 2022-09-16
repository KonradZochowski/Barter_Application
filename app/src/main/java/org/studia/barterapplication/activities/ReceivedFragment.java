package org.studia.barterapplication.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.adapters.ReceivedOffersAdapter;
import org.studia.barterapplication.models.Offer;
import org.studia.barterapplication.models.User;

import java.util.ArrayList;

/**
 * This is the class of the Received fragment
 */
public class ReceivedFragment extends Fragment {

    private RecyclerView offersRecyclerView;
    private FirebaseFirestore db;
    private ArrayList<Offer> offers;
    private User currentUser;
    private String currentUserId;

    /**
     * This is the first method called when an object of this class is created
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offers, container, false);
    }

    /**
     * This method fills all the fields needed on this view
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the database object
        db = FirebaseFirestore.getInstance();

        // Initialize offers list and set current user and current user Id
        offers = new ArrayList<>();
        currentUser = Barter.getCurrentUser();
        currentUserId = currentUser.getDocumentId();

        // take all the offers where senderId is equal to currentUserId and set the recyclerview
        db.collection("offersObj").whereEqualTo("receiverId",currentUserId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots) {
                    offers.add(documentSnapshots.toObject(Offer.class));
                }
                ReceivedOffersAdapter receivedOffersAdapter = new ReceivedOffersAdapter(offers, getContext());
                offersRecyclerView.setAdapter(receivedOffersAdapter);
            }
        });

        offersRecyclerView = view.findViewById(R.id.offersRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        offersRecyclerView.setLayoutManager(layoutManager);
    }



}
