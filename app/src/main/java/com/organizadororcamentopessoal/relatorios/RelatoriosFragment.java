package com.organizadororcamentopessoal.relatorios;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.organizadororcamentopessoal.R;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.MovimentacaoDao;
import com.organizadororcamentopessoal.datasource.UserDao;
import com.organizadororcamentopessoal.entities.Movimentacao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class RelatoriosFragment extends Fragment {
    private static float DIA = 3600 * 1000 * 24;
    private static float HORA = 3600 * 1000;
    private static float MIN = 60 * 1000;
    private String username;
    private MovimentacaoDao movimentacaoDao;
    private UserDao usuarioDao;
    private LineChart lineChart;
    private Date inicio, fim;

    public RelatoriosFragment() {   }

    public static RelatoriosFragment newInstance() {
        RelatoriosFragment fragment = new RelatoriosFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            username = RelatoriosFragmentArgs.fromBundle(args).getUsername();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_relatorios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = view.findViewById(R.id.lineChart);
        movimentacaoDao = FinancasDbHelper.getMovimentacaoDao(getContext());
        usuarioDao = FinancasDbHelper.getUserDao(getContext());

        updateLineChart();
    }

    private void updateLineChart() {
        lineChart.animateX(1200, Easing.EaseInSine);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1F);
        //xAxis.setValueFormatter( new MovimentacaoFormatter(data) );
        //xAxis.setValueFormatter(lineChart.getDefaultValueFormatter());

        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setExtraRightOffset(30f);

        Legend legend = lineChart.getLegend();
        legend.setOrientation( Legend.LegendOrientation.VERTICAL );
        legend.setVerticalAlignment( Legend.LegendVerticalAlignment.TOP );
        legend.setHorizontalAlignment( Legend.LegendHorizontalAlignment.CENTER );
        legend.setTextSize(15f);
        legend.setForm(Legend.LegendForm.LINE);
        Date inicio = new Date(LocalDate.of(1, 1, 1).toEpochDay() * 3600*24*1000);
        Date fim = new Date(LocalDate.of(9999, 12, 31).toEpochDay() * 3600*24*1000);
        int grupo = MovimentacaoDao.DIA;
        List<Movimentacao> gastos = movimentacaoDao.obterMovimentacaoNoIntervaloAgrupado(username, inicio, fim, MovimentacaoDao.GASTO, grupo);
        List<Movimentacao> recebimentos = movimentacaoDao.obterMovimentacaoNoIntervaloAgrupado(username, inicio, fim, MovimentacaoDao.RECEBIMENTO, grupo);

        LineDataSet recebimentoDataSet = new LineDataSet(buildDaysLine(gastos, HORA), "Recebimentos");
        LineDataSet gastoDataSet = new LineDataSet(buildDaysLine(recebimentos, HORA), "Gastos");
        List<ILineDataSet> dataSets = configureDataSet(recebimentoDataSet, gastoDataSet);

        //List<Movimentacao> movimentacoes = new ArrayList<>(gastos.size() + recebimentos.size());
        //movimentacoes.addAll(gastos);
        //movimentacoes.addAll(recebimentos);
        //movimentacoes.sort(Comparator.comparing(Movimentacao::getDataMovimentacao));

        lineChart.setData(new LineData(dataSets));
        xAxis.setValueFormatter(new LocalEpochDayFormatter());
        lineChart.invalidate();
    }

    private List<ILineDataSet> configureDataSet(LineDataSet recebimentoDataSet, LineDataSet gastoDataSet) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(gastoDataSet);
        dataSets.add(recebimentoDataSet);
        recebimentoDataSet.setColor(ContextCompat.getColor(getContext(), R.color.verde_recebimento));
        gastoDataSet.setColor(ContextCompat.getColor(getContext(), R.color.vermelho_gasto));
        for(ILineDataSet dataSet : dataSets) {
            ((LineDataSet)(dataSet)).setLineWidth(3f);
            dataSet.setValueTextSize(15f);
            ((LineDataSet)dataSet).setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setValueTextColor( ContextCompat.getColor(getContext(), R.color.black) );
        }
        return dataSets;
    }
    private List<Entry> buildDaysLine(List<Movimentacao> movimentacoes, float base) {
        List<Entry> pontos = new ArrayList<>();
        for(Movimentacao movimentacao : movimentacoes) {
            float localEpochDays = (movimentacao.getDataMovimentacao().getTime() + TimeZone.getDefault().getRawOffset()) / base;
            pontos.add( new Entry( localEpochDays, Math.abs((float) movimentacao.getValor())) );
        }
        return pontos;
    }


    private static class LocalEpochDayFormatter extends IndexAxisValueFormatter {
        private int modo;
        private SimpleDateFormat dateFormat;
        public LocalEpochDayFormatter() {
        }

        public void setModo(int modo) {
            this.modo = modo;
            if(modo == MovimentacaoDao.MINUTO) {
                dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            }
            if(modo == MovimentacaoDao.HORA) {
                dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            }
            if(modo == MovimentacaoDao.DIA) {
                //dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            }
            if(modo == MovimentacaoDao.SEMANA) {
                dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            }
        }
        public DateFormat getDateFormat() {
            return dateFormat;
        }
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if(modo == MovimentacaoDao.MINUTO) {
                return getDateFormat().format(((long)value) * 60*1000);
            }
            if(modo == MovimentacaoDao.HORA) {
                return getDateFormat().format(((long)value) * 3600*1000);
            }
            if(modo == MovimentacaoDao.DIA) {
                return dateFormat.format(((long)value) * 3600*1000*24);
            }
            if(modo == MovimentacaoDao.SEMANA) {
                return dateFormat.format(((long)value) * 3600*1000*24*7);
            }
            throw new IllegalStateException();
        }
    }
}