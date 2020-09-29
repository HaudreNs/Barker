package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

public class FriendListActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        returnToHomeActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        ListView pView = findViewById(R.id.friendListView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {
                String sInformation = extras.getString("message");

                Toast.makeText(FriendListActivity.this, sInformation, Toast.LENGTH_SHORT).show();
            }
        }

        Button bAddFriend;
        bAddFriend = findViewById(R.id.bFriendListAdd);

        bAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddFriendActivity();
            }
        });

        String sXML = "        <Barker requestType=\"getFriends\">\n" +
                "            <getFriends>\n" +
                "                <email>" + SessionParameters.getApplicationUser().getEmail() + "</email>\n" +
                "                <onlyAccepted>no</onlyAccepted>\n" +
                "            </getFriends>\n" +
                "        </Barker>";

        String sResponse = "";
        ArrayList<User> vpFriends = new ArrayList<User>();

        try {
            sResponse = Tools.sendRequest(sXML);
            Log.println(Log.ERROR, "Response is: ", sResponse);

            if (sResponse == "Error" || sResponse.isEmpty()) {
                returnToHomeActivity("There was an error viewing friends");
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

            int nResponseCode = (int) nTmp;

            if(nResponseCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
            {

                pExp = pXpath.compile("count(Barker/friend)");
                nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);
                int nAcceptedCount = (int) nTmp;

                String sUsername = "", sName = "", sEmail = "";
                String sTmp = "";
                boolean bIsAccepted = false;
                for(int i=1;i<=nAcceptedCount;++i)
                {
                    pExp = pXpath.compile("Barker/friend[" + i + "]/username");
                    sUsername = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                    pExp = pXpath.compile("Barker/friend[" + i + "]/name" );
                    sName = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                    pExp = pXpath.compile("Barker/friend[" + i + "]/email" );
                    sEmail = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                    pExp = pXpath.compile("Barker/friend[" + i + "]/isAccepted" );
                    sTmp = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                    bIsAccepted = sTmp.trim().equals("yes");

                    User pFriend = new User(sEmail,sUsername,sName,bIsAccepted);

                    vpFriends.add(pFriend);

                }

                FriendAdapter pAdapter = new FriendAdapter(this, R.layout.friend_inflate, vpFriends);
                pView.setAdapter(pAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnToHomeActivity(String sMessage)
    {
        Intent pHomeIntent = new Intent(FriendListActivity.this, HomeActivity.class);
        pHomeIntent.putExtra("message", sMessage);
        startActivity(pHomeIntent);
    }

    private void openAddFriendActivity()
    {
        Intent pIntent = new Intent(FriendListActivity.this, AddFriendActivity.class);
        startActivity(pIntent);
    }

    private void returnToHomeActivity()
    {
        Intent pHomeIntent = new Intent(FriendListActivity.this, HomeActivity.class);
        startActivity(pHomeIntent);
    }


}
