package com.organizadororcamentopessoal.adicionar_movimentacao;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.MovimentacaoDao;
import com.organizadororcamentopessoal.tempo.Tempo;
import com.organizadororcamentopessoal.text.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AdicionarMovimentacaoDialog extends Dialog implements View.OnClickListener {
    public static final int GASTO = 0, RECEBIMENTO = 1;
    private String username;
    private EditText dataEditText, valorEditText, descricaoEditText, timeEditText;
    private Spinner tipoSpinner;
    private Button salvarButton, cancelarButton, datePickerButton, timePickerButton;
    private MovimentacaoDao movimentacaoDao;
    private int defaultSelectedType = GASTO;
    private List<OnSaveListener> onSaveListeners = new ArrayList<>(1);
    public  interface OnSaveListener {
            void OnSave(AdicionarMovimentacaoDialog dialog, long idMovimentacao);
    }

    public AdicionarMovimentacaoDialog(Context context, String username, MovimentacaoDao movimentacaoDao) {
        super(context);
        this.username = username;
        this.movimentacaoDao = movimentacaoDao;
    }

    public AdicionarMovimentacaoDialog(Context context, String username) {
        this(context, username, FinancasDbHelper.getMovimentacaoDao(context));
    }

    public void setOnSaveListener(OnSaveListener listener) {
        onSaveListeners.add(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_adicionar_movimentacao);
        dataEditText = findViewById(R.id.dataEditText);
        timeEditText = findViewById(R.id.timeEditText);
        valorEditText = findViewById(R.id.valorEditText);
        descricaoEditText = findViewById(R.id.descricaoValueEditText);
        tipoSpinner = findViewById(R.id.tipoSpinner);
        salvarButton = findViewById(R.id.salvarButton);
        cancelarButton = findViewById(R.id.cancelarButton);
        datePickerButton = findViewById(R.id.datePickerButton);
        timePickerButton = findViewById(R.id.timePickerButton);

        Calendar c = Calendar.getInstance();
        setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));

        datePickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = Calendar.getInstance();
                    DatePickerDialog dataPicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            setDate(dayOfMonth, month+1, year);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    dataPicker.show();
                }
        });
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                TimePickerDialog timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setTime(hourOfDay, minute);
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                timePicker.show();
            }
        });

        String[] tipos = new String[]{ this.getContext().getString(R.string.gasto), this.getContext().getString(R.string.recebimento) };
        tipoSpinner.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spinner_item, tipos));
        tipoSpinner.setSelection(defaultSelectedType);
        salvarButton.setOnClickListener(this);
        cancelarButton.setOnClickListener(this);

        // Set the width of the dialog proportional to 90% of the screen width
        Window window = getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    public void setDefaultSelectedType(int selectedType) {
        defaultSelectedType = selectedType;
    }

    private void setDate(int year, int month, int day) {
        dataEditText.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year));
    }

    private void setTime(int hour, int min) {
        timeEditText.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, min));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.salvarButton) {
            salvar();
        } else if(id == R.id.cancelarButton) {
            cancelar();
        }
    }

    public void salvar() {
        try {
            String rawDate = dataEditText.getText().toString();
            String rawTime = timeEditText.getText().toString();
            SimpleDateFormat ddMMyy_hhmmFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            long dateMilli = ddMMyy_hhmmFormatter.parse(rawDate).getTime();
            long timeMilli = 0;
            if(!Text.isBlank(rawTime)) {
                SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm", Locale.getDefault());
                timeFormatter.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
                timeMilli = timeFormatter.parse(rawTime).getTime();
            }
            Date dateTime = new Date(dateMilli + timeMilli);

            double valor = Double.parseDouble(valorEditText.getText().toString());
            String descricao = descricaoEditText.getText().toString();

            int tipo = tipoSpinner.getSelectedItemPosition();
            if(tipo == GASTO) {
                valor = -valor;
            }

            long idMovimentacao = movimentacaoDao.criarMovimentacao(username, valor, descricao, dateTime);
            for (OnSaveListener listener : onSaveListeners) {
                listener.OnSave(this, idMovimentacao);
            }
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelar() {
        cancel();
    }
}
