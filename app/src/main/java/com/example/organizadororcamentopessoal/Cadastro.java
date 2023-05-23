package com.example.organizadororcamentopessoal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Cadastro extends AppCompatActivity {
    private EditText edtLoginCadastro, edtNome, edtSenhaCadastro, edtConfirma;
    private Button btnCadastrar;
    private Intent it;
    private DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtLoginCadastro = (EditText) findViewById(R.id.edtLoginCadastro);
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtSenhaCadastro = (EditText) findViewById(R.id.edtSenhaCadastro);
        edtConfirma = (EditText) findViewById(R.id.edtConfirma);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        DB = new DBHelper(this);

        it = getIntent();

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtLoginCadastro.getText().toString();
                String nome = edtNome.getText().toString();
                String sen = edtSenhaCadastro.getText().toString();
                String conf =  edtConfirma.getText().toString();

                if(email.equals ("") || nome.equals("") || sen.equals("") || conf.equals("")){
                    Toast.makeText(Cadastro.this, "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(sen.equals(conf)){
                        Boolean checkUser = DB.checkEmail(email);
                        if(checkUser == false){
                            Boolean insert = DB.insertData(email,sen, nome);
                            if(insert == true){
                                Toast.makeText(Cadastro.this, "Registro feito com sucesso", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent(getApplicationContext(), Home.class);
                                startActivity(it);
                                finish();
                            }else{
                                Toast.makeText(Cadastro.this, "Falha no registro", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(Cadastro.this, "Usuário já existe", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(Cadastro.this, "As senhas não são iguais", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }


}