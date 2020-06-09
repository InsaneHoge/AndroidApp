package com.clydehoge.homestock;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AddEditActivity extends AppCompatActivity implements AddEditActivityFragment.OnSaveClicked {
    private static final String TAG = "AddEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AddEditActivityFragment fragment = new AddEditActivityFragment();

        Bundle arguments = getIntent().getExtras();
        fragment.setArguments(arguments);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment); // replace calls remove and then add method. will work even if there are no fragments to replace
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveClicked() {
        finish();
    }
}
