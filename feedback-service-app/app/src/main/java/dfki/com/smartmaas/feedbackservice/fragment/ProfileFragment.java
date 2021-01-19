package dfki.com.smartmaas.feedbackservice.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.activity.MainActivity;
import dfki.com.smartmaas.feedbackservice.model.CustomFragment;
import dfki.com.smartmaas.feedbackservice.util.Utils;

//import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends CustomFragment {
    private static final String TAG = ProfileFragment.class.getName();
    private Button logOutButton;
    private SharedPreferences sharedPreferences;
    private MainActivity activity;
    private ConstraintLayout navigationMenu;
    private TextView usernameTxView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.activity = (MainActivity) context;
        } else this.activity = (MainActivity) getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        sharedPreferences = activity.getSharedPreferences(
                getResources().getString(R.string.authent_shrd_prfs_key), 0);
        initialiseViews(view);
        initialiseLogOutBttn();
        return view;
    }

    private void initialiseViews(View view) {
        logOutButton = view.findViewById(R.id.logOutBttnID);
//        TextView emailTextView = view.findViewById(R.id.emailTxVwIDPrflFrgmnt);
//        emailTextView.setText(sharedPreferences.getString(
//                getResources().getString(R.string.user_email_shrd_prf_key), null));
        navigationMenu = activity.findViewById(R.id.container);
        usernameTxView = view.findViewById(R.id.usernameTxVwIDPrflFragment);
        usernameTxView.setText("Username: " + Utils.fetchStringFromPreferences(activity.getApplicationContext(),
                getResources().getString(R.string.username_key_shrd_prf)));
    }

    private void initialiseLogOutBttn() {
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();

                Utils.cleanSharedPreferences(activity.getApplicationContext());
                openLoginFragment();
            }
        });
    }

    private void openLoginFragment() {
        activity.replaceFragment(activity.getLoginFragment(),
                activity.getLoginFragment().getCustomTAG(),
                false, 0, 0);
        Utils.hideNavigationBottomView(navigationMenu);
    }

    @Override
    public String getCustomTAG() {
        return TAG;
    }
}
