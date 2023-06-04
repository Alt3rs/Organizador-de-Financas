package com.organizadororcamentopessoal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.MovimentacaoDao;
import com.organizadororcamentopessoal.datasource.UserDao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdicionarMovimentacaoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdicionarMovimentacaoFragment extends Fragment {
    public  static final String ARG_PARAM1 = "idUsuario";

    private String userName;
    private UserDao userDao;
    private MovimentacaoDao movimentacaoDao;
    private Button adicionarRecebimentoButton, adicionarGastoButton;
    private TextView limiteDiarioValueTextView, totalGastoDiarioValueTextView, totalRecebimentoValueTextView;

    public AdicionarMovimentacaoFragment() {    }

    public static AdicionarMovimentacaoFragment newInstance(String userName) {
        AdicionarMovimentacaoFragment fragment = new AdicionarMovimentacaoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adicionar_movimentacao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userDao = FinancasDbHelper.getUserDao(getContext().getApplicationContext());
        movimentacaoDao = FinancasDbHelper.getMovimentacaoDao(getContext().getApplicationContext());

        adicionarRecebimentoButton = view.findViewById(R.id.adicionarRecebimentoButton);
        adicionarGastoButton = view.findViewById(R.id.adicionarGastoButton);
        limiteDiarioValueTextView = view.findViewById(R.id.limiteDiarioValueTextView);
        totalRecebimentoValueTextView = view.findViewById(R.id.totalRecebimentoValueTextView);
        totalGastoDiarioValueTextView = view.findViewById(R.id.totalGastoDiarioValueTextView);

        Calendar calendar = GregorianCalendar.getInstance();
        //Date now = calendar.get(Calendar.DATE);
        //Date tomorrow = calendar.add(Calendar.HOUR, 24);
        //movimentacaoDao.obterMovimentacaoNoIntervalo(userName, now.getTime(), now.(Calendar.HOUR, 24) );
    }
}