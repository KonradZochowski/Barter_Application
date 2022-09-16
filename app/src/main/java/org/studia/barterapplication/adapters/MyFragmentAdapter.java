package org.studia.barterapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.studia.barterapplication.activities.ReceivedFragment;
import org.studia.barterapplication.activities.SentFragment;

/**
 * This is an adapter class for the my offers activity
 */
public class MyFragmentAdapter extends FragmentStateAdapter {
    public MyFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new SentFragment();
        }
        return new ReceivedFragment();
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
