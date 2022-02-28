package com.example.privnotes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class notesactivity extends AppCompatActivity {
    public static  final String SORT = "ok";

    FloatingActionButton mcreatenotesfab;
    private FirebaseAuth firebaseAuth;


    RecyclerView mrecyclerview;
    GridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;
    FirestoreRecyclerOptions<firebasemodel>allusernotes;
    Query query;

    /* SharedPreferences mSharedPref;
     String mSorting = mSharedPref.getString("Sort", "newest");*/
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    public static String isNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notesactivity);

        mcreatenotesfab=findViewById(R.id.createnotefab);
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser=firebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isNew = prefs.getString(SORT,"newest");



        getSupportActionBar().setTitle("All Notes");

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(notesactivity.this,createnote.class));


            }
        });
        myQuery(query,isNew);


    }

    private void myQuery(Query myQuery, String mSorting) {


        if (mSorting.equals("newest")){
            myQuery=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("date", Query.Direction.DESCENDING).limitToLast(100);
        }else if(mSorting.equals("oldest")){
            myQuery=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("date", Query.Direction.ASCENDING).limitToLast(100);
        }else if(mSorting.equals("name")){
            myQuery=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);
        }


        mrecyclerview=findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager=new GridLayoutManager(notesactivity.this,2,GridLayoutManager.VERTICAL,false);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);

        allusernotes=new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(myQuery,firebasemodel.class).build();

        noteAdapter=new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {


                ImageView popupbutton=noteViewHolder.itemView.findViewById(R.id.menupopbutton);


                int colourcode=getRandomColor();
//new
                String col = firebasemodel.getBgColor();
//new
                if (TextUtils.isEmpty(col)){
                    noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode,null));
                }else {
                    int colorCode = Integer.parseInt(col);

                    noteViewHolder.mnote.setBackgroundColor(colorCode);


                }



                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());


                String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //we have to open note detail activity

                        Intent intent=new Intent(v.getContext(),notedetails.class);
                        intent.putExtra("title",firebasemodel.getTitle());
                        intent.putExtra("content",firebasemodel.getContent());
//new
                        intent.putExtra("bgColor",firebasemodel.getBgColor());
                        intent.putExtra("noteId",docId);

                        v.getContext().startActivity(intent);


                        //Toast.makeText(getApplicationContext(), "This is Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                Intent intent=new Intent(v.getContext(),editnoteactivity.class);
                                intent.putExtra("title",firebasemodel.getTitle());
                                intent.putExtra("content",firebasemodel.getContent());
//new
                                intent.putExtra("bgColor",firebasemodel.getBgColor());
                                intent.putExtra("noteId",docId);
                                intent.putExtra("date",firebasemodel.getDate());
                                v.getContext().startActivity(intent);
                                return true;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //Toast.makeText(v.getContext(), "This Note Is Deleted", Toast.LENGTH_SHORT).show();
//new
                                //alert dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(notesactivity.this);
                                //set title
                                builder.setTitle("Delete Note");
                                //set message
                                builder.setMessage("Are you sure you want to remove this note?");
                                //positive yes
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish activity
                                        DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(v.getContext(), "This Note Is Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), "Failed To Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                                //negative no
                                builder.setNegativeButton("NO", (dialog, which) -> {
                                    //dismiss dialog
                                    dialog.dismiss();
                                });
                                builder.show();
//end of new

                                return false;
                            }
                        });

                        popupMenu.show();

                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };



        mrecyclerview.setAdapter(noteAdapter);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(notesactivity.this,MainActivity.class));
                break;
            case R.id.newest:
                query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes");
                prefs = PreferenceManager.getDefaultSharedPreferences(this);
                editor = prefs.edit();
                editor.putString(SORT,"newest");
                editor.apply();
                isNew = "newest";
                myQuery(query, isNew);
                Toast.makeText(this, "Sorted Newest to  Oldest", Toast.LENGTH_SHORT).show();

                recreate();
                break;
            case R.id.oldest:
                query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes");
                prefs = PreferenceManager.getDefaultSharedPreferences(this);
                editor = prefs.edit();
                editor.putString(SORT,"oldest");
                editor.apply();
                isNew = "oldest";
                myQuery(query, isNew);
                Toast.makeText(this, "Sorted Oldest to Newest", Toast.LENGTH_SHORT).show();
                recreate();
                break;

            case R.id.name:
                query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes");
                prefs = PreferenceManager.getDefaultSharedPreferences(this);
                editor = prefs.edit();
                editor.putString(SORT,"name");
                editor.apply();
                isNew = "name";
                myQuery(query, isNew);
                Toast.makeText(this, "Sorted by Name", Toast.LENGTH_SHORT).show();
                recreate();


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
        {
            noteAdapter.stopListening();
        }
    }


    public  class NoteViewHolder extends RecyclerView.ViewHolder
    {

        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);

        }
    }

    private int getRandomColor()
    {

        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.green);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);


        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);

    }


}