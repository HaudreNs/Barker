package com.nbu.barker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Vector;

public class HomeActivity extends AppCompatActivity {

    ImageView ivProfile = null;
    ImageView ivForum = null;
    ImageView ivWalks = null;
    ImageView ivFriendList = null;
    ImageView ivAccommodations = null;

    @Override
    public void onBackPressed() {
        openRegisterActivity("You have been logged out");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ivProfile = findViewById(R.id.ivHomeProfile);
        ivForum = findViewById(R.id.ivHomeForum);
        ivFriendList = findViewById(R.id.ivHomeFriends);
        ivAccommodations = findViewById(R.id.ivHomeAccommodations);
        ivWalks = findViewById(R.id.ivHomeWalks);

        ivProfile.setClickable(true);
        ivForum.setClickable(true);
        ivFriendList.setClickable(true);
        ivAccommodations.setClickable(true);
        ivWalks.setClickable(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {
                String sInformation = extras.getString("message");

                Toast.makeText(HomeActivity.this, sInformation, Toast.LENGTH_LONG).show();
            }
        }

        ivForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivForum.setClickable(false);
                openForumActivity();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivProfile.setClickable(false);
                openProfileActivity();
            }
            });

        ivFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFriendList.setClickable(false);
                openFriendActivity();
            }
        });

        ivAccommodations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccommodations.setClickable(false);
                openAccommodationActivity();
            }
        });

        ivWalks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivWalks.setClickable(false);
                openWalksActivity();
            }
        });
    }

    private void openProfileActivity() {
        Intent pProfileIntent = new Intent(this,ProfileActivity.class);
        startActivity(pProfileIntent);

    }

    private void openForumActivity()
    {
        Intent pForumIntent = new Intent(this,ForumActivity.class);
        startActivity(pForumIntent);
    }

    private void openFriendActivity()
    {
        Intent pIntent = new Intent(HomeActivity.this, FriendListActivity.class);
        startActivity(pIntent);
    }

    private void openAccommodationActivity()
    {
        Intent pIntent = new Intent(HomeActivity.this, AccommodationsActivity.class);
        startActivity(pIntent);
    }

    private void openWalksActivity()
    {
        Intent pIntent = new Intent(HomeActivity.this, FindWalkActivity.class);
        startActivity(pIntent);
    }

    private void openRegisterActivity(String sMessage)
    {
        Intent pIntent = new Intent(HomeActivity.this, MainActivity.class);
        pIntent.putExtra("message", sMessage);
        startActivity(pIntent);

    }



}
