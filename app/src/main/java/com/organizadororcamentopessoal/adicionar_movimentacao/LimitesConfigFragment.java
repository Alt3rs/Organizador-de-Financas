package com.organizadororcamentopessoal.adicionar_movimentacao;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.datasource.DatabaseContract;
import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.LimiteDao;

import java.text.NumberFormat;
import java.util.Locale;

public class LimitesConfigFragment extends Fragment {
    private String username;
    private Switch limitadoHabilitadoSwitch;
    private EditText limiteValorValueEditText;
    private LinearLayout limiteValorLinearLayout;
    private LimiteDao limiteDao;
    public LimitesConfigFragment() {}

    public static LimitesConfigFragment newInstance(String username) {
        LimitesConfigFragment fragment = new LimitesConfigFragment();
        Bundle args = new Bundle();
        args.putString(DatabaseContract.UsuarioTable.USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            username = savedInstanceState.getString(DatabaseContract.UsuarioTable.USERNAME);
        }

        Bundle args = getArguments();
        if (args != null) {
            username = LimitesConfigFragmentArgs.fromBundle(args).getUsername();
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_limites_config, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        limiteDao = FinancasDbHelper.getLimiteDao(getContext().getApplicationContext());

        limitadoHabilitadoSwitch = view.findViewById(R.id.limiteHabilitadoSwitch);
        limiteValorValueEditText = view.findViewById(R.id.limiteValorEditText);
        limiteValorLinearLayout = view.findViewById(R.id.limiteValorLinearLayout);

        limitadoHabilitadoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                limiteValorLinearLayout.setEnabled(isChecked);
                for ( int i = 0; i < limiteValorLinearLayout.getChildCount();  i++ ){
                    View view = limiteValorLinearLayout.getChildAt(i);
                    view.setEnabled(isChecked);
                }
            }
        });

        limitadoHabilitadoSwitch.setChecked(limiteDao.limiteEstaHabilitado(username));
        double valorLimite = limiteDao.listarTodosLimitesDoUsuario(username).get(0).getValor();
        limiteValorValueEditText.setText(String.format(Locale.getDefault(), "%.2f", valorLimite));
    }

    @Override
    public void onPause() {
        super.onPause();
        saveChanges();
    }

    private void saveChanges() {
        try {
            boolean limiteHabilitado = limitadoHabilitadoSwitch.isChecked();
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
            double limiteValor = format.parse(limiteValorValueEditText.getText().toString()).doubleValue();
            limiteDao.atualizarLimite(username, limiteValor);
            limiteDao.habilitarLimite(username, limiteHabilitado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}