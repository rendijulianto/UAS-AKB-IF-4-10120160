package com.proyek.uasakbif410120160;

/**
 * NIM : 10120160
 * Nama : Rendi Julianto
 * Kelas : IF-4
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyek.uasakbif410120160.model.Note;
import com.proyek.uasakbif410120160.service.MyFirebaseMessagingService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InputActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_title, et_category, et_content;
    private Button btnSave;

    private Note note;

    public final int ALERT_DIALOG_CLOSE = 10;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        et_title = findViewById(R.id.et_title);
        et_category = findViewById(R.id.et_category);
        et_content = findViewById(R.id.et_content);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);

        note = new Note();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tambah Data");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return super.onCreateOptionsMenu(menu);
    }

    //pilih menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan tambah pada form?";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(dialogTitle);
            alertDialogBuilder.setMessage(dialogMessage)
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (isDialogClose) {
                                finish();
                            }
                        }
                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            saveNote();
        }
    }

    private void saveNote() {
        String title = et_title.getText().toString().trim();
        String category = et_category.getText().toString().trim();
        String content = et_content.getText().toString().trim();

        boolean isEmptyFields = false;

        if (TextUtils.isEmpty(title)) {
            isEmptyFields = true;
            et_title.setError("Field ini tidak boleh kosong!");
        }
        if (TextUtils.isEmpty(category)) {
            isEmptyFields = true;
            et_category.setError("Field ini tidak boleh kosong!");
        }
        if (TextUtils.isEmpty(content)) {
            isEmptyFields = true;
            et_content.setError("Field ini tidak boleh kosong!");
        }
        if (!isEmptyFields) {
            firebaseAuth = firebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            String userId = firebaseUser.getUid();
            DatabaseReference dbNote = mDatabase.child("note/" + userId);
            String id = dbNote.push().getKey();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            note.setId(id);
            note.setCategory(category);
            note.setContent(content);
            note.setDate(formatter.format(date));
            note.setTitle(title);
            dbNote.child(id).setValue(note);
            finish();
            MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
            myFirebaseMessagingService.sendNotification("Berhasil", "Menambahkan catatan baru: " + title + "");
        }
    }
}