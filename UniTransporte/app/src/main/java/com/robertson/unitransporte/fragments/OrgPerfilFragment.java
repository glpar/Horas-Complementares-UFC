package com.robertson.unitransporte.fragments;

import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.robertson.unitransporte.R;
import com.robertson.unitransporte.objetos.Organizador;

public class OrgPerfilFragment extends Fragment {

    TextView textViewNome, textViewEmail, textViewCidade;
    Organizador organizador;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_perfil, container, false);

        Toolbar toolbar = view.findViewById(R.id.mToolbar);
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
        organizador = data.getParcelable("organizador");

        WebView webViewMaps = (WebView) view.findViewById(R.id.webViewOrgPerfil);
        webViewMaps.getSettings().setJavaScriptEnabled(true);
        webViewMaps.loadUrl("https://www.google.com/maps/search/?api=1&query=" + organizador.getLatitude() + "," + organizador.getLongitude());

        textViewNome.setText(organizador.getNome());
        textViewEmail.setText(organizador.getEmail());
        textViewCidade.setText(organizador.getMunicipio().getNome());

        return view;
    }
}