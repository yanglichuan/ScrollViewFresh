package com.example.administrator.animfreshview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnimFreshView extends RelativeLayout {
    public AnimFreshView(Context context) {
        super(context);
    }

    private View freshView;

    public AnimFreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimFreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int getPx(int idp) {
        DisplayMetrics ccc = new DisplayMetrics();
        ((Activity) getContext())
                .getWindowManager().getDefaultDisplay().getMetrics(ccc);
        return (int) (ccc.density * (float) idp + 0.10f);
    }

    private ViewGroup menu;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        menu = (ViewGroup) findViewById(R.id.menu);
        mainchild = findViewById(R.id.mainchild);

        freshView = menu.findViewById(R.id.freshView);
        tv_tip = (TextView) menu.findViewById(R.id.tv_tip);
    }

    private final int FRESH_BEFORE = 1;
    private final int FRESH_OVER = 2;
    private final int FRESH_ING = 3;
    private int fresh_flag = FRESH_BEFORE;


    public void setFreshBefore() {
        tv_tip.setText("下拉刷新");
    }

    public void setFreshOver() {
        tv_tip.setText("释放刷新");
    }

    public void setFreshing() {
        tv_tip.setText("正在刷新");
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int menuH = menu.getMeasuredHeight();

        menu.layout(l, t - menuH, r, t);
    }

    int iRecordY = 0;

    private boolean isTouchPointInView(View view, float x, float y) {
        int[] locations = new int[2];

        view.getLocationInWindow(locations);

        int left = locations[0];
        int top = locations[1];
        int right = locations[0] + view.getMeasuredWidth();
        int bottom = locations[1] + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    private boolean bOpen = false;
    private boolean bSlideOk = false;


    private boolean bOverFreshLine = false;//超过了刷新线了
    private TextView tv_tip;

    private View mainchild;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:


                break;
            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    int tempy = 0;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                iRecordY = (int) ev.getY();
                tempy = (int) ev.getY();


                break;

            case MotionEvent.ACTION_MOVE:

                int movey = (int) ev.getY();
                int deltay = movey - tempy;

                int scrollY = getScrollY();
                if (scrollY == 0 && deltay > 10 && mainchild.getScrollY() == 0) {
                    return true;
                }
                tempy = movey;
                break;

            case MotionEvent.ACTION_UP:

                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                iRecordY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                int moveY = (int) event.getY();
                int deltaY = moveY - iRecordY;
                iRecordY = moveY;

                if (fresh_flag == FRESH_ING) {
                    return false;
                }
                scrollBy(0, -deltaY);


                int currentScrollY = (int) getScrollY();
                if (currentScrollY <= -menu.getMeasuredHeight()) {
                    scrollTo(0, -menu.getMeasuredHeight());
                } else if (currentScrollY >= 0) {
                    scrollTo(0, 0);
                }

                //
                if (currentScrollY < -freshView.getMeasuredHeight()) {
                    bOverFreshLine = true;
                    setFreshOver();
                } else {
                    bOverFreshLine = false;
                    setFreshBefore();
                }

                break;

            case MotionEvent.ACTION_UP:
                if (fresh_flag == FRESH_ING) {
                    return false;
                }
                if (bOverFreshLine) {
                    doFreshing();

                    mHander.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                        }
                    }, 2000);
                } else {
                    reset();
                }

                break;
        }
        return true;
    }


    private void reset() {
        int currentScrollY = (int) getScrollY();
        bOverFreshLine = false;
        fresh_flag = FRESH_BEFORE;
        setFreshBefore();
        createValueAnimator(currentScrollY, 0);
    }

    private void doFreshing() {
        int currentScrollY = (int) getScrollY();
        bOverFreshLine = false;
        createValueAnimator(currentScrollY, -freshView.getMeasuredHeight());
        fresh_flag = FRESH_ING;
        setFreshing();
    }


    private Handler mHander = new Handler();


    private int getMinY() {
        return AnimFreshView.this.getRight() - menu.getMeasuredWidth();
    }

    private int getMaxY() {
        return AnimFreshView.this.getRight();
    }

    private int getOpenY() {
        return AnimFreshView.this.getRight() - menu.getMeasuredWidth() / 10;
    }

    private int getCloseY() {
        return AnimFreshView.this.getRight() - menu.getMeasuredWidth() + menu.getMeasuredWidth() / 10;
    }


    private void createValueAnimator(int from, int to) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
//        valueAnimator.setDuration(xx*300);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo(0, (Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

//    @Override
//    public void computeScroll() {
//        super.computeScroll();
//        if(mScroller.computeScrollOffset()){
//            menu.scrollTo(0,mScroller.getCurrY());
//            menu.invalidate();
//        }
//    }
}
