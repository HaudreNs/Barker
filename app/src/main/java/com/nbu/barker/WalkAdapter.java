package com.nbu.barker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class WalkAdapter extends ArrayAdapter<Walk> {

    private Context m_pContext;
    private int m_nResource;

    public WalkAdapter( Context context, int resource, ArrayList<Walk> objects) {
        super(context, resource,objects);
        m_pContext = context;
        m_nResource = resource;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater pInflater = LayoutInflater.from(m_pContext);
        convertView = pInflater.inflate(m_nResource,parent,false);


        TextView tvUsername = convertView.findViewById(R.id.tvWalkUserUsername);
        TextView tvMessage = convertView.findViewById(R.id.tvWalkMessage);
        TextView tvDate = convertView.findViewById(R.id.tvWalkTime);
        Button bMessage = convertView.findViewById(R.id.bWalkMessage);

        final View convertViewPassed = convertView;

        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWalk(getItem(position).getId(),convertViewPassed);
            }
        });

        bMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMessageActivity(getItem(position).getUsername(), convertViewPassed);
            }
        });


        tvUsername.setText(getItem(position).getUsername());
        tvMessage.setText(getItem(position).getMessage());
        tvDate.setText(getItem(position).getTimePassed() + " minutes ago");



        return convertView;
    }

    private void openWalk(int nId, View pView) {
        Intent pIntent = new Intent(pView.getContext(), WalkPickedActivity.class);
        pIntent.putExtra("id", nId);
        m_pContext.startActivity(pIntent);
    }

    private void openMessageActivity(String sUsername, View pView)
    {
        Intent pIntent = new Intent(pView.getContext(), MessageActivity.class);
        pIntent.putExtra("username", sUsername);
        m_pContext.startActivity(pIntent);
    }
}
