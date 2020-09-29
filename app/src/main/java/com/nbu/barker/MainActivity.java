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
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button bLogin;
    private Button bRegister;
    private TextView tForgotPassword;
    private TextView tInfo;


    @Override
    public void onBackPressed() {
    this.finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        bRegister = (Button) findViewById(R.id.bRegister);
        tForgotPassword = (TextView) findViewById(R.id.ctForgotPassword);
        //information regarding login/registration
        tInfo = (TextView) findViewById(R.id.tMainInformation);


        //to use for information sent from register and forgottenPassword activities
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {
               String sInformation = extras.getString("message");

                Toast.makeText(MainActivity.this, sInformation, Toast.LENGTH_LONG).show();

                //only need that to show information to the user
                tInfo.setVisibility(View.INVISIBLE);
            }
        }


        //on login click
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bLogin.setClickable(false);

                String sEmail = etEmail.getText().toString().trim();
                String sPassword = etPassword.getText().toString().trim();
                //check if email is valid
                if (Tools.validateEmail(sEmail)) {
                    if (Tools.validatePassword(sPassword))
                    {
                        tInfo.setVisibility(View.INVISIBLE);

                        String sPasswordHashed = Tools.hashPassword(sPassword,"SHA-256");

                        Log.println(Log.INFO,"bRegister.onClickListener", "Password Hashed is: " + sPasswordHashed);

                        String sXML = "            <Barker requestType=\"" + Constants.requestTypeToText(Constants.RequestType.LOG_IN)  + "\">\r\n" +
                                      "                <login>\r\n" +
                                      "                    <email>" + sEmail + "</email>\r\n" +
                                      "                    <password>" + sPasswordHashed + "</password>\r\n" +
                                      "                </login>\r\n" +
                                      "            </Barker>\r\n";


                        Log.println(Log.INFO,"bRegister.onClickListener", "XML Created is: " + sXML);

                        String sResponse = "";
                        int nStatusCode = 0;

                        int nUserId = 0;
                        String sUserUsername = "";
                        String sUserNames = "";

                        double nTmp;
                        try
                        {
                            sResponse = Tools.sendRequest(sXML);
                            Log.println(Log.ERROR, "Response is: ", sResponse);

                            if(sResponse == "Error" || sResponse.isEmpty())
                            {
                                tInfo.setText("There was an internal error while processing you log in");
                                etPassword.setText("");
                                bLogin.setClickable(true);
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
                            nTmp = (double)pExp.evaluate( pDoc, XPathConstants.NUMBER );
                            nStatusCode =(int) nTmp;
                            //success
                            if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
                            {
                 /* Response:
                 <Barker requestType="login">
                    <statusCode>200</statusCode>
                    <statusText>OK</statusText>
                    <id>1</id>
                    <username>HaudreN</username>
                    <name>Kaloyan Nikolov</name>
                    <email>haudrennb@gmail.com </email>
                </Barker>
                 */
                                pExp = pXpath.compile("Barker/id");
                                nTmp = (double)pExp.evaluate( pDoc, XPathConstants.NUMBER );
                                nUserId =(int) nTmp;

                                pExp = pXpath.compile("Barker/username");
                                sUserUsername = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                                pExp = pXpath.compile("Barker/name");
                                sUserNames = (String) pExp.evaluate(pDoc,XPathConstants.STRING);

                                User pApplicationUser = new User();

                                pApplicationUser.setEmail(sEmail);
                                pApplicationUser.setNames(sUserNames);
                                pApplicationUser.setUsername(sUserUsername);
                                pApplicationUser.setId(nUserId);
                                pApplicationUser.setPassword(sPasswordHashed);

                                SessionParameters.setApplicationUser(pApplicationUser);

                                bLogin.setClickable(true);
                                openHomeActivity();

                            }
                            else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.MISSING_USER))
                            {
                                Toast.makeText(MainActivity.this, "No user found with such email", Toast.LENGTH_LONG).show();
                                etPassword.setText("");
                                bLogin.setClickable(true);
                                return;
                            }

                            else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.BAD_PASSWORD))
                            {
                                Toast.makeText(MainActivity.this, "Password incorrect.Please try again", Toast.LENGTH_LONG).show();
                                etPassword.setText("");
                                bLogin.setClickable(true);
                                return;
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "There was an internal error. Please try again", Toast.LENGTH_LONG).show();
                                bLogin.setClickable(true);
                                return;
                            }
                        }
                        catch(Exception e)
                        {
                            bLogin.setClickable(true);
                            Log.println(Log.ERROR,"Registration Error", e.getMessage());
                        }



                    }
                    else
                        {
                        tInfo.setVisibility(View.VISIBLE);
                        tInfo.setText("Incorrect password. Please retype your password and try again");
                            bLogin.setClickable(true);
                        }
                } else {
                    tInfo.setVisibility(View.VISIBLE);
                    tInfo.setText("Incorrect email. Please retype your email and try again");
                    bLogin.setClickable(true);
                }

            }


        });

        tForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPasswordForgottenActivity();
            }

        });

        //on register click
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }

        });

    }



    public void openHomeActivity()
    {
        Intent pHomeIntent = new Intent(this, HomeActivity.class);
        startActivity(pHomeIntent);
    }

    public void openRegisterActivity()
    {
        Intent pRegisterIntent = new Intent(this, RegisterActivity.class);
        startActivity(pRegisterIntent);
    }

        private void openPasswordForgottenActivity() {
        Intent pPasswordForgottenIntent = new Intent(this,ForgottenPasswordActivity.class);
        startActivity(pPasswordForgottenIntent);
    }


}
