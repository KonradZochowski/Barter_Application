package org.studia.barterapplication.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.studia.barterapplication.Barter;
import org.studia.barterapplication.R;
import org.studia.barterapplication.models.Category;

import java.util.ArrayList;

/**
 * This class represents a dialog box that pops up when menu icon in the advertisements is clicked
 */
public class AdvertisementsMenuDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener{

    private EditText firstPriceEditText;
    private EditText secondPriceEditText;

    private TextView enterVoivodeshipTextView;
    private TextView enterCityTextView;
    private TextView enterCategoryTextView;

    private Spinner voivodeshipSpinner;
    private Spinner citySpinner;
    private Spinner categorySpinner;

    private AdvertisementsMenuDialogListener listener;

    private Context parentContext;

    private String filteredVoivodeship;
    private String filteredCity;

    private FirebaseFirestore db;
    private ArrayList<String> allCategoryNames;
    private String selectedCategoryName;


    /**
     * This is the first method called when an instance of this class is created
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for the menu dialog and create a view object
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_advertisements_menu_dialog,null);

        // Set title
        builder.setView(view);
        builder.setTitle("Filtruj ogłoszenia");

        // Give a negative button to close
        builder.setNegativeButton("Zamknij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Give a positive button to apply the changes
        builder.setPositiveButton("Zastosuj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the prices
                String firstPrice = firstPriceEditText.getText().toString();
                String secondPrice = secondPriceEditText.getText().toString();

                if (firstPrice.equals("")){
                    firstPrice = "0";
                }
                if (secondPrice.equals("")){
                    secondPrice = "999999999";
                } else if (secondPrice.length() > 9){
                    Toast.makeText(getContext(), "Maksymalna cena to 999 999 999 zł", Toast.LENGTH_SHORT).show();
                    secondPrice = "999999999";
                }

                // Call the Advertisements activity (which is the listener) to apply filterings
                listener.applyTexts(filteredVoivodeship, filteredCity, firstPrice, secondPrice, selectedCategoryName);

                listener.setVoivodeship(filteredVoivodeship);
                listener.setCity(filteredCity);
                listener.setFirstPrice(firstPrice);
                listener.setSecondPrice(secondPrice);
                listener.setCategory(selectedCategoryName);
            }
        });

        // This clears all the applied filters
        builder.setNeutralButton("Wyczyść filtr", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((AdvertisementsActivity) listener).resetFilters();
                listener.setVoivodeship(null);
                listener.setCity(null);
                listener.setFirstPrice(null);
                listener.setSecondPrice(null);
                listener.setCategory(null);
            }
        });

        db = FirebaseFirestore.getInstance();

        allCategoryNames = new ArrayList<>();
        db.collection("categoriesObj").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                allCategoryNames.add("Wszystkie kategorie");
                for(QueryDocumentSnapshot documentSnapshots: queryDocumentSnapshots)
                {
                    // Pull the category and add to arraylists
                    Category aCategoryFromBase = documentSnapshots.toObject(Category.class);
                    Log.d("name", aCategoryFromBase.getCategoryName());
                    allCategoryNames.add(aCategoryFromBase.getCategoryName());
                }

            }
        })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if ( task.isSuccessful() ) {

                            // When the process is completed, set the adapter for the categoryNameSpinner which displays all the category names
                            ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, allCategoryNames);

                            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            categorySpinner.setAdapter(categoryAdapter);
                            if (listener.getCategory() != null){
                                categorySpinner.setSelection(((ArrayAdapter) categorySpinner.getAdapter()).getPosition(listener.getCategory()));
                            }
                        }
                    }
                });


        // Initialize all the edit texts and spinners and text views
        firstPriceEditText = (EditText) view.findViewById(R.id.firstPriceEditText);
        secondPriceEditText = (EditText) view.findViewById(R.id.secondPriceEditText);
        enterVoivodeshipTextView = (TextView) view.findViewById(R.id.enterVoivodeshipTextView);
        enterCityTextView = (TextView) view.findViewById(R.id.enterCityTextView);
        enterCategoryTextView = (TextView) view.findViewById(R.id.enterCategoryTextView);
        voivodeshipSpinner = (Spinner) view.findViewById(R.id.voivodeshipSpinner);
        citySpinner = (Spinner) view.findViewById(R.id.citySpinner);
        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);


        enterVoivodeshipTextView.setText(R.string.enter_voivodeship);
        enterCityTextView.setText(R.string.enter_city);
        enterCategoryTextView.setText(R.string.enter_category);

        // ArrayAdapter for the spinner
        ArrayAdapter<CharSequence> voivodeshipAdapter = ArrayAdapter.createFromResource(parentContext,R.array.voivodeships, android.R.layout.simple_spinner_item);

        // Set the drop down view resource
        voivodeshipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter
        voivodeshipSpinner.setAdapter(voivodeshipAdapter);



        // Give listeners to spinners which is this class
        voivodeshipSpinner.setOnItemSelectedListener( (AdapterView.OnItemSelectedListener) this);
        citySpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        categorySpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Set saved before selections
        if (listener.getVoivodeship() != null){
            voivodeshipSpinner.setSelection(((ArrayAdapter) voivodeshipSpinner.getAdapter()).getPosition(listener.getVoivodeship()));
        }
        if (listener.getFirstPrice() != null){
            firstPriceEditText.setText(listener.getFirstPrice());
        }
        if (listener.getFirstPrice() != null){
            secondPriceEditText.setText(listener.getSecondPrice());
        }

        return builder.create();
    }





    /**
     * This method is called when this dialog box is called from an activity (context). This helps to hold references to that
     * activity (in this case it's advertisements)
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        parentContext = context;

        try {
            listener = (AdvertisementsMenuDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AdvertisementsMenuDialogListener");
        }
    }

    /**
     * This method is automatically called by the spinner.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Store the selected data in filteredVoivodeship and filteredCity
        if ( parent.getId() == voivodeshipSpinner.getId() ) {
            filteredVoivodeship = voivodeshipSpinner.getItemAtPosition(position).toString();
            fillCitiesSpinner(Barter.chooseCitiesFromVoivodeship(filteredVoivodeship));
        }
        else if ( parent.getId() == citySpinner.getId() ) {
            filteredCity = citySpinner.getItemAtPosition(position).toString();
        }
        else if ( parent.getId() == categorySpinner.getId() ) {
            selectedCategoryName = categorySpinner.getItemAtPosition(position).toString();
        }
    }

    /**
     * Must be implemented
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This is a function to set city adapter
     */
    private void fillCitiesSpinner(@ArrayRes int array){
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(parentContext, array, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        if (listener.getCity() != null){
            citySpinner.setSelection(((ArrayAdapter) citySpinner.getAdapter()).getPosition(listener.getCity()));
        }
    }

    /**
     * This interface is implemented by the AdvertisementsActivity
     */
    public interface AdvertisementsMenuDialogListener {
        void applyTexts(String filteredVoivodeship, String filteredCity, String firstPrice, String secondPrice, String filteredCategory);

        String getVoivodeship();
        void setVoivodeship(String savedVoivodeship);
        String getCity();
        void setCity(String savedCity);
        String getFirstPrice();
        void setFirstPrice(String savedFirstPrice);
        String getSecondPrice();
        void setSecondPrice(String savedSecondPrice);
        String getCategory();
        void setCategory(String savedCategory);
    }

}
