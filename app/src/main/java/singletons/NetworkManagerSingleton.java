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
import java.util.List;
import java.util.Map;

import models.Achievement;
import models.Level;
import models.Puzzle;
import models.User;
import models.UserAchievement;

public class NetworkManagerSingleton {
    private static NetworkManagerSingleton sNetworkManagerSingletonInstance;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private SettingsSingleton mSettingsSingleton;
    private RequestQueue mRequestQueue;
    private Context mContext;
    private String mClassName = getClass().toString();

    private static final int TIME_OUT_INTERVAL = 5000;
    private static final String LOGIN_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/login";
    private static final String REGISTER_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/register";
    private static final String LEADERBOARD_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/leaderboard";
    private static final String LEVELS_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/levels";
    private static final String ADMIN_LEVELS_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/adminlevels";
    private static final String ADMIN_PUZZLES_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/adminpuzzles";
    private static final String USER_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/user";
    private static final String UPDATE_USER_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/updateuser";
    private static final String UPDATE_USERLEVEL_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/updateuserlevel";
    private static final String PUZZLES_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/puzzles";
    private static final String ACHIEVEMENTS_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/achievements";
    private static final String USERACHIEVEMENTS_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/userachievements";
    private static final String AVERAGE_LEVEL_DATA_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/averagelevel";
    private static final String UPDATE_USERACHIEVEMENTS_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/updateuserachievements";
    private static final String AVERAGE_USER_DATA_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/average";
    private static final String STUDENT_DATA_URL_STRING = "http://progamer.csdev.nmmu.ac.za/api/data/studentdata";

    private NetworkManagerSingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(mContext);
        mSettingsSingleton = SettingsSingleton.getInstance(mContext);
    }

    //If NetworkManagerSingleton instance is null assign a new instance, else return the assigned instance.
    public static NetworkManagerSingleton getInstance(Context context) {
        if (sNetworkManagerSingletonInstance == null) {
            sNetworkManagerSingletonInstance = new NetworkManagerSingleton(context);
        }
        return sNetworkManagerSingletonInstance;
    }

    //returns whether user can sync data, by checking if user specified to only sync over wifi and if a valid wifi detection is detected.
    public boolean canSyncData() {
        return !mSettingsSingleton.getSyncWifiOnly() || checkForWifi();
    }

    //Uses system services to check if valid wifi connection is detected.
    public boolean checkForWifi() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    //If Volley request queue is null assign a new request queue, else return the assigned instance of the Volley request queue.
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    //Method parameters: String of a JsonRequest method name
    //Cancels all Volley requests that contains this tag
    public void cancelJSONRequest(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    //GET METHOD: checks if user exists in external database with user_student_number and user_password.
    //If successful, user is added to local database and getAchievementsJsonRequest is called to continue user sync,
    //else false with error message is passed to listener.
    public synchronized void getLoginUserJsonRequest(User user, final BooleanResponseListener booleanResponseListener) {
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
                                    user.setUser_type(response.getString("user_type"));
                                    user.setUser_avatar(response.getInt("user_avatar"));
                                    user.setUser_is_private(response.getInt("user_is_private"));

                                    if (mDatabaseHandlerSingleton.insertUser(user) != -1) {
                                        getAchievementsJsonRequest(user, booleanResponseListener);
                                    } else {
                                        Log.e(mClassName, "getLoginUserJsonRequest Error: Failed to add user to local database");
                                        booleanResponseListener.getResult(false, "Failed to add user to local database");
                                    }
                                } else {
                                    Log.e(mClassName, "getLoginUserJsonRequest Error: " + response.getString("response_message"));
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "getLoginUserJsonRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "getLoginUserJsonRequest Error: " + error);
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

    //POST METHOD: inserts new user in external database.
    //If successful, true is passed to listener, else false with error message.
    public synchronized void postRegisterUserJsonRequest(User user, final BooleanResponseListener booleanResponseListener) {
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
                                if (validResponse(response)) {
                                    booleanResponseListener.getResult(true, response.getString("response_message"));
                                } else {
                                    Log.e(mClassName, "putRegisterUserJsonRequest Error: " + response.getString("response_message"));
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "putRegisterUserJsonRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "putRegisterUserJsonRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("putRegisterUserJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "putRegisterUserJsonRequest Error: Prevented from syncing");
            booleanResponseListener.getResult(false, "");
        }
    }

    //GET METHOD: fetches all users that match the user in progress from external database.
    //If successful a list of users is passed to listener and true, else false with error message.
    public synchronized void getLeaderboardJsonRequest(final ObjectResponseListener<ArrayList<User>> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", mDatabaseHandlerSingleton.getLoggedUser().getUser_student_number_id());
            jsonParams.put("user_type", mDatabaseHandlerSingleton.getLoggedUser().getUser_type());
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
                                        user.setUser_total_score(jsonObject.getInt("user_total_score"));
                                        user.setUser_total_attempts(jsonObject.getInt("user_total_attempts"));
                                        user.setUser_total_time(jsonObject.getInt("user_total_time"));
                                        userList.add(user);
                                    }
                                    objectResponseListener.getResult(userList, true, response.getString("response_message"));
                                } else {
                                    Log.e(mClassName, "getLeaderboardJsonRequest Error: " + response.getString("response_message"));
                                    objectResponseListener.getResult(null, false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "getLeaderboardJsonRequest Error: " + ex.getMessage());
                                objectResponseListener.getResult(null, false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "getLeaderboardJsonRequest Error: " + error);
                                objectResponseListener.getResult(null, false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("getLeaderboardJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "getLeaderboardJsonRequest Error: Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    public synchronized void getStudentDataJsonRequest(final ObjectResponseListener<ArrayList<User>> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", mDatabaseHandlerSingleton.getLoggedUser().getUser_student_number_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, STUDENT_DATA_URL_STRING, new JSONObject(jsonParams),
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
                                        user.setUser_levels_completed(jsonObject.getInt("user_levels_completed"));
                                        user.setUser_total_score(jsonObject.getInt("user_total_score"));
                                        user.setUser_total_attempts(jsonObject.getInt("user_total_attempts"));
                                        user.setUser_total_time(jsonObject.getInt("user_total_time"));
                                        user.setUser_average_score(jsonObject.getDouble("user_average_score"));
                                        user.setUser_average_attempts(jsonObject.getDouble("user_average_attempts"));
                                        user.setUser_average_time(jsonObject.getDouble("user_average_time"));
                                        userList.add(user);
                                    }
                                    objectResponseListener.getResult(userList, true, response.getString("response_message"));
                                } else {
                                    Log.e(mClassName, "getStudentDataJsonRequest Error: " + response.getString("response_message"));
                                    objectResponseListener.getResult(null, false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "getStudentDataJsonRequest Error: " + ex.getMessage());
                                objectResponseListener.getResult(null, false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "getStudentDataJsonRequest Error: " + error);
                                objectResponseListener.getResult(null, false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("getStudentDataJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "getStudentDataJsonRequest Error: Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    //GET METHOD: fetches all achievements from external database.
    //If successful all achievements are added to local database and getLoggedUserAchievementsJSONRequest is called to continue user sync,
    //else false with error message is passed to listener.
    public synchronized void getAchievementsJsonRequest(final User user, final BooleanResponseListener booleanResponseListener) {
        if (!mDatabaseHandlerSingleton.checkHasAchievements()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", user.getUser_student_number_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ACHIEVEMENTS_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    JSONArray jsonArray = response.getJSONArray("achievement_list");
                                    for (int x = 0; x < jsonArray.length(); x++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                                        Achievement achievement = new Achievement();
                                        //pk auto increment
                                        achievement.setAchievement_database_id(jsonObject.getInt("achievement_id")); //database pk
                                        achievement.setAchievement_title(jsonObject.getString("achievement_title"));
                                        achievement.setAchievement_description(jsonObject.getString("achievement_description"));
                                        achievement.setAchievement_total(jsonObject.getInt("achievement_total"));
                                        achievement.setAchievement_target(jsonObject.getString("achievement_target"));
                                        mDatabaseHandlerSingleton.insertAchievement(achievement);
                                    }
                                    getLoggedUserAchievementsJSONRequest(user, booleanResponseListener);
                                } else {
                                    Log.e(mClassName, "getAchievementsJsonRequest Error: " + response.getString("response_message"));
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "getAchievementsJsonRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "getAchievementsJsonRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("getAchievementsJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            getLoggedUserAchievementsJSONRequest(user, booleanResponseListener);
        }
    }

    //GET METHOD: fetches all current user's achievements from external database.
    //If successful all user achievements are added to local database and getLevelsJsonRequest is called to continue user sync,
    //else false with error message is passed to listener.
    public synchronized void getLoggedUserAchievementsJSONRequest(final User user, final BooleanResponseListener booleanResponseListener) {
        if (!mDatabaseHandlerSingleton.checkHasLoggedUserAchievements(user) && !user.getUser_type().equals("admin")) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", user.getUser_student_number_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, USERACHIEVEMENTS_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    JSONArray jsonArray = response.getJSONArray("userachievement_list");
                                    for (int x = 0; x < jsonArray.length(); x++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                                        UserAchievement userAchievement = new UserAchievement();
                                        //pk auto increment
                                        userAchievement.setUserachievement_database_id(jsonObject.getInt("userachievement_id")); //fk
                                        userAchievement.setUser_student_number(jsonObject.getString("user_student_number")); //database pk
                                        userAchievement.setAchievement_id(jsonObject.getInt("achievement_id"));
                                        userAchievement.setUserachievement_progress(jsonObject.getInt("userachievement_progress"));
                                        userAchievement.setUserachievement_completed(jsonObject.getInt("userachievement_completed"));
                                        userAchievement.setUserachievement_notified(jsonObject.getInt("userachievement_notified"));
                                        userAchievement.setUserachievement_date_completed(jsonObject.getString("userachievement_date_completed"));
                                        long achievement_id = mDatabaseHandlerSingleton.insertUserAchievement(userAchievement);
                                    }
                                    getLevelsJsonRequest(user, booleanResponseListener);
                                } else {
                                    Log.e(mClassName, "getLoggedUserAchievementsJSONRequest Error: " + response.getString("response_message"));
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "getLoggedUserAchievementsJSONRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "getLoggedUserAchievementsJSONRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("getLoggedUserAchievementsJSONRequest");
            getRequestQueue().add(objectRequest);
        } else {
            getLevelsJsonRequest(user, booleanResponseListener);
        }
    }

    //GET METHOD: fetches all user's levels from external database
    //If successful all user levels are added to local database and true is passed to listener, else false and error message
    public synchronized void getLevelsJsonRequest(User user, final BooleanResponseListener booleanResponseListener) {
        if (!mDatabaseHandlerSingleton.checkUserHasLevels(user)) {
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
                                        long level_id = mDatabaseHandlerSingleton.insertLevel(level);
                                        JSONArray jsonArray2 = jsonObject1.getJSONArray("puzzle_list");
                                        for (int a = 0; a < jsonArray2.length(); a++) {
                                            JSONObject jsonObject2 = jsonArray2.getJSONObject(a);
                                            Puzzle puzzle = new Puzzle();
                                            // pk auto increment
                                            puzzle.setPuzzle_database_id(jsonObject2.getInt("puzzle_id")); //refers to database pk
                                            puzzle.setPuzzle_level_id((int) level_id); //fk
                                            puzzle.setPuzzle_instructions(jsonObject2.getString("puzzle_instructions"));
                                            puzzle.setPuzzle_data(jsonObject2.getString("puzzle_data"));
                                            mDatabaseHandlerSingleton.insertPuzzle(puzzle);
                                        }
                                    }
                                    booleanResponseListener.getResult(true, response.getString("response_message"));
                                } else {
                                    Log.e(mClassName, "getLevelsJsonRequest Error: Invalid response");
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "getLevelsJsonRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "getLevelsJsonRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("getLevelsJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            booleanResponseListener.getResult(true, "User data complete");
        }
    }

    //GET METHOD: fetches all admin's levels from external database
    //If successful all user levels are added to local database and true is passed to listener, else false and error message
    public synchronized void getAdminLevelsJsonRequest(User user, final ObjectResponseListener<ArrayList<Level>> objectResponseListener) {
        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("user_student_number", user.getUser_student_number_id());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ADMIN_LEVELS_URL_STRING, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (validResponse(response)) {
                                JSONArray jsonArray = response.getJSONArray("level_list");
                                ArrayList<Level> level_list = new ArrayList<>();
                                for (int x = 0; x < jsonArray.length(); x++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(x);
                                    Level level = new Level();
                                    //pk auto increment
                                    level.setLevel_database_id(jsonObject.getInt("level_id"));
                                    level.setLevel_id(jsonObject.getInt("level_id"));
                                    level.setLevel_number(jsonObject.getInt("level_number"));
                                    level.setLevel_title(jsonObject.getString("level_title"));
                                    level_list.add(level);
                                }
                                objectResponseListener.getResult(level_list, true, response.getString("response_message"));
                            } else {
                                Log.e(mClassName, "getAdminLevelsJsonRequest Error: Invalid response");
                                objectResponseListener.getResult(null, false, response.getString("response_message"));
                            }
                        } catch (JSONException ex) {
                            Log.e(mClassName, "getAdminLevelsJsonRequest Error: " + ex.getMessage());
                            objectResponseListener.getResult(null, false, ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        if (ex != null) {
                            String error = getVolleyError(ex);
                            Log.e(mClassName, "getAdminLevelsJsonRequest Error: " + error);
                            objectResponseListener.getResult(null, false, error);
                        }
                    }
                }
        );
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objectRequest.setRetryPolicy(policy);
        objectRequest.setTag("getAdminLevelsJsonRequest");
        getRequestQueue().add(objectRequest);
    }

    //GET METHOD: fetches all admin's levels from external database
    //If successful all user levels are added to local database and true is passed to listener, else false and error message
    public synchronized void getAdminPuzzlesJsonRequest(int level_id, final ObjectResponseListener<ArrayList<Puzzle>> objectResponseListener) {
        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("level_id", level_id);
        Log.e("asd", level_id+"");
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ADMIN_PUZZLES_URL_STRING, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (validResponse(response)) {
                                JSONArray jsonArray = response.getJSONArray("puzzle_list");
                                ArrayList<Puzzle> puzzle_list = new ArrayList<>();
                                for (int x = 0; x < jsonArray.length(); x++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(x);
                                    Puzzle puzzle = new Puzzle();
                                    //pk auto increment
                                    puzzle.setPuzzle_database_id(jsonObject.getInt("puzzle_id"));
                                    puzzle.setPuzzle_id(jsonObject.getInt("puzzle_id"));
                                    puzzle.setPuzzle_instructions(jsonObject.getString("puzzle_instructions"));
                                    puzzle.setPuzzle_data(jsonObject.getString("puzzle_data"));
                                    puzzle_list.add(puzzle);
                                }
                                objectResponseListener.getResult(puzzle_list, true, response.getString("response_message"));
                            } else {
                                Log.e(mClassName, "getAdminPuzzlesJsonRequest Error: Invalid response");
                                objectResponseListener.getResult(null, false, response.getString("response_message"));
                            }
                        } catch (JSONException ex) {
                            Log.e(mClassName, "getAdminPuzzlesJsonRequest Error: " + ex.getMessage());
                            objectResponseListener.getResult(null, false, ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        if (ex != null) {
                            String error = getVolleyError(ex);
                            Log.e(mClassName, "getAdminPuzzlesJsonRequest Error: " + error);
                            objectResponseListener.getResult(null, false, error);
                        }
                    }
                }
        );
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objectRequest.setRetryPolicy(policy);
        objectRequest.setTag("getAdminPuzzlesJsonRequest");
        getRequestQueue().add(objectRequest);
    }


    //GET METHOD: fetches all of a user's achievements from external database.
    //If successful a list of user achievements passed to listener and true, else false with error message.
    public synchronized void getUserAchievementsJSONRequest(final User user, final ObjectResponseListener<ArrayList<UserAchievement>> objectResponseListener) {
        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("user_student_number", user.getUser_student_number_id());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, USERACHIEVEMENTS_URL_STRING, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (validResponse(response)) {
                                ArrayList<UserAchievement> userAchievements = new ArrayList<>();
                                JSONArray jsonArray = response.getJSONArray("userachievement_list");
                                for (int x = 0; x < jsonArray.length(); x++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(x);
                                    UserAchievement userAchievement = new UserAchievement();
                                    //pk auto increment
                                    userAchievement.setAchievement_title(jsonObject.getString("achievement_title"));
                                    userAchievement.setAchievement_description(jsonObject.getString("achievement_description"));
                                    userAchievement.setAchievement_total(jsonObject.getInt("achievement_total"));
                                    userAchievement.setUserachievement_progress(jsonObject.getInt("userachievement_progress"));
                                    userAchievement.setUserachievement_completed(jsonObject.getInt("userachievement_completed"));
                                    userAchievement.setUserachievement_date_completed(jsonObject.getString("userachievement_date_completed"));

                                    userAchievements.add(userAchievement);
                                }
                                objectResponseListener.getResult(userAchievements, true, response.getString("response_message"));
                            } else {
                                Log.e(mClassName, "getUserAchievementsJSONRequest Error: Invalid response");
                                objectResponseListener.getResult(null, false, response.getString("response_message"));
                            }
                        } catch (JSONException ex) {
                            Log.e(mClassName, "getUserAchievementsJSONRequest Error: " + ex.getMessage());
                            objectResponseListener.getResult(null, false, ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        if (ex != null) {
                            String error = getVolleyError(ex);
                            Log.e(mClassName, "getUserAchievementsJSONRequest Error: " + error);
                            objectResponseListener.getResult(null, false, error);
                        }
                    }
                }
        );
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objectRequest.setRetryPolicy(policy);
        objectRequest.setTag("getUserAchievementsJSONRequest");
        getRequestQueue().add(objectRequest);
    }

    //GET METHOD: fetches a users data from external database
    //If successful the user is passed to listener and true, else false and error message
    public synchronized void getUserJsonRequest(User selected_user, final ObjectResponseListener<User> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", selected_user.getUser_student_number_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, USER_URL_STRING, new JSONObject(jsonParams),
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
                                    user.setUser_total_score(response.getInt("user_total_score"));
                                    user.setUser_total_attempts(response.getInt("user_total_attempts"));
                                    user.setUser_total_time(response.getInt("user_total_time"));
                                    user.setUser_average_score(response.getInt("user_average_score"));
                                    user.setUser_average_attempts(response.getInt("user_average_attempts"));
                                    user.setUser_average_time(response.getInt("user_average_time"));
                                    objectResponseListener.getResult(user, true, "");
                                } else {
                                    Log.e(mClassName, "getUserJsonRequest Error: Invalid response");
                                    objectResponseListener.getResult(null, false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "Error getUserJsonRequest: " + ex.getMessage());
                                objectResponseListener.getResult(null, false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "Error getUserJsonRequest: " + error);
                                objectResponseListener.getResult(null, false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("getUserJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "Error getUserJsonRequest: Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    //GET METHOD: fetches all of a user's achievements from external database.
    //If successful a list of user achievements passed to listener and true, else false with error message.
    public synchronized void getPuzzlesJsonRequest(final Level current_level, final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("level_id", current_level.getLevel_database_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, PUZZLES_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    mDatabaseHandlerSingleton.deleteLevelPuzzles(current_level);
                                    JSONArray jsonArray = response.getJSONArray("puzzle_list");
                                    for (int x = 0; x < jsonArray.length(); x++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                                        Puzzle incoming_puzzle = new Puzzle();
                                        incoming_puzzle.setPuzzle_database_id(jsonObject.getInt("puzzle_id")); //refers to database pk
                                        // pk auto increment
                                        incoming_puzzle.setPuzzle_level_id(current_level.getLevel_id()); //fk
                                        incoming_puzzle.setPuzzle_instructions(jsonObject.getString("puzzle_instructions"));
                                        incoming_puzzle.setPuzzle_data(jsonObject.getString("puzzle_data"));
                                        mDatabaseHandlerSingleton.insertPuzzle(incoming_puzzle);
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

    //PUT METHOD:inserts/updates user in external database
    //If successful true is passed to listener, else false with error message.
    public synchronized void putUserJsonRequest(final BooleanResponseListener booleanResponseListener) {
        if (canSyncData()) {
            User current_user = mDatabaseHandlerSingleton.getLoggedUser();
            if (current_user == null) {
                booleanResponseListener.getResult(false, "putUserJsonRequest Error: user null");
                return;
            }
            if (current_user.getUser_updated() == 0) {
                putUserLevelsJSONRequest(booleanResponseListener);
                return;
            }
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", current_user.getUser_student_number_id());
            jsonParams.put("user_nickname", current_user.getUser_nickname());
            jsonParams.put("user_avatar", current_user.getUser_avatar());
            jsonParams.put("user_is_private", current_user.getUser_avatar());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_USER_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    if (mDatabaseHandlerSingleton.resetUserUpdated() == 1) {
                                        putUserLevelsJSONRequest(booleanResponseListener);
                                    } else {
                                        Log.e(mClassName, "putUserJsonRequest Error: failed to reset user update fields");
                                        booleanResponseListener.getResult(false, "putUserJsonRequest Error: failed to reset user update fields");
                                    }
                                } else {
                                    Log.e(mClassName, "putUserJsonRequest Error: " + response.getString("response_message"));
                                    booleanResponseListener.getResult(false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "putUserJsonRequest Error: " + ex.getMessage());
                                booleanResponseListener.getResult(false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "putUserJsonRequest Error: " + error);
                                booleanResponseListener.getResult(false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("putUserJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            putUserLevelsJSONRequest(booleanResponseListener);
        }
    }

    //PUT METHOD:inserts/updates user levels in external database
    //If successful true is passed to listener, else false with error message.
    public synchronized void putUserLevelsJSONRequest(final BooleanResponseListener booleanResponseListener) {
        ArrayList<Level> current_levels = mDatabaseHandlerSingleton.getLevels();
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
                Log.e("levelUpdate", current_level.getLevel_id() + " : " + current_level.getLevel_updated());
                if (current_level.getLevel_updated() == 1)
                    outgoing_levels.put(outgoing_level);
            }
            if (outgoing_levels.length() == 0) {
                Log.e("outgoing_levels", "length: " + outgoing_levels.length());
                putUserAchievementsJSONRequest(booleanResponseListener);
                return;
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
                                Log.e("updateLevels", "called");
                                if (mDatabaseHandlerSingleton.resetLevelsUpdated() > 0) {
                                    putUserAchievementsJSONRequest(booleanResponseListener);
                                } else {
                                    Log.e(mClassName, "putUserLevelsJSONRequest Error: failed to reset user update fields\"");
                                    booleanResponseListener.getResult(false, "putUserLevelsJSONRequest Error: failed to reset user update fields");
                                }
                            } else {
                                Log.e(mClassName, "putUserLevelsJSONRequest Error: Invalid response");
                                booleanResponseListener.getResult(false, response.getString("response_message"));
                            }
                        } catch (JSONException ex) {
                            Log.e(mClassName, "putUserLevelsJSONRequest Error: " + ex.getMessage());
                            booleanResponseListener.getResult(false, ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        if (ex != null) {
                            String error = getVolleyError(ex);
                            Log.e(mClassName, "putUserLevelsJSONRequest Error: " + error);
                            booleanResponseListener.getResult(false, error);
                        }
                    }
                }
        );
        RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objectRequest.setRetryPolicy(policy);
        objectRequest.setTag("uploadUserLevelsJSONRequest");
        getRequestQueue().add(objectRequest);
    }

    //PUT METHOD:inserts/updates user achievements in external database
    //If successful true is passed to listener, else false with error message.
    public synchronized void putUserAchievementsJSONRequest(final BooleanResponseListener booleanResponseListener) {
        List<UserAchievement> current_userachievements = mDatabaseHandlerSingleton.getUserAchievementsUpdated();
        if (current_userachievements.size() == 0) {
            booleanResponseListener.getResult(true, "Data successfully synced");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray outgoing_userachievements = new JSONArray();
            for (UserAchievement current_userachievement : current_userachievements) {
                Log.e(mClassName, "userachievement_progress: " + current_userachievement.getUserachievement_progress());
                Log.e(mClassName, "userachievement_progress: " + current_userachievement.getUserachievement_completed());
                Log.e(mClassName, "userachievement_notified: " + current_userachievement.getUserachievement_notified());
                JSONObject outgoing_userachievement = new JSONObject();
                outgoing_userachievement.put("userachievement_id", current_userachievement.getUserachievement_id());
                outgoing_userachievement.put("userachievement_progress", current_userachievement.getUserachievement_progress());
                outgoing_userachievement.put("userachievement_completed", current_userachievement.getUserachievement_completed());
                outgoing_userachievement.put("userachievement_notified", current_userachievement.getUserachievement_notified());
                outgoing_userachievements.put(outgoing_userachievement);
            }
            jsonObject.put("userachievement_list", outgoing_userachievements);
        } catch (JSONException e) {
            if (e.getMessage() != null) {
                Log.e("JSONException", e.getMessage());
            }
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_USERACHIEVEMENTS_URL_STRING, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (validResponse(response)) {
                                JSONArray jsonArray = response.getJSONArray("userachievement_list");
                                for (int x = 0; x < jsonArray.length(); x++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(x);
                                    UserAchievement userAchievement = new UserAchievement();
                                    //pk auto increment
                                    userAchievement.setUserachievement_database_id(jsonObject.getInt("userachievement_id")); //fk
                                    userAchievement.setUserachievement_date_completed(jsonObject.getString("userachievement_date_completed"));
                                    userAchievement.setUserachievement_updated(0);
                                    mDatabaseHandlerSingleton.updateUserAchievementsUpdated(userAchievement);
                                }
                                booleanResponseListener.getResult(true, response.getString("response_message"));
                            } else {
                                Log.e(mClassName, "putUserAchievementsJSONRequest Error: Invalid response");
                                booleanResponseListener.getResult(false, response.getString("response_message"));
                            }
                        } catch (JSONException ex) {
                            Log.e(mClassName, "putUserAchievementsJSONRequest Error: " + ex.getMessage());
                            booleanResponseListener.getResult(false, ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        if (ex != null) {
                            String error = getVolleyError(ex);
                            Log.e(mClassName, "putUserAchievementsJSONRequest Error: " + error);
                            booleanResponseListener.getResult(false, error);
                        }
                    }
                }
        );
        RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objectRequest.setRetryPolicy(policy);
        objectRequest.setTag("putUserAchievementsJSONRequest");
        getRequestQueue().add(objectRequest);
    }

    //GET METHOD: fetches the average level data from external database
    //If successful the user is passed to listener and true, else false and error message
    public synchronized void getAverageLevelDataJsonRequest(Level current_level, final ObjectResponseListener<Level> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("level_id", current_level.getLevel_database_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, AVERAGE_LEVEL_DATA_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    Level level = new Level();
                                    level.setLevel_score(response.getInt("level_score"));
                                    level.setLevel_attempts(response.getInt("level_attempts"));
                                    level.setLevel_time(response.getInt("level_time"));
                                    objectResponseListener.getResult(level, true, "");
                                } else {
                                    Log.e(mClassName, "getAverageLevelDataJsonRequest Error: Invalid response");
                                    objectResponseListener.getResult(null, false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "Error getAverageLevelDataJsonRequest: " + ex.getMessage());
                                objectResponseListener.getResult(null, false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "Error getAverageLevelDataJsonRequest: " + error);
                                objectResponseListener.getResult(null, false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("getAverageLevelDataJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "Error getAverageLevelDataJsonRequest: Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    //GET METHOD: fetches the average users data from external database
    //If successful the user is passed to listener and true, else false and error message
    public synchronized void getAverageUserDataJsonRequest(final ObjectResponseListener<User> objectResponseListener) {
        if (canSyncData()) {
            Map<String, Object> jsonParams = new HashMap<>();
            jsonParams.put("user_student_number", mDatabaseHandlerSingleton.getLoggedUser().getUser_student_number_id());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, AVERAGE_USER_DATA_URL_STRING, new JSONObject(jsonParams),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (validResponse(response)) {
                                    User user = new User();
                                    user.setUser_average_score(response.getInt("user_average_score"));
                                    user.setUser_average_attempts(response.getInt("user_average_attempts"));
                                    user.setUser_average_time(response.getInt("user_average_time"));
                                    objectResponseListener.getResult(user, true, "");
                                } else {
                                    Log.e(mClassName, "getAverageLevelDataJsonRequest Error: Invalid response");
                                    objectResponseListener.getResult(null, false, response.getString("response_message"));
                                }
                            } catch (JSONException ex) {
                                Log.e(mClassName, "Error getAverageLevelDataJsonRequest: " + ex.getMessage());
                                objectResponseListener.getResult(null, false, ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError ex) {
                            if (ex != null) {
                                String error = getVolleyError(ex);
                                Log.e(mClassName, "Error getAverageLevelDataJsonRequest: " + error);
                                objectResponseListener.getResult(null, false, error);
                            }
                        }
                    }
            );
            RetryPolicy policy = new DefaultRetryPolicy(TIME_OUT_INTERVAL, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            objectRequest.setRetryPolicy(policy);
            objectRequest.setTag("getAverageLevelDataJsonRequest");
            getRequestQueue().add(objectRequest);
        } else {
            Log.e(mClassName, "Error getAverageLevelDataJsonRequest: Prevented from syncing");
            objectResponseListener.getResult(null, false, "");
        }
    }

    private String getVolleyError(VolleyError ex) {
        if (ex instanceof TimeoutError || ex instanceof NoConnectionError) {
            return "No internet access or server is down.";
        } else if (ex instanceof AuthFailureError) {
            return "Authentication error.";
        } else if (ex instanceof ServerError) {
            return "Server error.";
        } else if (ex instanceof NetworkError) {
            return "Network error.";
        } else if (ex instanceof ParseError) {
            return "Parse error.";
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