package xyz.pitongku.fishinfo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import xyz.pitongku.fishinfo.R;

public class MenuHargaActivity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_harga);

        getSupportActionBar().setTitle("Menu Harga Ikan");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void btnPasarClicked(View view) {
        Intent intent = new Intent(this, IkanKonsumsiActivity.class);
        intent.putExtra("JENIS", "0");
        startActivity(intent);
    }

    public void btnGrosirClicked(View view) {
        Intent intent = new Intent(this, IkanKonsumsiActivity.class);
        intent.putExtra("JENIS", "1");
        startActivity(intent);
    }

    public void btnProdusenClicked(View view) {
        Intent intent = new Intent(this, IkanKonsumsiActivity.class);
        intent.putExtra("JENIS", "2");
        startActivity(intent);
    }
}
