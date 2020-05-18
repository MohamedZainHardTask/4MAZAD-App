package app.mazad.classes;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class Navigator {

    /*loadFragment parameters >> 1-the context 2-the fragment which navigation to it 3-the id of container of fragment
     4-isStacked variable used in for back purpose(true>>the fragment still in progress and can back to it , false >> the fragment will be destroyed)*/
    public static void loadFragment(FragmentActivity activity, Fragment baseFragment, int containerId, boolean isStacked) {

        if (!isStacked)
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(containerId, baseFragment).commitAllowingStateLoss();
        else
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(containerId, baseFragment).addToBackStack("").commit();
    }

/*
*
Context simple explaining :

As the name suggests, it's the context of current state of the application/object.
It lets newly-created objects understand what has been going on.
Typically you call it to get information regarding another part of your program (activity and package/application).

You can get the context by invoking getApplicationContext(), getContext(), getBaseContext() or this (when in a class that extends from Context,
such as the Application, Activity, Service and IntentService classes).

Typical uses of context:

Creating new objects: Creating new views, adapters, listeners:

TextView tv = new TextView(getContext());
ListAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), ...);
Accessing standard common resources: Services like LAYOUT_INFLATER_SERVICE, SharedPreferences:

context.getSystemService(LAYOUT_INFLATER_SERVICE)
getApplicationContext().getSharedPreferences(*name*, *mode*);
Accessing components implicitly: Regarding content providers, broadcasts, intent

getApplicationContext().getContentResolver().query(uri, ...)
* */
}

