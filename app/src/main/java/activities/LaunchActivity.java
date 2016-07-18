package activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;

import models.Level;
import models.User;
import singletons.DatabaseHandlerSingleton;
import singletons.SettingsSingleton;

public class LaunchActivity extends AppCompatActivity {

    private TextView launchScreenTextView;
    private final Handler launchScreenHandler = new Handler();
    private DatabaseHandlerSingleton databaseHandlerSingleton;
    private SettingsSingleton settingsSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        assignSingletons();
        assignViews();
        assignFonts();
        doLaunchScreen();
    }

    public void assignSingletons() {
        settingsSingleton = SettingsSingleton.getInstance(this);
        databaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
    }

    public void doLaunchScreen() {
        if (settingsSingleton.getLaunchScreenActive()) {
            if (settingsSingleton.getFirstTimeLaunch()) {
                launchScreenTextView.setText("WELCOME");
                settingsSingleton.setFirstTimeLaunch(false);
            } else {
                launchScreenTextView.setText("WELCOME BACK");
            }
            launchScreenHandler.postDelayed(jobInboxRunnable, settingsSingleton.getLaunchScreenTime());
        } else {
            launchActivity();
        }
    }

    private Runnable jobInboxRunnable = new Runnable() {
        @Override
        public void run() {
            launchActivity();
        }
    };

    private void launchActivity() {
        /*User lucius = new User(); //todo:testing
        lucius.setUser_student_number("s211266337");
        lucius.setUser_nickname("Lucien");
        lucius.setUser_avatar(5);
        lucius.setUser_logged_in(1);
        databaseHandlerSingleton.insertOrUpdateUser(lucius);*/
        if (databaseHandlerSingleton.getLoggedUser() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void assignViews() {
        launchScreenTextView = (TextView) findViewById(R.id.launchScreenTextView);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        launchScreenTextView.setTypeface(Roboto_Medium);
    }
}
