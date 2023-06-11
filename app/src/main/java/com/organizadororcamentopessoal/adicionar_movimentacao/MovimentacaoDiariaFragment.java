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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovimentacaoDiariaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovimentacaoDiariaFragment extends Fragment implements AdicionarMovimentacaoDialog.OnSaveListener {
    public static final String USERNAME = DatabaseContract.UsuarioTable.USERNAME;
    private static final String CURRENT_DATE = "currentDate";

    private String username;
    private LocalDate currentDate;
    private UserDao userDao;
    private MovimentacaoDao movimentacaoDao;
    private Button adicionarRecebimentoButton, adicionarGastoButton;
    private TextView limiteDiarioValueTextView, totalGastoDiarioValueTextView,
            totalRecebimentoValueTextView, saldoValueTextView;
    private RecyclerView recyclerView;
    private MovimentacaoAdapter movimentacaoAdapter;
    private LinearLayoutManager layoutManager;


    public MovimentacaoDiariaFragment() {    }
    public static MovimentacaoDiariaFragment newInstance(String username) {
        MovimentacaoDiariaFragment fragment = new MovimentacaoDiariaFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    public LocalDate getCurrentDate(){
        if(currentDate == null) {
            return LocalDate.now();
        }
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            username = savedInstanceState.getString(USERNAME);
            String currentDateSaved = savedInstanceState.getString(CURRENT_DATE);
            if(currentDateSaved != null) {
                currentDate = LocalDate.parse(currentDateSaved);
            }
        }

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentDate != null) {
            outState.putString(CURRENT_DATE, currentDate.toString());
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
        limiteDiarioValueTextView = view.findViewById(R.id.orcamentoValueTextView);
        totalRecebimentoValueTextView = view.findViewById(R.id.totalRecebimentoValueTextView);
        totalGastoDiarioValueTextView = view.findViewById(R.id.totalGastoValueTextView);
        saldoValueTextView = view.findViewById(R.id.saldoValueTextView);
        recyclerView =  view.findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        movimentacaoAdapter = new MovimentacaoAdapter(new ArrayList<>());
        recyclerView.setAdapter(movimentacaoAdapter);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setScrollbarFadingEnabled(false);
        updateDisplay();

        adicionarGastoButton.setOnClickListener((button) -> {
            AdicionarMovimentacaoDialog dialog = new AdicionarMovimentacaoDialog(getActivity(), username);
            dialog.setDefaultSelectedType(AdicionarMovimentacaoDialog.GASTO);
            dialog.setOnSaveListener(this);
            dialog.show();
        });
        adicionarRecebimentoButton.setOnClickListener((button) -> {
            AdicionarMovimentacaoDialog dialog = new AdicionarMovimentacaoDialog(getActivity(), username);
            dialog.setDefaultSelectedType(AdicionarMovimentacaoDialog.RECEBIMENTO);
            dialog.setOnSaveListener(this);
            dialog.show();
        });
    }

    private void updateDisplay() {
        double gastoTotal = movimentacaoDao.totalGastoNoIntervalo(username, Tempo.getTodayStart(), Tempo.getTodayEnd());
        double recebimentoTotal = movimentacaoDao.totalRecebimentoNoIntervalo(username, Tempo.getTodayStart(), Tempo.getTodayEnd());
        double saldo = recebimentoTotal + gastoTotal;
        totalGastoDiarioValueTextView.setText(String.format(Locale.getDefault(), "%.2f", gastoTotal));
        totalRecebimentoValueTextView.setText(String.format(Locale.getDefault(), "%.2f", recebimentoTotal));
        saldoValueTextView.setText(String.format(Locale.getDefault(), "%.2f", saldo));
        //movimentacaoAdapter.getDataSet().clear();
        //movimentacaoAdapter.getDataSet().addAll(obterMovimentacaoHoje());
        movimentacaoAdapter.setDataSet(obterMovimentacaoHoje());
        movimentacaoAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(movimentacaoAdapter.getDataSet().size() -1);
    }

    private List<Movimentacao> obterMovimentacaoHoje() {
        LocalDate currentDate = getCurrentDate();
        return movimentacaoDao.obterMovimentacaoNoIntervalo(username,
                new Date(Tempo.localDateStartInMilli(currentDate)),
                new Date(Tempo.localDateEndInMilli(currentDate)),
                MovimentacaoDao.ASC);
    }

    @Override
    public void OnSave(AdicionarMovimentacaoDialog dialog, long idMovimentacao) {
        if(idMovimentacao != -1) {
            updateDisplay();
        }
    }
}