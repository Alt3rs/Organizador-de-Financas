package com.example.organizadororcamentopessoal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Home extends AppCompatActivity {

    private TextView txtData;
    private Button btnResumo, btnCalendario, btnMovimentacao, btnConfig;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    private DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtData = (TextView) findViewById(R.id.txtData);
        btnResumo = (Button) findViewById(R.id.btnResumo);
        btnCalendario = (Button) findViewById(R.id.btnCalendario);
        btnMovimentacao = (Button) findViewById(R.id.btnMovimentacao);
        btnConfig = (Button) findViewById(R.id.btnConfig);

        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(calendar.getTime());
        txtData.setText(date);

        btnResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), Resumo.class);
                startActivity(it);
            }
        });

        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), Calendario.class);
                startActivity(it);
            }
        });

        btnMovimentacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), Movimentacao.class);
                startActivity(it);
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), Configuracao.class);
                startActivity(it);
            }
        });
    }
}