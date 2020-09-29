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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail = null;
    EditText etNames = null;
    EditText etPassword = null;
    EditText etUsername = null;
    TextView tRegistrationErrors = null;
    Button bRegister = null;
    Button bCancel = null;

    private String m_sResponse = "";


    @Override
    public void onBackPressed() {
        returnToMainMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etNames = (EditText) findViewById(R.id.etNames);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tRegistrationErrors = (TextView) findViewById(R.id.tRegisterErrors);
        etUsername = findViewById(R.id.etRegisterUsername);

        bRegister = (Button) findViewById(R.id.bRegisterSend);
        bCancel = findViewById(R.id.bCancel);



        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bRegister.setClickable(false);
                bCancel.setClickable(false);

                String sEmail = etEmail.getText().toString();
                String sPassword = etPassword.getText().toString();
                String sNames = etNames.getText().toString();
                String sUsername = etUsername.getText().toString();

                String sRegistrationErrors = Tools.validateProfileParameters( sEmail ,sUsername,sNames,sPassword);

                if(!sRegistrationErrors.isEmpty()) {
                    Log.println(Log.ERROR,"bRegister.onClickListener", "Registration errors not empty." +
                            "Errors:" + sRegistrationErrors);
                    tRegistrationErrors.setText(sRegistrationErrors);
                    bRegister.setClickable(true);
                    bCancel.setClickable(true);
                }
                else
                {
                    Log.println(Log.INFO,"bRegister.onClickListener", "Registration to be created and send");
                    String sPasswordHashed = Tools.hashPassword(sPassword,"SHA-256");

                    Log.println(Log.INFO,"bRegister.onClickListener", "Password Hashed is: " + sPasswordHashed);

                    String sXML = "            <Barker requestType=\"register\">\r\n" +
                            "                <register>\r\n" +
                            "                    <username>" + sUsername + "</username>\r\n"+
                            "                    <name>" + sNames + "</name>\r\n" +
                            "                    <password>" + sPasswordHashed + "</password>\r\n" +
                            "                    <email>" + sEmail + "</email>\r\n" +
                            "                </register>\r\n" +
                            "            </Barker>\r\n";


                    Log.println(Log.INFO,"bRegister.onClickListener", "XML Created is: " + sXML);

                    String sResponse = "";
                    String sStatusCode = "";
                    try
                    {
                        sResponse = Tools.sendRequest(sXML);
                        Log.println(Log.ERROR, "Response is: ", sResponse);

                        if(sResponse == "Error" || sResponse.isEmpty())
                        {
                            tRegistrationErrors.setText("There was an internal error while parsing your registration");
                            bRegister.setClickable(true);
                            bCancel.setClickable(true);
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
                        sStatusCode = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    }
                    catch(Exception e)
                    {
                        bRegister.setClickable(true);
                        bCancel.setClickable(true);
                        Log.println(Log.ERROR,"Registration error", e.getMessage());
                        return;
                    }

                    //success
                    if(sStatusCode.equals("200"))
                    {
                        returnToMainMenu("Registration success");
                    }
                    //user already exists
                    else if(sStatusCode.equals("402"))
                    {
                        bRegister.setClickable(true);
                        bCancel.setClickable(true);
                        Toast.makeText(RegisterActivity.this, "User with such email already exists", Toast.LENGTH_SHORT).show();
                    }
                    //all other general types of errors
                    else
                    {
                        bRegister.setClickable(true);
                        bCancel.setClickable(true);
                        returnToMainMenu("There was an error with your request. Please try again later");
                    }

                    Log.println(Log.ERROR,"helloWorld", "Response is: " + sResponse);

                }


            }
            });


        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainMenu();
            }
        });




    }

    private void returnToMainMenu() {
        Intent pMainIntent = new Intent(this, MainActivity.class);
        startActivity(pMainIntent);
    }

    private void returnToMainMenu(String sMessage) {
        Intent pMainIntent = new Intent(this, MainActivity.class);
        pMainIntent.putExtra("message", sMessage);
        startActivity(pMainIntent);
    }

}
