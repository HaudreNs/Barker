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

public class CommentAdapter  extends ArrayAdapter<Comment> {
    private Context m_pContext;
    private int m_nResource;

    public CommentAdapter( Context context, int resource, ArrayList<Comment> objects) {
        super(context, resource,objects);
        m_pContext = context;
        m_nResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final Comment pComment = getItem(position);

        LayoutInflater pInflater = LayoutInflater.from(m_pContext);
        convertView = pInflater.inflate(m_nResource,parent,false);

        TextView tvComment = convertView.findViewById(R.id.tvCommentText);
        TextView tvUsername = convertView.findViewById(R.id.tvCommentUsername);

        tvComment.setText(pComment.getComment());
        tvUsername.setText(pComment.getUserUsername());

        tvUsername.setClickable(true);

        final View convertViewPassed = convertView;
        final String sUserUsername = pComment.getUserUsername();
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pComment.getUserUsername().equals(SessionParameters.getApplicationUser().getUsername()))
                {
                    Intent pIntent = new Intent(convertViewPassed.getContext(), ProfileActivity.class);
                    m_pContext.startActivity(pIntent);
                }
                Intent pIntent = new Intent(convertViewPassed.getContext(), UserProfileActivity.class);
                pIntent.putExtra("username", sUserUsername);
                m_pContext.startActivity(pIntent);
            }
        });

        return convertView;
    }


}
