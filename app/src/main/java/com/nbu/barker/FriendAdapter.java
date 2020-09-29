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

public class FriendAdapter extends ArrayAdapter<User> {
    private Context m_pContext;
    private int m_nResource;

    public FriendAdapter( Context context, int resource, ArrayList<User> objects) {
        super(context, resource,objects);
        m_pContext = context;
        m_nResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String sUsername = getItem(position).getUsername();
        final String sEmail = getItem(position).getEmail();
        String sNames = getItem(position).getNames();
        final int nId = getItem(position).getId();
        boolean bIsAccepted = getItem(position).getIsFriendAccepted();

        LayoutInflater pInflater = LayoutInflater.from(m_pContext);
        convertView = pInflater.inflate(m_nResource,parent,false);

        TextView tvUsername = convertView.findViewById(R.id.tvFriendUsername);
        TextView tvNames = convertView.findViewById(R.id.tvFriendNames);
        final Button bAcceptFriend = convertView.findViewById(R.id.bFriendFriendNtoAcceptedAccept);
        final Button bRejectFriend = convertView.findViewById(R.id.bFriendFriendNtoAcceptedDecline);

        if(bIsAccepted)
        {
            bAcceptFriend.setVisibility(View.INVISIBLE);
            bRejectFriend.setVisibility(View.INVISIBLE);
        }
        final View convertViewPassed = convertView;

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity(sUsername,convertViewPassed);
            }
        });

        tvUsername.setText(sUsername);
        tvNames.setText(sNames);

        if(!bIsAccepted)
        {
            bAcceptFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sXMLAcceptFriend = "<Barker requestType=\"acceptFriend\">\n" +
                            "                <acceptFriend>\n" +
                            "                    <fromAddress>" + sEmail + "</fromAddress>\n" +
                            "                    <toAddress>" + SessionParameters.getApplicationUser().getEmail() + "</toAddress>\n" +
                            "                    <isAccepted>yes</isAccepted>\n" +
                            "                </acceptFriend>\n" +
                            "            </Barker> \n";

                    String sResponse = "";
                    try {
                        sResponse = Tools.sendRequest(sXMLAcceptFriend);
                        Log.println(Log.ERROR, "Response is: ", sResponse);

                        if (sResponse == "Error" || sResponse.isEmpty()) {
                            Toast.makeText(m_pContext, "Couldn't accept friend request. Please try again later", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Document pDoc = null;
                        DocumentBuilder pBuilder = null;
                        DocumentBuilderFactory pFactory = DocumentBuilderFactory.newInstance();
                        pBuilder = pFactory.newDocumentBuilder();
                        pDoc = pBuilder.parse(new InputSource(new StringReader(sResponse)));

                        XPathFactory pXpathFactory = XPathFactory.newInstance();
                        XPath pXpath = pXpathFactory.newXPath();
                        XPathExpression pExp = null;


                        pExp = pXpath.compile("Barker/statusCode");
                        double nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);
                        int nStatusCode = (int) nTmp;

                        if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
                        {
                            Toast.makeText(m_pContext, "Friend has been accepted", Toast.LENGTH_SHORT).show();
                            bAcceptFriend.setVisibility(View.INVISIBLE);
                            bRejectFriend.setVisibility(View.INVISIBLE);
                        }
                        else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.MISSING_FRIEND_REQUEST))
                        {
                            Toast.makeText(m_pContext, "This request no longer exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(m_pContext, "There was an error accepting friend", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

            bRejectFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String sXMLReject = "<Barker requestType=\"acceptFriend\">\n" +
                            "                <acceptFriend>\n" +
                            "                    <fromAddress>" + sEmail + "</fromAddress>\n" +
                            "                    <toAddress>" + SessionParameters.getApplicationUser().getEmail() + "</toAddress>\n" +
                            "                    <isAccepted>no</isAccepted>\n" +
                            "                </acceptFriend>\n" +
                            "            </Barker> \n";

                    String sResponse = "";
                    try {
                        sResponse = Tools.sendRequest(sXMLReject);
                        Log.println(Log.ERROR, "Response is: ", sResponse);

                        if (sResponse == "Error" || sResponse.isEmpty()) {
                            Toast.makeText(m_pContext, "Couldn't accept friend request. Please try again later", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Document pDoc = null;
                        DocumentBuilder pBuilder = null;
                        DocumentBuilderFactory pFactory = DocumentBuilderFactory.newInstance();
                        pBuilder = pFactory.newDocumentBuilder();
                        pDoc = pBuilder.parse(new InputSource(new StringReader(sResponse)));

                        XPathFactory pXpathFactory = XPathFactory.newInstance();
                        XPath pXpath = pXpathFactory.newXPath();
                        XPathExpression pExp = null;


                        pExp = pXpath.compile("Barker/statusCode");
                        double nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);
                        int nStatusCode = (int) nTmp;

                        if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
                        {
                            Toast.makeText(m_pContext, "Friend request has been declined", Toast.LENGTH_SHORT).show();
                            bAcceptFriend.setVisibility(View.INVISIBLE);
                            bRejectFriend.setVisibility(View.INVISIBLE);
                        }
                        else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.MISSING_FRIEND_REQUEST))
                        {
                            Toast.makeText(m_pContext, "This request no longer exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(m_pContext, "There was an error accepting friend", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }

        return convertView;
    }

    private void openProfileActivity(String sUsername, View pView) {
        Intent pIntent = new Intent(pView.getContext(), UserProfileActivity.class);
        pIntent.putExtra("username", sUsername);
        m_pContext.startActivity(pIntent);
    }

}
