package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class CreateWalkActivity extends AppCompatActivity {

    FusedLocationProviderClient pLocClient = null;
    EditText etMessage;
    TextView tvLocation;
    Button bCreate;

    LocationRequest pLocRequest = null;

    @Override
    public void onBackPressed() {
        returnToWalks();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_walk);
        etMessage = (EditText) findViewById(R.id.etCreateWalkMessage);
        tvLocation = (TextView) findViewById(R.id.tvCreateWalkLocationChangable);
        bCreate = (Button) findViewById(R.id.bCreateWalkCreate);

        pLocRequest = new LocationRequest();

        pLocRequest.setInterval(30000);
        pLocRequest.setFastestInterval(5000);
        pLocRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        String sLocation = "";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {
                returnToHomeScreen("Your android version is not high enough to use this function");
            }
        }

        LocationCallback mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        String sResult = Double.toString(location.getLatitude());
                        Log.println(Log.ERROR,"location", sResult);
                        Geocoder pGeocoder = new Geocoder(CreateWalkActivity.this);
                        try {
                            List<Address> pAddresses = pGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            tvLocation.setText(pAddresses.get(0).getAddressLine(0));
                        } catch (Exception e) {
                            Log.println(Log.ERROR,"GEOCODER LOCATION", "ERROR RECEIVING LOCATION:" + e.getMessage());
                        }
                    }
                }
            }


        };

        LocationServices.getFusedLocationProviderClient(CreateWalkActivity.this).requestLocationUpdates(pLocRequest, mLocationCallback, null);

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sMessage = etMessage.getText().toString();
                String sLocation = tvLocation.getText().toString();

                if (sMessage.isEmpty()) {
                    Toast.makeText(CreateWalkActivity.this, "Please write a message", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(sLocation.isEmpty() || sLocation.equals("Location"))
                {
                    Toast.makeText(CreateWalkActivity.this, "There was a problem finding your locaton", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sRequest = "<Barker requestType=\"createWalk\">\n" +
                        "    <createWalk>\n" +
                        "        <email>" + SessionParameters.getApplicationUser().getEmail() + "</email>\n" +
                        "        <location>" + sLocation + "</location>\n" +
                        "        <message>" + sMessage + "</message>\n" +
                        "    </createWalk>\n" +
                        "</Barker>";

                String sResponse = "";
                try {
                    sResponse = Tools.sendRequest(sRequest);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        Toast.makeText(CreateWalkActivity.this, "There was an error creating walk.Please try again", Toast.LENGTH_LONG).show();
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

                        returnToWalks("Successfully created walk");
                    }
                    else
                    {
                        Toast.makeText(CreateWalkActivity.this, "Couldn't create walk. Please try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(CreateWalkActivity.this, "Couldn't create walk. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void returnToHomeScreen(String sMessage)
    {
        Intent pIntent = new Intent(CreateWalkActivity.this,HomeActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

    private void returnToWalks()
    {
        Intent pIntent = new Intent(CreateWalkActivity.this,FindWalkActivity.class);
        startActivity(pIntent);
    }

    private void returnToWalks(String sMessage)
    {
        Intent pIntent = new Intent(CreateWalkActivity.this,FindWalkActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

}