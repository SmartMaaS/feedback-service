package dfki.com.smartmaas.feedbackservice.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.activity.MainActivity;
import dfki.com.smartmaas.feedbackservice.exception.InvalidLocationNameException;
import dfki.com.smartmaas.feedbackservice.model.CustomFragment;
import dfki.com.smartmaas.feedbackservice.model.Feedback;
import dfki.com.smartmaas.feedbackservice.model.Location;
import dfki.com.smartmaas.feedbackservice.model.Reason;
import dfki.com.smartmaas.feedbackservice.util.Utils;

public class FeedbackFragment extends CustomFragment {
    private static final String TAG = FeedbackFragment.class.getName();
    private Spinner measurementSpinner, vehicleSpinner;
    private ImageButton currentLocationBtn;
    private EditText stuckEditText, locationEditText, otherReasonEditText, vehicleNoEditText;
    private CheckBox rainChB, snowChB, brokenTrLightChB, fogChB, trafficJamChB, otherChB, potholeChB, failingBoardChB, detourChB, malfuncVehivleChB;
    private Button submitButton, cleanButton;
    private Feedback feedback;
    private Location location;
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
        Utils.requestFocusAndShowKeyboard(getContext(), stuckEditText);

        return view;
    }

    private void initializeCheckBoxes(View view) {
        rainChB = view.findViewById(R.id.rainCheckBox);
        fogChB = view.findViewById(R.id.fogCheckBox);
        brokenTrLightChB = view.findViewById(R.id.brokenLightCheckBox);
        snowChB = view.findViewById(R.id.snowCheckBox);
        trafficJamChB = view.findViewById(R.id.trafficJamCheckBox);
        otherChB = view.findViewById(R.id.otherCheckBox);
        detourChB = view.findViewById(R.id.detourCheckBox);
        failingBoardChB = view.findViewById(R.id.failingBoardCheckBox);
        potholeChB = view.findViewById(R.id.potholeCheckBox);
        malfuncVehivleChB = view.findViewById(R.id.malfuncVehicleheckBox);
    }

    private void initialiseViews(View view) {
        initializeCheckBoxes(view);
        stuckEditText = view.findViewById(R.id.stuckEditText);
        measurementSpinner = view.findViewById(R.id.measurementSpinner);
        vehicleSpinner = view.findViewById(R.id.vehicleSpinner);
        submitButton = view.findViewById(R.id.submitButtonTrJmFrg);
        locationEditText = view.findViewById(R.id.locationEdTxId);
        currentLocationBtn = view.findViewById(R.id.currentLocationBtnId);
        otherReasonEditText = view.findViewById(R.id.otherEditText);
        vehicleNoEditText = view.findViewById(R.id.vehicleNumberEdTxFeedbackFrgmt);
        cleanButton = view.findViewById(R.id.cleanButtonTrJmFrg);
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
        cleanCheckBoxes();
        stuckEditText.setText("");
        measurementSpinner.setSelection(0);
        vehicleSpinner.setSelection(0);
        vehicleNoEditText.setText("");
        locationEditText.setText("");
        location.cleanUp();
    }

    private void cleanCheckBoxes() {
        rainChB.setChecked(false);
        snowChB.setChecked(false);
        brokenTrLightChB.setChecked(false);
        otherChB.setChecked(false);
        trafficJamChB.setChecked(false);
        fogChB.setChecked(false);
        potholeChB.setChecked(false);
        detourChB.setChecked(false);
        failingBoardChB.setChecked(false);
        malfuncVehivleChB.setChecked(false);
        otherReasonEditText.setText("");
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

    private void submitData() {
        String currentTime = Utils.getCurrentTime();

        int stuckTime = Integer.parseInt(stuckEditText.getText().toString());
        String measurementUnit = measurementSpinner.getSelectedItem().toString();
        String vehicle = vehicleSpinner.getSelectedItem().toString();
        String vehicleNo = "";
        if (vehicleNoEditText.getVisibility() == View.VISIBLE) {
            vehicleNo = vehicleNoEditText.getText().toString();
        }

        List<Reason> reasons = findOutReasons();

        if (location == null) {
            location = new Location();
        }
        location.setName(locationEditText.getText().toString());
        HashMap<String, Double> latLng;
        try {
            latLng = Utils.convertAddressToLatLng(location.getName(), activity);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.makeShortToast(getContext(), "Current location couldn't be identified. Details:\n" +
                    "An address name(" + location.getName() + ") cannot be converted to coordinates.");
            Log.e(TAG, "An address name(" + location.getName() + ") cannot be converted to coordinates.");
            return;
        } catch (InvalidLocationNameException e) {
            e.printStackTrace();
            Utils.makeLongToast(activity, e.getMessage());
            locationEditText.setHint(e.getMessage());
            return;
        }
        location.setLat(latLng.get("latitude"));
        location.setLng(latLng.get("longitude"));

        if (feedback == null) {
            feedback = new Feedback(new Location(this.location),
                    stuckTime, measurementUnit, vehicle, vehicleNo, currentTime);
        } else {
            feedback.setLocation(location);
            feedback.setStuckTime(stuckTime);
            feedback.setTimeMeasurementUnit(measurementUnit);
            feedback.setVehicle(vehicle);
            feedback.setVehicleNo(vehicleNo);
            feedback.setDateTime(currentTime);
        }
        feedback.setReasons(reasons);
        feedback.setUsername(Utils.fetchStringFromPreferences(getContext(), getResources().getString(R.string.username_key_shrd_prf)));
        String[] rdfFormats = getResources().getStringArray(R.array.formats);
        String contentType = getResources().getStringArray(R.array.content_types)[2];

        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setMessage(getResources().getString(R.string.are_you_sure_message))
                .setPositiveButton(R.string.word_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String result = feedback.getFeedbackMessage();
                        Gson gson = new Gson();
                        Utils.saveStringToPreferences(activity,
                                getResources().getString(R.string.feedback_object_SH_PR_key),
                                gson.toJson(feedback));
                        String size = getResources().getString(
                                R.string.size_of_list_per_request_from_feedback_web_service);
                        Utils.saveStringToPreferences(activity,
                                getResources().getString(R.string.size_SH_PR_key), size);
                        HashMap<String, String> headers = new HashMap<String, String>();
//                            String token = Utils.fetchStringFromPreferences(activity,
//                                    getContext().getResources().getString(R.string.firebase_token_SH_PR_key));
//                            headers.put(getContext().getResources().getString(R.string.token_header_key), token);
//                            headers.put(getContext().getResources().getString(
//                                    R.string.page_header_key), getResources().getString(
//                                    R.string.page_number_for_first_request_from_feedback_web_service));
//                            headers.put(getContext().getResources().getString(R.string.size_header_key), size);

                        Utils.postToFeedbWS(getContext(), result,
                                getResources().getString(R.string.feedback_web_service_url),
                                contentType, headers,
                                getResources().getString(R.string.feedback_submitted_message),
                                getResources().getString(R.string.feedback_not_submitted_message));
                        activity.replaceFragment(activity.getFeedbackFragment(),
                                TAG, false,
                                0, 0);
                        activity.setMenuItemActive(activity.getFeedbackFragment().getCustomTAG());


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
            Reason rain = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_rain_title)),
                    Reason.PASSING_REASON);
            reasons.add(rain);
        }
        if (brokenTrLightChB.isChecked()) {
            Reason brokenTrafficJam = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_broken_jam_title)),
                    Reason.PERMANENT_REASON);
            reasons.add(brokenTrafficJam);
        }
        if (snowChB.isChecked()) {
            Reason snow = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_snow_title)),
                    Reason.PASSING_REASON);
            reasons.add(snow);
        }
        if (trafficJamChB.isChecked()) {
            Reason trafficJam = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_traffic_jam_title)),
                    Reason.PASSING_REASON);
            reasons.add(trafficJam);
        }
        if (fogChB.isChecked()) {
            Reason fog = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_fog_title)),
                    Reason.PASSING_REASON);
            reasons.add(fog);
        }
        if (potholeChB.isChecked()) {
            Reason pothole = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_pothole_title)),
                    Reason.PERMANENT_REASON);
            reasons.add(pothole);
        }
        if (failingBoardChB.isChecked()) {
            Reason failingBoard = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_failing_info_board_title)),
                    Reason.PERMANENT_REASON);
            reasons.add(failingBoard);
        }
        if (malfuncVehivleChB.isChecked()) {
            Reason malfuncVehicle = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_malfunctioning_vehicle_title)),
                    Reason.PERMANENT_REASON);
            reasons.add(malfuncVehicle);
        }
        if (detourChB.isChecked()) {
            Reason detour = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_detour_title)),
                    Reason.PASSING_REASON);
            reasons.add(detour);
        }
        if (otherChB.isChecked()) {
            Reason other = new Reason(Utils.removeAllSpaces(getResources().getString(R.string.reason_other_title)),
                    Reason.OTHER_REASON);
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
        return TAG;
    }
}
