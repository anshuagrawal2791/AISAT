package com.cognitio.aisat.drawer_fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cognitio.aisat.Quiz;
import com.cognitio.aisat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Dashboard extends Fragment {

    Button button1,button2;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    OnTestSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnTestSelectedListener {
        public void onArticleSelected(int position);
    }

    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_dashboard, container, false);
        button1 = (Button) v.findViewById(R.id.sectional_test_button);
        button2 = (Button) v.findViewById(R.id.complete_test_button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onArticleSelected(1);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),Quiz.class);
                i.putExtra("type",1);
                startActivity(i);
//                startActivity(new Intent(getActivity(), Quiz.class));
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnTestSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
