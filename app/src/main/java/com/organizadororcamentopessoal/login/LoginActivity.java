package com.organizadororcamentopessoal.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.organizadororcamentopessoal.datasource.DatabaseContract;
import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.UserDao;
import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.home.HubActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edtLogin, edtSenha;
    private Button btnEntrar;
    private UserDao DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtLogin = findViewById(R.id.edtLogin);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar1);
        DB = FinancasDbHelper.getUserDao(getApplicationContext());

        btnEntrar.setOnClickListener((view) ->{
            String username = edtLogin.getText().toString().trim();
            String sen = edtSenha.getText().toString();
            if(username.trim().isEmpty() || sen.trim().isEmpty()){
                Toast.makeText(LoginActivity.this, "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
            }else if(DB.checkUsernameSenha(username,sen)){
                Toast.makeText(LoginActivity.this, "Login feito com sucesso", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getApplicationContext(), HubActivity.class);
                it.putExtra(DatabaseContract.UsuarioTable.USERNAME, username);
                startActivity(it);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "Login invalido", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
