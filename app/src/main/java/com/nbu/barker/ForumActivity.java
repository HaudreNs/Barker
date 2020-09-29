package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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

public class ForumActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
       returnToHome();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {
                String sInformation = extras.getString("message");

                Toast.makeText(ForumActivity.this, sInformation, Toast.LENGTH_SHORT).show();
            }
        }

        ListView pView = findViewById(R.id.forumListView);
        int nStartSubject = 1, nEndSubject = 20;

        String sXML = "        <Barker requestType=\"getForumSubjects\">\n" +
                "            <getForumSubjects>\n" +
                "                <fromSubject>" + nStartSubject + "</fromSubject>\n" +
                "                <toSubject>" + nEndSubject + "</toSubject>\n" +
                "            </getForumSubjects>\n" +
                "        </Barker> \n";

        String sResponse = "";
        ArrayList<Subject> vpSubjects = new ArrayList<Subject>();

        try
        {
            sResponse = Tools.sendRequest(sXML);
            Log.println(Log.ERROR, "Response is: ", sResponse);

            if(sResponse == "Error" || sResponse.isEmpty())
            {
                returnToHome("There was an error opening forum topics. Please try again");
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
                pExp = pXpath.compile("count(Barker/subject)");
                nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                int nResult = (int) nTmp;

                String sName = "", sUserUsername = "";
                int nId;
                for(int i =1;i<= nResult;++i)
                {
                    pExp = pXpath.compile("//Barker/subject[" + i + "]/name");
                    sName = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    pExp = pXpath.compile("//Barker/subject[" + i + "]/customerUsername");
                    sUserUsername = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    pExp = pXpath.compile("//Barker/subject[" + i + "]/id");
                    nTmp = (double) pExp.evaluate( pDoc, XPathConstants.NUMBER );

                    nId = (int) nTmp;

                    Subject pTmpSubject = new Subject(nId, sName, sUserUsername);

                    vpSubjects.add(pTmpSubject);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        SubjectAdapter pAdapter = new SubjectAdapter(this, R.layout.forum_topic_inflate, vpSubjects);
        pView.setAdapter(pAdapter);

        Button bAddTopic = (Button) findViewById(R.id.bForumAddTopic);

        bAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateTopicActivity();
            }
        });


    }
    private void openForumSubjectActivity(int nId)
    {

        Intent pForumIntent = new Intent(this,ForumTopicActivity.class);
        pForumIntent.putExtra("id",nId);
        startActivity(pForumIntent);
    }

    private void openCreateTopicActivity()
    {
        Intent pIntent = new Intent(ForumActivity.this,CreateForumTopicActivity.class);
        startActivity(pIntent);
    }

    private void returnToHome(String sMessage)
    {
        Intent pIntent = new Intent(ForumActivity.this, HomeActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);
    }

    private void returnToHome()
    {
        Intent pIntent = new Intent(ForumActivity.this, HomeActivity.class);
        startActivity(pIntent);
    }
}
