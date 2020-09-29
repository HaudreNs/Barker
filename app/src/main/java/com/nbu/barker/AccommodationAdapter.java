package com.nbu.barker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class AccommodationAdapter extends ArrayAdapter<Accommodation> {
    private Context m_pContext;
    private int m_nResource;

    public AccommodationAdapter( Context context, int resource, ArrayList<Accommodation> objects) {
        super(context, resource,objects);
        m_pContext = context;
        m_nResource = resource;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater pInflater = LayoutInflater.from(m_pContext);
        convertView = pInflater.inflate(m_nResource,parent,false);


        TextView tvName = convertView.findViewById(R.id.tvAccommodationName);
        TextView tvRating = convertView.findViewById(R.id.tvAccommodationRating);

        final View convertViewPassed = convertView;

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccommodation(getItem(position).getId(),convertViewPassed);


            }
        });


        tvName.setText(getItem(position).getName());
        String sRating = Double.toString(getItem(position).getRating());

        if(sRating.length() > 3) sRating = sRating.substring(0,3);

        tvRating.setText("Rating: " + sRating + "/" + getItem(position).getVoted());



        return convertView;
    }

    private void openAccommodation(int nId, View pView) {
        Intent pIntent = new Intent(pView.getContext(), AccommodationPickedActivity.class);
        pIntent.putExtra("id", nId);
        m_pContext.startActivity(pIntent);
    }

}