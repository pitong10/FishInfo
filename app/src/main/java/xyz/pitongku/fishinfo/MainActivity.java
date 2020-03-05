package xyz.pitongku.fishinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import xyz.pitongku.fishinfo.Activity.GraphActivity;
import xyz.pitongku.fishinfo.Activity.HomeActivity;
import xyz.pitongku.fishinfo.Activity.LoginActivity;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences mUser;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_nav_menu);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        MenuItem menuItem = bottomNavigation.getMenu().getItem(0);
        MenuItem menuItem2 = bottomNavigation.getMenu().getItem(1);
        menuItem.setCheckable(false);
        menuItem2.setCheckable(false);

        mUser = getSharedPreferences("User", Context.MODE_PRIVATE);

        checkSharedPreferences();
    }

    private void checkSharedPreferences() {
        String nama = mUser.getString("nama", "");
        String passwd = mUser.getString("passwd", "");
        String username = mUser.getString("username", "");
        String kodeKota = mUser.getString("kodeKota", "");
        if (!nama.equals("") && !passwd.equals("") && !username.equals("") && !kodeKota.equals("")) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("NAMA_SURVEYOR", nama);
            intent.putExtra("KODE_SURVEYOR", username);
            intent.putExtra("KODE_KOTA", kodeKota);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.login_menu:
                Intent goLogin = new Intent(this, LoginActivity.class);
                startActivity(goLogin);
                break;

            case R.id.graph_menu:
                startActivity(new Intent(this, GraphActivity.class));
                break;
        }

        return false;
    }
}
