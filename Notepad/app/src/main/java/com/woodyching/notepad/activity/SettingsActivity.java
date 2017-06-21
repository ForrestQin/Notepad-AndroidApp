package com.woodyching.notepad.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.woodyching.notepad.R;
import com.woodyching.notepad.bean.User;

import org.litepal.crud.DataSupport;

import static android.content.ContentValues.TAG;

public class SettingsActivity extends AppCompatPreferenceActivity {

    PreferenceManager pm;
    EditTextPreference etp;


    EditText email;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

        pm = getPreferenceManager();
        etp = (EditTextPreference) pm.findPreference("set_info");


        etp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Dialog dd = etp.getDialog();

                name =(EditText)dd.findViewById(R.id.dialog_username);
                email =(EditText)dd.findViewById(R.id.dialog_email);

                return true;
            }
        });
        etp.setPositiveButtonText("确定");
        etp.setNegativeButtonText("取消");
        
        etp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                TextView a_username = (TextView)  MainActivity.instance.headerView.findViewById(R.id.header_username);
                TextView a_email = (TextView)  MainActivity.instance.headerView.findViewById(R.id.mail);

                User user = DataSupport.findFirst(User.class);
                user.setUsername(name.getText().toString());
                user.setEmail(email.getText().toString());
                user.save();
                return false;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                Log.d(TAG, "onOptionsItemSelected: FFFuck");
                this.finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }





}
