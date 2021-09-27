package com.example.gwatracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class AddSubjectActivity extends AppCompatActivity {

    private BlurView blurView;
    private Button btnAdd;
    private TextInputEditText txtTo, txtTerm, txtSubject, txtUnits, txtGrades;
    private RelativeLayout blurLayout;
    private int shortAnimationDuration;
    private CardView term, grade, review;
    private TextView rSY, rTerm, rSubject, rGrades, rUnit;
    private AutoCompleteTextView txtFrom;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        db = new DatabaseHelper(this);
        blurView = findViewById(R.id.blur);
        btnAdd = findViewById(R.id.btnAdd);
        txtFrom = findViewById(R.id.txtSYFrom);
        txtTo = findViewById(R.id.txtSYTo);
        txtTerm = findViewById(R.id.txtTerm);
        txtSubject = findViewById(R.id.txtSubject);
        txtUnits = findViewById(R.id.txtUnits);
        txtGrades = findViewById(R.id.txtGrades);
        blurLayout = findViewById(R.id.blurLayout);
        term = findViewById(R.id.termSelectorLayout);
        grade = findViewById(R.id.gradesSelector);
        review = findViewById(R.id.reviewDetails);
        rSY = findViewById(R.id.reviewSY);
        rTerm = findViewById(R.id.reviewTerm);
        rSubject = findViewById(R.id.reviewSubject);
        rGrades = findViewById(R.id.reviewGrades);
        rUnit = findViewById(R.id.reviewUnits);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        blurBG();
        addYear();
        gradesUnitsValidation();
        addSubject();
        populateFromYear();
    }

    private void populateFromYear() {
        List<String> yearList = db.showYear();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, yearList);
        txtFrom.setAdapter(adapter);
    }

    private void addSubject() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtFrom.getText().length() != 0 && txtTo.getText().length() != 0 &&
                        txtTerm.getText().length() != 0 && txtSubject.getText().length() != 0&&
                        txtGrades.getText().length() != 0 && txtUnits.getText().length() != 0){
                    int yearNow = Calendar.getInstance().get(Calendar.YEAR);
                    int fromYear = Integer.parseInt(txtFrom.getText().toString());

                    if (fromYear <= yearNow){
                        String year1 = txtFrom.getText().toString();
                        String year2 = txtTo.getText().toString();

                        rSY.setText(year1 + "-" + year2);
                        rTerm.setText(txtTerm.getText().toString());
                        rSubject.setText(txtSubject.getText().toString());
                        rGrades.setText(txtGrades.getText().toString());
                        rUnit.setText(txtUnits.getText().toString());

                        showBlur();
                        review.setVisibility(View.VISIBLE);
                    } else {
                        boolean isValid = false; //actually di ko to gets. HAHAHAHA pero needed to para maipakita na may error dun sa edittext
                        if (isValid){
                            txtFrom.setError(null);

                        } else {
                            txtFrom.setError("Please enter a valid year!");
                            txtTo.setError("Please enter a valid year!");
                        }
                    }
                } else {
                    boolean isValid = false; //actually di ko to gets. HAHAHAHA pero needed to para maipakita na may error dun sa edittext
                    if (isValid){
                        txtFrom.setError(null);
                        txtTo.setError(null);
                        txtTerm.setError(null);
                        txtSubject.setError(null);
                        txtGrades.setError(null);
                        txtUnits.setError(null);

                    } else {
                        txtFrom.setError("Please fill in this field!");
                        txtTo.setError("Please fill in this field!");
                        txtTerm.setError("Please fill in this field!");
                        txtSubject.setError("Please fill in this field!");
                        txtGrades.setError("Please fill in this field!");
                        txtUnits.setError("Please fill in this field!");
                    }
                }
            }
        });
    }

    public void insertToDatabase(View v) {
        String year1 = txtFrom.getText().toString();
        String year2 = txtTo.getText().toString();
        String sy = year1.substring(2) + "-" + year2.substring(2);
        String gradesValue = txtGrades.getText().toString();
        int year = Integer.parseInt(year1);

        double grades;
        double units = Double.parseDouble(txtUnits.getText().toString());
        String sy_term = sy + " " + txtTerm.getText().toString();
        String subject = txtSubject.getText().toString();

        if (gradesValue.equals("INC")){
            grades = 0;
        } else {
            grades = Double.parseDouble(gradesValue);
        }

        add(sy_term, year, subject, grades, units);

        txtFrom.setText("");
        txtTo.setText("");
        txtTerm.setText("");
        txtSubject.setText("");
        txtGrades.setText("");
        txtUnits.setText("");

        txtFrom.setError(null);
        txtTo.setError(null);
        txtTerm.setError(null);
        txtSubject.setError(null);
        txtGrades.setError(null);
        txtUnits.setError(null);

        populateFromYear();

        review.setVisibility(View.GONE);
        hideBlur();
    }

    public void gradesUnitsValidation() {
        txtUnits.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 1){
                    txtUnits.append(".00");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txtUnits.getWindowToken(), 0);
                }
            }
        });

        txtFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txtFrom.getText().toString().trim().length() == 4 ){
                    txtFrom.clearFocus();
                    txtSubject.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() == 4){
                    txtTo.setError(null);
                }
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

    public void showDialogBox(View v){
        switch (v.getId()){
            case R.id.button:
                showBlur();
                term.setVisibility(View.VISIBLE);
                break;
            case R.id.btnGrades:
                showBlur();
                grade.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void hideDialogBox(View v){
        switch (v.getId()){
            case R.id.add_CancelBTN:
                term.setVisibility(View.GONE);
                hideBlur();
                break;
            case R.id.add_CancelBTN2:
                grade.setVisibility(View.GONE);
                hideBlur();
                break;
            case R.id.cancel_add:
                review.setVisibility(View.GONE);
                hideBlur();
                break;
        }
    }

    public void showBlur(){
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

        txtFrom.setEnabled(false);
        txtSubject.setEnabled(false);
        txtUnits.setEnabled(false);
        btnAdd.setEnabled(false);

    }

    public void hideBlur(){
        txtFrom.setEnabled(true);
        txtSubject.setEnabled(true);
        txtUnits.setEnabled(true);
        btnAdd.setEnabled(true);

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

    public void close(View v){
        Intent i = new Intent(AddSubjectActivity.this, MainActivity.class);
        startActivity(i);

        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
        finish();
    }

    public void addYear(){
        txtFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 4){
                    int nextYear = Integer.parseInt(editable.toString()) + 1;
                    txtTo.setText(String.valueOf(nextYear));
                }

            }
        });
    }

    public void onSelectedItems(View v){
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()){
            case R.id.firstTerm:
                if(checked)
                    txtTerm.setText("1st Term");
                break;
            case R.id.secondTerm:
                if(checked)
                    txtTerm.setText("2nd Term");
                    break;
            case R.id.summerTerm:
                if(checked)
                    txtTerm.setText("Summer Term");
                    break;
        }
        txtTerm.setError(null);
        term.setVisibility(View.GONE);
        hideBlur();
    }

    public void selectedGrade(View v){
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()){
            case R.id.g1:
                if(checked)
                    txtGrades.setText("1.00");
                break;
            case R.id.g2:
                if(checked)
                    txtGrades.setText("1.25");
                break;
            case R.id.g3:
                if(checked)
                    txtGrades.setText("1.50");
                break;
            case R.id.g4:
                if(checked)
                    txtGrades.setText("1.75");
                break;
            case R.id.g5:
                if(checked)
                    txtGrades.setText("2.00");
                break;
            case R.id.g6:
                if(checked)
                    txtGrades.setText("2.25");
                break;
            case R.id.g7:
                if(checked)
                    txtGrades.setText("2.50");
                break;
            case R.id.g8:
                if(checked)
                    txtGrades.setText("2.75");
                break;
            case R.id.g9:
                if(checked)
                    txtGrades.setText("3.00");
                break;
            case R.id.g10:
                if(checked)
                    txtGrades.setText("5.00");
                break;
            case R.id.g11:
                if(checked)
                    txtGrades.setText("INC");
                break;
        }
        txtGrades.setError(null);
        grade.setVisibility(View.GONE);
        hideBlur();
    }

    public void toast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void add(String sy, int year, String subject, double grades, double units){
        boolean add = db.addSucject(sy, year, subject, grades, units);
        if(add){
            toast("Subject Added");
        } else {
            toast("Failed!");
        }
    }

    @Override
    public void onBackPressed(){

    }


}
