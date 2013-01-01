package applab.sliderbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import applab.sliderbar.widget.SliderBar;
import applab.sliderbar.widget.SliderLayoutListener;

/**
 * {@link Fragment} that implements the sliders and their actions. The fragment
 * is the container, with each slider processing its own actions and the
 * {@link SliderLayoutListener} processing the actions of the sliders as a
 * group.
 */
public class SliderFragment extends Fragment {

	public interface OnSliderChangedListener {
		public void onSliderChanged(List<SliderBar> sliders,
				List<Integer> slidersChanged);
	}

	private List<SliderBar> sliders;
	private SliderLayoutListener sliderLayoutListener;
	private OnSliderChangedListener sliderListener;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// initialize all sliders definded in the layout
		sliders = new ArrayList<SliderBar>();
		// Listener that can handle all actions from the sliders in the layout
		sliderLayoutListener = new SliderLayoutListener(sliders, this);
		LinearLayout layout = (LinearLayout) getActivity().findViewById(
				R.id.slider_layout);

		int sliderIndex = 0; // index of the slider used to restore its state
		Random rand = new Random();
		for (int i = 0; i < layout.getChildCount(); i++) {
			View v = layout.getChildAt(i);
			Class c = v.getClass();
			if (c == SliderBar.class) {
				// Setup the slider and add it to the List of all SliderBars
				SliderBar slider;
				slider = (SliderBar) v;
				slider.setSliderListener(sliderLayoutListener);
				slider.setMinMaxPos(1, 7);
				slider.setSliderBorderOffset(10);
				if (savedInstanceState != null) {
					// restore slider position
					slider.setSliderPosition(savedInstanceState
							.getFloatArray("sliderValues")[sliderIndex]);
				} else {
					slider.setInitialPosition(rand.nextInt(6)); // set random
																// initial
																// position
				}
				sliders.add(slider);
				sliderIndex++;
			}
		}

		if (savedInstanceState != null) {
			ArrayList<Integer> actionQueue = savedInstanceState
					.getIntegerArrayList("actionQueue");
			if (actionQueue.size() > 0) {
				// send request that has not yet been sent
				SliderActivity sa = (SliderActivity) this.getActivity();
				sa.onSliderChanged(sliders, actionQueue);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Make sure the activity implements the slider listener
		try {
			sliderListener = (OnSliderChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSliderChangedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.activity_slider, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Save the slider state on rotate
		super.onSaveInstanceState(outState);
		float[] sliderValues = new float[sliders.size()];
		for (int i = 0; i < sliders.size(); i++) {
			sliderValues[i] = sliders.get(i).getSliderPosition();
		}
		outState.putFloatArray("sliderValues", sliderValues);
		outState.putIntegerArrayList("actionQueue",
				sliderLayoutListener.getActionQueue());
	}
}
