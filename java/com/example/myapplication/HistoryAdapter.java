package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<HistoryItem> historyList;
    private final Context context;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm:ss", Locale.getDefault());

    public HistoryAdapter(List<HistoryItem> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        // Format timestamp
        String formattedDate = "";
        if (item.getTimestamp() != null) {
            Date date = item.getTimestamp().toDate();
            formattedDate = sdf.format(date);
        }
        holder.textDate.setText(formattedDate);

        // Format total emission with 2 decimals
        holder.textTotalEmission.setText(String.format(Locale.getDefault(), "%.2f kg CO₂", item.getTotalEmission()));

        // Set color based on emission level
        if (item.getTotalEmission() > 1000) {
            holder.colorIndicator.setBackgroundColor(context.getResources().getColor(R.color.colorHigh));
        } else if (item.getTotalEmission() > 500) {
            holder.colorIndicator.setBackgroundColor(context.getResources().getColor(R.color.colorModerate));
        } else {
            holder.colorIndicator.setBackgroundColor(context.getResources().getColor(R.color.colorLow));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HistoryDetailActivity.class);
            intent.putExtra("historyId", item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textTotalEmission;
        View colorIndicator;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textTotalEmission = itemView.findViewById(R.id.textTotalEmission);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }
}
