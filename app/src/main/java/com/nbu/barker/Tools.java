package com.nbu.barker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tools {


    private static String m_sResponse = "";

    public static boolean validateEmail(String sEmail)
    {
        //match letter/number or _ at least 2 times followed by @ then a-z at least 2 times then .
        // at least 1 time and finish it with a-z at least 2 times(. at least once because of domains like co.uk)
        //example email knnikolov@mail.co.uk

        String sRegex = "^(.+)@(.+)$";
        Pattern pEmailPattern = Pattern.compile(sRegex);
        Matcher pEmailMatcher = pEmailPattern.matcher(sEmail);
        //true if complete match false otherwise
        return pEmailMatcher.matches();
    }

    public static boolean validatePassword(String sPassword)
    {
        //matches all alphabetic characters, numbers and _ between 4 and 15 times
        String sRegex = "([A-Za-z1-9_]){4,15}";
        Pattern pPasswordPattern = Pattern.compile(sRegex);
        Matcher pPasswordMatcher = pPasswordPattern.matcher(sPassword);
        //true if complete match false otherwise
        return pPasswordMatcher.matches();
    }

    public static boolean validateNames(String sNames)
    {
        //matches all alphabetic characters, then space then alphabetical characters and spaces
        String sRegex = "([A-Za-z]){2,}(\\s)([A-Za-z\\s]{2,})";
        Pattern pNamesPattern = Pattern.compile(sRegex);
        Matcher pNamesMatcher = pNamesPattern.matcher(sNames);
        //true if complete match false otherwise
        return pNamesMatcher.matches();
    }

    public static String hashPassword(String sPassword, String sAlgorithm)
    {
        try {
            MessageDigest md = MessageDigest.getInstance( "SHA-256" );
            // Change this to UTF-16 if needed
            md.update( sPassword.getBytes( StandardCharsets.UTF_8 ) );
            byte[] digest = md.digest();
            String hex = String.format( "%064x", new BigInteger( 1, digest ) );

            return hex;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static boolean bIsFinished = false;

    public static String sendRequest(final String sXML) {
        String sResponse = "";

        ExecutorService pExecutor = Executors.newSingleThreadExecutor();

        Future<String> pFuture = pExecutor.submit(new Callable() {
            @Override
            public String call() throws Exception {
                String sResponse = "";

                bIsFinished = false;


                try {
                    Socket socket = new Socket(InetAddress.getByName(Config.serverIp), Config.serverPort);
                    String path = "";

                    // Send headers
                    BufferedWriter wr =
                            new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
                    wr.write("POST " + path + " HTTP/1.0\r\n");
                    wr.write("Content-Length: " + sXML.length() + "\r\n");
                    wr.write("Content-Type: application/x-www-form-urlencodedrn");
                    wr.write("\r\n");

                    // Send parameters
                    wr.write(sXML);
                    wr.write("\r\n");
                    wr.flush();


                    // Get response
                    BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sResponse += line ;
                    }

                    wr.close();
                    rd.close();

                } catch (Exception e) {
                    Log.println(Log.ERROR, "REQUEST", "RECEIVED XML " + sResponse);
                    Log.println(Log.ERROR,"REQUEST", "ERROR " + e.getMessage());
                    return "Error";
                }

                String sXML = "";
                try
                {
                    if(!sResponse.contains("<Barker") || !sResponse.contains("</Barker>"))
                    {
                        Log.println(Log.ERROR, "Request Error on parsing string", sResponse + " \n STRING DOES NOT CONTAIN FIELDS REQUIRED");
                        return "Error";
                    }
                    sXML = sResponse.substring(sResponse.indexOf("<Barker"),
                            sResponse.indexOf("</Barker>") + "</Barker>".length());
                }
                catch(Exception e)
                {
                Log.println(Log.ERROR, "Request Error on parsing string", e.getMessage());
                return "Error";
                }


                Log.println(Log.ERROR,"PARSE", "PARSE HAS FINISHED SUCCESSFULLY");
                bIsFinished = true;

                return sXML;
            }
        });



        try
        {
//            while(!bIsFinished)
//            {
//                Thread.sleep(500);
//            }
            sResponse = pFuture.get();

            pExecutor.shutdown();

            try {
                pExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Log.println(Log.ERROR,"REQUEST", "ERROR FROM AWAIT TERMINATION " + e.getMessage());
            }

        }
        catch(Exception e)
        {
            Log.println(Log.ERROR, "REQUEST", "RECEIVED XML FROM FUTURE IS " + sResponse);
            Log.println(Log.ERROR,"REQUEST", "ERROR FROM FUTURE " + e.getMessage());
            return "Error";
        }

        return sResponse;
    }



    public static String validateProfileParameters(String sEmail ,String sUsername, String sNames, String sPassword)
    {
        String sErrors = "";
        sErrors = validateProfileParameters(sEmail, sUsername,sNames);

        if(sPassword.isEmpty())
        {
            sErrors += "Required field password \n";
        }
        else
        {

            if(!Tools.validatePassword(sPassword)) sErrors += "Wrong password format. Password must be between 4 and 15 symbols and contain letters, numbers or underscores \n";

        }


        return sErrors;
    }

    public static String validateProfileParameters(String sEmail, String sUsername,String sNames)
    {
        String sErrors = "";

        if(sEmail.isEmpty())
        {
            sErrors = "Required field email \n";
        }
        else
        {
            if(!Tools.validateEmail(sEmail)) sErrors = "Wrong email format \n";

        }

        if(sNames.isEmpty())
        {
            sErrors+= "Required field Names \n";
        }
        else
        {
            if(!Tools.validateNames(sNames)) sErrors += "Wrong field names";
        }



        return sErrors;
    }



}
