package com.robertson.unitransporte.objetos;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ConectaApiIbge extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] objects) {
        StringBuilder respostaIBGE = new StringBuilder();
        if(objects[0].equals("estado")) {
            try {
                URL urlEstados = new URL("https://servicodados.ibge.gov.br/api/v1/localidades/estados/");
                HttpURLConnection conexao = (HttpURLConnection) urlEstados.openConnection();
                conexao.setRequestMethod("GET");
                conexao.setRequestProperty("Content-type", "application/json");
                conexao.setDoOutput(true);
                conexao.setConnectTimeout(3000);
                conexao.connect();

                Scanner scanner = new Scanner(urlEstados.openStream());
                while (scanner.hasNext()) {
                    respostaIBGE.append(scanner.next());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return respostaIBGE.toString();
        }else if(objects[0].equals("municipio")){
            try {
                String sigla = (String) objects[1];
                URL urlMunicipios = new URL("https://servicodados.ibge.gov.br/api/v1/localidades/estados/"+sigla+"/municipios");
                HttpURLConnection conexao = (HttpURLConnection) urlMunicipios.openConnection();
                conexao.setRequestMethod("GET");
                conexao.setRequestProperty("Content-type", "application/json");
                conexao.setDoOutput(true);
                conexao.setConnectTimeout(3000);
                conexao.connect();

                Scanner scanner = new Scanner(urlMunicipios.openStream());
                while (scanner.hasNext()) {
                    respostaIBGE.append(scanner.next());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return respostaIBGE.toString();
        }
        return null;
    }
}
