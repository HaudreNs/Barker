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
import javax.xml.xpath.XPathFactory;

public class ProfileActivity extends AppCompatActivity {

    EditText etUsername = null;
    EditText etEmail = null;
    EditText etNames = null;
    EditText etBirthDate = null;
    TextView tProfileChangeErrors = null;

    Button bChangeProfile = null;
    final String sTAG = "PROFILE";
    Button bBack = null;

    @Override
    public void onBackPressed() {
        openHomeActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final User pActivityUser = SessionParameters.getApplicationUser();

        if(pActivityUser == null)
        {
            returnToLogin();
        }

        etUsername = findViewById(R.id.etProfileUsername);
        etUsername.setText(pActivityUser.getUsername());

        etEmail = findViewById(R.id.etProfileEmail);
        etEmail.setText(pActivityUser.getEmail());

        etNames = findViewById(R.id.etProfileNames);
        etNames.setText(pActivityUser.getNames());

        tProfileChangeErrors = findViewById(R.id.tProfileChangeErrors);

        bBack = findViewById(R.id.bProfileBack);
        bChangeProfile = findViewById(R.id.bProfileSave);

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
            }
        });

        bChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bChangeProfile.setClickable(false);
                bBack.setClickable(false);

                tProfileChangeErrors.setVisibility(View.INVISIBLE);

                String sUsername = etUsername.getText().toString();
                String sNames = etNames.getText().toString();
                String sEmail = etEmail.getText().toString();
                String sProfileChangeErrors = Tools.validateProfileParameters(sEmail,sUsername,sNames);
                int nStatusCode = 0;

                if(!sProfileChangeErrors.isEmpty())
                {
                    tProfileChangeErrors.setText(sProfileChangeErrors);
                    tProfileChangeErrors.setVisibility(View.VISIBLE);
                    bChangeProfile.setClickable(true);
                    bBack.setClickable(true);
                    return;
                }

                String sXML = "<Barker requestType=\"changeProfileParameters\">\n" +
                        "    <changeProfileParameters>\n" +
                        "        <oldName>" + pActivityUser.getNames() + "</oldName>\n" +
                        "        <oldEmail>" + pActivityUser.getEmail() + "</oldEmail>\n" +
                        "        <oldUsername>" + pActivityUser.getUsername() + "</oldUsername>\n" +
                        "        <password>" + pActivityUser.getPassword() + "</password>\n" +
                        "        <newName>" + sNames + "</newName>\n" +
                        "        <newEmail>" + sEmail + "</newEmail>\n" +
                        "        <newUsername>" + sUsername + "</newUsername>\n" +
                        "    </changeProfileParameters>\n" +
                        "</Barker>";



                Log.println(Log.INFO,"bChangeProfile.onClickListener", "XML Created is: " + sXML);

                String sResponse = "";
                String sStatusCode = "";
                try
                {
                    sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if(sResponse == "Error" || sResponse.isEmpty())
                    {
                        Toast.makeText(ProfileActivity.this, "There was an error changing your user information.Please try again", Toast.LENGTH_LONG).show();
                        bChangeProfile.setClickable(true);
                        bBack.setClickable(true);
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
                    nStatusCode = (int) nTmp;
                }
                catch(Exception e)
                {
                    bChangeProfile.setClickable(true);
                    bBack.setClickable(true);
                    Log.println(Log.ERROR, "Profile error", e.getMessage());
                    Toast.makeText(ProfileActivity.this, "There was an error trying to change the profile parameters", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                //success
                if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
                {
                    Log.println(Log.ERROR, sTAG ,"Profile change has been successful");
                    User pUserUpdated = new User();
                    pUserUpdated.setEmail(sEmail);
                    pUserUpdated.setNames(sNames);
                    pUserUpdated.setUsername(sUsername);
                    pUserUpdated.setPassword(SessionParameters.getApplicationUser().getPassword());

                    SessionParameters.setApplicationUser(pUserUpdated);

                    Toast.makeText(ProfileActivity.this, "Information successfully changed", Toast.LENGTH_LONG).show();
                    Log.println(Log.ERROR, sTAG, "New profile is:" + SessionParameters.getApplicationUser().getEmail()
                            + " " +  SessionParameters.getApplicationUser().getNames() + " " +  SessionParameters.getApplicationUser().getUsername());
                    bChangeProfile.setClickable(true);
                    bBack.setClickable(true);
                }
                else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.USER_EMAIL_ALREADY_EXISTS))
                {
                    Toast.makeText(ProfileActivity.this, "User with such email already exists", Toast.LENGTH_LONG).show();
                    bChangeProfile.setClickable(true);
                    bBack.setClickable(true);
                }
                else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.USER_ALREADY_EXISTS))
                {
                    Toast.makeText(ProfileActivity.this, "User with such username already exists", Toast.LENGTH_LONG).show();
                    bChangeProfile.setClickable(true);
                    bBack.setClickable(true);
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "There was an error changing your account information.Please try again", Toast.LENGTH_LONG).show();
                    bChangeProfile.setClickable(true);
                    bBack.setClickable(true);
                    return;
                }
            }
        });
    }

    private void returnToLogin() {
        Intent pLoginIntent = new Intent(this, MainActivity.class);
        startActivity(pLoginIntent);
    }

    private void openHomeActivity() {
        Intent pHomeIntent = new Intent(this, HomeActivity.class);
        startActivity(pHomeIntent);
    }
}
