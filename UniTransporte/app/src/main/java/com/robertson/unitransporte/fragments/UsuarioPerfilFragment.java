package com.robertson.unitransporte.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.robertson.unitransporte.R;

public class UsuarioPerfilFragment extends Fragment {
    TextView textViewNome, textViewEmail, textViewCidade;
    String strNome, strEmail, strCidade;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_perfil, container, false);

        toolbar = view.findViewById(R.id.mToolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        textViewNome = view.findViewById(R.id.textViewNomeOrg);
        textViewEmail = view.findViewById(R.id.textViewEmailOrg);
        textViewCidade = view.findViewById(R.id.textViewCidadeOrg);

        Bundle data = getArguments();

        if(data != null){
            strNome = data.getString("nomeCompleto");
            strEmail = data.getString("email");
            strCidade = data.getString("cidade");
            textViewNome.setText(strNome);
            textViewEmail.setText(strEmail);
            textViewCidade.setText(strCidade);
        }

        return view;
    }
}