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

public class UpdateActivity extends AppCompatActivity  implements View.OnClickListener  {
    private EditText et_title, et_category, et_content;
    private Button btnUpdate;

    public static final String EXTRA_NOTE = "extra_note";
    public final int ALERT_DIALOG_CLOSE = 10;
    public final int ALERT_DIALOG_DELETE = 20;
    private MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();

    private Note note;
    private String noteId;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        et_title = findViewById(R.id.et_title);
        et_category = findViewById(R.id.et_category);
        et_content = findViewById(R.id.et_content);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);
        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (note != null) {
            noteId = note.getId();
        } else {
            note = new Note();
        }
        if (note != null) {
            et_title.setText(note.getTitle());
            et_category.setText(note.getCategory());
            et_content.setText(note.getContent());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ubah Data");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_update) {
            updateNote();
        }
    }

    private void updateNote() {
        String title = et_title.getText().toString().trim();
        String category = et_category.getText().toString().trim();
        String content = et_content.getText().toString().trim();

        boolean isEmptyFields = false;

        if (TextUtils.isEmpty(title)) {
            isEmptyFields = true;
            et_title.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(category)) {
            isEmptyFields = true;
            et_category.setError("Field ini tidak boleh kosong");
        }

        if (TextUtils.isEmpty(content)) {
            isEmptyFields = true;
            et_content.setError("Field ini tidak boleh kosong");
        }

        if (! isEmptyFields) {
            note.setTitle(title);
            note.setContent(content);
            note.setCategory(category);
            firebaseAuth = firebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            String userId = firebaseUser.getUid();
            DatabaseReference dbNote = mDatabase.child("note/"+userId);
            dbNote.child(noteId).setValue(note);
            finish();
            myFirebaseMessagingService.sendNotification("Berhasil", "Mengubah catatan : "+title+"");
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //pilih menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
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
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        } else {
            dialogTitle = "Hapus Data";
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        String title = et_title.getText().toString().trim();
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder.setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            firebaseAuth = firebaseAuth.getInstance();
                            firebaseUser = firebaseAuth.getCurrentUser();
                            String userId = firebaseUser.getUid();
                            DatabaseReference dbMahasiswa =
                                    mDatabase.child("note/"+userId).child(noteId);
                            dbMahasiswa.removeValue();
                            finish();
                            myFirebaseMessagingService.sendNotification("Berhasil", "Menghapus catatan: "+title+"");
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