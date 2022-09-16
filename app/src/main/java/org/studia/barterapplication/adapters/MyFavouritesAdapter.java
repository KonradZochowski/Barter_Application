package org.studia.barterapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.activities.PostPage;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.Post;
import org.studia.barterapplication.models.User;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This is the adapter class for the recycler view in the favourites page
 */
public class MyFavouritesAdapter extends RecyclerView.Adapter<MyFavouritesAdapter.FavouritesViewHolder> {

    private ArrayList<Post> favourites;
    private Context context;
    private User currentUser;
    private String postTitle;
    private String postUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * This constructor takes the username and lists of favourites. It also takes a reference to the favourites page as a Context object
     * @param currentUser
     * @param favourites
     * @param context
     */
    public MyFavouritesAdapter(User currentUser, ArrayList<Post> favourites, Context context) {
        this.favourites = favourites;
        this.context = context;
        this.currentUser = currentUser;
    }

    /**
     * This method creates a FavouritesViewHolder object which holds references to the gui (view) elements
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view from the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_favourites, parent,false);

        FavouritesViewHolder holder = new FavouritesViewHolder(view);

        return holder;
    }

    /**
     * This method is called when binding rows (elements)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder holder, int position) {
        // Set the textview with related info


        db.collection("postsObj").document(favourites.get(position).getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    postTitle = documentSnapshot.toObject(Post.class).getTitle();
                    postUser = documentSnapshot.toObject(Post.class).getSellerName();
                } else {
                    postTitle = favourites.get(position).getTitle();
                    postUser = favourites.get(position).getSellerName();
                }
                holder.favouritesTitleTextView.setText(postTitle);
                holder.favouritesUserTextView.setText(postUser);
            }
        });

        // Load the image
        Picasso.get().load(favourites.get(position).getPicture()).fit().into(holder.favouritesImageView);

        // When the item is clicked, it will lead to the Post page
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("postsObj").document(favourites.get(position).getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Barter.setCurrentPost(documentSnapshot.toObject(Post.class));
                            Intent intent = new Intent(context, PostPage.class);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Przedmiot został usunięty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // When an item is deleted
        holder.favouritesTrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("current favourites", Barter.getCurrentUser().getFavourites().toString());

                // Remove the item from the list
                favourites.remove(position);
                notifyDataSetChanged();

                // Save the changes to database
                saveUserDataBase(favourites);
            }
        });
    }

    /**
     * This method saves the changes to favourites to the database
     * @param favourites
     */
    private void saveUserDataBase(ArrayList<Post> favourites)
    {
        db.collection("usersObj").document(Barter.getCurrentUser().getDocumentId()).update("favourites", favourites)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Przedmiot został usunięty z listy ulubionych!",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * This method returns the number of elements(rows)
     * @return
     */
    @Override
    public int getItemCount() {
        return favourites.size();
    }

    /**
     * This is an inner class that holds references to the views (gui)
     */
    public class FavouritesViewHolder extends RecyclerView.ViewHolder{
        ImageView favouritesImageView;
        TextView favouritesTitleTextView;
        TextView favouritesUserTextView;
        ConstraintLayout parentLayout;
        ImageButton favouritesTrashBtn;

        public FavouritesViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the elements
            favouritesImageView = itemView.findViewById(R.id.favouritesImageView);
            favouritesTitleTextView = itemView.findViewById(R.id.favouritesTitleTextView);
            favouritesUserTextView = itemView.findViewById(R.id.favouritesUserTextView);
            favouritesTrashBtn = itemView.findViewById(R.id.favouritesTrashBtn);
            parentLayout = itemView.findViewById(R.id.favouritesLayout);
        }
    }
}
