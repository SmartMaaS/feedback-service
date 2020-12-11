package dfki.com.smartmaas.feedbackservice.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.activity.MainActivity;
import dfki.com.smartmaas.feedbackservice.model.CustomFragment;
import dfki.com.smartmaas.feedbackservice.util.Utils;


public class PreliminaryFragment extends CustomFragment {
    private final static String TAG = PreliminaryFragment.class.getName();
    private TextView continueTxVw;
    private MainActivity activity;
    private ConstraintLayout navigationMenu;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) this.activity = (MainActivity) context;
        else this.activity = (MainActivity) getContext();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preliminary_fragment, container, false);
        initialiseViews(view);
        Utils.setBlinking(continueTxVw);
        Utils.hideNavigationBottomView(navigationMenu);
        return view;
    }

    private void initialiseViews(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment loginFragment = new LoginFragment();
                activity.replaceFragment(loginFragment, loginFragment.getCustomTAG(),
                        false, android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        continueTxVw = view.findViewById(R.id.contineTxVw);
        navigationMenu = activity.findViewById(R.id.container);
    }

    @Override
    public String getCustomTAG() {
        return TAG;
    }
}
