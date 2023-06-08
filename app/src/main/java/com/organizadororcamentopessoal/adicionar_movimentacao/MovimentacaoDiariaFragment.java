package com.organizadororcamentopessoal.adicionar_movimentacao;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.datasource.DatabaseContract;
import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.MovimentacaoDao;
import com.organizadororcamentopessoal.datasource.UserDao;
import com.organizadororcamentopessoal.entities.Movimentacao;
import com.organizadororcamentopessoal.tempo.Tempo;

import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovimentacaoDiariaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovimentacaoDiariaFragment extends Fragment {
    private static String USERNAME = DatabaseContract.UsuarioTable.USERNAME;
    private String username;
    private UserDao userDao;
    private MovimentacaoDao movimentacaoDao;
    private Button adicionarRecebimentoButton, adicionarGastoButton;
    private TextView limiteDiarioValueTextView, totalGastoDiarioValueTextView, totalRecebimentoValueTextView;
    private RecyclerView recyclerView;
    private MovimentacaoAdapter movimentacaoAdapter;

    public MovimentacaoDiariaFragment() {    }

    public static MovimentacaoDiariaFragment newInstance(String username) {
        MovimentacaoDiariaFragment fragment = new MovimentacaoDiariaFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movimentacao_diaria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userDao = FinancasDbHelper.getUserDao(getContext().getApplicationContext());
        movimentacaoDao = FinancasDbHelper.getMovimentacaoDao(getContext().getApplicationContext());
        username = MovimentacaoDiariaFragmentArgs.fromBundle(getArguments()).getUsername();

        adicionarRecebimentoButton = view.findViewById(R.id.adicionarRecebimentoButton);
        adicionarGastoButton = view.findViewById(R.id.adicionarGastoButton);
        limiteDiarioValueTextView = view.findViewById(R.id.limiteDiarioValueTextView);
        totalRecebimentoValueTextView = view.findViewById(R.id.totalRecebimentoValueTextView);
        totalGastoDiarioValueTextView = view.findViewById(R.id.totalGastoDiarioValueTextView);
        recyclerView =  view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        movimentacaoAdapter = new MovimentacaoAdapter(obterMovimentacaoHoje());
        recyclerView.setAdapter(movimentacaoAdapter);

        adicionarGastoButton.setOnClickListener((button) -> {
            AdicionarMovimentacaoDialog dialog = new AdicionarMovimentacaoDialog(getActivity(), username);
            dialog.setDefaultSelectedType(AdicionarMovimentacaoDialog.GASTO);
            dialog.show();
        });
        adicionarRecebimentoButton.setOnClickListener((button) -> {
            AdicionarMovimentacaoDialog dialog = new AdicionarMovimentacaoDialog(getActivity(), username);
            dialog.setDefaultSelectedType(AdicionarMovimentacaoDialog.RECEBIMENTO);
            dialog.show();
        });

        double gastoTotal = movimentacaoDao.totalGastoNoIntervalo(username, Tempo.getTodayStart(), Tempo.getTodayEnd());
        double recebimentoTotal = movimentacaoDao.totalRecebimentoNoIntervalo(username, Tempo.getTodayStart(), Tempo.getTodayEnd());
        totalGastoDiarioValueTextView.setText(String.format(Locale.getDefault(), "R$ %.2f", gastoTotal));
        totalRecebimentoValueTextView.setText(String.format(Locale.getDefault(), "R$ %.2f", recebimentoTotal));
    }

    private List<Movimentacao> obterMovimentacaoHoje() {
        return movimentacaoDao.obterMovimentacaoNoIntervalo(username, Tempo.getTodayStart(), Tempo.getTodayEnd());
    }


}