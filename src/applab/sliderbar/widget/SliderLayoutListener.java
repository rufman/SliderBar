package applab.sliderbar.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.support.v4.app.Fragment;
import applab.sliderbar.SliderActivity;
import applab.sliderbar.SliderFragment;
import applab.sliderbar.widget.SliderBar.SliderBarListener;

/**
 * The {@link SliderLayoutListener} implements the {@link SliderBarListener}
 * defined in the {@link SliderBar}.
 * 
 * This listener is called by all {@link SliderBar}s defined and implements all
 * global actions (e.g. Moving multiple sliders within a certain time frame is
 * seen as one action).
 */
public class SliderLayoutListener implements SliderBarListener {

	private static int DELAY = 2000; // wait 1.5 sec for input before backend
										// request action
	private List<SliderBar> sliders; // sliders in the layout
	// The fragment that is associated with the listener
	private SliderFragment caller;
	private ArrayList<Integer> actionQueue; // index of sliders that executed an
	// action
	private ScheduledExecutorService inputDelay;
	private ScheduledFuture<?> scheduledAction;
	private boolean sliderDragging; // true if one of the sliders is being
									// dragged
	private List<SliderBar> slidersBeingDragged; // all sliders currently being
													// dragged
	private Long lastSliderActivation = 0l;

	/**
	 * Constructor that creates the layout listener for the Sliders
	 * 
	 * @param sliders
	 *            A {@link List} containing all the {@link SliderBar}s defined
	 * @param caller
	 *            The {@link Fragment} that calls the layout listener (the {@link Fragment} containing the {@link SliderBar}s). This is
	 *            used to send on the event when the listener detects a change
	 *            in the sliders.
	 */
	public SliderLayoutListener(List<SliderBar> sliders, SliderFragment caller) {
		this.sliders = sliders;
		this.caller = caller;
		sliderDragging = false;
		actionQueue = new ArrayList<Integer>();
		slidersBeingDragged = new ArrayList<SliderBar>();
		inputDelay = Executors.newScheduledThreadPool(1);
	}

	public ArrayList<Integer> getActionQueue() {
		return actionQueue;
	}

	@Override
	public void onPositionChanged(SliderBar slider) {
		actionQueue.add(sliders.indexOf(slider));
		if (scheduledAction != null) {
			// reset the wait time after every action
			scheduledAction.cancel(true);
		}
		lastSliderActivation = System.currentTimeMillis();
		// Waits for additional input before triggering action
		scheduledAction = inputDelay.schedule(new Runnable() {

			@Override
			public void run() {
				caller.getActivity().runOnUiThread(new Runnable() {
					// Needed for updates that change something in the UI
					@Override
					public void run() {
						if (!sliderDragging) {
							// no action if a slider is dragging
							SliderActivity sa = (SliderActivity) caller
									.getActivity();
							sa.onSliderChanged(sliders, actionQueue);
							actionQueue.clear();
						}
					}
				});

			}
		}, DELAY, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onSliderDragged(SliderBar slider, boolean state) {
		if (!state) {
			if (slidersBeingDragged.contains(slider)) {
				slidersBeingDragged.remove(slider); // remove the slider from the blocking list
				if (slidersBeingDragged.isEmpty()) {
					// only allow update after all drags have been released
					sliderDragging = state;
				}
			}
		} else {
			sliderDragging = state;
			if (!slidersBeingDragged.contains(slider)) {
				// add the slider to the list of sliders blocking the passing on of the event
				slidersBeingDragged.add(slider);
			}
		}
	}
}
