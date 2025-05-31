package com.parkinson.detection.ui.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter for profile tabs
 */
public class ProfilePagerAdapter extends FragmentStateAdapter {
    
    private static final int TAB_COUNT = 2;
    private static final int TAB_ACCOUNT_INFO = 0;
    private static final int TAB_SECURITY = 1;
    
    public ProfilePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case TAB_ACCOUNT_INFO:
                return new AccountInfoFragment();
            case TAB_SECURITY:
                return new SecurityFragment();
            default:
                throw new IllegalArgumentException("Invalid tab position: " + position);
        }
    }
    
    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
} 