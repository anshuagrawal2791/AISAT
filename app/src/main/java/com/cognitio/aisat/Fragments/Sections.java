package com.cognitio.aisat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cognitio.aisat.QuizSec;
import com.cognitio.aisat.R;


public class Sections extends Fragment {
    Button ga,vr,lr,qa,la,sa,ge,m;


    public Sections() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sections, container, false);

        ga = (Button) v.findViewById(R.id.ga);
        vr = (Button) v.findViewById(R.id.vr);
        lr = (Button) v.findViewById(R.id.lr);
        qa = (Button) v.findViewById(R.id.qa);
        la = (Button) v.findViewById(R.id.la);
        sa = (Button) v.findViewById(R.id.sa);
        ge = (Button) v.findViewById(R.id.ge);
        m = (Button) v.findViewById(R.id.m);

        ga.setTag("ga");
        vr.setTag("vr");
        lr.setTag("lr");
        qa.setTag("qa");
        la.setTag("la");
        sa.setTag("sa");
        ge.setTag("ge");
        m.setTag("m");

        View.OnClickListener a = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), QuizSec.class);
                i.putExtra("category",v.getTag().toString());
                startActivity(i);
            }
        };

        ga.setOnClickListener(a);
        vr.setOnClickListener(a);
        lr.setOnClickListener(a);
        qa.setOnClickListener(a);
        la.setOnClickListener(a);
        sa.setOnClickListener(a);
        ge.setOnClickListener(a);
        m.setOnClickListener(a);



        return v;
    }


}
