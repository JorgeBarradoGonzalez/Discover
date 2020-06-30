package jb.dam2.discover.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import jb.dam2.discover.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //JB: Se indica una espera antes de la carga de la siguiente actividad
        new Handler().postDelayed(() -> goToLoginActivity(), 1200);
    }

    /*
        //JB: goToLoginActivity() Da el paso a la actividad de Login y esta se destruye
    */
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
