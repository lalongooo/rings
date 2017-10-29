package com.lalongooo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lalongooo.rings.R;

public class Rings extends View {

    private static final String TAG = Rings.class.getSimpleName();

    // Default values variables
    private float defaultTextSize;
    private float defaultTextMarginLeft;
    private float defaultInnerStrokeWidth;
    private float defaultInnerStrokeUnfinishedWidth;
    private float defaultOuterStrokeWidth;
    private float defaultOuterStrokeUnfinishedWidth;
    private int defaultRingUnfinishedColor;
    private int defaultRingFilledColor;
    private float defaultRingOverallProgress;
    private float defaultRingInnerThirdProgress;
    private float defaultRingInnerSecondProgress;
    private float defaultRingInnerFirstProgress;
    private String overAllText;
    private String innerFirstText;
    private String innerSecondText;
    private String innerThirdText;

    // Attributes variables
    private float textSize;
    private float textMarginLeft;
    private float innerStrokeWidth;
    private float innerStrokeWidthUnfinished;
    private float outerStrokeWidth;
    private float outerStrokeWidthUnfinished;
    private int ringOverallColor;
    private int ringInnerThirdColor;
    private int ringInnerSecondColor;
    private int ringInnerFirstColor;
    private int ringUnfinishedColor;
    private float ringOverallProgress;
    private float ringInnerThirdProgress;
    private float ringInnerSecondProgress;
    private float ringInnerFirstProgress;

    private boolean highlighted = false;
    private short highlightedRing = -1;
    private float startAngle = 90f;
    private float emptyArcAngle = 270f;

    // Paint objects
    private Paint textPaint;
    private Paint ringPaint;

    // RectF objects used to draw the arcs
    private RectF ringOverall;
    private RectF ringInnerThird;
    private RectF ringInnerSecond;
    private RectF ringInnerFirst;

    // Rect objects used by all text drawn. Used to detect user touches.
    private Rect rectOverallText;
    private Rect rectInnerThirdText;
    private Rect rectInnerSecondText;
    private Rect rectInnerFirstText;

    private Rect auxRect;

    /**
     * Highlight the overall ring.
     * Use with {@link #highlight}.
     */
    public static final short RING_OVERALL = 1;

    /**
     * Highlight the third inner ring.
     * Use with {@link #highlight}.
     */
    public static final short THIRD_INNER_RING = 2;

    /**
     * Highlight the second inner ring.
     * Use with {@link #highlight}.
     */
    public static final short SECOND_INNER_RING = 3;

    /**
     * Highlight the first inner ring.
     * Use with {@link #highlight}.
     */
    public static final short FIRST_INNER_RING = 4;

    /**
     * Set all the rings clickable.
     */
    private boolean areRingsClickable = true;

    public Rings(Context context) {
        this(context, null);
    }

    public Rings(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Rings(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Default values initialization
        defaultTextSize = Utils.sp2px(getResources(), 18);
        defaultTextMarginLeft = Utils.sp2px(getResources(), 10);
        defaultInnerStrokeWidth = Utils.sp2px(getResources(), 8);
        defaultInnerStrokeUnfinishedWidth = Utils.sp2px(getResources(), 10);
        defaultOuterStrokeWidth = Utils.sp2px(getResources(), 12);
        defaultOuterStrokeUnfinishedWidth = Utils.sp2px(getResources(), 12);
        defaultRingUnfinishedColor = Color.GRAY;
        defaultRingFilledColor = Color.parseColor("#E6E6E6");
        defaultRingOverallProgress = 0;
        defaultRingInnerThirdProgress = 0;
        defaultRingInnerSecondProgress = 0;
        defaultRingInnerFirstProgress = 0;
        overAllText = "Ring Overall";
        innerFirstText = "Ring One";
        innerSecondText = "Ring Second";
        innerThirdText = "Ring Third";

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Rings, defStyle, 0);
        initByAttributes(attributes);
        attributes.recycle();
        initPainters();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        ringOverall.set(
                outerStrokeWidth / 2f,
                outerStrokeWidth / 2f,
                MeasureSpec.getSize(widthMeasureSpec) - (outerStrokeWidth / 2f),
                MeasureSpec.getSize(heightMeasureSpec) - (outerStrokeWidth / 2f)
        );

        ringInnerThird.set(
                ringOverall.left + (outerStrokeWidth + innerStrokeWidth),
                ringOverall.top + (outerStrokeWidth + innerStrokeWidth),
                ringOverall.right - (outerStrokeWidth + innerStrokeWidth),
                ringOverall.bottom - (outerStrokeWidth + innerStrokeWidth)
        );

        ringInnerSecond.set(
                ringInnerThird.left + (outerStrokeWidth + innerStrokeWidth),
                ringInnerThird.top + (outerStrokeWidth + innerStrokeWidth),
                ringInnerThird.right - (outerStrokeWidth + innerStrokeWidth),
                ringInnerThird.bottom - (outerStrokeWidth + innerStrokeWidth)
        );

        ringInnerFirst.set(
                ringInnerSecond.left + (outerStrokeWidth + innerStrokeWidth),
                ringInnerSecond.top + (outerStrokeWidth + innerStrokeWidth),
                ringInnerSecond.right - (outerStrokeWidth + innerStrokeWidth),
                ringInnerSecond.bottom - (outerStrokeWidth + innerStrokeWidth)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw empty rings
        ringPaint.setColor(ringUnfinishedColor);
        ringPaint.setStrokeWidth(outerStrokeWidthUnfinished);
        canvas.drawArc(ringOverall, startAngle, emptyArcAngle, false, ringPaint);
        ringPaint.setStrokeWidth(innerStrokeWidthUnfinished);
        canvas.drawArc(ringInnerThird, startAngle, emptyArcAngle, false, ringPaint);
        canvas.drawArc(ringInnerSecond, startAngle, emptyArcAngle, false, ringPaint);
        canvas.drawArc(ringInnerFirst, startAngle, emptyArcAngle, false, ringPaint);

        textPaint.getTextBounds(overAllText, 0, overAllText.length(), auxRect);
        rectOverallText.set(getWidth() / 2 + (int) textMarginLeft, getHeight() - (int) outerStrokeWidth, (getWidth() / 2) + auxRect.width() + (int) textMarginLeft, getHeight());
        textPaint.getTextBounds(innerThirdText, 0, innerThirdText.length(), auxRect);
        rectInnerThirdText.set(getWidth() / 2 + (int) textMarginLeft, (int) (ringInnerThird.bottom - (int) textSize), (getWidth() / 2) + auxRect.width() + (int) textMarginLeft, (int) (ringInnerThird.bottom + (innerStrokeWidth)));
        textPaint.getTextBounds(innerSecondText, 0, innerSecondText.length(), auxRect);
        rectInnerSecondText.set(getWidth() / 2 + (int) textMarginLeft, (int) (ringInnerSecond.bottom - (int) textSize), (getWidth() / 2) + auxRect.width() + (int) textMarginLeft, (int) (ringInnerSecond.bottom + (innerStrokeWidth)));
        textPaint.getTextBounds(innerFirstText, 0, innerFirstText.length(), auxRect);
        rectInnerFirstText.set(getWidth() / 2 + (int) textMarginLeft, (int) (ringInnerFirst.bottom - (int) textSize), (getWidth() / 2) + auxRect.width() + (int) textMarginLeft, (int) (ringInnerFirst.bottom + (innerStrokeWidth)));

        if (!highlighted) {
            // Draw text
            textPaint.setColor(ringOverallColor);
            canvas.drawText(overAllText, (getWidth() / 2f) + textMarginLeft, getHeight(), textPaint);
            textPaint.setColor(ringInnerThirdColor);
            canvas.drawText(innerThirdText, (getWidth() / 2f) + textMarginLeft, ringInnerThird.bottom + (innerStrokeWidth / 2), textPaint);
            textPaint.setColor(ringInnerSecondColor);
            canvas.drawText(innerSecondText, (getWidth() / 2f) + textMarginLeft, ringInnerSecond.bottom + (innerStrokeWidth / 2), textPaint);
            textPaint.setColor(ringInnerFirstColor);
            canvas.drawText(innerFirstText, (getWidth() / 2f) + textMarginLeft, ringInnerFirst.bottom + (innerStrokeWidth / 2), textPaint);

            // Draw filled rings
            ringPaint.setStrokeWidth(outerStrokeWidth);
            ringPaint.setColor(ringOverallColor);
            canvas.drawArc(ringOverall, startAngle, getChartRingOverallProgress(), false, ringPaint);

            ringPaint.setStrokeWidth(innerStrokeWidth);
            ringPaint.setColor(ringInnerThirdColor);
            canvas.drawArc(ringInnerThird, startAngle, getChartRingSpeedProgress(), false, ringPaint);
            ringPaint.setColor(ringInnerSecondColor);
            canvas.drawArc(ringInnerSecond, startAngle, getChartRingBrakingProgress(), false, ringPaint);
            ringPaint.setColor(ringInnerFirstColor);
            canvas.drawArc(ringInnerFirst, startAngle, getChartRingAccelerationProgress(), false, ringPaint);

        } else {

            switch (highlightedRing) {
                case RING_OVERALL:

                    // Draw text
                    textPaint.setColor(ringOverallColor);
                    canvas.drawText(overAllText, (getWidth() / 2f) + textMarginLeft, getHeight(), textPaint);
                    textPaint.setColor(defaultRingFilledColor);
                    canvas.drawText(innerThirdText, (getWidth() / 2f) + textMarginLeft, ringInnerThird.bottom + (innerStrokeWidth / 2), textPaint);
                    canvas.drawText(innerSecondText, (getWidth() / 2f) + textMarginLeft, ringInnerSecond.bottom + (innerStrokeWidth / 2), textPaint);
                    canvas.drawText(innerFirstText, (getWidth() / 2f) + textMarginLeft, ringInnerFirst.bottom + (innerStrokeWidth / 2), textPaint);

                    // Draw filled rings
                    ringPaint.setStrokeWidth(outerStrokeWidth);
                    ringPaint.setColor(ringOverallColor);
                    canvas.drawArc(ringOverall, startAngle, getChartRingOverallProgress(), false, ringPaint);
                    ringPaint.setStrokeWidth(innerStrokeWidth);
                    ringPaint.setColor(defaultRingFilledColor);
                    canvas.drawArc(ringInnerThird, startAngle, getChartRingSpeedProgress(), false, ringPaint);
                    canvas.drawArc(ringInnerSecond, startAngle, getChartRingBrakingProgress(), false, ringPaint);
                    canvas.drawArc(ringInnerFirst, startAngle, getChartRingAccelerationProgress(), false, ringPaint);

                    break;

                case THIRD_INNER_RING:

                    // Draw text
                    textPaint.setColor(ringInnerThirdColor);
                    canvas.drawText(innerThirdText, (getWidth() / 2f) + textMarginLeft, ringInnerThird.bottom + (innerStrokeWidth / 2), textPaint);
                    textPaint.setColor(defaultRingFilledColor);
                    canvas.drawText(overAllText, (getWidth() / 2f) + textMarginLeft, getHeight(), textPaint);
                    canvas.drawText(innerSecondText, (getWidth() / 2f) + textMarginLeft, ringInnerSecond.bottom + (innerStrokeWidth / 2), textPaint);
                    canvas.drawText(innerFirstText, (getWidth() / 2f) + textMarginLeft, ringInnerFirst.bottom + (innerStrokeWidth / 2), textPaint);

                    // Draw filled rings
                    ringPaint.setStrokeWidth(innerStrokeWidth);
                    ringPaint.setColor(ringInnerThirdColor);
                    canvas.drawArc(ringInnerThird, startAngle, getChartRingSpeedProgress(), false, ringPaint);
                    ringPaint.setColor(defaultRingFilledColor);
                    canvas.drawArc(ringInnerSecond, startAngle, getChartRingBrakingProgress(), false, ringPaint);
                    canvas.drawArc(ringInnerFirst, startAngle, getChartRingAccelerationProgress(), false, ringPaint);
                    ringPaint.setStrokeWidth(outerStrokeWidth);
                    canvas.drawArc(ringOverall, startAngle, getChartRingOverallProgress(), false, ringPaint);

                    break;

                case SECOND_INNER_RING:

                    // Draw text
                    textPaint.setColor(ringInnerSecondColor);
                    canvas.drawText(innerSecondText, (getWidth() / 2f) + textMarginLeft, ringInnerSecond.bottom + (innerStrokeWidth / 2), textPaint);
                    textPaint.setColor(defaultRingFilledColor);
                    canvas.drawText(overAllText, (getWidth() / 2f) + textMarginLeft, getHeight(), textPaint);
                    canvas.drawText(innerThirdText, (getWidth() / 2f) + textMarginLeft, ringInnerThird.bottom + (innerStrokeWidth / 2), textPaint);
                    canvas.drawText(innerFirstText, (getWidth() / 2f) + textMarginLeft, ringInnerFirst.bottom + (innerStrokeWidth / 2), textPaint);

                    // Draw filled rings
                    ringPaint.setStrokeWidth(innerStrokeWidth);
                    ringPaint.setColor(ringInnerSecondColor);
                    canvas.drawArc(ringInnerSecond, startAngle, getChartRingBrakingProgress(), false, ringPaint);
                    ringPaint.setColor(defaultRingFilledColor);
                    canvas.drawArc(ringInnerThird, startAngle, getChartRingSpeedProgress(), false, ringPaint);
                    canvas.drawArc(ringInnerFirst, startAngle, getChartRingAccelerationProgress(), false, ringPaint);
                    ringPaint.setStrokeWidth(outerStrokeWidth);
                    canvas.drawArc(ringOverall, startAngle, getChartRingOverallProgress(), false, ringPaint);

                    break;

                case FIRST_INNER_RING:

                    // Draw text
                    textPaint.setColor(ringInnerFirstColor);
                    canvas.drawText(innerFirstText, (getWidth() / 2f) + textMarginLeft, ringInnerFirst.bottom + (innerStrokeWidth / 2), textPaint);
                    textPaint.setColor(defaultRingFilledColor);
                    canvas.drawText(overAllText, (getWidth() / 2f) + textMarginLeft, getHeight(), textPaint);
                    canvas.drawText(innerThirdText, (getWidth() / 2f) + textMarginLeft, ringInnerThird.bottom + (innerStrokeWidth / 2), textPaint);
                    canvas.drawText(innerSecondText, (getWidth() / 2f) + textMarginLeft, ringInnerSecond.bottom + (innerStrokeWidth / 2), textPaint);

                    // Draw filled rings
                    ringPaint.setStrokeWidth(innerStrokeWidth);
                    ringPaint.setColor(ringInnerFirstColor);
                    canvas.drawArc(ringInnerFirst, startAngle, getChartRingAccelerationProgress(), false, ringPaint);
                    ringPaint.setColor(defaultRingFilledColor);
                    canvas.drawArc(ringInnerThird, startAngle, getChartRingSpeedProgress(), false, ringPaint);
                    canvas.drawArc(ringInnerSecond, startAngle, getChartRingBrakingProgress(), false, ringPaint);
                    ringPaint.setStrokeWidth(outerStrokeWidth);
                    canvas.drawArc(ringOverall, startAngle, getChartRingOverallProgress(), false, ringPaint);

                    break;

                default:
                    throw new IllegalArgumentException("Use one of the constants provided to highlight a ring: FIRST_INNER_RING, SECOND_INNER_RING, THIRD_INNER_RING or RING_OVERALL");
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (getRingsClickable()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:

                    if (isOnRing(event, ringOverall, outerStrokeWidth) && isInSweep(event, ringOverall, startAngle, emptyArcAngle)
                            || rectOverallText.contains((int) event.getX(), (int) event.getY()))
                        highlight(RING_OVERALL);

                    if (isOnRing(event, ringInnerThird, outerStrokeWidth) && isInSweep(event, ringInnerThird, startAngle, emptyArcAngle)
                            || rectInnerThirdText.contains((int) event.getX(), (int) event.getY()))
                        highlight(THIRD_INNER_RING);

                    if (isOnRing(event, ringInnerSecond, outerStrokeWidth) && isInSweep(event, ringInnerSecond, startAngle, emptyArcAngle)
                            || rectInnerSecondText.contains((int) event.getX(), (int) event.getY()))
                        highlight(SECOND_INNER_RING);

                    if (isOnRing(event, ringInnerFirst, outerStrokeWidth) && isInSweep(event, ringInnerFirst, startAngle, emptyArcAngle)
                            || rectInnerFirstText.contains((int) event.getX(), (int) event.getY()))
                        highlight(FIRST_INNER_RING);

                    break;
            }

            return true;
        }

        return false;
    }

    private void initByAttributes(TypedArray attributes) {
        textSize = attributes.getDimension(R.styleable.Rings_rings_text_size, defaultTextSize);
        textMarginLeft = attributes.getDimension(R.styleable.Rings_rings_text_margin_left, defaultTextMarginLeft);
        innerStrokeWidth = attributes.getDimension(R.styleable.Rings_rings_inner_stroke_width, defaultInnerStrokeWidth);
        innerStrokeWidthUnfinished = attributes.getDimension(R.styleable.Rings_rings_inner_stroke_width_unfinished, defaultInnerStrokeUnfinishedWidth);
        outerStrokeWidth = attributes.getDimension(R.styleable.Rings_rings_outer_stroke_width, defaultOuterStrokeWidth);
        outerStrokeWidthUnfinished = attributes.getDimension(R.styleable.Rings_rings_outer_stroke_width_unfinished, defaultOuterStrokeUnfinishedWidth);

        ringUnfinishedColor = attributes.getColor(R.styleable.Rings_rings_unfinished_color, defaultRingUnfinishedColor);
        defaultRingFilledColor = attributes.getColor(R.styleable.Rings_rings_default_filled_color, defaultRingFilledColor);

        ringOverallColor = attributes.getColor(R.styleable.Rings_rings_overall_color, defaultRingFilledColor);
        ringInnerThirdColor = attributes.getColor(R.styleable.Rings_rings_inner_third_color, defaultRingFilledColor);
        ringInnerSecondColor = attributes.getColor(R.styleable.Rings_rings_inner_second_color, defaultRingFilledColor);
        ringInnerFirstColor = attributes.getColor(R.styleable.Rings_rings_inner_first_color, defaultRingFilledColor);

        overAllText = attributes.getString(R.styleable.Rings_rings_overall_text);
        innerFirstText = attributes.getString(R.styleable.Rings_rings_inner_first_text);
        innerSecondText = attributes.getString(R.styleable.Rings_rings_inner_second_text);
        innerThirdText = attributes.getString(R.styleable.Rings_rings_inner_third_text);

        setRingOverallProgress(attributes.getFloat(R.styleable.Rings_rings_overall_progress, defaultRingOverallProgress), false);
        setRingInnerThirdProgress(attributes.getFloat(R.styleable.Rings_rings_inner_third_progress, defaultRingInnerThirdProgress), false);
        setRingInnerSecondProgress(attributes.getFloat(R.styleable.Rings_rings_inner_second_progress, defaultRingInnerSecondProgress), false);
        setRingInnerFirstProgress(attributes.getFloat(R.styleable.Rings_rings_inner_first_progress, defaultRingInnerFirstProgress), false);
    }

    private void initPainters() {
        // Ring Rectangle objects
        ringOverall = new RectF();
        ringInnerThird = new RectF();
        ringInnerSecond = new RectF();
        ringInnerFirst = new RectF();

        // Init rectangles used by texts
        rectOverallText = new Rect();
        rectInnerThirdText = new Rect();
        rectInnerSecondText = new Rect();
        rectInnerFirstText = new Rect();
        // Auxiliary rect to get the width size used by text
        auxRect = new Rect();

        // Ring Paint
        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);

        // Text Paint
        textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
    }

    private static boolean isOnRing(MotionEvent event, RectF bounds, float strokeWidth) {
        // Figure the distance from center point to touch point.
        final float distance = distance(event.getX(), event.getY(),
                bounds.centerX(), bounds.centerY());

        // Assuming square bounds to figure the radius.
        final float radius = bounds.width() / 2f;

        // The Paint stroke is centered on the circumference,
        // so the tolerance is half its width.
        final float halfStrokeWidth = strokeWidth / 2f;

        // Compare the difference to the tolerance.
        return Math.abs(distance - radius) <= halfStrokeWidth;
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private static boolean isInSweep(MotionEvent event, RectF bounds, float startAngle, float sweepAngle) {
        // Figure atan2 angle.
        final float at = (float) Math.toDegrees(Math.atan2(event.getY() - bounds.centerY(), event.getX() - bounds.centerX()));

        // Convert from atan2 to standard angle.
        final float angle = (at + 360) % 360;

        // Check if in sweep.
        return angle >= startAngle && angle <= startAngle + sweepAngle;
    }

    /**
     * Determines which of the rings will be highlighted.
     *
     * @param ring One of {@link #RING_OVERALL}, {@link #THIRD_INNER_RING}, {@link #SECOND_INNER_RING} or {@link #FIRST_INNER_RING}.
     */
    public void highlight(short ring) {
        highlighted = true;
        highlightedRing = ring;
        invalidate();
    }

    public void unhighlight() {
        highlighted = false;
        highlightedRing = -1;
        invalidate();
    }

    /**
     * @return One of {@link #RING_OVERALL}, {@link #THIRD_INNER_RING}, {@link #SECOND_INNER_RING} or {@link #FIRST_INNER_RING}
     */
    public short getHighlightedRing() {
        return highlightedRing;
    }

    public float getChartRingOverallProgress() {
        return ringOverallProgress;
    }

    /**
     * Sets the progress for the overall ring
     *
     * @param overAllProgress progress for the overall ring. From 0 to 100.
     * @param invalidate      causes the view to be redrawn itself by calling {@link View#invalidate()}
     */
    public void setRingOverallProgress(float overAllProgress, boolean invalidate) {
        this.ringOverallProgress = (emptyArcAngle / 100f) * overAllProgress;
        if (invalidate)
            invalidate();
    }

    public float getChartRingSpeedProgress() {
        return ringInnerThirdProgress;
    }

    /**
     * Sets the progress for the third inner ring
     *
     * @param innerThirdProgress progress for the overall ring. From 0 to 100.
     * @param invalidate         causes the view to be redrawn itself by calling {@link View#invalidate()}
     */
    public void setRingInnerThirdProgress(float innerThirdProgress, boolean invalidate) {
        this.ringInnerThirdProgress = (emptyArcAngle / 100f) * innerThirdProgress;
        if (invalidate)
            invalidate();
    }

    public float getChartRingBrakingProgress() {
        return ringInnerSecondProgress;
    }

    /**
     * Sets the progress for the second inner ring
     *
     * @param innerSecondProgress progress for the overall ring. From 0 to 100.
     * @param invalidate          causes the view to be redrawn itself by calling {@link View#invalidate()}
     */
    public void setRingInnerSecondProgress(float innerSecondProgress, boolean invalidate) {
        this.ringInnerSecondProgress = (emptyArcAngle / 100f) * innerSecondProgress;
        if (invalidate)
            invalidate();
    }

    public float getChartRingAccelerationProgress() {
        return ringInnerFirstProgress;
    }

    /**
     * Sets the progress for the first inner ring
     *
     * @param innerFirstProgress progress for the overall ring. From 0 to 100.
     * @param invalidate         causes the view to be redrawn itself by calling {@link View#invalidate()}
     */
    public void setRingInnerFirstProgress(float innerFirstProgress, boolean invalidate) {
        this.ringInnerFirstProgress = (emptyArcAngle / 100f) * innerFirstProgress;
        if (invalidate)
            invalidate();
    }

    /**
     * Sets the rings to be clickable
     *
     * @param areRingsClickable Whether the rings are clickable or not.
     */
    public void setRingsClickable(boolean areRingsClickable) {
        this.areRingsClickable = areRingsClickable;
    }

    /**
     * @return True if rings are clickable, false otherwise. If {@link #setRingsClickable(boolean)} has not been called, returns true.
     */
    public boolean getRingsClickable() {
        return this.areRingsClickable;
    }
}