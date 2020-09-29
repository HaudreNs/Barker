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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class ForgottenPasswordResetActivity extends AppCompatActivity {

    EditText etPassword = null;
    EditText etCode = null;
    Button bReset = null;

    @Override
    public void onBackPressed() {
        returnToForgottenPasswordActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password_reset);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String sEmail = "";
        //receive user email
        if (extras != null) {
            if (extras.containsKey("email")) {
                sEmail = extras.getString("email");
            }
        }
        if(sEmail.isEmpty())
        {
            returnToForgottenPasswordActivity();
        }

        final String sRequestEmail = sEmail;

        bReset = findViewById(R.id.bForgottenPasswordReset);
        etPassword = findViewById(R.id.etForgottenPasswordResetPassword);
        etCode = findViewById(R.id.etForgottenPasswordResetCode);

        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sPassword = etPassword.getText().toString();
                String sCode = etCode.getText().toString();

                if(!Tools.validatePassword(sPassword))
                {
                    Toast.makeText(ForgottenPasswordResetActivity.this, "Wrong password format", Toast.LENGTH_LONG).show();
                    return;
                }

                sPassword = Tools.hashPassword(sPassword, "SHA-256");

                String sXML = "<Barker requestType=\"passwordReset\">\n" +
                        "    <passwordReset>\n" +
                        "        <email>" + sRequestEmail + "</email>\n" +
                        "        <password>" + sPassword + "</password> \n" +
                        "        <code>" + sCode + "</code>\n" +
                        "    </passwordReset>\n" +
                        "</Barker>";

                String sResponse = "";
                double nTmp;
                int nStatusCode;
                try {
                    sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        Toast.makeText(ForgottenPasswordResetActivity.this, "There was an error resetting you password. Please try again later"
                                , Toast.LENGTH_SHORT).show();
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
                    nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);
                    nStatusCode = (int) nTmp;
                    //success
                    if (nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS)) {
                        goToHomeActivity("Password has been reset");

                    }
                    //wrong code has been given
                    else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.BAD_XML))
                    {
                        Toast.makeText(ForgottenPasswordResetActivity.this, "Code is incorrect.Please retype it and try again", Toast.LENGTH_LONG).show();
                    }

                }
                catch(Exception e)
                {
                    Log.println(Log.ERROR, "reset password", e.getMessage());
                }
            }
        });


    }

    private void returnToForgottenPasswordActivity()
    {
        Intent pIntent = new Intent(this, ForgottenPasswordActivity.class);
        startActivity(pIntent);
    }

    private void goToHomeActivity(String sStatus)
    {
        Intent pIntent = new Intent(this, MainActivity.class);
        pIntent.putExtra("message", sStatus);
        startActivity(pIntent);
    }


}
