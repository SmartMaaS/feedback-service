package dfki.com.smartmaas.feedbackservice.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bikcrum.locationupdate.LocationUpdate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.IOException;
import java.util.List;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.fragment.FeedbackFragment;
import dfki.com.smartmaas.feedbackservice.fragment.LoginFragment;
import dfki.com.smartmaas.feedbackservice.fragment.PreliminaryFragment;
import dfki.com.smartmaas.feedbackservice.fragment.ProfileFragment;
import dfki.com.smartmaas.feedbackservice.fragment.SettingsFragment;
import dfki.com.smartmaas.feedbackservice.intrface.FragmentController;
import dfki.com.smartmaas.feedbackservice.model.CustomFragment;
import dfki.com.smartmaas.feedbackservice.util.Utils;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends BaseActivity implements FragmentController, LocationUpdate.OnLocationUpdatedListener {
    private static final String TAG = MainActivity.class.getName();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction;
    private final FeedbackFragment feedbackFragment = new FeedbackFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();
    private LoginFragment loginFragment = new LoginFragment();
    private BottomNavigationView bottomNavigationView;
    private boolean singleBack;
    private CustomFragment activeFragment = feedbackFragment;
    private LocationUpdate locationUpdate;
    private dfki.com.smartmaas.feedbackservice.model.Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(tag, getResources().getString(
//                                    R.string.firebase_unsuccess_message), task.getException());
//                            return;
//                        }
//                        if (task.getResult() != null) {
//                            String token = task.getResult().getToken();
//                            Utils.saveStringToPreferences(getApplicationContext(),
//                                    getResources().getString(R.string.firebase_token_SH_PR_key), token);
//                        }
//                    }
//                });
        initialiseLocationUpdate(savedInstanceState);
        initialiseBottomNavigationView();
        initialisePreliminaryFragment();
        initialiseKeyboard();
    }

    private void initialisePreliminaryFragment() {
        PreliminaryFragment preliminaryFragment = new PreliminaryFragment();
        replaceFragment(preliminaryFragment, preliminaryFragment.getCustomTAG(),
                false, 0, 0);
    }

    private void initialiseLocationUpdate(Bundle savedInstanceState) {
        locationUpdate = LocationUpdate.getInstance(this);
        locationUpdate.onCreate(savedInstanceState);
    }

    private void initialiseBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.feedbackMenuItem:
                                if (activeFragment != feedbackFragment) {
                                    replaceFragment(feedbackFragment, feedbackFragment.getCustomTAG(),
                                            true, 0, 0);
                                    activeFragment = feedbackFragment;
                                }
                                break;
                            case R.id.profileMenuItem:
                                if (activeFragment != profileFragment) {
                                    replaceFragment(profileFragment, profileFragment.getCustomTAG(),
                                            true, 0, 0);
                                    activeFragment = profileFragment;
                                }
                                break;
                            case R.id.settingsMenuItem:
                                if (activeFragment != settingsFragment) {
                                    replaceFragment(settingsFragment, settingsFragment.getCustomTAG(),
                                            true, 0, 0);
                                    activeFragment = settingsFragment;
                                }
                                break;
                        }
                        return false;
                    }
                });
    }

    private void initialiseKeyboard() {
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            bottomNavigationView.setVisibility(View.GONE);
                        } else {
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void replaceFragment(CustomFragment fragment, String tag, boolean addToBackStack,
                                int anim_in, int anim_out) {
        activeFragment = fragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(anim_in, anim_out);
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.commit();
    }

    @Override
    public void attachFragment(CustomFragment fragment, String tag, boolean addToBackStack,
                               int anim_in, int anim_out) {
        activeFragment = fragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(anim_in, anim_out);
        fragmentTransaction.attach(fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();

    }

    @Override
    public void detachFragment(CustomFragment fragment, String tag, boolean addToBackStack,
                               int anim_in, int anim_out) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(anim_in, anim_out);
        fragmentTransaction.detach(fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void removeFragment(CustomFragment fragment) {
        fragmentManager.beginTransaction().remove(fragment).commit();
    }

    @Override
    public void addFragment(CustomFragment fragment, String tag, boolean addToBackStack,
                            int anim_in, int anim_out) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(anim_in, anim_out);
        fragmentTransaction.add(R.id.container, fragment, fragment.getTag());
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (singleBack || fragmentManager.getBackStackEntryCount() > 0) {
            removeFragment(activeFragment);
            super.onBackPressed();
            List<Fragment> fragments = fragmentManager.getFragments();
            activeFragment = (CustomFragment) fragments.get(fragments.size() - 1);
            setMenuItemActive(activeFragment.getCustomTAG());
            return;
        }

        this.singleBack = true;
        Utils.makeShortToast(this, getResources().getString(R.string.double_click_to_exit_message));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                singleBack = false;
            }
        }, 2000);
    }

    public LoginFragment getLoginFragment() {
        return loginFragment;
    }

    public FeedbackFragment getFeedbackFragment() {
        if (feedbackFragment == null) {
            return new FeedbackFragment();
        }
        return feedbackFragment;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }

    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }

    public CustomFragment getActiveFragment() {
        return activeFragment;
    }

    public void setActiveFragment(CustomFragment activeFragment) {
        this.activeFragment = activeFragment;
    }

    public void setMenuItemActive(String tag) {
        if (tag.equals(feedbackFragment.getCustomTAG())) {
            bottomNavigationView.getMenu().findItem(R.id.feedbackMenuItem).setChecked(true);
        } else if (tag.equals(profileFragment.getCustomTAG())) {
            bottomNavigationView.getMenu().findItem(R.id.profileMenuItem).setChecked(true);
        } else if (tag.equals(settingsFragment.getCustomTAG())) {
            bottomNavigationView.getMenu().findItem(R.id.settingsMenuItem).setChecked(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationUpdate.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationUpdate.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        locationUpdate.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLocationUpdated(Location mCurrentLocation, String mLastUpdateTime) {
        double lat = mCurrentLocation.getLatitude();
        double lng = mCurrentLocation.getLongitude();

        if (feedbackFragment.getLocation() != null) {
            this.location = feedbackFragment.getLocation();
        } else {
            this.location = new dfki.com.smartmaas.feedbackservice.model.Location();
        }
        this.location.setLat(lat);
        this.location.setLng(lng);
        try {
            this.location.setName(Utils.convertLatLongToAddress(lat, lng, this));
        } catch (IOException e) {
            e.printStackTrace();
            Utils.makeLongToast(getApplicationContext(), "Current location couldn't be identified. " +
                    "Latitude(" + lat + ") and longitude(" + lng + ") cannot be converted to an address.");
            Log.e(TAG, "Latitude(" + lat + ") and longitude(" + lng + ") cannot be converted to an address name.");
        }
        locationUpdate.stopLocationUpdates();
        feedbackFragment.setLocation(this.location);
    }

    public LocationUpdate getLocationUpdate() {
        return locationUpdate;
    }

    public dfki.com.smartmaas.feedbackservice.model.Location getLocation() {
        return this.location;
    }
}

