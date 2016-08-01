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

    private TextView settingLaunchTextView1, settingLaunchTextView2, settingLaunchTextView3, settingLaunchTextView4, settingLaunchTextView5,
            settingLaunchTextView6, settingLaunchTextView7;
    private Switch settingsLaunchActiveSwitch, settingsSyncWifiSwitch;
    private Spinner settingsLaunchDurationSpinner;
    private Toolbar toolbar;
    private SettingsSingleton settingsSingleton;

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

    private void setSettings() {
        settingsLaunchActiveSwitch.setChecked(settingsSingleton.getLaunchScreenActive());
        settingLaunchTextView4.setEnabled(settingsLaunchActiveSwitch.isChecked());
        settingLaunchTextView5.setEnabled(settingsLaunchActiveSwitch.isChecked());
        settingsLaunchDurationSpinner.setEnabled(settingsLaunchActiveSwitch.isChecked());
        settingsLaunchDurationSpinner.setSelection((settingsSingleton.getLaunchScreenTime() / 1000) - 1);
        settingsSyncWifiSwitch.setChecked(settingsSingleton.getSyncWifiOnly());
    }

    private void assignSingletons() {
        settingsSingleton = SettingsSingleton.getInstance(this);
    }

    private void assignListeners() {
        settingsLaunchActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingLaunchTextView4.setEnabled(isChecked);
                settingLaunchTextView5.setEnabled(isChecked);
                settingsLaunchDurationSpinner.setEnabled(isChecked);
                settingsSingleton.setLaunchScreenActive(isChecked);

            }
        });
        settingsLaunchDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settingsSingleton.setLaunchScreenTime((position + 1) * 1000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        settingsSyncWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsSingleton.setSyncWifiOnly(isChecked);
            }
        });
    }

    private void assignActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void assignViews() {
        toolbar = (Toolbar) findViewById(R.id.app_actionbar);
        settingLaunchTextView1 = (TextView) findViewById(R.id.settingLaunchTextView1);
        settingLaunchTextView2 = (TextView) findViewById(R.id.settingLaunchTextView2);
        settingLaunchTextView3 = (TextView) findViewById(R.id.settingLaunchTextView3);
        settingLaunchTextView4 = (TextView) findViewById(R.id.settingLaunchTextView4);
        settingLaunchTextView5 = (TextView) findViewById(R.id.settingLaunchTextView5);
        settingLaunchTextView6 = (TextView) findViewById(R.id.settingLaunchTextView6);
        settingLaunchTextView7 = (TextView) findViewById(R.id.settingLaunchTextView7);
        settingsLaunchActiveSwitch = (Switch) findViewById(R.id.settingsLaunchActiveSwitch);
        settingsSyncWifiSwitch = (Switch) findViewById(R.id.settingsSyncWifiSwitch);
        String[] launchScreenDurations = {"1 Second", "2 Seconds", "3 Seconds"};
        settingsLaunchDurationSpinner = (Spinner) findViewById(R.id.settingsLaunchDurationSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, launchScreenDurations);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        settingsLaunchDurationSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        settingLaunchTextView1.setTypeface(Roboto_Regular);
        settingLaunchTextView2.setTypeface(Roboto_Regular, Typeface.BOLD);
        settingLaunchTextView3.setTypeface(Roboto_Regular);
        settingLaunchTextView4.setTypeface(Roboto_Regular, Typeface.BOLD);
        settingLaunchTextView5.setTypeface(Roboto_Regular);
        settingLaunchTextView6.setTypeface(Roboto_Regular);
        settingLaunchTextView7.setTypeface(Roboto_Regular, Typeface.BOLD);
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
}
