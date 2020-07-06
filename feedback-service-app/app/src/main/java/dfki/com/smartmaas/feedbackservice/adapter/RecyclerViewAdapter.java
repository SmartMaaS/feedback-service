package dfki.com.smartmaas.feedbackservice.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import dfki.com.smartmaas.feedbackservice.R;
import dfki.com.smartmaas.feedbackservice.model.Stop;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewViewHolder> {
    private static final String tag = "RecyclerViewAdapter";
    private List<Stop> stopList;

    static class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        RecyclerViewViewHolder(TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }

    public RecyclerViewAdapter(List<Stop> stops) {
        this.stopList = stops;
    }

    @NonNull
    @Override
    public RecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView mTextView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stop_txt_view_layout, parent, false);


        return new RecyclerViewViewHolder(mTextView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolder holder, int position) {
        Collections.sort(stopList);
        String distance = new DecimalFormat("##.#").format(stopList.get(position).getDistanceToUser());
        String textViewContent = (position + 1) + ")" + stopList.get(position).getName() + "<br>" +
                "<font color=\"#FFA500\">" + distance + "</font> km away</br>";

        holder.textView.setText(Html.fromHtml(textViewContent));
    }

    @Override
    public int getItemCount() {
        return stopList.size();
    }
}
