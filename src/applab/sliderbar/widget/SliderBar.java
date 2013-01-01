package applab.sliderbar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import applab.sliderbar.R;

/**
 * A custom view that represents a slider. The slider can be moved up and down
 * within a defined space. The slider bar is draw accordingly.
 * 
 */
public class SliderBar extends View implements OnTouchListener {

	/**
	 * Listener for a {@link SliderBar}.
	 */
	public static abstract interface SliderBarListener {
		/**
		 * Listener called when a sliders position is changed
		 * 
		 * @param slider
		 *            The slider that was changed
		 */
		public abstract void onPositionChanged(SliderBar slider);

		/**
		 * Listener called when a slider is being dragged.
		 * 
		 * @param slider
		 *            The slider being dragged
		 * @param state
		 *            The state (dragged or not).
		 */
		public abstract void onSliderDragged(SliderBar slider, boolean state);
	}

	private static final int DELAY = 10;
	private final Drawable sliderBar;
	private Rect viewRect;
	private int sliderBorderOffset;
	private float max;
	private float min;
	private float discreteFactor;
	private int SliderMaxPos;
	private int SliderMinPos;
	private float sliderPosition;
	private float sliderTagX;
	private float sliderTagY;
	private boolean actionUp;
	private boolean actionDrag;
	private long lastTick = 0;
	private int posDelta = 0;
	private Paint sliderTag;
	private String sliderName;
	private String sliderTypeface;

	private SliderBarListener sliderListner;

	public SliderBar(Context context) {
		this(context, null, 0);
	}

	public SliderBar(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SliderBar(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SliderBar);
		sliderBar = a.getDrawable(R.styleable.SliderBar_slider_drawable);
		sliderName = a.getString(R.styleable.SliderBar_slider_name);
		sliderTypeface = a.getString(R.styleable.SliderBar_tag_typeface);
		sliderTag = new Paint(); // The tag name of the slider, specified in XML
		sliderTag.setTypeface(Typeface.createFromAsset(context.getAssets(),
				sliderTypeface)); // Set custom typeface
		sliderTag.setAntiAlias(true);
		// Set text sized based on screen
		sliderTag.setTextSize(context.getApplicationContext().getResources()
				.getDimensionPixelSize(R.dimen.slider_textsize));
		sliderTag.setColor(Color.WHITE);
		sliderTag.setTextAlign(Paint.Align.CENTER);
		sliderBorderOffset = 0;
		max = 1;
		min = -1;
		sliderPosition = (max - min) / 2 + min;
		sliderListner = null;
		actionUp = false;
		actionDrag = false;
		setOnTouchListener(this);
	}

	public float getMaxPos() {
		return max;
	}

	public float getMinPos() {
		return min;
	}

	public float getSliderPosition() {
		return sliderPosition;
	}

	/**
	 * Draws the slider on the canvas. As the position changes the top edge of
	 * the slider is drawn in the corresponding position.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (viewRect == null) {
			viewRect = new Rect();
			getDrawingRect(viewRect);
			SliderMaxPos = viewRect.top + sliderBorderOffset;
			SliderMinPos = viewRect.bottom;
			sliderBar.setBounds(viewRect.left, SliderMaxPos, viewRect.right,
					SliderMinPos);
			discreteFactor = (max - min) / (SliderMinPos - SliderMaxPos);

			// Get the pixel position of the starting position
			float posInitial = SliderMinPos
					+ ((SliderMaxPos - SliderMinPos) / (max - min)) * (min);
			Rect textBounds = new Rect();
			sliderTag.getTextBounds("a", 0, 1, textBounds); // V align text to a
															// baseline
			// Slider tag positioning on the canvas
			sliderTagX = (viewRect.right - viewRect.left) / 2;
			sliderTagY = (viewRect.bottom - posInitial) / 2 + posInitial
					+ textBounds.height() / 2;
		}

		float pos;
		final int left;
		final int right;
		final int top;
		final int bottom;

		// translate discrete position to absolute position on the screen
		pos = SliderMinPos + ((SliderMaxPos - SliderMinPos) / (max - min))
				* (sliderPosition);
		if (actionUp == true) {
			int posRounded = (int) (SliderMinPos + ((SliderMaxPos - SliderMinPos) / (max - min))
					* (Math.round(sliderPosition)));

			int newTopBound = 0;
			long time = (System.currentTimeMillis() - lastTick);
			// the delay time has passed. set next frame
			if (time >= DELAY) {
				lastTick = System.currentTimeMillis();
				// set new bound depending on which way the slider should move
				newTopBound = posRounded > pos ? (int) pos + posDelta
						: (int) pos - posDelta;
				sliderBar.setBounds(viewRect.left, newTopBound, viewRect.right,
						SliderMinPos);
				sliderBar.draw(canvas);
				canvas.drawText(sliderName, sliderTagX, sliderTagY, sliderTag);
				if (newTopBound == posRounded) {
					actionUp = false;
					posDelta = 0;
					// position slider at correct final height
					setSliderPosition(Math.round(sliderPosition));
				} else {
					posDelta++;
					postInvalidate(); // recalls onDraw when dragging.
				}
			} else {
				newTopBound = posRounded > pos ? (int) pos + posDelta
						: (int) pos - posDelta;
				sliderBar.setBounds(viewRect.left, newTopBound, viewRect.right,
						SliderMinPos);
				sliderBar.draw(canvas);
				canvas.drawText(sliderName, sliderTagX, sliderTagY, sliderTag);
				postInvalidate();
			}
		} else {
			top = (int) pos;
			left = viewRect.left;
			right = viewRect.right;
			bottom = SliderMinPos;
			sliderBar.setBounds(left, top, right, bottom);
			sliderBar.draw(canvas);
			canvas.drawText(sliderName, sliderTagX, sliderTagY, sliderTag);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
	}

	/**
	 * The touch action is classified as either an up action (finger released
	 * from the screen) or a drag action (finger dragging across the screen).
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final float pos;
		// discretize absolute coordinates to fit our model
		// this will take into account the minimum height of the bar
		pos = max - discreteFactor * event.getY() - min;
		posDelta = 0; // Reset after each touch event
		// only send the motion event that triggers the listener when the user
		// releases the bar
		if (event.getAction() == MotionEvent.ACTION_UP) {
			actionUp = true;
			actionDrag = false;
		}
		// Action for sliders being dragged
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			actionDrag = true;
		}
		setSliderPosition(pos);
		return true;
	}

	/**
	 * Set the relative position (not absolute in pixels) of the slider at when
	 * the SliderBar is initialized
	 * 
	 * @param position
	 *            The position of the slider
	 */
	public void setInitialPosition(float position) {
		position = within(position, min, max);
		sliderPosition = position;
		sliderListner.onPositionChanged(this);
		invalidate();
	}

	/**
	 * Sets the minimum and maximum position of the slider
	 * 
	 * @param min
	 *            The minimum position
	 * @param max
	 *            The maximum position
	 */
	public void setMinMaxPos(final float min, final float max) {
		if ((this.min != min) || (this.max != max)) {
			if (min > max) {
				throw new IllegalArgumentException(
						"setMinMax: min must be smaller than max.");
			}
			this.min = min;
			this.max = max;
			setSliderPosition(sliderPosition);
			invalidate();
		}
	}

	/**
	 * Set the offset of the slider with the border of the containing view
	 * 
	 * @param sliderBorderOffset
	 *            The offset in pixels
	 */
	public void setSliderBorderOffset(int sliderBorderOffset) {
		this.sliderBorderOffset = sliderBorderOffset;
	}

	/**
	 * Set the slider's listener.
	 * 
	 * @param listener
	 *            The listener to call on all slider actions
	 */
	public void setSliderListener(final SliderBarListener listener) {
		sliderListner = listener;
	}

	/**
	 * Set the relative position (not absolute in pixels) of the slider.
	 * 
	 * @param position
	 *            The position of the slider
	 */
	public void setSliderPosition(float position) {
		position = within(position, min, max);

		if (position != sliderPosition) {
			// exectued regardless of action, if the position is changed
			sliderPosition = position;
			invalidate();
		}
		if (sliderListner != null && actionDrag) {
			// executed only when slider is being dragged
			sliderListner.onSliderDragged(this, true);
		}
		if (sliderListner != null && actionUp) {
			// executed only when slider is released
			sliderListner.onSliderDragged(this, false);
			sliderListner.onPositionChanged(this);
			invalidate();
		}
	}

	/**
	 * Checks that the position of the slider is in between min and max.
	 * 
	 * @param position
	 *            The position of the slider
	 * @param min
	 *            The minimum position
	 * @param max
	 *            The Maximum position
	 * @return Returns the position of the slider or min, max if the bounds have
	 *         been exceeded.
	 */
	private float within(float position, final float min, final float max) {
		if (position < min) {
			position = min;
		}
		// The position is maxed out at max - 1, to accommodate for the
		// sliderBorderOffset (pixel space between top of the screen and the max
		// pos of the slider)
		if (position > max - 1) {
			position = max - 1;
		}
		return position;
	}

}
