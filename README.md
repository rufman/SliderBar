SliderBar
=========

SliderBar widget for Android used for the AppDJ app (https://play.google.com/store/apps/details?id=appdj.android).

Using the Example code
----------------------
The code contains both the core widget (applab.sliderbar.widget) and the Activities, Fragments and Layouts needed to use the widget. The Activities and Fragments in applab.sliderbar are an example of how the widget can be integrated into an Android App.

Setting up the Example with Eclipse to try it out:

1. Create a new Android project called SliderBar with the package name applab.sliderbar
2. Import the src, res, and assets folders from the downloaded SliderBar code into the newly created SliderBar Eclipse project. The AndroidManifest file in the root directory should also be included.
3. Create a new Android Application Run Configuration for the project.
4. Connect a device or start an AVD and run the app.

###Defining Sliders
Sliders are defined in an Layout Andorid XML file (activity_slider.xml). All the attributes of the sliders and its containing layout can be defined here. 

###Loading Slider
The SliderFragment loads the sliders defined in the slider Layout file. The sliders defined in the XML file are created as `SliderBar` classes and added to a List of SliderBars. 

###Handeling the SliderBar events
The implementation of the `SliderListener` handels the events comming from the sliders. The `SliderFragement` creates the `SliderListener` and registers itself as the caller, so that the listener can pass on events via the `onSliderChanged` function. This function then implements what happens with the event (the example passes the changed sliders on to another `Fragement` that shows a `Dialog`). The `SliderFragement` ensures that its `Activity` contains this function via the `onAttach` method.

SliderBar.java
--------------
This class implements the slider bar functionality for one instance of a slider. There can be many sliders in one single layout as shown by the example. The drawing and the control of a slider is implemented on a per slider basis, but the global interaction between sliders is implemented in the listener.

SliderListener.java
-------------------
The listener controls the actions of one or more slider bars. Using one single listener as in the example the global state of all slider bars associated with the listener can be controlled from one location. The listener then passes the events of the sliders on to the activity within which the sliders reside.

