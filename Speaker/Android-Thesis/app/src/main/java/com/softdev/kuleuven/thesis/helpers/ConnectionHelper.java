package com.softdev.kuleuven.thesis.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.softdev.kuleuven.thesis.helpers.OnEventListeners.VolleyOnEventListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Singleton class that makes requests to a Google Cloud Function for processing in Dialogflow.
 * August Martens 2020
 */

public class ConnectionHelper {

    private static final String GATEWAY_URL = "https://europe-west1-thesis-c30c3.cloudfunctions.net/dialogflowGateway/";
    private static final String CHECK_REGISTRATION_URL = "https://europe-west1-thesis-c30c3.cloudfunctions.net/checkRegistration";
    private static final String AUDIO_GATEWAY_URL = "https://europe-west1-thesis-c30c3.cloudfunctions.net/dialogflowAudioGateway/";

    private static ConnectionHelper _instance;
    private RequestQueue queue;
    private VolleyOnEventListener callback;


    private ConnectionHelper() {
    }

    public static ConnectionHelper getInstance() {
        if (_instance == null) {
            _instance = new ConnectionHelper();
        }
        return _instance;
    }

    public void onStop(){
        _instance = null;
        queue = null;
        callback = null;
    }

    public void translateAudioMessage(Context context, String toSend, String id, VolleyOnEventListener mCallBack) {
        queue = Volley.newRequestQueue(context);
        callback =mCallBack;
        Map<String, String> postParam = new HashMap<>();
        postParam.put("id", id);
        postParam.put("text", toSend);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                AUDIO_GATEWAY_URL, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volly Error", error.toString());
                callback.onFailure(error);

            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


        };

        jsonObjReq.setTag(TAG);

        // Retry policy
        jsonObjReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    public void sendQueryToServer(Context context, String toSend, String id, VolleyOnEventListener mCallBack) {
        queue = Volley.newRequestQueue(context);
        callback =mCallBack;
        Map<String, String> postParam = new HashMap<>();
        postParam.put("id", id);
        postParam.put("text", toSend);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GATEWAY_URL, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volly Error", error.toString());
                callback.onFailure(error);

            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


        };

        jsonObjReq.setTag(TAG);

        // Retry policy
        jsonObjReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }


    public void checkRegistration(Context context, String toSend, VolleyOnEventListener mCallBack){
        queue = Volley.newRequestQueue(context);
        callback =mCallBack;
        Map<String, String> postParam = new HashMap<>();
        postParam.put("speakerId", toSend);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                CHECK_REGISTRATION_URL, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volly Error", error.toString());
                callback.onFailure(error);

            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


        };

        jsonObjReq.setTag(TAG);

        // Retry policy
        jsonObjReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

}
