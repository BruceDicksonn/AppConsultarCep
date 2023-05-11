package com.example.consultarcep;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.consultarcep.databinding.FragmentResultSearchCepBinding;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

public class ResultSearchCepFragment extends Fragment {

    FragmentResultSearchCepBinding binding;
    OnFragmentInteractionListener mListener;
    Endereco currentAddress;

    public static ResultSearchCepFragment newInstance(String jsonResponse) {

        ResultSearchCepFragment fragment = new ResultSearchCepFragment();
        Bundle args = new Bundle();
        args.putString("json", jsonResponse);
        fragment.setArguments(args);
        return fragment;

    }

    public void fillComponents(Endereco address){

        binding.outputLogradouro.setText(address.getLogradouro() + address.getComplemento());
        binding.outputBairro.setText(address.getBairro());
        binding.outputUf.setText(address.getUf());
        binding.outputCep.setText(address.getCep());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentResultSearchCepBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mListener = MainActivity.mListener;

        if(getArguments() != null) {

            String json = getArguments().getString("json");
            Endereco address = new Gson().fromJson(json, Endereco.class);
            currentAddress = address;
            fillComponents(address);

        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null){

            String stateJson = savedInstanceState.getString("stateJson");
            Endereco address = new Gson().fromJson(stateJson, Endereco.class);
            fillComponents(address);

        }

        binding.buttonNovaBusca.setOnClickListener(novaBusca);

        super.onViewCreated(view, savedInstanceState);
    }

    View.OnClickListener novaBusca = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mListener != null) {
                mListener.onSomethingHappened();
            }

        }
    };

    public void setOnFragmentInterationListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if(currentAddress != null){

            String json = new Gson().toJson(currentAddress);
            outState.putString("stateJson", json);

        }

        super.onSaveInstanceState(outState);
    }
}