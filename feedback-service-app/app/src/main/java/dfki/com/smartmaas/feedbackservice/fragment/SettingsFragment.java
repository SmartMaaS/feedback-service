package dfki.com.smartmaas.feedbackservice.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.model.CustomFragment;


public class SettingsFragment extends CustomFragment {
    private static final String TAG = SettingsFragment.class.getName();
    private CheckBox englishChB, germanChB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        initialiseViews(view);
        setCheckBoxListeners();
        return view;
    }

    private void initialiseViews(View view) {
        englishChB = view.findViewById(R.id.englishChBID);
        germanChB = view.findViewById(R.id.germanChBID);
    }

    private void setCheckBoxListeners() {
        englishChB.setOnClickListener(v -> {
        });
        germanChB.setOnClickListener(v -> {
        });
    }

    @Override
    public String getCustomTAG() {
        return TAG;
    }
}
