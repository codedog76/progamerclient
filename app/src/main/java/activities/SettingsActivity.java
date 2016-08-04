package activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.progamer.R;

import singletons.SettingsSingleton;

public class SettingsActivity extends AppCompatActivity {

    private TextView mTextLaunchScreenTitle, mTextLaunchScreenActive, mTextLaunchScreenActiveDescription,
            mTextLaunchScreenTime, mTextLaunchScreenTimeDescription, mTextDataUsageTitle, mTextSyncOverWifi;
    private Switch mSwitchLaunchScreenActive, mSwitchSyncOverWifi;
    private Spinner mSpinnerLaunchScreenTime;
    private Toolbar mToolbar;
    private SettingsSingleton mSettingsSingleton;
    private String[] mLaunchScreenDurations = new String[] {"1 Second", "2 Seconds", "3 Seconds"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        assignSingletons();
        assignViews();
        assignActionBar();
        assignFonts();
        assignListeners();
        setSettings();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSettings() {
        mSwitchLaunchScreenActive.setChecked(mSettingsSingleton.getLaunchScreenActive());
        mTextLaunchScreenTime.setEnabled(mSwitchLaunchScreenActive.isChecked());
        mTextLaunchScreenTimeDescription.setEnabled(mSwitchLaunchScreenActive.isChecked());
        mSpinnerLaunchScreenTime.setEnabled(mSwitchLaunchScreenActive.isChecked());
        mSpinnerLaunchScreenTime.setSelection((mSettingsSingleton.getLaunchScreenTime() / 1000) - 1);
        mSwitchSyncOverWifi.setChecked(mSettingsSingleton.getSyncWifiOnly());
    }

    private void assignSingletons() {
        mSettingsSingleton = SettingsSingleton.getInstance(this);
    }

    private void assignListeners() {
        mSwitchLaunchScreenActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTextLaunchScreenTime.setEnabled(isChecked);
                mTextLaunchScreenTimeDescription.setEnabled(isChecked);
                mSpinnerLaunchScreenTime.setEnabled(isChecked);
                mSettingsSingleton.setLaunchScreenActive(isChecked);

            }
        });
        mSpinnerLaunchScreenTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSettingsSingleton.setLaunchScreenTime((position + 1) * 1000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSwitchSyncOverWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettingsSingleton.setSyncWifiOnly(isChecked);
            }
        });
    }

    private void assignActionBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void assignViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_actionbar);
        mTextLaunchScreenTitle = (TextView) findViewById(R.id.text_launch_screen_title);
        mTextLaunchScreenActive = (TextView) findViewById(R.id.text_launch_screen_active);
        mTextLaunchScreenActiveDescription =
                (TextView) findViewById(R.id.text_launch_screen_active_description);
        mTextLaunchScreenTime = (TextView) findViewById(R.id.text_launch_screen_time);
        mTextLaunchScreenTimeDescription = (TextView) findViewById(R.id.text_launch_screen_time_description);
        mTextDataUsageTitle = (TextView) findViewById(R.id.text_data_usage_title);
        mTextSyncOverWifi = (TextView) findViewById(R.id.text_sync_over_wifi);
        mSwitchLaunchScreenActive = (Switch) findViewById(R.id.switch_launch_screen_active);
        mSpinnerLaunchScreenTime = (Spinner) findViewById(R.id.spinner_launch_screen_time);
        mSwitchSyncOverWifi = (Switch) findViewById(R.id.switch_sync_over_wifi);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                mLaunchScreenDurations);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLaunchScreenTime.setAdapter(spinnerArrayAdapter);
    }

    private void assignFonts() {
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTextLaunchScreenTitle.setTypeface(Roboto_Regular);
        mTextLaunchScreenActive.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextLaunchScreenActiveDescription.setTypeface(Roboto_Regular);
        mTextLaunchScreenTime.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextLaunchScreenTimeDescription.setTypeface(Roboto_Regular);
        mTextDataUsageTitle.setTypeface(Roboto_Regular);
        mTextSyncOverWifi.setTypeface(Roboto_Regular, Typeface.BOLD);
    }
}
