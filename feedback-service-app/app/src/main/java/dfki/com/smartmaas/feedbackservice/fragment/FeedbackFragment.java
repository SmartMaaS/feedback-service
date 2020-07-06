package dfki.com.smartmaas.feedbackservice.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.activity.MainActivity;
import dfki.com.smartmaas.feedbackservice.model.CustomFragment;
import dfki.com.smartmaas.feedbackservice.model.Feedback;
import dfki.com.smartmaas.feedbackservice.model.Location;
import dfki.com.smartmaas.feedbackservice.model.Reason;
import dfki.com.smartmaas.feedbackservice.util.Utils;

public class FeedbackFragment extends CustomFragment {
    private static final String tag = "FeedbackFragment";
    private Spinner measurementSpinner, vehicleSpinner;
    private ImageButton currentLocationBtn;
    private EditText stuckEditText, locationEditText, otherReasonEditText, vehicleNoEditText;
    private CheckBox rainChB, crashChB, brokenTrLightChB, maintenanceChB, trafficJamChB, otherChB;
    private Button submitButton, cleanButton;
    private Feedback feedback;
    private Location location;
    private TextView radiusTextView;
    private SeekBar radiusSeekBar;
    private int defaultRadius = 10;
    private MainActivity activity;

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
        View view = inflater.inflate(R.layout.feedback_fragment, container, false);
        initialiseViews(view);
        initialiseSpinners();
        initialiseVehicleSpinner();
        initialiseSubmitButton();
        initialiseCleanButton();
        initialiseCurrentLocationButton();
        initialiseOtherCheckBox();
        initialiseSeekBar();
        Utils.requestFocusAndShowKeyboard(getContext(), stuckEditText);

        return view;
    }

    private void initialiseViews(View view) {
        stuckEditText = view.findViewById(R.id.stuckEditText);
        measurementSpinner = view.findViewById(R.id.measurementSpinner);
        vehicleSpinner = view.findViewById(R.id.vehicleSpinner);
        rainChB = view.findViewById(R.id.rainCheckBox);
        crashChB = view.findViewById(R.id.crashCheckBox);
        brokenTrLightChB = view.findViewById(R.id.brokenTrfLightCheckBox);
        maintenanceChB = view.findViewById(R.id.maintenanceCheckBox);
        trafficJamChB = view.findViewById(R.id.trafficJamCheckBox);
        otherChB = view.findViewById(R.id.otherCheckBox);
        submitButton = view.findViewById(R.id.submitButtonTrJmFrg);
        locationEditText = view.findViewById(R.id.locationEdTxId);
        currentLocationBtn = view.findViewById(R.id.currentLocationBtnId);
        otherReasonEditText = view.findViewById(R.id.otherEditText);
        vehicleNoEditText = view.findViewById(R.id.vehicleNumberEdTxFeedbackFrgmt);
        cleanButton = view.findViewById(R.id.cleanButtonTrJmFrg);
        radiusTextView = view.findViewById(R.id.radiusTextViewFeedbackFragID);
        radiusTextView.setText(activity.getResources().getString(R.string.radius_message, defaultRadius));
        radiusSeekBar = view.findViewById(R.id.radiusSeekBarFeedbackFragID);
    }

    private void initialiseSpinners() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(activity,
                R.array.measurementSpinnerEntries, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurementSpinner.setAdapter(arrayAdapter);

        arrayAdapter = ArrayAdapter.createFromResource(activity,
                R.array.vehicleSpinnerEntries, android.R.layout.simple_spinner_item);
        vehicleSpinner.setAdapter(arrayAdapter);
    }

    private void initialiseVehicleSpinner() {
        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0 || i == 1) {
                    vehicleNoEditText.setHint(getResources().getString(R.string.vehicle_number));
                    vehicleNoEditText.setVisibility(View.VISIBLE);
                } else {
                    vehicleNoEditText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initialiseCleanButton() {
        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanData();
            }
        });
    }

    private void cleanData() {
        stuckEditText.setText("");
        measurementSpinner.setSelection(0);
        vehicleSpinner.setSelection(0);
        vehicleNoEditText.setText("");
        rainChB.setChecked(false);
        crashChB.setChecked(false);
        brokenTrLightChB.setChecked(false);
        otherChB.setChecked(false);
        trafficJamChB.setChecked(false);
        maintenanceChB.setChecked(false);
        otherReasonEditText.setText("");
        locationEditText.setText("");
        location.cleanUp();
    }

    private void initialiseSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(stuckEditText.getText().toString())) {
                    Utils.requestFocusAndShowKeyboard(getContext(), stuckEditText);
                    Toast.makeText(activity, R.string.fill_empty_slot, Toast.LENGTH_SHORT).show();
                } else if ((vehicleSpinner.getSelectedItemPosition() == 0 || vehicleSpinner.getSelectedItemPosition() == 1)
                        && TextUtils.isEmpty(vehicleNoEditText.getText().toString())) {
                    Utils.requestFocusAndShowKeyboard(getContext(), vehicleNoEditText);
                    Toast.makeText(activity, R.string.fill_empty_slot, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(locationEditText.getText().toString())) {
                    Utils.requestFocusAndShowKeyboard(getContext(), locationEditText);
                    Toast.makeText(activity, R.string.fill_empty_slot, Toast.LENGTH_SHORT).show();
                } else if (otherChB.isChecked() && TextUtils.isEmpty(otherReasonEditText.getText().toString())) {
                    Utils.requestFocusAndShowKeyboard(getContext(), otherReasonEditText);
                    Toast.makeText(activity, R.string.fill_empty_slot, Toast.LENGTH_SHORT).show();
                } else {
                    submitData();
                }
            }
        });
    }

    private void initialiseCurrentLocationButton() {
        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getLocationUpdate().startLocationUpdates();
                if (location == null) {
                    locationEditText.setText(getResources().getString(R.string.try_again));
                } else
                    locationEditText.setText(location.getName());
                Utils.hideKeyboard(activity, view);
            }
        });
    }

    private void initialiseOtherCheckBox() {
        otherChB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    otherReasonEditText.setVisibility(View.VISIBLE);
                } else {
                    otherReasonEditText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initialiseSeekBar() {
        int minimumValue = 1;
        radiusSeekBar.setMax(149);
        radiusSeekBar.setProgress(defaultRadius - 1);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusTextView.setText(getContext().getResources().getString(R.string.radius_message, (progress + minimumValue)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                defaultRadius = seekBar.getProgress() + minimumValue;
            }
        });
    }

    private void submitData() {
        int stuckTime = Integer.parseInt(stuckEditText.getText().toString());
        String measurementUnit = measurementSpinner.getSelectedItem().toString();
        String vehicle = vehicleSpinner.getSelectedItem().toString();
        String vehicleNo = "";
        if (vehicleNoEditText.getVisibility() == View.VISIBLE) {
            vehicleNo = vehicleNoEditText.getText().toString();
        }

        List<Reason> reasons = findOutReasons();
        if (feedback == null) {
            feedback = new Feedback(new Location(this.location),
                    stuckTime, measurementUnit, vehicle, vehicleNo);
        } else {
            feedback.setLocation(location);
            feedback.setStuckTime(stuckTime);
            feedback.setMeasurementUnit(measurementUnit);
            feedback.setVehicle(vehicle);
            feedback.setVehicleNo(vehicleNo);
        }
        feedback.setRadius(this.defaultRadius);
        Utils.saveStringToPreferences(activity,
                getActivity().getResources().getString(R.string.nearby_stops_radius_SH_PR_key), String.valueOf(this.defaultRadius));

        feedback.setReasons(reasons);
        String[] rdfFormats = getResources().getStringArray(R.array.formats);
        String contentType = getResources().getStringArray(R.array.content_types)[2];

        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setMessage(getResources().getString(R.string.are_you_sure_message))
                .setPositiveButton(R.string.word_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String result = feedback.getRDFOutput(rdfFormats[0]);
                            Gson gson = new Gson();
                            Utils.saveStringToPreferences(activity,
                                    getResources().getString(R.string.feedback_object_SH_PR_key),
                                    gson.toJson(feedback));
                            String size = getResources().getString(
                                    R.string.size_of_list_per_request_from_feedback_web_service);
                            Utils.saveStringToPreferences(activity,
                                    getResources().getString(R.string.size_SH_PR_key), size);
                            HashMap<String, String> headers = new HashMap<String, String>();
                            String token = Utils.fetchStringFromPreferences(activity,
                                    getContext().getResources().getString(R.string.firebase_token_SH_PR_key));
                            headers.put(getContext().getResources().getString(R.string.token_header_key), token);
                            headers.put(getContext().getResources().getString(
                                    R.string.page_header_key), getResources().getString(
                                    R.string.page_number_for_first_request_from_feedback_web_service));
                            headers.put(getContext().getResources().getString(R.string.size_header_key), size);

                            Utils.postToFeedbWS(getContext(), result,
                                    getResources().getString(R.string.feedback_web_service_url),
                                    contentType, headers,
                                    getResources().getString(R.string.feedback_submitted_message),
                                    getResources().getString(R.string.feedback_not_submitted_message));
                            activity.replaceFragment(activity.getFeedbackFragment(),
                                    tag, false,
                                    0, 0);
                            activity.setMenuItemActive(activity.getFeedbackFragment().getCustomTAG());
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                })
                .setNegativeButton(R.string.word_no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    private List<Reason> findOutReasons() {
        List<Reason> reasons = new ArrayList<>();
        if (rainChB.isChecked()) {
            Reason rain = new Reason(getResources().getString(R.string.reason_rain_title));
            reasons.add(rain);
        }
        if (brokenTrLightChB.isChecked()) {
            Reason brokenTrafficJam = new Reason(getResources().getString(R.string.reason_broken_jam_title));
            reasons.add(brokenTrafficJam);
        }
        if (crashChB.isChecked()) {
            Reason crash = new Reason(getResources().getString(R.string.reason_crash_title));
            reasons.add(crash);
        }
        if (trafficJamChB.isChecked()) {
            Reason trafficJam = new Reason(getResources().getString(R.string.reason_traffic_jam_title));
            reasons.add(trafficJam);
        }
        if (maintenanceChB.isChecked()) {
            Reason maintenance = new Reason(getResources().getString(R.string.reason_maintenance_title));
            reasons.add(maintenance);
        }
        if (otherChB.isChecked()) {
            Reason other = new Reason(getResources().getString(R.string.reason_other_title));
            other.setAdditionalInfo(otherReasonEditText.getText().toString());
            reasons.add(other);
        }
        return reasons;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getCustomTAG() {
        return tag;
    }
}
