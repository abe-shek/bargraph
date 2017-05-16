package io.yoky.graphtest;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import io.yoky.bargraph.BarGraph;
import io.yoky.bargraph.BarGraphTouchListener;

public class Main extends AppCompatActivity {

    LinearLayout whiteBarCon, yellowBarCon;
    int swidth, sheight;
    Random random;
    Handler handler;
    LinearLayout.LayoutParams lp;
    int totalBars;

    LinearLayout monthCon;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        swidth = size.x;
        sheight = size.y;
        random = new Random();
        handler = new Handler();
        initUI();
    }

    private void initUI() {
        whiteBarCon = (LinearLayout) findViewById(R.id.whiteBarCon_ll);
        yellowBarCon = (LinearLayout) findViewById(R.id.verticalBarCon_ll);
        monthCon = (LinearLayout) findViewById(R.id.monthCon);
        totalBars = 30;
        final BarGraph barGraph = new BarGraph(getApplicationContext(),yellowBarCon,whiteBarCon);
        ArrayList<Integer> dl = new ArrayList<>();
        for(int i=0;i<30;i++) dl.add((random.nextInt(24) + 1)*60);
        barGraph.addGraph(dl,G.dpToPx(36),G.dpToPx(36), new BarGraphTouchListener() {
            @Override
            public void onTouch(boolean isHovered, int childIndex) {
                Log.e("main", "onTouch: "+ isHovered + " " + childIndex);
            }
        },1000);
        addMonths();
        findViewById(R.id.reanimate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barGraph.animateGraph(1000);
            }
        });
    }

    private void addMonths() {
        monthCon.setWeightSum(12);
        for (int i = 0; i < 12; i++) {
            TextView child = new TextView(getApplicationContext());
            lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 10, 0);
            lp.gravity = Gravity.CENTER;
            lp.weight = 1f;
            child.setLayoutParams(lp);
            child.setPadding(G.dpToPx(4), G.dpToPx(4), G.dpToPx(4), G.dpToPx(4));
            child.setTextSize(16, TypedValue.COMPLEX_UNIT_SP);
            child.setText(getMonth(i));
            child.setTextColor(0xACFFFFFF);
            child.setId(random.nextInt(1000000000) + 1);
            monthCon.addView(child);
            monthCon.invalidate();
        }

    }

    private String getMonth(int i) {
        switch (i) {
            case 0:
                return "JAN";
            case 1:
                return "FEB";
            case 2:
                return "MAR";
            case 3:
                return "APR";
            case 4:
                return "MAY";
            case 5:
                return "JUN";
            case 6:
                return "JUL";
            case 7:
                return "AUG";
            case 8:
                return "SEP";
            case 9:
                return "OCT";
            case 10:
                return "NOV";
            case 11:
                return "DEC";
            default:
                return "";
        }
    }

    public void disableClipOnParents(View v, boolean disable) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(disable);
        }

        if (v.getParent() instanceof View) {
            disableClipOnParents((View) v.getParent(), disable);
        }
    }
}