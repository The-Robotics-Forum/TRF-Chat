package com.theroboticsforum.trfchat;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Context mCtx;
    private ArrayList<Message> chats;

    public CustomAdapter(Context mCtx, ArrayList<Message> chats) {
        this.mCtx = mCtx;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.layout_message , parent , false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TextView nameTextView = holder.nameTextView;
        TextView message = holder.messageTextView;

        Message msg = chats.get(position);

        String[] names = msg.getSenderEmail().split("@");

        nameTextView.setText(names[0]);
        message.setText(msg.getMessage());

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView messageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.senderEmail);
            messageTextView = itemView.findViewById(R.id.messageTextView);

        }
    }
}
