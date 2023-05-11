package com.example.consultarcep;

import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.consultarcep.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    static ActivityMainBinding binding;
    static final String URL = "https://viacep.com.br/ws/";
    boolean hiddenComponents = false;
    static OnFragmentInteractionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(savedInstanceState != null) {
            hiddenComponents = savedInstanceState.getBoolean("state");
        }

        if(hiddenComponents) {
            hideComponents();
        } else {
            showComponents();
        }

        mListener = this;
        binding.buttonBuscar.setOnClickListener(buscarCep);

    }

    void errorAlert(View view, String message) {

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.getView().setBackgroundColor(Color.RED);

        snackbar.show();

     }

    void showFragmentResponse(String jsonResponse) {

        FragmentManager manager = getSupportFragmentManager();
        ResultSearchCepFragment fragment = ResultSearchCepFragment.newInstance(jsonResponse);

        manager.beginTransaction()
               .replace(R.id.fragmentResponse, fragment, "responseView")
               .commit();

        hideComponents();
        fragment.setOnFragmentInterationListener(this);


    }

    void showComponents(){

        binding.fragmentResponse.setVisibility(View.GONE);

        binding.linear.setVisibility(View.VISIBLE);
        binding.title.setVisibility(View.VISIBLE);
        binding.buttonBuscar.setVisibility(View.VISIBLE);

        hiddenComponents = false;
    }

    void hideComponents(){

        binding.fragmentResponse.setVisibility(View.VISIBLE);

        binding.linear.setVisibility(View.GONE);
        binding.title.setVisibility(View.GONE);
        binding.buttonBuscar.setVisibility(View.GONE);

        hiddenComponents = true;

    }

    void clearInputCep(){
        binding.inputCep.getText().clear();
    }

    @Override
    public void onSomethingHappened() {

        clearInputCep();
        //hideComponents();
        showComponents();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putBoolean("state", hiddenComponents);
        super.onSaveInstanceState(outState);

    }

    View.OnClickListener buscarCep = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String cep = binding.inputCep.getText().toString();

            if(cep.isEmpty()){
                errorAlert(binding.buttonBuscar, "Preencha o campo vazio!");
                return;
            }

            binding.progressLoad.setVisibility(View.VISIBLE);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ViaCepInterface cepService = retrofit.create(ViaCepInterface.class);

            Call<Endereco> call = cepService.getAddress(cep);
            call.enqueue(new Callback<Endereco>() {
                @Override
                public void onResponse(Call<Endereco> call, Response<Endereco> response) {

                    binding.progressLoad.setVisibility(View.GONE);

                    if(response.body().cep == null){
                        errorAlert(binding.buttonBuscar, "Cep inválido ou não encontrado");
                        return;
                    }

                    String jsonResponse = new Gson().toJson(response.body());
                    showFragmentResponse(jsonResponse);

                }

                @Override
                public void onFailure(Call<Endereco> call, Throwable t) {

                    binding.progressLoad.setVisibility(View.GONE);
                    errorAlert(binding.buttonBuscar, "Erro");
                    Log.e("RetrofitOnFailure", t.getMessage());

                }
            });

        }
    };

}