package com.example.inboxmsg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inboxmsg.model.Sms;

import java.util.ArrayList;
import java.util.List;

public class HomePageAdapter extends  RecyclerView.Adapter<HomePageAdapter.HomePageViewHolder> {

    private Context context;

    private List<Sms> senderInfo= new ArrayList<>();

    public HomePageAdapter(Context context, List<Sms> senderInfo) {
        this.context = context;
        this.senderInfo = senderInfo;
    }

    @Override
    public HomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.homepage_viewholder, parent,false);
         return new HomePageViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePageViewHolder holder, int position) {
        holder.setSenderPhoneNo.setText(senderInfo.get(position).getSenderInfo());
        holder.setSenderBody.setText(senderInfo.get(position).getSenderBodyList().get(0));
    }

    @Override
    public int getItemCount() {
        return senderInfo.size();
    }

    public class HomePageViewHolder extends RecyclerView.ViewHolder{

        private TextView setSenderPhoneNo;
        private  TextView setSenderBody;
        public HomePageViewHolder(@NonNull View itemView) {
            super(itemView);
           setSenderPhoneNo=itemView.findViewById(R.id.senderPhoneno_txt);
           setSenderBody=itemView.findViewById(R.id.senderBodyDemo_txt);
        }
    }
}
