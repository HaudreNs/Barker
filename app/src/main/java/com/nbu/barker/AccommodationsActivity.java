package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

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

public class AccommodationsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner pAccommodationType = null;
    String sAccommodationType = "all";

    @Override
    public void onBackPressed() {
        openHomeActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accommodations);

        pAccommodationType = (Spinner) findViewById(R.id.spAccommodationType);
        pAccommodationType.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.accommodation_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pAccommodationType.setAdapter(adapter);



        Button bAdd = (Button) findViewById(R.id.bAccommodationAdd);

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateAccommodation();
            }
        });
    }

    private void returnToHomeActivity(String sMessage)
    {
        Intent pHomeIntent = new Intent(AccommodationsActivity.this, HomeActivity.class);
        pHomeIntent.putExtra("message", sMessage);
        startActivity(pHomeIntent);
    }

    private void openCreateAccommodation()
    {
        Intent pIntent = new Intent(AccommodationsActivity.this, CreateAccommodationActivity.class);
        startActivity(pIntent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            sAccommodationType = (String) parent.getItemAtPosition(position);

        ListView pView = findViewById(R.id.accommodationsListView);
        int nStartAccommodation = 1;

        String sXML = "        <Barker requestType=\"getAccommodations\">\n" +
                "            <getAccommodations>\n" +
                "                <fromAccommodation>" + nStartAccommodation + "</fromAccommodation>\n" +
                "                <showAccommodations>10</showAccommodations>\n" +
                "                <accommodationType>" + sAccommodationType + "</accommodationType> \n" +
                "            </getAccommodations>\n" +
                "        </Barker> \n";

        String sResponse = "";
        ArrayList<Accommodation> vpAccommodations = new ArrayList<Accommodation>();

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
                pExp = pXpath.compile("count(Barker/accommodation)");
                nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);
                int nAcceptedCount = (int) nTmp;

                String sUsername = "", sName = "", sDescription = "";
                double nRating = 0;
                int nVoted = 0 , nId = 0;
                for(int i=1;i<=nAcceptedCount;++i)
                {
                    pExp = pXpath.compile("Barker/accommodation[" + i + "]/name");
                    sName = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                    pExp = pXpath.compile("Barker/accommodation[" + i + "]/rating" );
                    nRating = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                    pExp = pXpath.compile("Barker/accommodation[" + i + "]/voted" );
                    nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                    nVoted = (int) nTmp;

                    pExp = pXpath.compile("Barker/accommodation[" + i + "]/description" );
                    sDescription = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                    pExp = pXpath.compile("Barker/accommodation[" + i + "]/username" );
                    sUsername = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                    pExp = pXpath.compile("Barker/accommodation[" + i + "]/id" );
                    nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                    nId = (int) nTmp;

                    Accommodation pAccommodation = new Accommodation(nId, sName, sDescription, sUsername, nRating, nVoted);


                    vpAccommodations.add(pAccommodation);

                }

                AccommodationAdapter pAdapter = new AccommodationAdapter(this, R.layout.accommodation_inflate, vpAccommodations);
                pView.setAdapter(pAdapter);
            }
            else {
                returnToHomeActivity("Couldn't load accommodations.Please try again later");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            sAccommodationType = "all";

    }

    private void openHomeActivity()
    {
        Intent pIntent = new Intent(AccommodationsActivity.this, HomeActivity.class);
        startActivity(pIntent);
    }
}

