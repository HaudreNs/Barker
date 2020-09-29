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

public class CreateForumTopicActivity extends AppCompatActivity {


    Button bAddTopic;

    @Override
    public void onBackPressed() {
    openForumTopics();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_forum_topic);

        bAddTopic = (Button) findViewById(R.id.bCreateTopic);

        bAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAddTopic.setClickable(false);
                EditText etName = (EditText) findViewById(R.id.etCreateTopicName);
                EditText etDescription = (EditText) findViewById(R.id.etCreateTopicDescription);

                String sName = etName.getText().toString();
                String sDescription = etDescription.getText().toString();

                if(sName.isEmpty() || sDescription.isEmpty())
                {
                    bAddTopic.setClickable(true);
                    Toast.makeText(CreateForumTopicActivity.this, "Cannot create subject with empty name or description", Toast.LENGTH_LONG).show();
                    return;
                }

                if(sName.length() < 3)
                {
                    bAddTopic.setClickable(true);
                    Toast.makeText(CreateForumTopicActivity.this, "Topic name should be at least 2 characters long", Toast.LENGTH_LONG).show();
                    return;
                }
                if(sDescription.length() < 6)
                {
                    bAddTopic.setClickable(true);
                    Toast.makeText(CreateForumTopicActivity.this, "Topic description should be at least 5 characters long", Toast.LENGTH_LONG).show();
                    return;
                }

                String sXML = "        <Barker requestType=\"createForumSubject\">\n" +
                        "            <createForumSubject>\n" +
                        "                <userEmail>" + SessionParameters.getApplicationUser().getEmail() + "</userEmail>\n" +
                        "                <subject>" + sName + "</subject>\n" +
                        "                <text>" + sDescription + "</text>\n" +
                        "            </createForumSubject>\n" +
                        "        </Barker>";

                String sResponse = "";
                try {
                    sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        bAddTopic.setClickable(true);
                        Toast.makeText(CreateForumTopicActivity.this, "There was an error creating topic.Please try again", Toast.LENGTH_LONG).show();
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
                        bAddTopic.setClickable(true);
                        openForumTopics("Successfully created forum topic");
                    }
                    else
                    {
                        bAddTopic.setClickable(true);
                        openForumTopics("Couldn't create topic. Please try again");
                        return;
                    }
                }
                catch (Exception e)
                {
                    bAddTopic.setClickable(true);
                    Log.println(Log.ERROR,"CreateForumTopic Error:", e.getMessage());
                    openForumTopics("Couldn't create topic. Please try again");
                }
            }
        });
    }

    private void openForumTopics(String sMessage)
    {
        Intent pIntent = new Intent(CreateForumTopicActivity.this, ForumActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

    private void openForumTopics()
    {
        Intent pIntent = new Intent(CreateForumTopicActivity.this, ForumActivity.class);
        startActivity(pIntent);
    }

}
