package perklun.divebox.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import perklun.divebox.R;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.models.Dive;
import perklun.divebox.models.User;
import perklun.divebox.utils.Constants;

public class CreateDiveActivity extends AppCompatActivity {

    DiveBoxDatabaseHelper dbHelper;
    Button createDiveBtn;
    EditText diveTitleEditText;

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dive);
        // Shared Preferences
        mSettings = getSharedPreferences(getString(R.string.SHARED_PREFERENCE_FILE_KEY), Context.MODE_PRIVATE);
        diveTitleEditText = (EditText) findViewById(R.id.et_input_title);
        createDiveBtn = (Button)findViewById(R.id.btn_create_dive_submit);
        createDiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mSettings.getString(getString(R.string.SHARED_PREF_USERNAME_KEY),"");
                Dive dive = new Dive(new User(username), diveTitleEditText.getText().toString());
                dbHelper = DiveBoxDatabaseHelper.getDbInstance(getApplicationContext());
                int resultCode = dbHelper.addDive(dive);
                Intent i = new Intent();
                i.putExtra(Constants.DIVE,dive);
                setResult(resultCode, i);
                //TODO: May not call finish, depends on what addDive returns
                finish();
            }
        });
    }
}
