package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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

public class AccommodationPickedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accommodation_picked);

        int nTmp = 0;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                nTmp = extras.getInt("id");

            }
        }

        if(nTmp <= 0)
        {
            returnToAccommodations("Accommodation could not be found");
        }
        final int nId = nTmp;

        String sXML = "         <Barker requestType=\"viewAccommodation\">\n" +
                "            <viewAccommodation>\n" +
                "                <id>" + nId + "</id>\n" +
                "            </viewAccommodation>\n" +
                "        </Barker>";

        String sResponse = "";
        try {
            sResponse = Tools.sendRequest(sXML);
            Log.println(Log.ERROR, "Response is: ", sResponse);

            if (sResponse == "Error" || sResponse.isEmpty()) {
                Toast.makeText(AccommodationPickedActivity.this, "Couldn't rate activity.Please try again later", Toast.LENGTH_SHORT).show();
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
            double nTmpCode = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

            int nResponseCode = (int) nTmpCode;

            if (nResponseCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS)) {
                pExp = pXpath.compile("Barker/accommodation/name");
                String sName = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                pExp = pXpath.compile("Barker/accommodation/description");
                String sDescription = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                pExp = pXpath.compile("Barker/accommodation/user");
                String sUser = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                pExp = pXpath.compile("Barker/accommodation/rating");
                String sRating = (String) pExp.evaluate(pDoc, XPathConstants.STRING);
                if(sRating.length() > 3) sRating = sRating.substring(0,3);



                pExp = pXpath.compile("Barker/accommodation/votedBy");
                String sTmp = (String) pExp.evaluate(pDoc, XPathConstants.STRING);

                sRating += "/" + sTmp;

                TextView tvTitle = findViewById(R.id.tvAccommodationTitle);
                TextView tvDescription = findViewById(R.id.tvAccommodationTopicDescription);
                TextView tvVoted = findViewById(R.id.tvAccommodationTopicRate);

                tvTitle.setText(sName);
                tvDescription.setText(sDescription);
                tvVoted.setText(sRating);
            }
            else if (nResponseCode == Constants.requestStatusToCode(Constants.RequestServerStatus.MISSING_ACCOMMODATION)) {
                returnToAccommodations("Accommodation no longer exists");
            }
            else
            {
                returnToAccommodations("There was an internal error. Please try again later");
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        final Spinner spAccommodationRate = (Spinner) findViewById(R.id.spAccommodationTopicRate);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.accommodation_rating, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccommodationRate.setAdapter(adapter);

        Button bRate = findViewById(R.id.bAccommodationPickedRate);

        bRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             String sXML = "        <Barker requestType=\"rateAccommodation\">\n" +
                     "            <rateAccommodation>\n" +
                     "                <accommodationId>" + nId + "</accommodationId>\n" +
                     "                <rate>" + spAccommodationRate.getSelectedItem() + "</rate>\n" +
                     "                <userEmail>" + SessionParameters.getApplicationUser().getEmail() + "</userEmail>\n" +
                     "            </rateAccommodation>\n" +
                     "        </Barker>";

                String sResponse = "";
                try {
                    sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        Toast.makeText(AccommodationPickedActivity.this, "Couldn't rate activity.Please try again later", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AccommodationPickedActivity.this, "Accommodation has been rated", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                    else if(nResponseCode == Constants.requestStatusToCode(Constants.RequestServerStatus.ACCOMMODATION_ALREADY_RATED))
                    {
                        Toast.makeText(AccommodationPickedActivity.this, "You have already rated this accommodation", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(AccommodationPickedActivity.this, "There was an error. Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(AccommodationPickedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    private void returnToAccommodations(String sMessage)
    {
        Intent pIntent = new Intent(AccommodationPickedActivity.this,AccommodationsActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

}
