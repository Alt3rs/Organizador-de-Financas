package com.organizadororcamentopessoal.relatorios;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;

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
import com.organizadororcamentopessoal.tempo.Tempo;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class RelatoriosFragment extends Fragment {
    private String username;
    private MovimentacaoDao movimentacaoDao;
    private UserDao usuarioDao;
    private LineChart lineChart;
    private Date inicio, fim;
    private Spinner unidadeTempoSpinner;
    private int unidadeTempo = MovimentacaoDao.MES;

    public RelatoriosFragment() {   }

    public static RelatoriosFragment newInstance() {
        RelatoriosFragment fragment = new RelatoriosFragment();
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
        unidadeTempoSpinner = view.findViewById(R.id.visaoSpinner);

        Toolbar toolbar = getActivity().findViewById(R.id.main_toolbar);
        toolbar.setBackgroundColor(getContext().getColor(R.color.verde));
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getContext().getColor(R.color.verde));
        }

        String[] unidades = new String[] {"MES", "HORA", "DIA", "SEMANA", "ANO" };
        unidadeTempoSpinner.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, unidades));
        unidadeTempoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUnidadeTempo(getUnitCode( (String) parent.getItemAtPosition(position)));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateLineChart(getUnidadeTempo());
    }
    private void setUnidadeTempo(int unidadeTempo) {
        if(this.unidadeTempo != unidadeTempo) {
            this.unidadeTempo = unidadeTempo;
            updateLineChart(unidadeTempo);
        }
    }
    private int getUnidadeTempo() {
        if(unidadeTempo == -1) {
            unidadeTempo = getUnitCode((String)unidadeTempoSpinner.getSelectedItem());
        }
        return unidadeTempo;
    }

    private void updateLineChart(int unidade) {
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

        Date inicio = new Date(LocalDate.of(1900, 1, 1).toEpochDay() * 3600*24*1000);
        Date fim = new Date(LocalDate.of(2500, 12, 31).toEpochDay() * 3600*24*1000);

        List<Movimentacao> gastos = movimentacaoDao.obterMovimentacaoNoIntervaloAgrupado(username, inicio, fim, MovimentacaoDao.GASTO, unidade);
        List<Movimentacao> recebimentos = movimentacaoDao.obterMovimentacaoNoIntervaloAgrupado(username, inicio, fim, MovimentacaoDao.RECEBIMENTO, unidade);

        LineDataSet recebimentoDataSet = new LineDataSet(buildDaysLine(gastos, unidade), "Recebimentos");
        LineDataSet gastoDataSet = new LineDataSet(buildDaysLine(recebimentos, unidade), "Gastos");
        List<ILineDataSet> dataSets = configureDataSet(recebimentoDataSet, gastoDataSet);

        lineChart.setData(new LineData(dataSets));
        xAxis.setValueFormatter(new EpochToDateFormatter(unidade));
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
    private List<Entry> buildDaysLine(List<Movimentacao> movimentacoes, int unidade) {
        List<Entry> pontos = new ArrayList<>();
        for(Movimentacao movimentacao : movimentacoes) {
            long time = movimentacao.getDataMovimentacao().getTime();
            long localTime = movimentacao.getDataMovimentacao().getTime() + TimeZone.getDefault().getRawOffset();
            double localDateTime = Tempo.epochMilliTo((double) localTime, unidade);
            pontos.add( new Entry( (float)localDateTime, Math.abs((float) movimentacao.getValor())) );
        }
        return pontos;
    }

    private static int getUnitCode(String unit) {
        switch (unit) {
            case "MINUTO": return MovimentacaoDao.MINUTO;
            case "HORA": return (MovimentacaoDao.HORA);
            case "DIA": return (MovimentacaoDao.DIA);
            case "SEMANA": return (MovimentacaoDao.SEMANA);
            case "MES": return (MovimentacaoDao.MES);
            case "ANO": return (MovimentacaoDao.ANO);
        }
        return -1;
    }

}
class EpochToDateFormatter extends IndexAxisValueFormatter {
    private int unidadeTempo;
    private SimpleDateFormat dateFormat;
    public EpochToDateFormatter(int unidadeTempo) {
        setUnidadeTempo(unidadeTempo);
    }
    public void setUnidadeTempo(int unidadeTempo) {
        Log.d(EpochToDateFormatter.class.getCanonicalName(), Integer.toString(unidadeTempo));
        this.unidadeTempo = unidadeTempo;
        if(unidadeTempo == MovimentacaoDao.MINUTO) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        } else if(unidadeTempo == MovimentacaoDao.HORA) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        } else if(unidadeTempo == MovimentacaoDao.DIA) {
            //dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        } else if(unidadeTempo == MovimentacaoDao.SEMANA) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        } else if(unidadeTempo == MovimentacaoDao.MES) {
            dateFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        } else if(unidadeTempo == MovimentacaoDao.ANO) {
            dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        } else {
            throw new IllegalArgumentException();
        }
    }
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        float time = Tempo.toEpochMilli(value, unidadeTempo);
        String result = dateFormat.format(time);
        return result;
    }
}
