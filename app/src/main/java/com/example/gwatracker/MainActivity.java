package com.example.gwatracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class MainActivity extends AppCompatActivity {

    private BlurView blurView;
    private RelativeLayout blurLayout;
    private FloatingActionButton addSubject;
    private TextView totalUnits, gwaTerm, gwaAccu, termTitle, cancel, unitsTerm;
    private CardView termSelector;
    private int shortAnimationDuration;
    private ListView termList;

    DatabaseHelper db;

    private RecyclerView subjectView;
    private  RecyclerView.Adapter subject_details;
    private RecyclerView.LayoutManager layoutManager;
    Subject subjectAdapterList;

    private int rowCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        rowCount = db.getRowCount();

        subjectAdapterList = new Subject();
        subjectView = findViewById(R.id.subjectList);

        unitsTerm = findViewById(R.id.overallUnits2);
        termList = findViewById(R.id.termList);
        blurView = findViewById(R.id.blur);
        blurLayout = findViewById(R.id.blurLayout);
        addSubject = findViewById(R.id.addSubject);
        totalUnits = findViewById(R.id.overallUnits);
        gwaTerm = findViewById(R.id.gwaTerm);
        gwaAccu = findViewById(R.id.acuGWA);
        termTitle = findViewById(R.id.termTitle);
        termSelector = findViewById(R.id.termSelector);
        cancel = findViewById(R.id.cancelBtn);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);


        blurBG();
        termSelect();
        populateTermList();
        String term = "";
        if(rowCount == 0){
            term = "Please add Subjects";
        } else {
            term = "S.Y. " + db.getLastRow();
        }

        termTitle.setText(term);
        populateSubjectList(term);

        totalUnits.setText(db.overallUnits() + "0");
        unitsTerm.setText(db.termUnits(db.getLastRow()) + "0");
        gwaAccu.setText(String.valueOf(gwa(db.getAccumulatedGWA())));
        gwaTerm.setText(String.valueOf(gwa(db.getTermGwa(db.getLastRow()))));
    }

    public void populateSubjectList(String term){
        String termTrim = term.substring(5);

        layoutManager = new LinearLayoutManager(this);
        subject_details = new Adapter(this, db.getSubjects(termTrim));
        subjectView.setLayoutManager(layoutManager);
        subjectView.setAdapter(subject_details);
    }

    private void populateTermList() {
        Cursor data = db.getSYTerm();

        ArrayList<String> listData = new ArrayList<>();

        while (data.moveToNext()){
            listData.add(data.getString(0));
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        termList.setAdapter(adapter);

        termList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String termSy = "S.Y. " + adapterView.getItemAtPosition(i).toString();

                termTitle.setText(termSy);
                hideSelector();
                populateSubjectList(termSy);
                unitsTerm.setText(db.termUnits(adapterView.getItemAtPosition(i).toString()) + "0");
                gwaTerm.setText(String.valueOf(gwa(db.getTermGwa(adapterView.getItemAtPosition(i).toString()))));
            }
        });
    }

    private void blurBG() {
        float radius = 8f;

        View decorView = getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

    private void termSelect(){
        termSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the content view to 0% opacity but visible, so that it is visible
                // (but fully transparent) during the animation.
                blurLayout.setAlpha(0f);
                blurLayout.setVisibility(View.VISIBLE);

                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
                blurLayout.animate()
                        .alpha(1f)
                        .setDuration(shortAnimationDuration)
                        .setListener(null);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSelector();
            }
        });

    }

    public void hideSelector(){
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        blurLayout.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        blurLayout.setVisibility(View.GONE);
                    }
                });
    }

    public void addSubject(View v){
        Intent i = new Intent(MainActivity.this, AddSubjectActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

    }

    boolean doubleBackToExit = false;

    @Override
    public void onBackPressed(){
        if(doubleBackToExit){
            super.onBackPressed();
            return;
        }

        this.doubleBackToExit = true;
        Toast.makeText(this, "Please click again to Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExit = false;
            }
        }, 2000);
    }

    public void toast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public double gwa(double d){
        DecimalFormat three = new DecimalFormat("#.###");
        return Double.valueOf(three.format(d));
    }

}
