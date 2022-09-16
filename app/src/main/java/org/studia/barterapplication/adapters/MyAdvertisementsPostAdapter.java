package org.studia.barterapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.activities.PostPage;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.Advertisements;
import org.studia.barterapplication.models.Post;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This is the adapter class for the recycler view in the Advertisements page
 */
public class MyAdvertisementsPostAdapter extends RecyclerView.Adapter<MyAdvertisementsPostAdapter.AdvertisementsPostViewHolder> implements Filterable {

    // Properties
    private ArrayList<Post> posts;
    private ArrayList<Post> postsFull;
    private Context context;
    private Advertisements advertisements;

    /**
     * This constructor takes a advertisements object and also a reference to AdvertisementsActivity as a Context object
     * @param advertisements
     * @param context
     */
    public MyAdvertisementsPostAdapter(Advertisements advertisements, Context context) {
        this.advertisements = advertisements;
        this.posts = this.advertisements.getPosts();
        postsFull = new ArrayList<>(posts);
        this.context = context;
    }


    /**
     * This method takes a view as parameter so that we can understand where the method call came from (i.e. aToZ, ZtoA, and etc.)
     * @param view
     */
    public void sort( View view) {
        Advertisements filteredAdvertisements = advertisements;
        advertisements.setPosts(posts);

        // Check the id and then apply the related sorting
        if ( view.getId() == R.id.dateNewToOldBtn) {
            filteredAdvertisements.sortByDate(true);
        }

        else if ( view.getId() == R.id.dateOldToNewBtn) {
            filteredAdvertisements.sortByDate(false);
        }

        else if ( view.getId() == R.id.priceLowToHighBtn ) {
            filteredAdvertisements.sortByPrice(true);
        }

        else if ( view.getId() == R.id.priceHighToLowBtn ) {
            filteredAdvertisements.sortByPrice(false);
        }

        // Create a new posts arraylist and call notifyDataSetChanged() method
        posts = new ArrayList<>(filteredAdvertisements.getPosts());
        notifyDataSetChanged();
    }

    /**
     * This method takes voivodeship, city, price, second price information as parameters and applies them to the advertisements
     * @param filteredVoivodeship
     * @param filteredCity
     * @param firstPrice
     * @param secondPrice
     */
    public void filter(String filteredVoivodeship, String filteredCity, int firstPrice, int secondPrice, String filteredCategory) {
        Advertisements filteredAdvertisements = advertisements;

        // Apply the filters
        filteredAdvertisements = filteredAdvertisements.filterByCity(filteredCity,filteredVoivodeship).filterByPrice(firstPrice,secondPrice).filterByCategory(filteredCategory);

        // Create a new posts arraylist and call notifyDataSetChanged() method
        posts = new ArrayList<>(filteredAdvertisements.getPosts());
        notifyDataSetChanged();
    }

    /**
     * This method resets the filtering or sorting changes
     */
    public void resetFilters() {
        posts = postsFull;
        notifyDataSetChanged();
    }

    /**
     * This method creates a AdvertisementsPostViewHolder object which holds references to the gui (view) elements
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public AdvertisementsPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view from the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_advertisements_post,parent,false);

        AdvertisementsPostViewHolder holder = new AdvertisementsPostViewHolder(view);

        return holder;
    }

    /**
     * This method is called when binding rows (elements)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull AdvertisementsPostViewHolder holder, int position) {

        // Set the TextViews with related information
        holder.advertisementsPostDescriptionTextView.setText(posts.get(position).getTitle());
        holder.advertisementsPostDateTextView.setText(Barter.getDateFormat(posts.get(position).getTimestamp()));
        holder.advertisementsPostPriceTextView.setText(String.valueOf(posts.get(position).getPrice()) + " zł");
        holder.advertisementsPostSellerTextView.setText(posts.get(position).getSellerName());

        // If the post has a picture, then load it
        if ( posts.get(position).getPicture() != "" ) {
            Picasso.get().load(posts.get(position).getPicture()).fit().centerCrop().into(holder.advertisementsPostPictureImageView);
        }

        // When the item is clicked, it will lead to the Post page of that post
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostPage.class);
                Barter.setCurrentPost(posts.get(position));
                context.startActivity(intent);
            }
        });
    }

    /**
     * This method returns the number of elements(rows)
     * @return
     */
    @Override
    public int getItemCount() {
        return posts.size();
    }

    /**
     * This method is from Filterable interface and returns a Filter object
     * @return
     */
    @Override
    public Filter getFilter() {
        return advertisementsFilter;
    }

    // Filter class implementation
    private Filter advertisementsFilter = new Filter() {

        /**
         * This method filters the results with a given CharSequence as parameter
         * @param constraint
         * @return
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Post> filteredList = new ArrayList<>();

            if ( constraint == null || constraint.length() == 0 ) {
                filteredList.addAll(postsFull);
            }
            else {
                // Get rid of unnecessary part
                String filterPattern = constraint.toString().toLowerCase().trim();

                // Check all the post titles
                for ( Post aPost: posts ) {
                    if ( aPost.getTitle().toLowerCase().contains(filterPattern) ) {
                        filteredList.add(aPost);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        /**
         * This method helps us to publish the results in the AdvertisementsActivity page
         * @param constraint
         * @param results
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            posts.clear();
            posts.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * This is an inner class that holds references to the views (gui)
     */
    public class AdvertisementsPostViewHolder extends RecyclerView.ViewHolder {
        ImageView advertisementsPostPictureImageView;
        TextView advertisementsPostDescriptionTextView;
        TextView advertisementsPostDateTextView;
        TextView advertisementsPostPriceTextView;
        TextView advertisementsPostSellerTextView;
        ConstraintLayout parentLayout;


        public AdvertisementsPostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the elements
            advertisementsPostPictureImageView = (ImageView) itemView.findViewById(R.id.advertisementsPostPictureImageView);
            advertisementsPostDescriptionTextView = (TextView) itemView.findViewById(R.id.advertisementsPostDescriptionTextView);
            advertisementsPostDateTextView = (TextView) itemView.findViewById(R.id.advertisementsPostDateTextView);
            advertisementsPostPriceTextView = (TextView) itemView.findViewById(R.id.advertisementsPostPriceTextView);
            advertisementsPostSellerTextView = (TextView) itemView.findViewById(R.id.advertisementsPostSellerTextView);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.advertisementsPostLayout);
        }
    }
}
