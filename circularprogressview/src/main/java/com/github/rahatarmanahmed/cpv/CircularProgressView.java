package com.github.rahatarmanahmed.cpv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;


/**
 * TODO: document your custom view class.
 */
public class CircularProgressView extends View {

    private static final float INDETERMINANT_MIN_SWEEP = 15f;
    
    private Paint paint;
    private int size = 0;
    private RectF bounds;

    private boolean isIndeterminate, autostartAnimation, shouldSpin;
    private float currentProgress, maxProgress, indeterminateSweep, indeterminateRotateOffset;
    private int thickness, color, animDuration, animSteps;
    private float startAngle;
    private TypedArray typedArray;


    public CircularProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    protected void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircularProgressView, defStyle, 0);


        typedArray.recycle();

        initAttributes(attrs, defStyle);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        if(autostartAnimation)
            startAnimation();
    }

    private void initAttributes(AttributeSet attrs, int defStyle)
    {
        // Initialize attributes from styleable attributes
        currentProgress = typedArray.getFloat(R.styleable.CircularProgressView_cpv_progress, 0f);
        maxProgress = typedArray.getFloat(R.styleable.CircularProgressView_cpv_maxProgress, 100f);
        thickness = typedArray.getDimensionPixelSize(R.styleable.CircularProgressView_cpv_thickness, 4);
        isIndeterminate = typedArray.getBoolean(R.styleable.CircularProgressView_cpv_indeterminate, false);
        autostartAnimation = typedArray.getBoolean(R.styleable.CircularProgressView_cpv_animAutostart, true);
        shouldSpin = typedArray.getBoolean(R.styleable.CircularProgressView_cpv_shouldSpin, true);
        color = typedArray.getColor(R.styleable.CircularProgressView_cpv_color, getResources().getColor(R.color.material_blue_500));
        animDuration = typedArray.getInteger(R.styleable.CircularProgressView_cpv_animDuration, 4000);
        animSteps = typedArray.getInteger(R.styleable.CircularProgressView_cpv_animSteps, 3);
        startAngle = typedArray.getFloat(R.styleable.CircularProgressView_cpv_startAngle, -90f);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int xPad = getPaddingLeft() + getPaddingRight();
        int yPad = getPaddingTop() + getPaddingBottom();
        int width = getMeasuredWidth() - xPad;
        int height = getMeasuredHeight() - yPad;
        size = (width < height) ? width : height;
        setMeasuredDimension(size + xPad, size + yPad);
        updateBounds();
    }

    private void updateBounds()
    {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        bounds = new RectF(paddingLeft+thickness/2, paddingTop+thickness/2, size+paddingLeft-thickness/2, size+paddingTop-thickness/2);
    }
    
    private void updatePaint()
    {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        paint.setStrokeCap(Paint.Cap.BUTT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Set up the paint
        updatePaint();

        // Draw the arc
        float sweepAngle = (isInEditMode()) ? currentProgress/maxProgress*360 : actualProgress/maxProgress*360;
        if(!isIndeterminate)
            canvas.drawArc(bounds, startAngle, sweepAngle, false, paint);
        else
            canvas.drawArc(bounds, startAngle + indeterminateRotateOffset, indeterminateSweep, false, paint);
    }

    /**
     *
     * @return true if this view is in indeterminate mode.
     */
    public boolean isIndeterminate() {
        return isIndeterminate;
    }

    /**
     * Sets whether this CircularProgressView is indeterminate or not.
     * It will reset the animation if the mode has changed.
     * @param isIndeterminate
     */
    public void setIndeterminate(boolean isIndeterminate) {
        boolean reset = this.isIndeterminate == isIndeterminate;
        this.isIndeterminate = isIndeterminate;
        if(reset)
            resetAnimation();
    }

    /**
     *
     * @return the thickness of the progress bar arc
     */
    public int getThickness() {
        return thickness;
    }

    /**
     * Sets the thickness of the progress bar arc.
     * @param thickness the thickness of the progress bar arc
     */
    public void setThickness(int thickness) {
        this.thickness = thickness;
        updateBounds();
        invalidate();
    }

    /**
     *
     * @return the color of the progress bar
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the color of the progress bar.
     * @param color the color of the progress bar
     */
    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    /**
     * Gets the progress value considered to be 100% of the progress bar.
     * @return the maximum progress
     */
    public float getMaxProgress() {
        return maxProgress;
    }

    /**
     * Sets the progress value considered to be 100% of the progress bar.
     * @param maxProgress the maximum progress
     */
    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    /**
     *
     * @return current progress
     */
    public float getProgress() {
        return currentProgress;
    }

    /**
     * Sets the progress of the progress bar.
     * @param currentProgress
     */
    public void setProgress(float currentProgress) {
        this.currentProgress = currentProgress;
        // Reset the determinate animation to approach the new currentProgress
        if(!isIndeterminate)
        {
            if(progressAnimator != null && progressAnimator.isRunning())
                progressAnimator.cancel();
            progressAnimator = ValueAnimator.ofFloat(actualProgress, currentProgress);
            progressAnimator.setDuration(500);
            progressAnimator.setInterpolator(new LinearInterpolator());
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    actualProgress = (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            progressAnimator.start();
        }
        invalidate();
    }

    // Animation related stuff
    private float actualProgress;
    private ValueAnimator startAngleRotate;
    private ValueAnimator progressAnimator;
    private AnimatorSet indeterminateAnimator;

    /**
     * Starts the progress bar animation.
     * (This is an alias of resetAnimation() so it does the same thing.)
     */
    public void startAnimation() {
        resetAnimation();
    }

    /**
     * Resets the animation.
     */
    public void resetAnimation() {
        // Cancel all the old animators
        if(startAngleRotate != null && startAngleRotate.isRunning())
            startAngleRotate.cancel();
        if(progressAnimator != null && progressAnimator.isRunning())
            progressAnimator.cancel();
        if(indeterminateAnimator != null && indeterminateAnimator.isRunning())
            indeterminateAnimator.cancel();

        // Determinate animation
        if(!isIndeterminate)
        {
            // The cool 360 swoop animation at the start of the animation
            shouldSpin = false;
            startAngle = typedArray.getFloat(R.styleable.CircularProgressView_cpv_startAngle, -90f);
            if (shouldSpin) {
                startAngleRotate = ValueAnimator.ofFloat(-90f, 270f);
                startAngleRotate.setDuration(5000);
                startAngleRotate.setInterpolator(new DecelerateInterpolator(2));
                startAngleRotate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        startAngle = (Float) animation.getAnimatedValue();
                        invalidate();
                    }
                });
                startAngleRotate.start();
            }

            // The linear animation shown when progress is updated
            actualProgress = 0f;
            progressAnimator = ValueAnimator.ofFloat(actualProgress, currentProgress);
            progressAnimator.setDuration(500);
            progressAnimator.setInterpolator(new LinearInterpolator());
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    actualProgress = (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            progressAnimator.start();
        }
        // Indeterminate animation
        else
        {
            startAngle = -90f;
            indeterminateSweep = INDETERMINANT_MIN_SWEEP;
            // Build the whole AnimatorSet
            indeterminateAnimator = new AnimatorSet();
            AnimatorSet prevSet = null, nextSet;
            for(int k=0;k<animSteps;k++)
            {
                nextSet = createIndeterminateAnimator(k);
                AnimatorSet.Builder builder = indeterminateAnimator.play(nextSet);
                if(prevSet != null)
                    builder.after(prevSet);
                prevSet = nextSet;
            }

            // Listen to end of animation so we can infinitely loop
            indeterminateAnimator.addListener(new AnimatorListenerAdapter() {
                boolean wasCancelled = false;
                @Override
                public void onAnimationCancel(Animator animation) {
                    wasCancelled = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(!wasCancelled)
                        resetAnimation();
                }
            });
            indeterminateAnimator.start();
        }


    }

    // Creates the animators for one step of the animation
    private AnimatorSet createIndeterminateAnimator(float step)
    {
        final float maxSweep = 360f*(animSteps-1)/animSteps + INDETERMINANT_MIN_SWEEP;
        final float start = -90f + step*(maxSweep-INDETERMINANT_MIN_SWEEP);

        // Extending the front of the arc
        ValueAnimator frontEndExtend = ValueAnimator.ofFloat(INDETERMINANT_MIN_SWEEP, maxSweep);
        frontEndExtend.setDuration(animDuration/animSteps/2);
        frontEndExtend.setInterpolator(new DecelerateInterpolator(1));
        frontEndExtend.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateSweep = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        // Overall rotation
        ValueAnimator rotateAnimator1 = ValueAnimator.ofFloat(step*720f/animSteps, (step+.5f)*720f/animSteps);
        rotateAnimator1.setDuration(animDuration/animSteps/2);
        rotateAnimator1.setInterpolator(new LinearInterpolator());
        rotateAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
            indeterminateRotateOffset = (Float) animation.getAnimatedValue();
            }
        });

        // Followed by...

        // Retracting the back end of the arc
        ValueAnimator backEndRetract = ValueAnimator.ofFloat(start, start+maxSweep-INDETERMINANT_MIN_SWEEP);
        backEndRetract.setDuration(animDuration/animSteps/2);
        backEndRetract.setInterpolator(new DecelerateInterpolator(1));
        backEndRetract.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
            startAngle = (Float) animation.getAnimatedValue();
            indeterminateSweep = maxSweep - startAngle + start;
            invalidate();
            }
        });

        // More overall rotation
        ValueAnimator rotateAnimator2 = ValueAnimator.ofFloat((step+.5f)*720f/animSteps, (step+1)*720f/animSteps);
        rotateAnimator2.setDuration(animDuration/animSteps/2);
        rotateAnimator2.setInterpolator(new LinearInterpolator());
        rotateAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
            indeterminateRotateOffset = (Float) animation.getAnimatedValue();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(frontEndExtend).with(rotateAnimator1);
        set.play(backEndRetract).with(rotateAnimator2).after(rotateAnimator1);
        return set;
    }
}
