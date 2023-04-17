package com.example.aichatbot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    ImageButton imageButton;
    TextView textView;
    MessagesList messagesList;
    User us,Chatgpt;
    MessagesListAdapter<Message> adapter;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=findViewById(R.id.editTextTextPersonName);
        imageButton=findViewById(R.id.imageButton);

        messagesList=findViewById(R.id.messagesList);


         ImageLoader imageLoader= new ImageLoader() {
             @Override
             public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {

                 Picasso.get().load(url).into(imageView);

             }
         };



        adapter = new MessagesListAdapter<Message>("1",imageLoader);
        messagesList.setAdapter(adapter);

        us=new User("1","Divleen","");
        Chatgpt=new User("2","Chatgpt","");




        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Message message=new Message("m1",editText.getText().toString(),us, Calendar.getInstance().getTime(),null);
                adapter.addToStart(message,true);
                if(editText.getText().toString().toLowerCase().startsWith("generate image"))
                {
                    generateImage(editText.getText().toString());
                }
                else {
                    performAction(editText.getText().toString());
                }
                editText.setText("");
            }
        });



    }

    private void performAction(String input) {


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openai.com/v1/completions";

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("prompt",input);

        jsonObject.put("model", "text-davinci-003");
        jsonObject.put("max_tokens",250);
        jsonObject.put("temperature",0);




    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {

                        String ans=null;
                        try {

                            ans=response.getJSONArray("choices").getJSONObject(0).getString("text");

                           Message message=new Message("m2",ans.trim(),Chatgpt,Calendar.getInstance().getTime(),null);
                           adapter.addToStart(message,true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
            }
        }){
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {

            HashMap<String,String> map=new HashMap<>();
            map.put("Content-Type", "application/json");
            map.put("Authorization","Bearer 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");
            return map;
        }
    };

    jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
        @Override
        public int getCurrentTimeout() {
            return 60000;
        }

        @Override
        public int getCurrentRetryCount() {
            return 15;
        }

        @Override
        public void retry(VolleyError error) throws VolleyError {

        }
    });

        queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }




    private void generateImage(String input) {


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openai.com/v1/images/generations";

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("prompt",input);

            jsonObject.put("n", 2);
            jsonObject.put("size","1024x1024");





            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {

                            String ans=null;
                            try {

                                JSONArray imagesList=response.getJSONArray("data");

                                for(int i=0;i<imagesList.length();i++) {
                                    ans = imagesList.getJSONObject(i).getString("url");
                                    Message message = new Message("m2", "image", Chatgpt, Calendar.getInstance().getTime(), ans.trim());
                                    adapter.addToStart(message, true);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //textView.setText(ans);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                   // textView.setText("That didn't work!");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String,String> map=new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("Authorization","Bearer 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");
                    return map;
                }
            };

            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 15;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}