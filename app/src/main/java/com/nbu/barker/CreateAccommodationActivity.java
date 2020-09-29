package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CreateAccommodationActivity extends AppCompatActivity {

    Spinner pAccommodationType = null;

    @Override
    public void onBackPressed() {
        openAccommodationsActivity();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_accommodation);

        Button bCreate = findViewById(R.id.bCreateAccommodationAdd);

        pAccommodationType = (Spinner) findViewById(R.id.spCreateAccommodationType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.accommodation_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pAccommodationType.setAdapter(adapter);


        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etName = findViewById(R.id.etCreateAccommodationName);
                EditText etDescription = findViewById(R.id.etCreateAccommodationDescription);

                String sName = etName.getText().toString();
                String sDescription = etDescription.getText().toString();

                if(sName.isEmpty() || sDescription.isEmpty())
                {
                    Toast.makeText(CreateAccommodationActivity.this, "Please first fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sXML = "        <Barker requestType=\"createAccommodation\">\n" +
                        "            <createAccommodation>\n" +
                        "                <accommodationName>" + sName + "</accommodationName>\n" +
                        "                <accommodationDescription>" + sDescription + "</accommodationDescription>\n" +
                        "                <userEmail>" + SessionParameters.getApplicationUser().getEmail() + "</userEmail>\n" +
                        "                <accommodationType>" + pAccommodationType.getSelectedItem() + "</accommodationType> \n" +
                        "            </createAccommodation>\n" +
                        "        </Barker>";

                String sResponse = "";
                try {
                    sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        Toast.makeText(CreateAccommodationActivity.this, "There was an error creating accommodation.Please try again", Toast.LENGTH_LONG).show();
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
                    int nStatusCode = (int) nTmp;

                    if (nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS)) {

                        openAccommodationsActivity("Successfully created forum topic");
                    }
                    else
                    {
                        Toast.makeText(CreateAccommodationActivity.this, "Couldn't create accommodation. Please try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(CreateAccommodationActivity.this, "Couldn't create accommodation. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openAccommodationsActivity(String sMessage)
    {
        Intent pIntent = new Intent(CreateAccommodationActivity.this, AccommodationsActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

    private void openAccommodationsActivity()
    {
        Intent pIntent = new Intent(CreateAccommodationActivity.this, AccommodationsActivity.class);
        startActivity(pIntent);
    }

}
