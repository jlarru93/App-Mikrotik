package com.kael.mikrotik;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by kael on 29/08/2016.
 */
public class ServidorActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servidores);
        findControls();
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        ServidorFragment fragment;
        fragment = new ServidorFragment();

        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
        cambiarTitulo();

    }

    private void findControls() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
    }


    private void cambiarTitulo(){
        getSupportActionBar().setTitle("Servidores");
    }
}
