package com.example.consultarcep;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCepInterface {

    @GET("{cep}/json")
    Call<Endereco> getAddress(@Path("cep") String cep);

}
