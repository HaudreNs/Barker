package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class MessageActivity extends AppCompatActivity {

    EditText etMessage;
    Button bSend;
    ListView listView;
    ArrayList<String> vpMessages = new ArrayList<>();
    ArrayAdapter<String> pAdapter;
    int nLastID;
    String sUsername = "";
    Thread pRenewMessages;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pRenewMessages.interrupt();
        openHomeActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        nLastID = 0;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("username")) {
                sUsername = extras.getString("username");
            }
        }
        else
        {
            returnToHome("Couldn't find user");
        }

        etMessage = findViewById(R.id.etMessageWrite);
        bSend = findViewById(R.id.bMessageSend);
        listView = findViewById(R.id.messageListView);

        String sXML = "<Barker requestType=\"getMessages\">\n" +
                "    <getMessages>\n" +
                "        <user1>" + SessionParameters.getApplicationUser().getUsername() + "</user1>\n" +
                "        <user2>" + sUsername + "</user2>\n" +
                "        <afterID></afterID>\n" +
                "    </getMessages>\n" +
                "</Barker> ";

        Log.println(Log.ERROR,"happening", "REQUEST: \n" + sXML);

        Log.println(Log.INFO,"bChangeProfile.onClickListener", "XML Created is: " + sXML);

        String sResponse = "";
        try
        {
            int nStatusCode = 0;
            sResponse = Tools.sendRequest(sXML);
            Log.println(Log.ERROR, "Response is: ", sResponse);

            if(sResponse == "Error" || sResponse.isEmpty())
            {
                returnToHome("There was an error loading chat. Please try again later");
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

            //success
            if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
            {
                pExp = pXpath.compile("count(Barker/message)");
                nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                int nResult = (int) nTmp;

                String sUsernameFrom = "" , sMessage = "";

                for(int i =1;i<= nResult;++i)
                {
                    pExp = pXpath.compile("//Barker/message[" + i + "]/messageText");
                    sMessage = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    pExp = pXpath.compile("//Barker/message[" + i + "]/fromUser");
                    sUsernameFrom = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    vpMessages.add(sUsernameFrom + ": \n" + sMessage);

                    if(nResult == i)
                    {
                        pExp = pXpath.compile("//Barker/message[" + i + "]/id");
                        nTmp = (double) pExp.evaluate( pDoc, XPathConstants.NUMBER );
                        nLastID = (int) nTmp;
                    }

                }

            }
            else
            {
                returnToHome("There was an error loading chat. Please try again later");
                return;
            }
        }
        catch(Exception e)
        {
            returnToHome("There was an error loading chat. Please try again later");
            e.printStackTrace();
        }

        pAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vpMessages);

        listView.setAdapter(pAdapter);
        listView.setSelection(listView.getCount() - 1);


        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bSend.setClickable(false);
                String sMessage = etMessage.getText().toString();

                if (sMessage.isEmpty()) {
                    Toast.makeText(MessageActivity.this, "Please write a message first", Toast.LENGTH_SHORT).show();
                    bSend.setClickable(true);
                    return;
                }

                updateMessages();

                String sXML = "<Barker requestType=\"addMessage\">\n" +
                        "    <addMessage>\n" +
                        "        <userFrom>" + SessionParameters.getApplicationUser().getUsername() + "</userFrom>\n" +
                        "        <userTo>" + sUsername + "</userTo>\n" +
                        "        <messageText>" + sMessage + "</messageText>\n" +
                        "    </addMessage>\n" +
                        "</Barker>";

                try {
                    int nStatusCode = 0;
                    String sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        Log.println(Log.ERROR, "Response is: ", "THERE IS AN ERROR HERE");
                        returnToHome("There was an error loading chat. Please try again later");
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
                    nStatusCode = (int) nTmp;

                    //success
                    if (nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS)) {
                        vpMessages.add(SessionParameters.getApplicationUser().getUsername() + ": \n" + sMessage);

                        pExp = pXpath.compile("//Barker/messageId");
                        nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                        nLastID = (int) nTmp;


                        pAdapter.notifyDataSetChanged();
                        listView.setSelection(listView.getCount() - 1);
                        bSend.setClickable(true);
                        etMessage.setText("");

                    }


                    else
                {
                    bSend.setClickable(true);
                    returnToHome("There was an error loading chat. Please try again later");
                    return;
                }
            }
                catch(Exception e)
            {
                bSend.setClickable(true);
                Log.println(Log.ERROR, "Response is: ", e.getMessage());
                returnToHome("There was an error loading chat. Please try again later");
            }
        }


        });



        pRenewMessages= new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateMessages();
                            }
                        });
                    }
                    Log.println(Log.ERROR,"ERROR LOADING MESSAGES", "IT GOT INTERUPTED");
                } catch (InterruptedException e) {
                }
            }
        };
        pRenewMessages.start();




    }

    private void returnToHome(String sMessage)
    {
        Intent pIntent = new Intent(MessageActivity.this, HomeActivity.class);
        Log.println(Log.ERROR,"ERROR", sMessage);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

    private void updateMessages()
    {
        String sXML = "<Barker requestType=\"getMessages\">\n" +
                "    <getMessages>\n" +
                "        <user1>" + SessionParameters.getApplicationUser().getUsername() + "</user1>\n" +
                "        <user2>" + sUsername + "</user2>\n" +
                "        <afterID>" + nLastID + "</afterID>\n" +
                "    </getMessages>\n" +
                "</Barker> ";


        Log.println(Log.INFO,"bChangeProfile.onClickListener", "XML Created is: " + sXML);

        String sResponse = "";
        try
        {
            int nStatusCode = 0;
            sResponse = Tools.sendRequest(sXML);
            Log.println(Log.ERROR, "Response is: ", sResponse);

            if(sResponse == "Error" || sResponse.isEmpty())
            {
                returnToHome("There was an error loading chat. Please try again later");
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

            //success
            if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
            {
                pExp = pXpath.compile("count(Barker/message)");
                nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                int nResult = (int) nTmp;

                String sUsernameFrom = "" , sMessage = "";
                int nId;
                for(int i =1;i<= nResult;++i)
                {
                    pExp = pXpath.compile("//Barker/message[" + i + "]/messageText");
                    sMessage = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    pExp = pXpath.compile("//Barker/message[" + i + "]/fromUser");
                    sUsernameFrom = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    vpMessages.add(sUsernameFrom + ": \n" + sMessage);

                    pAdapter.notifyDataSetChanged();




                    nId = (int) nTmp;

                    if(nResult == nTmp)
                    {
                        pExp = pXpath.compile("//Barker/message[" + i + "]/id");
                        nTmp = (double) pExp.evaluate( pDoc, XPathConstants.NUMBER );
                        listView.setSelection(listView.getCount() - 1);
                        nLastID = (int) nTmp;
                    }

                }

            }
            else
            {
                returnToHome("There was an error loading chat. Please try again later MESSAGE 1");
                return;
            }
        }
        catch(Exception e)
        {
            returnToHome("There was an error loading chat. Please try again later MESSAGE 2");
            e.printStackTrace();
        }

    }

    private void openHomeActivity()
    {
        Intent pIntent = new Intent(MessageActivity.this, HomeActivity.class);
        startActivity(pIntent);
    }

}