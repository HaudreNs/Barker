package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AddFriendActivity extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        openFriendsActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        Button bAdd = findViewById(R.id.bAddFriendSendRequest);


        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etEmail = findViewById(R.id.etAddFriendEmail);

                String sEmail = etEmail.getText().toString();

                if(sEmail.isEmpty())
                {
                    Toast.makeText(AddFriendActivity.this, "Please provide an email address", Toast.LENGTH_SHORT).show();
                }
                if(sEmail.equals(SessionParameters.getApplicationUser().getEmail()))
                {
                    Toast.makeText(AddFriendActivity.this, "You cannot add yourself as a friend", Toast.LENGTH_SHORT).show();
                }

                if(!Tools.validateEmail(sEmail))
                {
                    Toast.makeText(AddFriendActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                }

                String sXML = "<Barker requestType=\"addFriend\">\n" +
                        "                <addFriend>\n" +
                        "                    <fromAddress>" + SessionParameters.getApplicationUser().getEmail() + "</fromAddress>\n" +
                        "                    <toAddress>" + sEmail + "</toAddress>\n" +
                        "                </addFriend>\n" +
                        "            </Barker>";

                String sResponse = "";
                ArrayList<User> vpFriends = new ArrayList<User>();

                try {
                    sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        returnToFriendList("There was an error adding friend");
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

                    if (nResponseCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS)) {
                        returnToFriendList("Successfully sent request");
                    }
                    else if (nResponseCode == Constants.requestStatusToCode(Constants.RequestServerStatus.MISSING_USER)) {
                        Toast.makeText(AddFriendActivity.this, "User with such email does not exist", Toast.LENGTH_SHORT).show();;
                    }
                    else if (nResponseCode == Constants.requestStatusToCode(Constants.RequestServerStatus.FRIEND_REQUEST_ALREADY_EXISTS)) {
                        Toast.makeText(AddFriendActivity.this, "Friend request already exists", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        returnToFriendList("There was an internal error proccessing your request");
                    }
                    }
                catch(Exception e)
                {
                    Toast.makeText(AddFriendActivity.this, "There was a problem adding friend.Please try again", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void returnToFriendList(String sMessage)
    {
        Intent pIntent = new Intent(AddFriendActivity.this, FriendListActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

    private void openFriendsActivity()
    {
        Intent pIntent = new Intent(AddFriendActivity.this, FriendListActivity.class);
        startActivity(pIntent);
    }
}
