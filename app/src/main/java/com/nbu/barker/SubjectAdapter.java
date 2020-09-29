package com.nbu.barker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SubjectAdapter extends ArrayAdapter<Subject> {
    private Context m_pContext;
    private int m_nResource;

    public SubjectAdapter( Context context, int resource, ArrayList<Subject> objects) {
        super(context, resource,objects);
        m_pContext = context;
        m_nResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String sName = getItem(position).getName();
        String sDescription = getItem(position).getDescription();
        final int nId = getItem(position).getId();
        final String sUsername = getItem(position).getUserUsername();

        LayoutInflater pInflater = LayoutInflater.from(m_pContext);
        convertView = pInflater.inflate(m_nResource,parent,false);

        final TextView tvUsername = convertView.findViewById(R.id.tvTopicUsername);
        TextView tvTopic = convertView.findViewById(R.id.tvTopicName);

        tvTopic.setText(sName);
        tvTopic.setClickable(true);
        tvUsername.setText(getItem(position).getUserUsername());
        tvUsername.setClickable(true);

        final View convertViewPassed = convertView;

        tvTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForumTopicAcivity(nId, convertViewPassed);
            }
        });
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserActivity(tvUsername.getText().toString(), convertViewPassed);
            }
        });

        return convertView;
    }

    private void openForumTopicAcivity(int nId, View pView) {
        Intent pIntent = new Intent(pView.getContext(), ForumTopicActivity.class);
        pIntent.putExtra("id", nId);
        m_pContext.startActivity(pIntent);
    }

    private void openUserActivity(String sUsername, View pView) {
        Intent pIntent = new Intent(pView.getContext(), ForumTopicActivity.class);
        pIntent.putExtra("username", sUsername);
        m_pContext.startActivity(pIntent);
    }
}

