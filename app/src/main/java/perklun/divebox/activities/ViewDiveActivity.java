package perklun.divebox.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import perklun.divebox.R;
import perklun.divebox.models.Dive;
import perklun.divebox.utils.Constants;

public class ViewDiveActivity extends AppCompatActivity {

    private Dive dive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dive);
        dive = getIntent().getParcelableExtra(Constants.DIVE);
        TextView tvDiveDetailTitle = (TextView)findViewById(R.id.tv_dive_detail_title);
        tvDiveDetailTitle.setText(dive.title);
    }
}
