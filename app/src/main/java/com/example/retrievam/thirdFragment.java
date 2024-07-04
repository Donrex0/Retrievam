package com.example.retrievam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class thirdFragment extends Fragment {

    Button btnLogout;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot= inflater.inflate(R.layout.fragment_third, container, false);
        btnLogout=viewRoot.findViewById(R.id.btnlogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), loginActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
                getActivity().finish();            }
        });
        return viewRoot;
    }
}