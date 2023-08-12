package com.proyek.uasakbif410120160;

/* NIM : 10120160
 * Nama : Rendi Julianto
 * Kelas : IF-4
 */
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.proyek.uasakbif410120160.service.MyFirebaseMessagingService;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    private ProfileFragment profileFragment = new ProfileFragment();
    private NoteFragment noteFragment = new NoteFragment();
    private InfoFragment infoFragment = new InfoFragment();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button btnSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                getSupportActionBar().setTitle("Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, profileFragment).commit();
                return true;
            case R.id.note:
                getSupportActionBar().setTitle("Catatan Harian");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, noteFragment).commit();
                return true;
            case R.id.info:
                getSupportActionBar().setTitle("Informasi");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, infoFragment).commit();
                return true;
            case R.id.logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Konfirmasi!");
                alertDialogBuilder.setMessage("Apakah anda ingin keluar dari aplikasi ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
                                myFirebaseMessagingService.sendNotification("Yahh", "Kamu telah keluar dari aplikasi :(");
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;

        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}