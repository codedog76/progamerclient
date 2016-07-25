package singletons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

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

import models.Level;
import models.Puzzle;
import models.User;

public class NetworkManagerSingleton {
    private static NetworkManagerSingleton sInstance;
    private static DatabaseHandlerSingleton sDatabaseHandlerSingleton;
    private static SettingsSingleton sSettingsSingleton;
    private static RequestQueue sRequestQueue;
    private Context mContext;
    private String mClassName = getClass().toString();

    private static final int TIME_OUT_INTERVAL = 5000;
    private static final String LOGIN_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/login";
    private static final String REGISTER_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/register";
    private static final String LEADERBOARD_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/leaderboard";
    private static final String LEVELS_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/levels";
    private static final String USER_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/user";
    private static final String UPDATE_USER_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/updateuser";
    private static final String UPDATE_USERLEVEL_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/updateuserlevel";
    private static final String PUZZLES_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/puzzles";

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

    public void cancelJSONRequest(String tag) {
        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(tag);
        }
    }

    public synchronized void loginJSONRequest(User user, final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", user.getUser_student_number_id());
            jsonParams.put("user_password", user.getUser_password());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    User user = new User();
                                    user.setUser_student_number_id(response.getString("user_student_number"));
                                    user.setUser_nickname(response.getString("user_nickname"));
                                    user.setUser_avatar(response.getInt("user_avatar"));
                                    user.setUser_is_private(response.getInt("user_is_private"));
                                    if (sDatabaseHandlerSingleton.insertUser(user) != -1) {
                                        downloadLevelsJSONRequest(user, booleanResponseListener);
                                    } else {
                                        Log.e(mClassName, "Failed to add user to local database");
                                        booleanResponseListener.getResult(false, "Failed to add user to local database");
                                    }
                                } else {
                                    Log.e(mClassName, "loginJSONRequest Error: Invalid response");
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
            objectRequest.setTag("loginJSONRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "loginJSONRequest Error: Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    public synchronized void registerJSONRequest(User user, final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", user.getUser_student_number_id());
            jsonParams.put("user_nickname", user.getUser_nickname());
            jsonParams.put("user_password", user.getUser_password());
            jsonParams.put("user_avatar", user.getUser_avatar());
            jsonParams.put("user_is_private", user.getUser_is_private());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, REGISTER_URL_STRING, new JSONObject(jsonParams),
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
            objectRequest.setTag("registerJSONRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "registerJSONRequest Error: Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    public synchronized void downloadLeaderboardJSONRequest(final ObjectResponseListener<ArrayList<User>> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", sDatabaseHandlerSingleton.getLoggedUser().getUser_student_number_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, LEADERBOARD_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    ArrayList<User> userList = new ArrayList<>();
                                    JSONArray jsonArray = response.getJSONArray("user_list");
                                    for (int x = 0; x < jsonArray.length(); x++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                                        User user = new User();
                                        user.setUser_student_number_id(jsonObject.getString("user_student_number"));
                                        user.setUser_nickname(jsonObject.getString("user_nickname"));
                                        user.setUser_avatar(jsonObject.getInt("user_avatar"));
                                        user.setUser_overall_score(jsonObject.getInt("user_overall_score"));
                                        user.setUser_overall_attempts(jsonObject.getInt("user_overall_attempts"));
                                        user.setUser_overall_time(jsonObject.getInt("user_overall_time"));
                                        userList.add(user);
                                    }
                                    Log.e(mClassName, "downloadLeaderboardJSONRequest: " + response.getString("response_message"));
                                    objectResponseListener.getResult(userList, true, response.getString("response_message"));
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
            objectRequest.setTag("downloadLeaderboardJSONRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "downloadLeaderboardJSONRequest Error: Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    public synchronized void downloadLevelsJSONRequest(User user, final BooleanResponseListener booleanResponseListener) {
        if (sDatabaseHandlerSingleton.checkUserHasLevels(user)) {
            booleanResponseListener.getResult(true, "User already has levels");
        } else {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", user.getUser_student_number_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, LEVELS_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    JSONArray jsonArray1 = response.getJSONArray("level_list");
                                    for (int x = 0; x < jsonArray1.length(); x++) {
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(x);
                                        Level level = new Level();
                                        //pk auto increment
                                        level.setLevel_user_student_number_ID(jsonObject1.getString("level_user_student_number_id")); //fk
                                        level.setLevel_database_id(jsonObject1.getInt("level_id")); //database pk
                                        level.setLevel_number(jsonObject1.getInt("level_number"));
                                        level.setLevel_title(jsonObject1.getString("level_title"));
                                        level.setLevel_description(jsonObject1.getString("level_description"));
                                        level.setLevel_completed(jsonObject1.getInt("level_completed"));
                                        level.setLevel_score(jsonObject1.getInt("level_score"));
                                        level.setLevel_attempts(jsonObject1.getInt("level_attempts"));
                                        level.setLevel_time(jsonObject1.getInt("level_time"));
                                        JSONArray jsonArray2 = jsonObject1.getJSONArray("puzzle_list");
                                        long level_id = sDatabaseHandlerSingleton.insertLevel(level);
                                        for (int a = 0; a < jsonArray2.length(); a++) {
                                            JSONObject jsonObject2 = jsonArray2.getJSONObject(a);
                                            Puzzle puzzle = new Puzzle();
                                            puzzle.setPuzzle_database_id(jsonObject2.getInt("puzzle_id")); //refers to database pk
                                            // pk auto increment
                                            puzzle.setPuzzle_level_id((int) level_id); //fk
                                            puzzle.setPuzzle_type(jsonObject2.getString("puzzle_type"));
                                            puzzle.setPuzzle_instructions(jsonObject2.getString("puzzle_instructions"));
                                            puzzle.setPuzzle_expected_output(jsonObject2.getString("puzzle_expected_output"));
                                            puzzle.setPuzzle_data(jsonObject2.getString("puzzle_data"));
                                            puzzle.setPuzzle_answer(jsonObject2.getString("puzzle_answer"));
                                            long puzzle_id = sDatabaseHandlerSingleton.insertPuzzle(puzzle);
                                        }
                                    }
                                    booleanResponseListener.getResult(true, response.getString("response_message"));
                                } else {
                                    Log.e(mClassName, "downloadLevelsJSONRequest Error: Invalid response");
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "downloadLevelsJSONRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "downloadLevelsJSONRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("downloadLevelsJSONRequest");
            getRequestQueue().add(objectRequest);
        }
    }

    public synchronized void downloadUserJSONRequest(User selected_user, final ObjectResponseListener<User> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", selected_user.getUser_student_number_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, USER_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                User user = new User();
                                user.setUser_student_number_id(response.getString("user_student_number"));
                                user.setUser_nickname(response.getString("user_nickname"));
                                user.setUser_avatar(response.getInt("user_avatar"));
                                user.setUser_is_private(response.getInt("user_is_private"));
                                objectResponseListener.getResult(user, true, "");
                            } catch (JSONException ex) {
                                Log.e(mClassName, "Error downloadUserJSONRequest: " + ex.getMessage());
                                objectResponseListener.getResult(null, false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "Error downloadUserJSONRequest: " + error);
                                objectResponseListener.getResult(null, false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("downloadUserJSONRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "Error downloadUserJSONRequest: Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    public synchronized void downloadPuzzlesJSONRequest(final Level current_level, final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("level_id", current_level.getLevel_database_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, PUZZLES_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    sDatabaseHandlerSingleton.deleteLevelPuzzles(current_level);
                                    JSONArray jsonArray = response.getJSONArray("puzzle_list");
                                    for (int x = 0; x < jsonArray.length(); x++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                                        Puzzle incoming_puzzle = new Puzzle();
                                        incoming_puzzle.setPuzzle_database_id(jsonObject.getInt("puzzle_id")); //refers to database pk
                                        // pk auto increment
                                        incoming_puzzle.setPuzzle_level_id(current_level.getLevel_id()); //fk
                                        incoming_puzzle.setPuzzle_type(jsonObject.getString("puzzle_type"));
                                        incoming_puzzle.setPuzzle_instructions(jsonObject.getString("puzzle_instructions"));
                                        incoming_puzzle.setPuzzle_expected_output(jsonObject.getString("puzzle_expected_output"));
                                        incoming_puzzle.setPuzzle_data(jsonObject.getString("puzzle_data"));
                                        incoming_puzzle.setPuzzle_answer(jsonObject.getString("puzzle_answer"));
                                        sDatabaseHandlerSingleton.insertPuzzle(incoming_puzzle);
                                    }
                                    booleanResponseListener.getResult(true, response.getString("response_message"));
                                } else {
                                    Log.e(mClassName, "downloadPuzzlesJSONRequest Error: Invalid response");
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "downloadPuzzlesJSONRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "downloadPuzzlesJSONRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("downloadPuzzlesJSONRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "Error downloadPuzzlesJSONRequest: Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    public synchronized void uploadUserJSONRequest(final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            User current_user = sDatabaseHandlerSingleton.getLoggedUser();
            jsonParams.put("user_student_number", current_user.getUser_student_number_id());
            jsonParams.put("user_nickname", current_user.getUser_nickname());
            jsonParams.put("user_avatar", current_user.getUser_avatar());
            jsonParams.put("user_is_private", current_user.getUser_avatar());
            if(current_user.getUser_updated()==1) {
                Log.e("uploadUserJSONRequest", "Updating user data");
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_USER_URL_STRING, new JSONObject(jsonParams),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getBoolean("response")) {
                                        if(sDatabaseHandlerSingleton.resetUserUpdated()==1) {
                                            uploadUserLevelsJSONRequest(booleanResponseListener);
                                        }
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
                objectRequest.setTag("uploadUserJSONRequest");
                getRequestQueue().add(objectRequest);
            } else {
                uploadUserLevelsJSONRequest(booleanResponseListener);
            }
        } else {
            Log.e(mClassName + ": ", "Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    public synchronized void uploadUserLevelsJSONRequest(final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            ArrayList<Level> current_levels = sDatabaseHandlerSingleton.getLevels();
            JSONObject jsonObject = new JSONObject();
            try {
                JSONArray outgoing_levels = new JSONArray();
                for (Level current_level : current_levels) {
                    JSONObject outgoing_level = new JSONObject();
                    outgoing_level.put("level_user_student_number_id", current_level.getLevel_user_student_number_ID());
                    outgoing_level.put("level_id", current_level.getLevel_id());
                    outgoing_level.put("level_completed", current_level.getLevel_completed());
                    outgoing_level.put("level_score", current_level.getLevel_score());
                    outgoing_level.put("level_attempts", current_level.getLevel_attempts());
                    outgoing_level.put("level_time", current_level.getLevel_time());
                    if (current_level.getLevel_updated() == 1)
                        outgoing_levels.put(outgoing_level);
                }
                jsonObject.put("level_list", outgoing_levels);
            } catch (JSONException e) {
                if (e.getMessage() != null) {
                    Log.e("JSONException", e.getMessage());
                }
            }
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_USERLEVEL_URL_STRING, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    sDatabaseHandlerSingleton.resetLevelsUpdated();
                                    booleanResponseListener.getResult(true, response.getString("response_message"));
                                } else {
                                    Log.e(mClassName, "uploadUserLevelsJSONRequest Error: Invalid response");
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "uploadUserLevelsJSONRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "uploadUserLevelsJSONRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("uploadUserLevelsJSONRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "uploadUserLevelsJSONRequest Error: Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    public synchronized void syncUserData(final BooleanResponseListener booleanResponseListener) {
        uploadUserJSONRequest(new BooleanResponseListener() {
            @Override
            public void getResult(Boolean response, String message) {
                if (response)
                    uploadUserLevelsJSONRequest(new BooleanResponseListener() {
                        @Override
                        public void getResult(Boolean response, String message) {
                            booleanResponseListener.getResult(true, "User data synced");
                        }
                    });
            }
        });
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
        if (!response.isNull("response_valid") && response.has("response_valid") && response.getBoolean("response_valid")) {
            return true;
        } else {
            Log.e(mClassName, response.getString("response_message"));
            return false;
        }
    }

    public interface BooleanResponseListener {
        void getResult(Boolean response, String message);
    }

    public interface ObjectResponseListener<T> {
        void getResult(T object, Boolean response, String message);
    }
}
