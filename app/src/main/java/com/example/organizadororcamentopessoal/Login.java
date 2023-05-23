package com.example.organizadororcamentopessoal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private EditText edtLogin, edtSenha;
    private Button btnEntrar;
    private Intent it;
    private DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtLogin = (EditText) findViewById(R.id.edtLogin);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
        btnEntrar = (Button) findViewById(R.id.btnEntrar1);
        DB = new DBHelper(this);

        it = getIntent();

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtLogin.getText().toString();
                String sen = edtSenha.getText().toString();
                if(email.equals("") || sen.equals("")){
                    Toast.makeText(Login.this, "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
                else{
                    Boolean checkuserpass = DB.checkUsernameSenha(email,sen);
                    if(checkuserpass == true){
                        Toast.makeText(Login.this, "Login feito com sucesso", Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(getApplicationContext(), Home.class);
                        startActivity(it);
                        finish();
                    }else{
                        Toast.makeText(Login.this, "Login invalido", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }


}