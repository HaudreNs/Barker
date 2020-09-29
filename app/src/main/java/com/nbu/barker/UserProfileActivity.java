package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class UserProfileActivity extends AppCompatActivity {

    Button bAddFriend;
    TextView tvUserEmail;
    TextView tvUserUsername;
    TextView tvUserNames;
    Button bCancel;

    @Override
    public void onBackPressed() {
    returnToMainMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String sUsername = "";
        if (extras != null) {
            if (extras.containsKey("username")) {
                sUsername = extras.getString("username");
            }
        }

        if(sUsername.isEmpty())
        {
            returnToMainMenu("Error displaying user");
        }

        if(sUsername.equals(SessionParameters.getApplicationUser().getUsername()))
        {
            openProfileActivity();
        }
        tvUserEmail = findViewById(R.id.tvUserProfileEmail);
        tvUserNames = findViewById(R.id.tvUserProfileNames);
        tvUserUsername = findViewById(R.id.tvUserProfileUsername);
        bAddFriend = findViewById(R.id.bUserProfileAddFriend);
        bCancel = findViewById(R.id.bUserProfileCancel);


        String sXML = "        <Barker requestType=\"viewProfile\">\n" +
                "            <viewProfile>\n" +
                "                <email></email>\n" +
                "                <username>" + sUsername + "</username>\n" +
                "                <requestFromEmail>" + SessionParameters.getApplicationUser().getEmail() + "</requestFromEmail>\n" +
                "            </viewProfile>\n" +
                "        </Barker>";

        String sResponse = "";
        try
        {
            sResponse = Tools.sendRequest(sXML);
            Log.println(Log.ERROR, "Response is: ", sResponse);

            if(sResponse == "Error" || sResponse.isEmpty())
            {
                returnToMainMenu("Couldn't load user profile");
                return;
            }

            Document pDoc= null;
            DocumentBuilder pBuilder = null;
            DocumentBuilderFactory pFactory = DocumentBuilderFactory.newInstance();
            pBuilder = pFactory.newDocumentBuilder();
            pDoc = pBuilder.parse( new InputSource( new StringReader(sResponse)) );

            XPathFactory pXpathFactory = XPathFactory.newInstance();
            XPath pXpath = pXpathFactory.newXPath();
            XPathExpression pExp = null;


            pExp = pXpath.compile("Barker/statusCode");
            double nTmp = (double)pExp.evaluate( pDoc, XPathConstants.NUMBER );
            int nStatusCode = (int) nTmp;

            if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
            {
                pExp = pXpath.compile("Barker/user/name");
                String sName = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                pExp = pXpath.compile("Barker/user/email");
                final String sEmail = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                pExp = pXpath.compile("Barker/user/friendAccepted");
                final String sFriendAccepted = (String)pExp.evaluate( pDoc, XPathConstants.STRING );


                if(sFriendAccepted.equals("0"))
                {
                    bAddFriend.setText("Pending Friend Request");
                    bAddFriend.setClickable(false);
                }
                else if(sFriendAccepted.equals("1"))
                {
                    bAddFriend.setText("MESSAGE");

                    final String sTmp = sUsername;

                    bAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openMessageActivity(sTmp);
                        }
                    });
                }
                else
                {
                    bAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sXMLAddFriend = "<Barker requestType=\"addFriend\">\n" +
                                    "    <addFriend>\n" +
                                    "        <fromAddress>" + SessionParameters.getApplicationUser().getEmail() + "</fromAddress>\n" +
                                    "        <toAddress>" + sEmail + "</toAddress>\n" +
                                    "    </addFriend>\n" +
                                    "</Barker> \n";

                            String sResponseAddFriend = Tools.sendRequest(sXMLAddFriend);


                            try {
                                Document pDoc2= null;
                                DocumentBuilder pBuilder2 = null;
                                DocumentBuilderFactory pFactory2 = DocumentBuilderFactory.newInstance();
                                pBuilder2 = pFactory2.newDocumentBuilder();
                                pDoc2 = pBuilder2.parse( new InputSource( new StringReader(sResponseAddFriend)) );

                                XPathFactory pXpathFactory2 = XPathFactory.newInstance();
                                XPath pXpath2 = pXpathFactory2.newXPath();
                                XPathExpression pExp2 = null;
                                pExp2 = pXpath2.compile("Barker/statusCode");
                                double nTmpNew = (double)pExp2.evaluate( pDoc2, XPathConstants.NUMBER );
                                int nStatusCodeFriendRequest = (int) nTmpNew;
                                if(nStatusCodeFriendRequest == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
                                {
                                    Toast.makeText(UserProfileActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else if(nStatusCodeFriendRequest == Constants.requestStatusToCode(Constants.RequestServerStatus.FRIEND_REQUEST_ALREADY_EXISTS))
                                {
                                    Toast.makeText(UserProfileActivity.this, "Such request already exists", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else
                                {
                                    Toast.makeText(UserProfileActivity.this, "Couldn't send friend request. Please try again later", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (Exception e) {
                                Toast.makeText(UserProfileActivity.this, "Couldn't send friend request. Please try again later", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                return;
                            }

                        }
                    });
                }

                tvUserEmail.setText(sEmail);
                tvUserNames.setText(sName);
                tvUserUsername.setText(sUsername);

                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        returnToMainMenu();
                    }
                });




            }
            else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.MISSING_USER))
            {
                returnToMainMenu("This user no longer exists");
            }
            else
            {
                returnToMainMenu("Could not load user information");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



    }

    private void returnToMainMenu()
    {
        Intent pIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
        startActivity(pIntent);
    }

    private void returnToMainMenu(String sMessage)
    {
        Intent pIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
        if(!sMessage.isEmpty()) pIntent.putExtra("message", sMessage);

        startActivity(pIntent);

    }

    private void openMessageActivity(String sUsername)
    {
        Intent pIntent = new Intent(UserProfileActivity.this, MessageActivity.class);
        pIntent.putExtra("username", sUsername);
        startActivity(pIntent);
    }

    private void openProfileActivity()
    {
        Intent pIntent = new Intent(UserProfileActivity.this, ProfileActivity.class);
        startActivity(pIntent);
    }



}
