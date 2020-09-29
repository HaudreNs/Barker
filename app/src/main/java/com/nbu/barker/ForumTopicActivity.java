package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ForumTopicActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
    returnToForum();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_topic);

        //to use for information sent from register and forgottenPassword activities
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int nTopicId = 0;
        if (extras != null) {
            if (extras.containsKey("id")) {
                nTopicId = extras.getInt("id");

            }
        }

        if(nTopicId == 0)
        {
            returnToForum("There was an error opening the subject");
        }

        String sXML = "        <Barker requestType=\"viewForumSubject\">\n" +
                "            <viewForumSubject>\n" +
                "                <id>" + nTopicId + "</id>\n" +
                "            </viewForumSubject>\n" +
                "        </Barker> \n";

        String sResponse = "";
        try
        {
            sResponse = Tools.sendRequest(sXML);
            Log.println(Log.ERROR, "Response is: ", sResponse);

            if(sResponse == "Error" || sResponse.isEmpty())
            {
                Toast.makeText(ForumTopicActivity.this, "There was an error changing your user information.Please try again", Toast.LENGTH_LONG).show();
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
            int nStatusCode = (int) nTmp;

            if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.SUCCESS))
            {
                pExp = pXpath.compile("Barker/subject/name");
                String sName = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                pExp = pXpath.compile("Barker/subject/description");
                String sDescription = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                pExp = pXpath.compile("Barker/subject/user");
                String sUserUsername = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                TextView tvName = findViewById(R.id.tvForumTopicTitle);
                TextView tvDescription = findViewById(R.id.tvForumTopicDescription);
                TextView tvUser = findViewById(R.id.tvForumTopicUser);

                tvName.setText(sName);
                tvDescription.setText(sDescription);
                tvUser.setText(sUserUsername);

                pExp = pXpath.compile("count(Barker/subject/comment)");
                nTmp = (double) pExp.evaluate(pDoc, XPathConstants.NUMBER);

                int nResult = (int) nTmp;

                ArrayList<Comment> vpComments = new ArrayList<Comment>();


                String sText = "" , sCommentUserUsername = "";
                int nCommentId = 0;
                for(int i =1;i<=nResult;++i)
                {
                    pExp = pXpath.compile("//Barker/subject/comment[" + i + "]/@id");
                    nTmp = (double) pExp.evaluate( pDoc, XPathConstants.NUMBER );
                    nCommentId = (int) nTmp;

                    pExp = pXpath.compile("//Barker/subject/comment[" + i + "]/text");
                    sText = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    pExp = pXpath.compile("//Barker/subject/comment[" + i + "]/user");
                    sCommentUserUsername = (String)pExp.evaluate( pDoc, XPathConstants.STRING );

                    Comment pComment = new Comment(nCommentId, sText, sCommentUserUsername);
                    vpComments.add(pComment);


                }

                ListView pView = findViewById(R.id.forumTopicListView);


                CommentAdapter pAdapter = new CommentAdapter(this, R.layout.comment_inflate, vpComments);
                pView.setAdapter(pAdapter);


            }
            else if(nStatusCode == Constants.requestStatusToCode(Constants.RequestServerStatus.MISSING_SUBJECT))
            {
                returnToForum("This subject no longer exists");
            }
            else
            {
                Toast.makeText(this, "Could not open subject please try again", Toast.LENGTH_SHORT).show();
                returnToForum("Could not open subject please try again");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Button bAddComment = (Button) findViewById(R.id.bforumTopicAddComment);
        final int nSubjectId = nTopicId;
        bAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etComment = (EditText) findViewById(R.id.etForumTopicAddComment);

                String sComment = etComment.getText().toString();

                String sXML = "        <Barker requestType=\"createSubjectComment\">\n" +
                        "            <createSubjectComment>\n" +
                        "                <userEmail>" + SessionParameters.getApplicationUser().getEmail() + "</userEmail>\n" +
                        "                <subjectId>" + nSubjectId + "</subjectId>\n" +
                        "                <text>" + sComment + "</text>\n" +
                        "            </createSubjectComment>" +
                        "   </Barker>";

                try {
                    String sResponse = Tools.sendRequest(sXML);
                    Log.println(Log.ERROR, "Response is: ", sResponse);

                    if (sResponse == "Error" || sResponse.isEmpty()) {
                        Toast.makeText(ForumTopicActivity.this, "There was an error changing your user information.Please try again", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(ForumTopicActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(ForumTopicActivity.this, "There was a problem adding your comment.Please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        });
    }

    private void returnToForum(String sMessage)
    {
        Intent pForumIntent = new Intent(ForumTopicActivity.this, ForumActivity.class);
        pForumIntent.putExtra("message" , sMessage);
        startActivity(pForumIntent);
    }

    private void returnToForum()
    {
        Intent pForumIntent = new Intent(ForumTopicActivity.this, ForumActivity.class);
        startActivity(pForumIntent);
    }

}
