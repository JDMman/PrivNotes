package com.example.privnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class editnoteactivity extends AppCompatActivity {

    Intent data;
    EditText medittitleofnote,meditcontentofnote;
    FloatingActionButton msaveeditnote;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    //new
    public static String bgColor = "";

    Button selectColor;

    int noteColor;
    int oldcolor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnoteactivity);
        medittitleofnote=findViewById(R.id.edittitleofnote);
        meditcontentofnote=findViewById(R.id.editcontentofnote);
        msaveeditnote=findViewById(R.id.saveeditnote);
        selectColor = findViewById(R.id.selectColorBtnEdit);

        data=getIntent();


        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//new
        String oldColor = data.getStringExtra("bgColor");
        if (!TextUtils.isEmpty(oldColor)){
            oldcolor = Integer.parseInt(oldColor);
            noteColor = oldcolor;
            selectColor.setBackgroundColor(oldcolor);
        }

//new
        selectColor.setOnClickListener(v -> {
            openColorPicker();

        });


        Toolbar toolbar=findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        msaveeditnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Save Button Clicked", Toast.LENGTH_SHORT).show();

                String newtitle=medittitleofnote.getText().toString();
                String newcontent=meditcontentofnote.getText().toString();




                if(newtitle.isEmpty()||newcontent.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Something is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
//new
                    String myColor = String.valueOf(noteColor);
                    DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    String ts = String.valueOf(Timestamp.now().toDate());
                    Calendar cal = Calendar.getInstance();
                    int mSeconds = cal.get(Calendar.MILLISECOND);
                    Map<String,Object> note=new HashMap<>();
                    note.put("title",newtitle);
                    note.put("content",newcontent);

//new
                    if (!TextUtils.isEmpty(myColor)){
                        note.put("bgColor",myColor);
                    }else{
                        note.put("bgColor",oldcolor);
                    }
                    note.put("date", String.valueOf(ts));

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note is Updated", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(editnoteactivity.this,notesactivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to Update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        String notetitle=data.getStringExtra("title");
        String notecontent=data.getStringExtra("content");

        meditcontentofnote.setText(notecontent);
        medittitleofnote.setText(notetitle);


    }
    //new
    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, noteColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                noteColor = color;
                selectColor.setBackgroundColor(noteColor);
                msaveeditnote.setEnabled(true);
                msaveeditnote.setClickable(true);
                msaveeditnote.setAlpha(1.0f);
                bgColor = String.valueOf(color);
                selectColor.setText("Color Selected");
            }
        });
        colorPicker.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }
}