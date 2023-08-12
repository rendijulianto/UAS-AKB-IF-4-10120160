package com.proyek.uasakbif410120160;
/**
 * NIM : 10120160
 * Nama : Rendi Julianto
 * Kelas : IF-4
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.proyek.uasakbif410120160.adapter.NoteAdapter;
import com.proyek.uasakbif410120160.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment implements View.OnClickListener{




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FloatingActionButton fab;
    private ListView listView;
    private NoteAdapter adapter;
    private ArrayList<Note> noteList;
    DatabaseReference dbNote;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query query;


    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_note, container, false);
        // Inflate the layout for this fragment
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), InputActivity.class));
            }
        });
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();

        // Format tanggal yang digunakan dalam Firebase
        SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Mendapatkan tanggal hari ini
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        // Membuat waktu awal dan akhir dari hari ini
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(today);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);

        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(today);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        dbNote = FirebaseDatabase.getInstance().getReference("note/" + userId);
        // Membuat query untuk mendapatkan data dengan tanggal hari ini
        query = dbNote.orderByChild("date")
                .startAt(firebaseDateFormat.format(todayStart.getTime()))
                .endAt(firebaseDateFormat.format(todayEnd.getTime()));


        listView = view.findViewById(R.id.lv_list);
        noteList = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), UpdateActivity.class);
                intent.putExtra(UpdateActivity.EXTRA_NOTE, noteList.get(i));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            Intent intent = new Intent(getContext(), InputActivity.class);
            startActivity(intent);
        }
    }

    private void read() {

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    noteList.add(note);
                }
                NoteAdapter adapter = new NoteAdapter(getContext());
                adapter.setNoteList(noteList);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        read();
    }
}