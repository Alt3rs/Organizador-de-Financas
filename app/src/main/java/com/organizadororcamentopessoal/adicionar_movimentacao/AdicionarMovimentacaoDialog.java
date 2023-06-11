package com.organizadororcamentopessoal.adicionar_movimentacao;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.MovimentacaoDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdicionarMovimentacaoDialog extends Dialog implements View.OnClickListener {
    public static final int GASTO = 0, RECEBIMENTO = 1;
    private String username;
    private EditText dataEditText, valorEditText, descricaoEditText;
    private Spinner tipoSpinner;
    private Button salvarButton, cancelarButton;
    private MovimentacaoDao movimentacaoDao;
    private int defaultSelectedType = GASTO;

    public AdicionarMovimentacaoDialog(Context context, String username, MovimentacaoDao movimentacaoDao) {
        super(context);
        this.username = username;
        this.movimentacaoDao = movimentacaoDao;
    }

    public AdicionarMovimentacaoDialog(Context context, String username) {
        this(context, username, FinancasDbHelper.getMovimentacaoDao(context));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_adicionar_movimentacao);
        dataEditText = findViewById(R.id.dataEditText);
        valorEditText = findViewById(R.id.valorEditText);
        descricaoEditText = findViewById(R.id.descricaoValueEditText);
        tipoSpinner = findViewById(R.id.tipoSpinner);
        salvarButton = findViewById(R.id.salvarButton);
        cancelarButton = findViewById(R.id.cancelarButton);

        String tipos[] = new String[]{ this.getContext().getString(R.string.gasto), this.getContext().getString(R.string.recebimento) };
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
            String rawData = dataEditText.getText().toString();
            SimpleDateFormat ddMMyy_hhmmFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat hhmmFormatter = new SimpleDateFormat("hh:mm", Locale.getDefault());
            Date data = ddMMyy_hhmmFormatter.parse(rawData);
            double valor = Double.parseDouble(valorEditText.getText().toString());
            String descricao = descricaoEditText.getText().toString();
            int tipo = tipoSpinner.getSelectedItemPosition();
            if(tipo == GASTO) {
                valor = -valor;
            } else if(tipo == RECEBIMENTO){
            } else {
                Toast.makeText(this.getContext(), "Selecione alguma modalidade", Toast.LENGTH_SHORT);
                return;
            };
            movimentacaoDao.criarMovimentacao(username, valor, descricao, data);
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelar() {
        cancel();
    }


    //    public static AlertDialog createDialog(Context context, LiveData<String> username, MovimentacaoDao movimentacaoDao, int defaultSelection) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_adicionar_movimentacao, null))
//                .setPositiveButton(R.string.salvar, (dialogInterface, id) -> {
//                    AlertDialog dialog = (AlertDialog) dialogInterface;
//                    EditText data = dialog.findViewById(R.id.dataEditText);
//                    EditText valor = dialog.findViewById(R.id.valorEditText);
//                    EditText descricao = dialog.findViewById(R.id.descricaoValueEditText);
//                    Spinner tipoSpinner = dialog.findViewById(R.id.tipoSpinner);
//
//                })
//                .setNegativeButton(R.string.cancelar, (dialogInterface, id) -> {
//                    AlertDialog dialog = (AlertDialog) dialogInterface;
//                    dialog.cancel();
//                });
//        AlertDialog dialog = builder.create();
//        Spinner tipoSpinner = dialog.findViewById(R.id.tipoSpinner);
//        String tipos[] = new String[]{ context.getString(R.string.gasto), context.getString(R.string.recebimento) };
//        tipoSpinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tipos));
//        tipoSpinner.setSelection(defaultSelection);
//        return dialog;
//    }
}
