package dfki.com.smartmaas.feedbackservice.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.adapter.RecyclerViewAdapter;
import dfki.com.smartmaas.feedbackservice.intrface.Inotification;
import dfki.com.smartmaas.feedbackservice.model.Feedback;
import dfki.com.smartmaas.feedbackservice.model.FirebaseMessage;
import dfki.com.smartmaas.feedbackservice.model.Stop;
import dfki.com.smartmaas.feedbackservice.service.FirebaseMessagingService;
import dfki.com.smartmaas.feedbackservice.util.Utils;

public class ActivityStops extends AppCompatActivity implements Inotification {
    private final static String tag = "ActivityStops";
    private TextView nearbyStopsTitleTextView, loadMoreTextView;
    private RecyclerView recyclerView;
    private FirebaseMessage firebaseMessage;
    private ProgressBar loadMoreProgBar;
    private Gson gson;
    private String data;
    private LinearLayout stopOptsLayout;
    private Button detailsButton, directionButton;
    private static TextView lastPressedTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        initialiseViews();
        gson = new Gson();
        data = Utils.fetchStringFromPreferences(getApplicationContext(),
                getResources().getString(R.string.nearby_stops_data_SH_PR_key));
        firebaseMessage = gson.fromJson(data, FirebaseMessage.class);
        addMoreStopsIfExist();
        String totalStops = Utils.fetchStringFromPreferences(
                this.getApplicationContext(), getResources()
                        .getString(R.string.stops_amount_SH_PR_key));
        if (firebaseMessage.getStops().size() == Integer.valueOf(totalStops)) {
            loadMoreTextView.setVisibility(View.GONE);
        }
        if (firebaseMessage.getStops() == null)
            firebaseMessage.setStops(new ArrayList<>());
        initialiseRecyclerView();
        initialiseTitleTextView();
        initialiseStopOptions();
    }

    private void initialiseViews() {
        loadMoreProgBar = findViewById(R.id.loadMorePrgBrID);
        loadMoreTextView = findViewById(R.id.loadMoreTxVwID);
        recyclerView = findViewById(R.id.recyclerViewId);
        nearbyStopsTitleTextView = findViewById(R.id.nearByStopsTextViewId);
        stopOptsLayout = findViewById(R.id.stopOptsLyoutID);
        detailsButton = findViewById(R.id.detailsBttnID);
        directionButton = findViewById(R.id.directionBttnId);

    }

    private void addMoreStopsIfExist() {
        if (getIntent().getExtras() != null && getIntent().getExtras()
                .getString(getResources().getString(R.string.firebase_message_key_for_intent)) != null) {
            data = getIntent().getExtras().getString(getResources().getString(R.string.firebase_message_key_for_intent));
            firebaseMessage.addStops(gson.fromJson(data, FirebaseMessage.class).getStops());
            Utils.saveStringToPreferences(getApplicationContext(), getResources()
                            .getString(R.string.nearby_stops_data_SH_PR_key),
                    gson.toJson(firebaseMessage));
        }
    }

    private void initialiseRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new RecyclerViewAdapter(
                firebaseMessage.getStops());
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initialiseTitleTextView() {
        String stopsNumber = Utils.fetchStringFromPreferences(getApplicationContext(),
                getResources().getString(R.string.stops_amount_SH_PR_key));
        nearbyStopsTitleTextView.setText(getResources().getString(R.string.nearby_stops_title,
                String.valueOf(firebaseMessage.getStops().size()), stopsNumber));
    }

    private void initialiseStopOptions() {
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastPressedTextView != null) {
                    Stop stop = findLatestPressedStop();
                    new AlertDialog.Builder(ActivityStops.this)
                            .setTitle(getResources().getString(R.string.details_message))
                            .setMessage(stop.getMessage())
                            .show();
                }
            }
        });

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastPressedTextView != null) {
                    Stop stop = findLatestPressedStop();
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                            stop.getLat() + "," + stop.getLng());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage(getResources().getString(R.string.google_maps_package));
                    ActivityStops.this.startActivity(mapIntent);
                }
            }
        });
    }

    private Stop findLatestPressedStop() {
        int position = Integer.parseInt(lastPressedTextView.getText().toString().split("\\)")[0]);
        return firebaseMessage.getStops().get(position - 1);
    }

    public void stopOnClick(View view) {
        view.setBackgroundColor(getResources().getColor(R.color.colorStopButtonBackground));

        if (lastPressedTextView != null &&
                (lastPressedTextView != view || stopOptsLayout.getVisibility() != View.GONE)) {
            lastPressedTextView.setBackground(getResources().getDrawable(R.drawable.text_view_shape));
        }

        if (stopOptsLayout.getVisibility() == View.GONE) {
            stopOptsLayout.setVisibility(View.VISIBLE);
        } else if (lastPressedTextView == view) {
            stopOptsLayout.setVisibility(View.GONE);
        }
        lastPressedTextView = (TextView) view;
    }

    public void loadMoreOnClick(View view) {
        FirebaseMessagingService.inotification = this;
        loadMoreProgBar.setVisibility(View.VISIBLE);
        loadMoreTextView.setVisibility(View.GONE);
        String size = Utils.fetchStringFromPreferences(getApplicationContext(), getResources().getString(R.string.size_SH_PR_key));
        String radius = Utils.fetchStringFromPreferences(getApplicationContext(),
                getResources().getString(R.string.nearby_stops_radius_SH_PR_key));
        String feedbackJson = Utils.fetchStringFromPreferences(getApplicationContext(),
                getResources().getString(R.string.feedback_object_SH_PR_key));
        Feedback feedback = new Gson().fromJson(feedbackJson, Feedback.class);
        feedback.setRadius(Integer.parseInt(radius));
        String page = Utils.fetchStringFromPreferences(this,
                getResources().getString(R.string.latest_page_SH_PR_key));

        page = String.valueOf(Integer.valueOf(page) + 1);
        HashMap<String, String> headers = new HashMap<String, String>();
        String token = Utils.fetchStringFromPreferences(this,
                getResources().getString(R.string.firebase_token_SH_PR_key));
        headers.put(getResources().getString(R.string.token_header_key), token);
        headers.put(getResources().getString(R.string.page_header_key), page);
        headers.put(getResources().getString(R.string.size_header_key), size);


        Utils.postToFeedbWS(this,
                feedback.getRDFOutput(getResources().getStringArray(R.array.formats)[0]),
                getResources().getString(R.string.feedback_web_service_url),
                getResources().getStringArray(R.array.content_types)[2], headers,
                getResources().getString(R.string.more_stops_success_message, size),
                getResources().getString(R.string.more_stops_error_message));
    }

    @Override
    public void notifyMoreStops() {
        loadMoreProgBar.setVisibility(View.GONE);
        loadMoreTextView.setVisibility(View.VISIBLE);
    }
}
