package dfki.com.smartmaas.feedbackservice.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.activity.MainActivity;
import dfki.com.smartmaas.feedbackservice.model.CustomFragment;
import dfki.com.smartmaas.feedbackservice.util.Utils;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends CustomFragment {
    private final static String TAG = LoginFragment.class.getName();
    private LinearLayout registerLoginLayout;
    private TextView registrationTextView, backToLoginTextView;
    private EditText usernameEdTx, passEdTx;
    private CheckBox loggedInChBox;
    private Button loginButton;
    //    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private MainActivity activity;
    private ConstraintLayout navigationMenu;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        } else {
            activity = (MainActivity) getContext();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
//        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = activity.getSharedPreferences(getResources().
                getString(R.string.authent_shrd_prfs_key), 0);
        initialiseViews(view);
        initialiseRegisterTextView();
        authenticateUser();
        return view;
    }

    private void initialiseViews(View view) {
        backToLoginTextView = view.findViewById(R.id.backToLoginTextViewID);
        backToLoginTextView.setOnClickListener(view1 -> logToRegisterAndViceVersa(false));
        progressBar = view.findViewById(R.id.progressBarLoginID);
        usernameEdTx = view.findViewById(R.id.usernameEdTx);
        passEdTx = view.findViewById(R.id.passEdTx);
        loggedInChBox = view.findViewById(R.id.loginChBox);
        loggedInChBox.setOnCheckedChangeListener((buttonView, isChecked) -> Utils.hideKeyboard(activity, buttonView));
        loginButton = view.findViewById(R.id.loginBttn);
        registerLoginLayout = view.findViewById(R.id.registr_logged_in_layout_id);
        registrationTextView = view.findViewById(R.id.registrationTextViewId);
        navigationMenu = activity.findViewById(R.id.container);
    }

    private void initialiseRegisterTextView() {
        // In oder to activate registration, uncomment the following line and
//        registrationTextView.setOnClickListener(view -> logToRegisterAndViceVersa(true));
    }

    private void authenticateUser() {
        loginButton.setOnClickListener(view -> {
            String email, password;
            email = usernameEdTx.getText().toString().trim();
            password = passEdTx.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Utils.makeShortToast(activity, getResources().getString(R.string.enter_username_text));
                Utils.requestFocusAndShowKeyboard(getContext(), usernameEdTx);
                return;
            }
            openFeedbackFragment();
            // There is no authentication (username and password with server) implemented yet. Therefore,
            // password is optional to enter. Besides, it might improve usability of the prototype.
            // To implement authentication and to request a password, uncomment the followings.

           /* if (TextUtils.isEmpty(password)) {
                Utils.makeShortToast(activity, getResources().getString(R.string.enter_password_text));
                Utils.requestFocusAndShowKeyboard(getContext(), passEdTx);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            if (loginButton.getText().equals(getResources().getString(R.string.word_registr))) {
                Utils.saveStringToPreferences(activity, getResources()
                        .getString(R.string.username_key_shrd_prf), email);

//                    firebaseAuth.createUserWithEmailAndPassword(email, password)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//
//                                    if (task.isSuccessful()) {
//                                        Utils.makeShortToast(activity, getResources()
//                                                .getString(R.string.register_success_message));
//                                        logToRegisterAndViceVersa(false);
//                                    } else {
////                                        Utils.makeToast(activity, getResources()
////                                                .getString(R.string.register_unsuccess_message));
//
//                                        Utils.makeShortToast(activity, task.getException().getMessage());
//                                    }
//                                    progressBar.setVisibility(View.GONE);
//                                }
//
//
//                            });
            } else {
                String savedPassword = Utils.fetchStringFromPreferences(activity, getResources().getString(R.string.password_key_shrd_prf));
                if (savedPassword.equals(password)) {
                    Utils.makeShortToast(activity, getResources().getString(R.string.login_success_message));
                    progressBar.setVisibility(View.GONE);
//                        if (loggedInChBox.isChecked()) {
//                            sharedPreferences.edit()
//                                    .putString(getResources().getString(
//                                            R.string.user_id_key_shrd_prfs),
//                                            firebaseAuth.getUid())
//                                    .putString(getResources().getString(
//                                            R.string.user_email_shrd_prf_key),
//                                            usernameEdTx.getText().toString()).apply();
//                        }
                    openFeedbackFragment();
                } else {
                    Utils.makeShortToast(activity, getResources()
                            .getString(R.string.log_unsuccess_message));
                    progressBar.setVisibility(View.GONE);
                }
//                    firebaseAuth.signInWithEmailAndPassword(email, password)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        Utils.makeShortToast(activity, getResources().getString(R.string.login_success_message));
//                                        progressBar.setVisibility(View.GONE);
//                                        if (loggedInChBox.isChecked()) {
//                                            sharedPreferences.edit()
//                                                    .putString(getResources().getString(
//                                                            R.string.user_id_key_shrd_prfs),
//                                                            firebaseAuth.getUid())
//                                                    .putString(getResources().getString(
//                                                            R.string.user_email_shrd_prf_key),
//                                                            usernameEdTx.getText().toString()).apply();
//                                        }
//                                        openFeedbackFragment();
//                                    } else {
//                                        Utils.makeShortToast(activity, getResources()
//                                                .getString(R.string.log_unsuccess_message));
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
            }*/
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        String savedUID = sharedPreferences.getString(getResources().
//                getString(R.string.user_id_key_shrd_prfs), null);
//        if (currentUser != null && savedUID != null && savedUID.equals(currentUser.getUid())) {


        // Since no authentification of user name is implemented, feedback_fragment is directly opened without requesting
        // username and password

//        openFeedbackFragment();
//        }
    }

    private void openFeedbackFragment() {
        Utils.saveStringToPreferences(Objects.requireNonNull(getContext()), getResources().getString(R.string.username_key_shrd_prf),
                usernameEdTx.getText().toString().trim());

        activity.replaceFragment(activity.getFeedbackFragment(),
                activity.getFeedbackFragment().getCustomTAG(),
                false, 0, 0);
        activity.setMenuItemActive(activity.getFeedbackFragment().getCustomTAG());
        Utils.showNavigationBottomView(navigationMenu);
        usernameEdTx.setText("");
        passEdTx.setText("");
    }

    private void logToRegisterAndViceVersa(boolean toRegistr) {
        if (toRegistr) {
            registerLoginLayout.setVisibility(View.GONE);
            loginButton.setText(getResources().getString(R.string.word_registr));
            backToLoginTextView.setVisibility(View.VISIBLE);
        } else {
            loginButton.setText(getResources().getString(R.string.word_login));
            registerLoginLayout.setVisibility(View.VISIBLE);
            backToLoginTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public String getCustomTAG() {
        return TAG;
    }
}
