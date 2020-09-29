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

public class ForgottenPasswordActivity extends AppCompatActivity {

    Button bResetPassword = null;
    Button bCancel = null;
    EditText etEmail = null;
    TextView tError = null;

    @Override
    public void onBackPressed() {
        loginActivityIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);


        bResetPassword = findViewById(R.id.bForgottenPasswordReset);
        etEmail = findViewById(R.id.etForgottenPasswordEmail);
        tError = findViewById(R.id.tForgottenPasswordError);
        bCancel = findViewById(R.id.bForgottenPasswordCancel);

        bResetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tError.setVisibility(View.INVISIBLE);
                String sEmail = etEmail.getText().toString();

                if(sEmail.isEmpty())
                {
                    tError.setText("Please enter your email first");
                    tError.setVisibility(View.VISIBLE);
                }
                if(!Tools.validateEmail(sEmail))
                {
                    tError.setText("Invalid email address. Please enter a valid email address and try again");
                    tError.setVisibility(View.VISIBLE);
                }

                String sXML = "<Barker requestType=\"passwordReset\">\n" +
                        "    <passwordReset>\n" +
                        "        <email>" +sEmail + "</email>\n" +
                        "        <password/> \n" +
                        "        <code/>\n" +
                        "    </passwordReset>\n" +
                        "</Barker>";

                String sResponse = "";
                double nTmp;
                int nStatusCode;
                try {
                    sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        Toast.makeText(ForgottenPasswordActivity.this, "There was an error resetting you password. Please try again later"
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

                        resetActivityIntent(sEmail);

                    }
                    else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.MISSING_USER))
                    {
                        Toast.makeText(ForgottenPasswordActivity.this, "User with such email does not exist", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else
                    {
                        Toast.makeText(ForgottenPasswordActivity.this, "Internal error", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                catch(Exception e)
                {
                    Log.println(Log.ERROR, "reset password", e.getMessage());
                }


                
            }

        });

        bCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginActivityIntent();
            }
        });
    }

    private void loginActivityIntent() {
        Intent pBackIntent = new Intent(this, MainActivity.class);
        startActivity(pBackIntent);
    }

    private void resetActivityIntent(String sEmail)
    {
        Intent pResetIntent = new Intent(ForgottenPasswordActivity.this, ForgottenPasswordResetActivity.class);
        pResetIntent.putExtra("email", sEmail);
        startActivity(pResetIntent);
    }

}
