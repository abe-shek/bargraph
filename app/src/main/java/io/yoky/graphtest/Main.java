package io.yoky.graphtest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.yoky.bargraph.BarGraph;
import io.yoky.bargraph.BarGraphTouchListener;

public class Main extends AppCompatActivity {

    final static public String TAG = "main";
    LinearLayout whiteBarCon, yellowBarCon;
    int swidth, sheight;
    Random random;
    Handler handler;
    LinearLayout.LayoutParams lp;
    int totalBars;

    RecyclerView monthCon;

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

    BarGraph barGraph;
    BarGraph.OnBarGraphAnimationListener barAnimationListener = new BarGraph.OnBarGraphAnimationListener() {
        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationEnd() {
            if(pendingSelectedPos==-1)return;
            MonthViewAdapter.MonthViewHolder vh = (MonthViewAdapter.MonthViewHolder) monthCon.findViewHolderForAdapterPosition(pendingSelectedPos);
            pendingSelectedPos = -1;
            if (vh != null)vh.clickView.callOnClick();
        }
    };

    private void initUI() {
        whiteBarCon = (LinearLayout) findViewById(R.id.whiteBarCon_ll);
        yellowBarCon = (LinearLayout) findViewById(R.id.verticalBarCon_ll);
        monthCon = (RecyclerView) findViewById(R.id.monthCon);
        totalBars = 30;
        barGraph = new BarGraph(getApplicationContext(), yellowBarCon, whiteBarCon);
        findViewById(R.id.reanimate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barGraph.animateGraph(1000);
            }
        });

        ArrayList<Integer> dl = new ArrayList<>();
        for (int i = 0; i < 30; i++) dl.add((random.nextInt(24) + 1) * 60);
        barGraph.addGraph(dl, G.dpToPx(36), G.dpToPx(36), new BarGraphTouchListener() {
            @Override
            public void onTouch(boolean isHovered, int childIndex) {
                Log.e("main", "onTouch: " + isHovered + " " + childIndex);
            }
        }, 1000, barAnimationListener);
        addMonths();
    }

    ArrayList<String> monthData = new ArrayList<>();

    private void addMonths() {
        for (int i = 0; i < 12; i++) monthData.add(getMonth(i));
        MonthViewAdapter monthAdapter = new MonthViewAdapter(getApplicationContext(), monthData);
        monthCon.setAdapter(monthAdapter);
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


    void setSelectedFalse() {
        if (selectedPos == -1) return;
        final MonthViewAdapter.MonthViewHolder vh = (MonthViewAdapter.MonthViewHolder) monthCon.findViewHolderForAdapterPosition(selectedPos);
        if (vh != null) {
            vh.isSelected = false;
            ObjectAnimator oa = ObjectAnimator.ofFloat(vh.highlighter, "translationY", -G.dpToPx(5));
            ObjectAnimator sa = ObjectAnimator.ofFloat(vh.highlighter, "alpha", 1, 0);
            ObjectAnimator ta = ObjectAnimator.ofFloat(vh.monthName, "translationY", 0);
            AnimatorSet set = new AnimatorSet();
            set.play(oa).with(sa).with(ta);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    vh.highlighter.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            set.setDuration(150);
            set.start();
        }
    }

    boolean checkIfSelected(MonthViewAdapter.MonthViewHolder recycledHolder) {
        if (selectedPos == -1) return false;
        String value = recycledHolder.monthName.getText().toString();
        return monthData.get(selectedPos).equals(value);
    }

    int selectedPos = -1, pendingSelectedPos = -1;

    class MonthViewAdapter extends RecyclerView.Adapter<MonthViewAdapter.MonthViewHolder> {
        Context mContext;
        ArrayList<String> monthList;


        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MonthViewHolder(LayoutInflater.from(mContext).inflate(R.layout.month_child_view, parent, false));
        }

        @Override
        public void onBindViewHolder(final MonthViewHolder holder, final int position) {
            final String value = monthList.get(position);
            if (value != null && value.length() > 0) {
                if(selectedPos==-1){
                    int month = Integer.parseInt(new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date()).split("/")[1]);
                    selectedPos = month-1;
                }
                holder.monthName.setText(value);
                holder.highlighter.setVisibility(selectedPos == position ? View.VISIBLE : View.INVISIBLE);
                holder.monthName.setTranslationY(selectedPos == position ? -G.dpToPx(10) : 0);
                holder.clickView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.isSelected || barGraph.isAnimating()) {
                            if(!holder.isSelected)pendingSelectedPos = position;
                            return;
                        }
                        setSelectedFalse();
                        barGraph.animateGraph(500);
                        selectedPos = position;
                        holder.isSelected = true;
                        ObjectAnimator oa = ObjectAnimator.ofFloat(holder.highlighter, "translationY", -G.dpToPx(5), 0);
                        ObjectAnimator sa = ObjectAnimator.ofFloat(holder.highlighter, "alpha", 0, 1);
                        ObjectAnimator ta = ObjectAnimator.ofFloat(holder.monthName, "translationY", -G.dpToPx(10));
                        AnimatorSet set = new AnimatorSet();
                        set.play(oa).with(sa).with(ta);
                        set.setInterpolator(new AccelerateDecelerateInterpolator());
                        set.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                holder.highlighter.setAlpha(0);
                                holder.highlighter.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        set.setDuration(150);
                        set.start();
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(MonthViewHolder holder) {
            if (!checkIfSelected(holder)) {
                holder.monthName.setTranslationY(0);
                holder.highlighter.setVisibility(View.INVISIBLE);
                holder.isSelected = false;
            }
            super.onViewAttachedToWindow(holder);
        }

        MonthViewAdapter(Context context, ArrayList<String> monthList) {
            this.mContext = context;
            this.monthList = monthList;
        }


        @Override
        public int getItemCount() {
            return monthList != null ? monthList.size() : 0;
        }

        class MonthViewHolder extends RecyclerView.ViewHolder {
            YTextView monthName;
            View highlighter, clickView;
            boolean isSelected = false;

            MonthViewHolder(View itemView) {
                super(itemView);
                monthName = (YTextView) itemView.findViewById(R.id.monthName);
                highlighter = itemView.findViewById(R.id.highlighter);
                clickView = itemView.getRootView();
            }

        }

    }
}