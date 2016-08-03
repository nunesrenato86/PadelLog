package com.renatonunes.padellog;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.renatonunes.padellog.domain.Championship;

public class AddChampionshipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_championship);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save_championship_form);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChampionship();
            }
        });
    }

    public void saveChampionship(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            TextView name = (TextView) findViewById(R.id.add_championship_name);
            TextView partner = (TextView) findViewById(R.id.add_partner_name);

            Championship championship = new Championship();
            championship.setName(name.getText().toString());
            championship.setName(partner.getText().toString());
            championship.setOwner(user.getUid());
            championship.saveDB();
        }
    }
}
