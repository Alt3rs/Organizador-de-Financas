package com.organizadororcamentopessoal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.home.HomeFragment;
import com.organizadororcamentopessoal.login.CadastroActivity;
import com.organizadororcamentopessoal.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnCadastrar, btnEntrar;
    private Intent it;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEntrar = findViewById(R.id.btnEntrar);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        it = getIntent();
    }

    public void entrar(View view){
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);

    }

    public void cadastrar(View view){
        Intent it = new Intent(this, CadastroActivity.class);
        startActivity(it);
    }
}