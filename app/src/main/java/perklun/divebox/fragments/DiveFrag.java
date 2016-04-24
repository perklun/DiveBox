package perklun.divebox.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import perklun.divebox.R;

public class DiveFrag extends Fragment {
    // newInstance constructor for creating fragment with arguments
    public static DiveFrag newInstance() {
        DiveFrag fragmentFirst = new DiveFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dive, container, false);
        return view;
    }
}
