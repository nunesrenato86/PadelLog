package com.renatonunes.padellog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.renatonunes.padellog.domain.Championship;
import com.renatonunes.padellog.domain.ChampionshipSummary;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartActivity extends CommonActivity implements OnChartValueSelectedListener,
        DatePickerDialog.OnDateSetListener,
        DialogInterface.OnCancelListener{

//    ArrayList<Championship> championships = new ArrayList<Championship>();

    @BindView(R.id.chart)
    PieChart mChart;

    @BindView(R.id.edt_chart_initial_date)
    EditText edtInitialDate;

    @BindView(R.id.edt_chart_final_date)
    EditText edtFinalDate;

    ChampionshipSummary mChampionshipSummary;

    //to handle dates
    private int year, month, day;
    private Long mInitialDate = Long.valueOf(0);
    private Long mFinalDate = Long.valueOf(0);

    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_chart);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                populateChart();
                getChampionshipSummary();
            }
        });

        edtInitialDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(true);
            }
        });

        edtFinalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(false);
            }
        });

        initViews();
    }

    private void setData() {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        if (mChampionshipSummary.getNoResult() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getNoResult(), getResources().getString(R.string.result_name_none)));
        }

        if (mChampionshipSummary.getDraw() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getDraw(), getResources().getString(R.string.round_draw)));
        }

        if (mChampionshipSummary.getRound64() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getRound64(), getResources().getString(R.string.round_64)));
        }

        if (mChampionshipSummary.getRound32() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getRound32(), getResources().getString(R.string.round_32)));
        }

        if (mChampionshipSummary.getRound16() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getRound16(), getResources().getString(R.string.round_16)));
        }

        if (mChampionshipSummary.getRound8() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getRound8(), getResources().getString(R.string.round_8)));
        }

        if (mChampionshipSummary.getQuarterfinals() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getQuarterfinals(), getResources().getString(R.string.round_4)));
        }

        if (mChampionshipSummary.getSemifinals() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getSemifinals(), getResources().getString(R.string.round_semi)));
        }

        if (mChampionshipSummary.getVice() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getVice(), getResources().getString(R.string.result_name_vice)));
        }

        if (mChampionshipSummary.getChampion() > 0){
            entries.add(new PieEntry(mChampionshipSummary.getChampion(), getResources().getString(R.string.result_name_champion)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        //data.setValueTextColor(Color.WHITE);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Por resultado");//\ndeveloped by Philipp Jahoda");
//        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
//        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
//        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
//        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
//        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

        Log.i("PieChart", "nothing selected");
    }

    private void populateChart(){
        if (mChampionshipSummary != null) {

            mChart.setUsePercentValues(true);
            mChart.setDescription("");
            mChart.setExtraOffsets(5, 10, 5, 5);

            mChart.setDragDecelerationFrictionCoef(0.95f);

            mChart.setCenterTextTypeface(mTfLight);
            mChart.setCenterText(generateCenterSpannableText());

            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColor(Color.WHITE);

            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);

            mChart.setHoleRadius(58f);
            mChart.setTransparentCircleRadius(61f);

            mChart.setDrawCenterText(true);

            mChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            mChart.setRotationEnabled(true);
            mChart.setHighlightPerTapEnabled(true);

            // mChart.setUnit(" €");
            // mChart.setDrawUnitsInChart(true);

            // add a selection listener
            mChart.setOnChartValueSelectedListener(this);

            setData();

            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
// mChart.spin(2000, 0, 360);

            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // entry label styling
            //mChart.setEntryLabelColor(Color.WHITE);
            mChart.setEntryLabelColor(Color.BLACK);
            mChart.setEntryLabelTypeface(mTfRegular);
            mChart.setEntryLabelTextSize(12f);
        }
    }

    public void getChampionshipSummary(){
        //championships.clear();
        mChampionshipSummary = new ChampionshipSummary();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        int count = 0;

        //FirebaseDatabase.getInstance().getReference().child("championships").child(userId).addValueEventListener(new ValueEventListener()
        // {
        FirebaseDatabase.getInstance().getReference().child("championships").child(userId).orderByChild("finalDate").startAt(mInitialDate).endAt(mFinalDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getUpdates(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        FirebaseDatabase.getInstance().getReference().child("championships").child(userId).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                getUpdates(dataSnapshot);
//            }
//
//            @Override
//            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//                getUpdates(dataSnapshot);
////                closeProgressBar();
//            }
//
//            @Override
//            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getUpdates(com.google.firebase.database.DataSnapshot dataSnapshot){
//        Championship championship = new Championship();
//        championship.setId(dataSnapshot.getKey());
//        championship.setName(dataSnapshot.getValue(Championship.class).getName());
//
//        String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        championship.setOwner(owner);
//        championship.setPartner(dataSnapshot.getValue(Championship.class).getPartner());
//        championship.setPlace(dataSnapshot.getValue(Championship.class).getPlace());
//        championship.setResult(dataSnapshot.getValue(Championship.class).getResult());
//        championship.setImageStr(dataSnapshot.getValue(Championship.class).getImageStr());
//        championship.setLat(dataSnapshot.getValue(Championship.class).getLat());
//        championship.setLng(dataSnapshot.getValue(Championship.class).getLng());
//        championship.setInitialDate(dataSnapshot.getValue(Championship.class).getInitialDate());
//        championship.setFinalDate(dataSnapshot.getValue(Championship.class).getFinalDate());
//        championship.setCategory(dataSnapshot.getValue(Championship.class).getCategory());
//        championship.setContext(this);
//
//        championships.add(championship);


        for (com.google.firebase.database.DataSnapshot ds: dataSnapshot.getChildren()){
            mChampionshipSummary.addResult(ds.getValue(Championship.class).getResult());
        }

        populateChart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else  //else if (id == R.id.action_deletar_todos){
            return super.onOptionsItemSelected(item);
    }

    private void setDate(Boolean initial){
        initDate(initial);

        Calendar cDefault = Calendar.getInstance();
        cDefault.set(year, month, day);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                cDefault.get(Calendar.YEAR),
                cDefault.get(Calendar.MONTH),
                cDefault.get(Calendar.DAY_OF_MONTH)
        );

        String tag = (initial ? "initial" : "final");

        datePickerDialog.setOnCancelListener(this);
        datePickerDialog.show( getFragmentManager(), tag );
    }

    private void initDate(Boolean initial){
        Long date = Long.valueOf(0);
        Calendar c = Calendar.getInstance();
        EditText edtDate;

        if (initial){
            date = mInitialDate;
        }else{
            //date = - 1 * mFinalDate;
            date = mFinalDate;
        }

        if (date != 0){ //already have a date
            c.setTimeInMillis(date);

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }else{ //without any date
            year = c.get(Calendar.YEAR);

            if (initial){
                month = 0;
                day = 1;

                Calendar cInitial = Calendar.getInstance();
                c.set(year, month, day);
                mInitialDate = c.getTimeInMillis();
                edtDate = edtInitialDate;
            }else{
                month = 11;
                day = 31;

                Calendar cInitial = Calendar.getInstance();
                c.set(year, month, day);
                mFinalDate = c.getTimeInMillis();
                edtDate = edtFinalDate;
            }

            edtDate.setText( (day < 10 ? "0" + day : day) + "/" +
                    (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/" +
                    year);
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        //year = month = day = 0;
        //edtInitialDate.setText("");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        EditText editText;

        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);

        if (view.getTag() == "initial"){
            editText = edtInitialDate;
            mInitialDate = c.getTimeInMillis();
        }
        else {
            //mFinalDate = - 1 * c.getTimeInMillis();
            mFinalDate = c.getTimeInMillis();
            editText = edtFinalDate;
        }

        editText.setText( (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" +
                (monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "/" +
                year);

        editText.setError(null);
    }

    private void initViews(){
        initDate(true);
        initDate(false);
    }
}
