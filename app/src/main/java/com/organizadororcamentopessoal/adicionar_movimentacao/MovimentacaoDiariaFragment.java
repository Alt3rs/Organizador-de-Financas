package com.organizadororcamentopessoal.adicionar_movimentacao;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.datasource.DatabaseContract;
import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.LimiteDao;
import com.organizadororcamentopessoal.datasource.MovimentacaoDao;
import com.organizadororcamentopessoal.datasource.UserDao;
import com.organizadororcamentopessoal.entities.Movimentacao;
import com.organizadororcamentopessoal.tempo.Tempo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class MovimentacaoDiariaFragment extends Fragment implements AdicionarMovimentacaoDialog.OnSaveListener {
    public static final String USERNAME = DatabaseContract.UsuarioTable.USERNAME;
    private static final String CURRENT_DATE = "currentDate";

    private String username;
    private LocalDate currentDate;
    private UserDao userDao;
    private MovimentacaoDao movimentacaoDao;
    private LimiteDao limiteDao;
    private Button adicionarRecebimentoButton, adicionarGastoButton;
    private TextView limiteDiarioValueTextView, totalGastoDiarioValueTextView,
            totalRecebimentoValueTextView, saldoValueTextView, limiteRestanteTextView;
    private TableRow limiteTableRow, limiteRemanscenteTableRow;
    private RecyclerView recyclerView;
    private MovimentacaoAdapter movimentacaoAdapter;
    private LinearLayoutManager layoutManager;
    private SelectionTracker<Long> tracker;
    private Toolbar toolbar;
    private View itemsSelecionadosBar;
    private TextView itemsSelecionadosTextView;
    private Button editarButton, apagarButton;
    private int shortAnimationDuration;

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
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userDao = FinancasDbHelper.getUserDao(getContext().getApplicationContext());
        movimentacaoDao = FinancasDbHelper.getMovimentacaoDao(getContext().getApplicationContext());
        limiteDao = FinancasDbHelper.getLimiteDao(getContext().getApplicationContext());
        username = MovimentacaoDiariaFragmentArgs.fromBundle(getArguments()).getUsername();

        adicionarRecebimentoButton = view.findViewById(R.id.adicionarRecebimentoButton);
        adicionarGastoButton = view.findViewById(R.id.adicionarGastoButton);
        limiteDiarioValueTextView = view.findViewById(R.id.limiteValueTextView);
        totalRecebimentoValueTextView = view.findViewById(R.id.totalRecebimentoValueTextView);
        totalGastoDiarioValueTextView = view.findViewById(R.id.totalGastoValueTextView);
        saldoValueTextView = view.findViewById(R.id.saldoValueTextView);
        limiteRestanteTextView = view.findViewById(R.id.limiteRemanescentValueTextView);
        limiteTableRow = view.findViewById(R.id.limiteTableRow);
        limiteRemanscenteTableRow = view.findViewById(R.id.limiteRemanescenteTableRow);
        recyclerView =  view.findViewById(R.id.recyclerView);

        toolbar = getActivity().findViewById(R.id.main_toolbar);
        itemsSelecionadosBar = view.findViewById(R.id.itemsSelecionadosBar);
        itemsSelecionadosTextView =  view.findViewById(R.id.itemsSelecionadosTextView);
        editarButton = view.findViewById(R.id.editarButton);
        apagarButton = view.findViewById(R.id.apagarButton);
        itemsSelecionadosBar.setVisibility(View.GONE);
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        movimentacaoAdapter = new MovimentacaoAdapter(new ArrayList<>());
        recyclerView.setAdapter(movimentacaoAdapter);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setScrollbarFadingEnabled(false);

        tracker = new SelectionTracker.Builder<>(
                "movimentacaoSelection",
                recyclerView,
                new StableIdKeyProvider(recyclerView),
                new MovimentacaoAdapter.MovimentacaoDetailsLookUp(recyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).build();
        movimentacaoAdapter.setTracker(tracker);
        tracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                int selectedItens =  tracker.getSelection().size();
                if(selectedItens > 0) {
//                    if(itemsSelecionadosBar.getVisibility() == View.GONE) {
//                        itemsSelecionadosBar.setAlpha(0f);
//                        itemsSelecionadosBar.setVisibility(View.VISIBLE);
//                        itemsSelecionadosBar.animate().alpha(1f).setDuration(shortAnimationDuration).setListener(null);
//                        toolbar.animate().alpha(0f).setDuration(shortAnimationDuration).setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                toolbar.setVisibility(View.GONE);
//                            }
//                        });
//                    }
                    itemsSelecionadosBar.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.GONE);
                    itemsSelecionadosTextView.setText(String.format(Locale.getDefault(), "%d items", selectedItens));
                } else {
//                    toolbar.setAlpha(0f);
//                    toolbar.setVisibility(View.VISIBLE);
//                    toolbar.animate().alpha(1f).setDuration(shortAnimationDuration).setListener(null);
//                    itemsSelecionadosBar.animate().alpha(0f).setDuration(shortAnimationDuration).setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    itemsSelecionadosBar.setVisibility(View.GONE);
//                                }
//                        });
                    itemsSelecionadosBar.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
        apagarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.getSelection().forEach((key) ->{
                    movimentacaoDao.excluirMovimentacao(movimentacaoAdapter.getDataSet().get(key.intValue()).getIdMovimentacao());
                });
                tracker.clearSelection();
                updateDisplay();
            }
        });

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

        updateDisplay();
    }

    private void updateDisplay() {
        double gastoTotal = movimentacaoDao.totalGastoNoIntervalo(username, Tempo.getTodayStart(), Tempo.getTodayEnd());
        double recebimentoTotal = movimentacaoDao.totalRecebimentoNoIntervalo(username, Tempo.getTodayStart(), Tempo.getTodayEnd());
        totalGastoDiarioValueTextView.setText(String.format(Locale.getDefault(), "%.2f", gastoTotal));
        totalRecebimentoValueTextView.setText(String.format(Locale.getDefault(), "%.2f", recebimentoTotal));

        if(limiteDao.limiteEstaHabilitado(username)) {
            double limite = limiteDao.listarTodosLimitesDoUsuario(username).get(0).getValor();
            double saldo = recebimentoTotal + gastoTotal;
            limiteDiarioValueTextView.setText(String.format(Locale.getDefault(), "%.2f", limite));
            saldoValueTextView.setText(String.format(Locale.getDefault(), "%.2f", saldo));
            double limiteRestante = limite - gastoTotal;
            limiteRestanteTextView.setText(String.format(Locale.getDefault(), "%.2f", limiteRestante));
            if(limiteRestante > 0) {
                limiteRestanteTextView.setTextColor(getContext().getResources().getColor(R.color.verde_recebimento, getContext().getTheme()));
            } else if(limiteRestante < 0 ) {
                limiteRestanteTextView.setTextColor(getContext().getResources().getColor(R.color.vermelho_gasto, getContext().getTheme()));
            } else {
                limiteRestanteTextView.setTextColor(getContext().getResources().getColor(R.color.black, getContext().getTheme()));
            }
            limiteTableRow.setVisibility(View.VISIBLE);
            limiteRemanscenteTableRow.setVisibility(View.VISIBLE);
        } else {
            double saldo = recebimentoTotal + gastoTotal;
            saldoValueTextView.setText(String.format(Locale.getDefault(), "%.2f", saldo));
            limiteTableRow.setVisibility(View.GONE);
            limiteRemanscenteTableRow.setVisibility(View.GONE);
        }
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