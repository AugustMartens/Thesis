package com.softdev.kuleuven.thesis.helpers.OnEventListeners;

import org.json.JSONObject;

public interface VolleyOnEventListener {
    void onSuccess(JSONObject response);
    void onFailure(Exception e);
}
