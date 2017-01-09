package com.example.rosem.getfacebookinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

//mk test2
//master test
//yunsuntest

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    //AccessToken accessToken;
    GraphRequest request;//get info
    JSONObject result;//loaded data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //accessToken = AccessToken.getCurrentAccessToken();
       // AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        LoginButton facebookLoginButton = (LoginButton)findViewById(R.id.facebook_login_button);
        final Button showButton = (Button)findViewById(R.id.show_button);
        //facebookLoginButton.setReadPermissions("email");
       // facebookLoginButton.setReadPermissions(Arrays.asList("id","email","age_range","birthday"));

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(),"login success",Toast.LENGTH_LONG).show();
//reuest for graph
                request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                result=object;
                                Thread savingThread = new SaveData();
                                savingThread.start();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,birthday,age_range");
                request.setParameters(parameters);
                request.executeAsync();
                showButton.setText("Show");


            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"login cancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"login error",Toast.LENGTH_LONG).show();
            }


        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    class SaveData extends Thread
    {
        public void run() {

         //   TextView text= (TextView)findViewById(R.id.text_result);
            StringBuilder showText = new StringBuilder();
            String str;
            Long id;//save for facebook id
            JSONObject age_range;
            int age=0;//save age
            //JSON Parsing
            try {
                Log.v("ENTERING THREAD","try/catch");

                if(result.has("name"))
                {
                    showText.append("name : "+result.getString("name")+"\n");
                }
                else
                {
                    showText.append("no info about name\n");
                }

                if(result.has("birthday"))
                {
                    showText.append("birthday : "+result.getString("birthday")+"\n");
                }
                else
                {
                    showText.append("no info about birthday\n");
                }

                if(result.has("id"))
                {
                    id = result.getLong("id");
                    showText.append("id : "+id.toString()+"\n");
                }
                else
                {
                    showText.append("no info about id\n");
                }

                if(result.has("age_range"))
                {
                    age_range = result.getJSONObject("age_range");
                    if(age_range.has("min"))
                    {
                        age = age_range.getInt("min");
                        showText.append("age : "+Integer.toString(age)+"\n");
                    }
                    else if(age_range.has("max"))
                    {
                        age=age_range.getInt("max");
                        showText.append("age : "+Integer.toString(age)+"\n");
                    }
                    else
                    {
                        showText.append("no info about age\n");
                    }
                }
                else
                {
                    showText.append("no info about age\n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("THREAD END",showText.toString());
           // text.setText(showText);
        }
    }
}
