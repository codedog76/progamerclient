package singletons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.User;

public class NetworkManagerSingleton {
    private static NetworkManagerSingleton sInstance;
    private static DatabaseHandlerSingleton sDatabaseHandlerSingleton;
    private static SettingsSingleton sSettingsSingleton;
    private static RequestQueue sRequestQueue;
    private Context mContext;
    private String mClassName = getClass().toString();

    private static final int TIME_OUT_INTERVAL = 5000;
    private static final String LOGIN_USER_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/login";
    private static final String REGISTER_USER_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/register";
    private static final String LEADERBOARD_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/leaderboard";
    private static final String GET_USER_URL_STRING = "http://";
    private static final String UPLOAD_USER_URL_STRING = "http://";
    private static final String GET_LEVEL_URL_STRING = "http://";

    private NetworkManagerSingleton(Context context) {
        mContext = context;
        sRequestQueue = getRequestQueue();
        sDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(mContext);
        sSettingsSingleton = SettingsSingleton.getInstance(mContext);
    }

    public boolean checkForWifi() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static synchronized NetworkManagerSingleton getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkManagerSingleton(context);
        }
        return sInstance;
    }

    public boolean canSyncData() {
        return !sSettingsSingleton.getSyncWifiOnly() || checkForWifi();
    }

    public RequestQueue getRequestQueue() {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return sRequestQueue;
    }

    public synchronized void loginJSONRequest(User user, final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", user.getUser_student_number());
            jsonParams.put("user_password", user.getUser_password());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_USER_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    User user = new User();
                                    user.setUser_student_number(response.getString("user_student_number"));
                                    user.setUser_nickname(response.getString("user_nickname"));
                                    user.setUser_avatar(response.getInt("user_avatar"));
                                    user.setUser_is_private(response.getInt("user_is_private"));
                                    if (sDatabaseHandlerSingleton.insertOrUpdateUser(user)) {
                                        if (sDatabaseHandlerSingleton.loginUser(response.getString("user_student_number"))) {
                                            booleanResponseListener.getResult(true, response.getString("response_message"));
                                            return;
                                        }
                                    }
                                    booleanResponseListener.getResult(false, "Failed modifying user in SQLite database");
                                } else {
                                    Log.e(mClassName, "loginJSONRequest Error: " + "Invalid response");
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "loginJSONRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "loginJSONRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "loginJSONRequest Error: Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    public synchronized void registerJSONRequest(User user, final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", user.getUser_student_number());
            jsonParams.put("user_nickname", user.getUser_nickname());
            jsonParams.put("user_password", user.getUser_password());
            jsonParams.put("user_avatar", user.getUser_avatar());
            jsonParams.put("user_is_private", user.getUser_is_private());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, REGISTER_USER_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e(mClassName, "registerJSONRequest: " + response.getString("response_message"));
                                booleanResponseListener.getResult(validResponse(response), response.getString("response_message"));
                            } catch (JSONException ex) {
                                Log.e(mClassName, "registerJSONRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "registerJSONRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "registerJSONRequest Error: Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    public synchronized void downloadLeaderboardJSONRequest(final ObjectResponseListener<ArrayList<User>> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", sDatabaseHandlerSingleton.getLoggedUser().getUser_student_number());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, LEADERBOARD_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    ArrayList<User> leaderboardList = new ArrayList<>();
                                    JSONArray jsonArray = response.getJSONArray("user_list");
                                    for (int x = 0; x < jsonArray.length(); x++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                                        User user = new User();
                                        user.setUser_student_number(jsonObject.getString("user_student_number"));
                                        user.setUser_nickname(jsonObject.getString("user_nickname"));
                                        user.setUser_avatar(jsonObject.getInt("user_avatar"));
                                        user.setUser_overall_score(jsonObject.getInt("user_overall_score"));
                                        user.setUser_overall_attempts(jsonObject.getInt("user_overall_attempts"));
                                        user.setUser_overall_time(jsonObject.getInt("user_overall_time"));
                                        leaderboardList.add(user);
                                    }
                                    Log.e(mClassName, "downloadLeaderboardJSONRequest: " + response.getString("response_message"));
                                    objectResponseListener.getResult(leaderboardList, true, response.getString("response_message"));
                                } else {
                                    Log.e(mClassName, "downloadLeaderboardJSONRequest Error: Invalid response");
                                    objectResponseListener.getResult(null, false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "downloadLeaderboardJSONRequest Error: " + ex.getMessage());
                                objectResponseListener.getResult(null, false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "downloadLeaderboardJSONRequest Error: " + error);
                                objectResponseListener.getResult(null, false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "downloadLeaderboardJSONRequest Error: Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    public synchronized void downloadUserJSONRequest(String user_student_number, final ObjectResponseListener<User> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", user_student_number);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, GET_USER_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                User user = new User();
                                user.setUser_student_number(response.getString("user_student_number"));
                                user.setUser_nickname(response.getString("user_nickname"));
                                objectResponseListener.getResult(user, true, "");
                            } catch (JSONException ex) {
                                Log.e(mClassName + ": ", "Error: " + ex.getMessage());
                                objectResponseListener.getResult(null, false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName + ": ", error);
                                objectResponseListener.getResult(null, false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName + ": ", "Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    public synchronized void uploadUserJSONRequest(final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", sDatabaseHandlerSingleton.getLoggedUser().getUser_student_number());
            jsonParams.put("user_nickname", sDatabaseHandlerSingleton.getLoggedUser().getUser_nickname());
            jsonParams.put("user_nickname", sDatabaseHandlerSingleton.getLoggedUser().getUser_nickname());
            jsonParams.put("user_avatar", sDatabaseHandlerSingleton.getLoggedUser().getUser_avatar());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_USER_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("response")) {
                                    uploadUserLevelsJSONRequest(booleanResponseListener);
                                } else {
                                    booleanResponseListener.getResult(false, "");
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName + ": ", "Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName + ": ", error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName + ": ", "Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    private synchronized void uploadUserLevelsJSONRequest(final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", sDatabaseHandlerSingleton.getLoggedUser().getUser_student_number());
            jsonParams.put("user_nickname", sDatabaseHandlerSingleton.getLoggedUser().getUser_nickname());
            jsonParams.put("user_nickname", sDatabaseHandlerSingleton.getLoggedUser().getUser_nickname());
            jsonParams.put("user_avatar", sDatabaseHandlerSingleton.getLoggedUser().getUser_avatar());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_USER_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("response")) {
                                    uploadUserLevelsJSONRequest(booleanResponseListener);
                                } else {
                                    booleanResponseListener.getResult(false, "");
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName + ": ", "Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName + ": ", error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName + ": ", "Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    private String getVolleyError(VolleyError ex) {
        if (ex instanceof TimeoutError || ex instanceof NoConnectionError) {
            return "No internet access or server is down.";
        } else if (ex instanceof AuthFailureError) {
            return "Authentication error";
        } else if (ex instanceof ServerError) {
            return "Server error";
        } else if (ex instanceof NetworkError) {
            return "Network error";
        } else if (ex instanceof ParseError) {
            return "Parse error";
        }
        if (ex.getMessage() != null)
            return ex.getMessage();
        return null;
    }

    private boolean validResponse(JSONObject response) throws JSONException {
        return !response.isNull("response_valid") && response.has("response_valid") && response.getBoolean("response_valid");
    }

    public interface BooleanResponseListener {
        void getResult(Boolean response, String message);
    }

    public interface ObjectResponseListener<T> {
        void getResult(T object, Boolean response, String message);
    }
}
