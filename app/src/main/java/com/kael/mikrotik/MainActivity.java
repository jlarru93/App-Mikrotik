package com.kael.mikrotik;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.kael.bean.Servidor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findControls();
        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        setupTabLayout(tabLayout);
        cambiarTitulo();
    }

    private void findControls(){
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    private void setupTabLayout(TabLayout tabLayout) {
        tabLayout.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        AdaptadorSecciones adapter = new AdaptadorSecciones(getSupportFragmentManager());

        SecretFragment secretFragment = new SecretFragment();
        Bundle bundle = getIntent().getExtras();
        secretFragment.setArguments(bundle);

        PPPInterfaceFragment pppInterfaceFragment = new PPPInterfaceFragment();
        pppInterfaceFragment.setArguments(bundle);

        adapter.addFragment(secretFragment, "Activaciones");
        adapter.addFragment(pppInterfaceFragment, "Conectados");

        viewPager.setAdapter(adapter);
    }


    private void cambiarTitulo(){
        Bundle bundle = getIntent().getExtras();
        String uptimeMikrotik;
        if(bundle!=null){
            Servidor servidor = (Servidor) bundle.getSerializable("servidor");
            uptimeMikrotik = bundle.getString("uptime");

            if(servidor!=null){
                getSupportActionBar().setTitle(servidor.getNameServer());
            }


            getSupportActionBar().setSubtitle("uptime :"+uptimeMikrotik);
        }

    }



    private class AdaptadorSecciones extends FragmentStatePagerAdapter {
        private final List<Fragment> fragmentos = new ArrayList<>();
        private final List<String> titulosFragmentos = new ArrayList<>();

        public AdaptadorSecciones(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragmentos.get(position);
        }

        @Override
        public int getCount() {
            return fragmentos.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            fragmentos.add(fragment);
            titulosFragmentos.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titulosFragmentos.get(position);
        }
    }
}
