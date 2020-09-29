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

public class FindWalkActivity extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        returnToHome();
    }

    Button bCreateWalk = null;
    ListView pView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_walk);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {
                String sInformation = extras.getString("message");

                Toast.makeText(FindWalkActivity.this, sInformation, Toast.LENGTH_SHORT).show();
            }
        }

        pView = findViewById(R.id.walkListView);

        ArrayList<Walk> vpWalks = new ArrayList<Walk>();


        String sRequest = "<Barker requestType=\"getWalks\">\n" +
                "    <getWalks>\n" +
                "        <email>" + SessionParameters.getApplicationUser().getEmail() + "</email>\n" +
                "    </getWalks>\n" +
                "</Barker>";

        String sResponse = "";

        try
        {
            sResponse = Tools.sendRequest(sRequest);
            Log.println(Log.ERROR, "Response is: ", sResponse);

            if(sResponse == "Error" || sResponse.isEmpty())
            {
                returnToHome("There was an error opening walks. Please try again");
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
            double nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

            int nResponseCode = (int) nTmp;

            if(nResponseCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
            {
                pExp = pXpath.compile("count(Barker/walk)");
                nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                int nResult = (int) nTmp;

                String sLocation = "", sUserUsername = "", sMessage = "";
                int nId = 0, nTime = 0;
                for(int i =1;i<= nResult;++i)
                {
                    pExp = pXpath.compile("//Barker/walk[" + i + "]/location");
                    sLocation = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    pExp = pXpath.compile("//Barker/walk[" + i + "]/username");
                    sUserUsername = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    pExp = pXpath.compile("//Barker/walk[" + i + "]/message");
                    sMessage = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    pExp = pXpath.compile("//Barker/walk[" + i + "]/id");
                    nTmp = (double) pExp.evaluate( pDoc, XPathConstants.NUMBER );

                    nId = (int) nTmp;

                    pExp = pXpath.compile("//Barker/walk[" + i + "]/time");
                    nTmp = (double) pExp.evaluate( pDoc, XPathConstants.NUMBER );

                    nTime = (int) nTmp;

                    Walk pTmpWalk = new Walk(sMessage, sUserUsername, nTime , nId);
                    vpWalks.add(pTmpWalk);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        WalkAdapter pAdapter = new WalkAdapter(this, R.layout.walk_inflate, vpWalks);
        pView.setAdapter(pAdapter);

        bCreateWalk = (Button) findViewById(R.id.bFindWalkCreateWalk);

        bCreateWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateWalkActivity();
            }
        });
    }

    private void openCreateWalkActivity()
    {
        Intent pIntent = new Intent(FindWalkActivity.this, CreateWalkActivity.class);
        startActivity(pIntent);
    }

    private void returnToHome()
    {
        Intent pIntent = new Intent(FindWalkActivity.this, HomeActivity.class);
        startActivity(pIntent);
    }

    private void returnToHome(String sMessage)
    {
        Intent pIntent = new Intent(FindWalkActivity.this, HomeActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

}