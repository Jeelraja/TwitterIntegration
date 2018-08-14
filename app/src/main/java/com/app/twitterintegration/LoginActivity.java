package com.app.twitterintegration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class LoginActivity extends AppCompatActivity {

    //Twitter
    private Twitter mTwitter;
    private RequestToken mRequestToken = null;
    private AccessToken mAccessToken;
    private String mStrAuthUrl, mStrAuthVerifier, mStrProfileUrl;
    private Dialog mAuthDialog;
    private WebView mWebview;
    private ProgressDialog mProgressDialog;
    private ImageView mImgLoginTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Twitter
        mImgLoginTwitter = (ImageView) findViewById(R.id.img_login_twitter);
        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(getResources().getString(R.string.CONSUMER_KEY), getResources().getString(R.string.CONSUMER_SECRET));

        mImgLoginTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TokenGet().execute();
            }
        });
    }

    /*Twitter Integration*/

    private class TokenGet extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                mRequestToken = mTwitter.getOAuthRequestToken();
                mStrAuthUrl = mRequestToken.getAuthorizationURL();
            } catch (twitter4j.TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return mStrAuthUrl;
        }

        @Override
        protected void onPostExecute(String oauth_url) {
            if (oauth_url != null) {
                Log.e("URL", oauth_url);
                mAuthDialog = new Dialog(LoginActivity.this);
                mAuthDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mAuthDialog.setContentView(R.layout.twitter_auth_dialog);
                mWebview = (WebView) mAuthDialog.findViewById(R.id.webv);
                mWebview.getSettings().setJavaScriptEnabled(true);
                mWebview.loadUrl(oauth_url);
                mWebview.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false) {
                            authComplete = true;
                            Log.e("Url", url);
                            Uri uri = Uri.parse(url);
                            mStrAuthVerifier = uri.getQueryParameter("oauth_verifier");
                            mAuthDialog.dismiss();
                            new AccessTokenGet().execute();
                        } else if (url.contains("denied")) {
                            mAuthDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Sorry !, Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mAuthDialog.show();
                mAuthDialog.setCancelable(true);


            } else {

                Toast.makeText(LoginActivity.this, "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();


            }
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage("Fetching Data ...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                mAccessToken = mTwitter.getOAuthAccessToken(mRequestToken, mStrAuthVerifier);
                AppSharedPref.getInstance(LoginActivity.this).setDataString(Constants.TWITTER_ACCESS_TOCKEN, mAccessToken.getToken());
                AppSharedPref.getInstance(LoginActivity.this).setDataString(Constants.TWITTER_ACCESS_TOCKEN_SECRET, mAccessToken.getTokenSecret());
                AppSharedPref.getInstance(LoginActivity.this).setSocialMediaLogedIn(2);
                User user = mTwitter.showUser(mAccessToken.getUserId());
                mStrProfileUrl = user.getOriginalProfileImageURL();
                AppSharedPref.getInstance(LoginActivity.this).setDataString(Constants.TWITTER_PROFILE_NAME, user.getName());
                AppSharedPref.getInstance(LoginActivity.this).setDataString(Constants.TWITTER_PROFILE_IMAGE, user.getOriginalProfileImageURL());

            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            if (response) {
                mProgressDialog.hide();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }


    }
}
