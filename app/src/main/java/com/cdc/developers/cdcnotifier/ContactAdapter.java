package com.cdc.developers.cdcnotifier;

/**
 * Created by Sampath Kumar on 1/14/2016.
 */

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<ContactInfo> contactList;


    public ContactAdapter(List<ContactInfo> contactList) {
        this.contactList = contactList;
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        ContactInfo ci = contactList.get(i);
        contactViewHolder.vSubject.setText(ci.subject);
        contactViewHolder.vMessage.setText(ci.message);
        contactViewHolder.vDate.setText(ci.date);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);


        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView vSubject;
        protected TextView vMessage;
        protected TextView vDate;

        public ContactViewHolder(View v) {
            super(v);
            vSubject =  (TextView) v.findViewById(R.id.tvSubject);
            vSubject.setTextColor(Color.parseColor("#388E3C"));
            vMessage = (TextView)  v.findViewById(R.id.tvMessage);
            vDate = (TextView)  v.findViewById(R.id.tvDate);
        }
    }
}

