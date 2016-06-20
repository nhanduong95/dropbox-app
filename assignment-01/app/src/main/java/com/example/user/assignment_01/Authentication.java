package com.example.user.assignment_01;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class Authentication extends Activity {
    private final String APP_KEY = "o40tyuthzkoj708";
    private final String APP_SECRET = "s084ijz3wwq1dqg";

    static final String ACCOUNT_PREFS_NAME = "prefs";
    private final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    Button btnAuth;
    
/*
* Initialize the application
* Create a new session
* Ask the user authorize the app
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide the title bar
        super.onCreate(savedInstanceState);

        // Check if the user has been authenticated
        if (DataStorage.mApi != null && DataStorage.mApi.getSession().isLinked()){
            //Move to the next Activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // Inflate the UI defined in authentication_screen.xml
        setContentView(R.layout.authentication_screen);

        // Create a new AuthSession so that we can use the DropBox API.
        AndroidAuthSession session = createSession();
        DataStorage.mApi = new DropboxAPI<AndroidAuthSession>(session);

        btnAuth = (Button) findViewById(R.id.auth_btn);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Starts the DropBox authentication process by launching an external app
                // (either the DropBox app if available or a web browser)
                // where the user will log in and allow your app access.
                DataStorage.mApi.getSession().startOAuth2Authentication(Authentication.this);
            }
        });

        btnAuth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    // When button is pressed, its UI changes
                    btnAuth.setTextColor(Color.parseColor("#F0F5F7"));
                    btnAuth.setBackgroundColor(Color.parseColor("#2F3541"));
                    btnAuth.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    // Finger was lifted, button's UI is back to normal
                    btnAuth.setTextColor(Color.parseColor("#2F3541"));
                    btnAuth.setBackgroundColor(Color.parseColor("#F0F5F7"));
                    btnAuth.setTypeface(Typeface.DEFAULT);
                }
                return false;
            }
        });
    }

/*
* Finish authentication after the user returns to your app
*/

    @Override
    protected void onResume(){
        super.onResume();

        AndroidAuthSession session = DataStorage.mApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication(); // Required to complete auth
                storeKeys(session); // Store the access tokens locally in the app for later use

                //Move to the next Activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } catch (IllegalStateException e) {
                // Send a ERROR log message and log the exception.
                // 1st arg: tag (String) || 2nd arg: message (String) || 3rd arg: exception (Throwable)
                Log.i("AuthLog", "Error authenticating", e);
                DataStorage.showNoti(this, "Could not link with Box Of Doom.");
            }
        }
    }

/*
 * Other functions that do not belong to the Activity cycle
 */

    private AndroidAuthSession createSession(){
        // Create a new session for authentication.
        // Check if the keys have been already existed.
        // If yes, only create a new session with the app key pair, no need for authenticating again
        // If no, create a new session and save the access token pair to avoid re-authentication
        String[] keys = loadKeys(); //Get the keys stored in the SharedPreference object
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        if (keys != null){
            if(keys[0].equals("oauth2:"))
                // Create a new session with the given app key pair and the stored OAuth 2 access secret.
                // The session will be linked to the account corresponding to the given OAuth 2 access token.
                session = new AndroidAuthSession(appKeyPair, keys[1]);
            else
                // Create a new session with the given app key pair and the stored OAuth 1 access token pair (key + secret).
                // The session will be linked to the account corresponding to the given OAuth 1 access token.
                session = new AndroidAuthSession(appKeyPair, new AccessTokenPair(keys[0],keys[1]));
        }
        else
            // Otherwise, create a new session to authenticate Android apps with the given app key pair only.
            session = new AndroidAuthSession(appKeyPair);
        return  session;
    }

/*
 * Functions for read and write(update) the values stored in SharedPreference object
 */
    private String[] loadKeys() {
        // Load the keys stored in a SharedPreference object and store in an array.
        // Return null if one of the keys is null or its length is 0
        String[] keys = new String[2];
        SharedPreferences sharedPref = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String appKey = sharedPref.getString(ACCESS_KEY_NAME, null);
        String appSecret = sharedPref.getString(ACCESS_SECRET_NAME, null);
        if (appKey == null || appSecret == null || appKey.length() == 0 || appSecret.length() == 0)
            keys = null;
        else{
            keys[0] = appKey;
            keys[1] = appSecret;
        }
        return keys;
    }

    private void storeKeys(AndroidAuthSession session){
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            //Store the access tokens in SharedPreferences by updating the values placed in it
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one. This is only necessary if you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            //Store the access token in SharedPreferences by updating the values placed in it
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }


}
