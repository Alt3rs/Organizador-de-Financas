package com.organizadororcamentopessoal.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.datasource.DatabaseContract;
import com.organizadororcamentopessoal.datasource.FinancasDbHelper;
import com.organizadororcamentopessoal.datasource.UserDao;
import com.organizadororcamentopessoal.home.HubActivity;

public class CadastroActivity extends AppCompatActivity {
    private EditText edtLoginCadastro, edtNome, edtSenhaCadastro, edtConfirma;
    private Button btnCadastrar;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtLoginCadastro = findViewById(R.id.edtLoginCadastro);
        edtNome = findViewById(R.id.edtNome);
        edtSenhaCadastro = findViewById(R.id.edtSenhaCadastro);
        edtConfirma = findViewById(R.id.edtConfirma);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        userDao = FinancasDbHelper.getUserDao(getApplicationContext());

        btnCadastrar.setOnClickListener((view) -> {
            String email = edtLoginCadastro.getText().toString().trim();
            String nome = edtNome.getText().toString().trim();
            String senha = edtSenhaCadastro.getText().toString();
            String conf =  edtConfirma.getText().toString();

            if(isBlank(email)|| isBlank(nome) || isBlank(senha) || isBlank(conf)){
                Toast.makeText(CadastroActivity.this, "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!senha.equals(conf)){
                Toast.makeText(CadastroActivity.this, "As senhas não são iguais", Toast.LENGTH_SHORT).show();
                return;
            }
            if(userDao.isUsernameRegistered(email)){
                Toast.makeText(CadastroActivity.this, "Usuário já existe", Toast.LENGTH_SHORT).show();
                return;
            }
            if(userDao.createUser(email,senha, nome)){
                Toast.makeText(CadastroActivity.this, "Registro feito com sucesso", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(CadastroActivity.this, HubActivity.class);
                it.putExtra(DatabaseContract.UsuarioTable.USERNAME, nome);
                startActivity(it);
                finish();
            } else {
                Toast.makeText(CadastroActivity.this, "Falha no registro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static boolean isBlank(String s) {
        return s.trim().isEmpty();
    }
}