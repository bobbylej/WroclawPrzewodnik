package com.guide.przewo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;

import com.guide.przewo.DataAccess.LocalRepository;

/*
 *
 * Fragment odpowiedzialny za ładowanie szczegółów miejsca.
 *
 */
public class DrawerFragment extends Fragment {

    View view;
    FragmentManager fragmentManager;
    LocalRepository localRepository;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Fragment fragmentToLoad;
    String stringToDisplay;
    boolean opened;


    public DrawerFragment() {
        // Required empty public constructor
    }

    //public DrawerFragment(Fragment frag, String stringToDisplay) {
   // }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_drawer, container, false);


        Button bt = (Button) view.findViewById(R.id.handle);
        bt.setText(stringToDisplay);

        fragmentManager = getChildFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_drawer_container, fragmentToLoad)
                .commit();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ImageButton bt = (ImageButton) getActivity().findViewById(R.id.search_button);
                    bt.setVisibility(View.VISIBLE);
                    terminateFragment();
                    return true;
                }
                return false;
            }
        });


        SlidingDrawer sd = (SlidingDrawer)view.findViewById(R.id.slidingD);
        if(opened) sd.animateOpen();
        

        return view;
    }

    public void replaceFragment(Fragment frag, String stringToDisplay){

        fragmentToLoad = frag;
        this.stringToDisplay = stringToDisplay;
        Button bt = (Button) view.findViewById(R.id.handle);
        bt.setText(stringToDisplay);
        //localRepository = ((App)getActivity().getApplicationContext()).getLocalRepository();

        fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_drawer_container, fragmentToLoad)
                .commit();

    }

    public void setFragemntstoLoad(Fragment toLoad, String stringToDisplay){
        this.fragmentToLoad = toLoad;
        this.stringToDisplay = stringToDisplay;
    }

    public void setOpened(boolean op){
        opened = op;
    }

    public void terminateFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
