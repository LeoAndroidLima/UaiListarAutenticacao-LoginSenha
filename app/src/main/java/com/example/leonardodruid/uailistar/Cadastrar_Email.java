package com.example.leonardodruid.uailistar;

import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Cadastrar_Email extends AppCompatActivity {

    private EditText editText_CadastrarEmail, editText_CadastrarSenha, editText_CadastrarRepetirSenha;
    private Button button_CadastrarEmail, button_VoltarCadastrar;
    private FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar__email);



        editText_CadastrarEmail = (EditText)findViewById(R.id.editText_CadastrarEmail);
        editText_CadastrarSenha = (EditText)findViewById(R.id.editText_CadastrarSenha);
        editText_CadastrarRepetirSenha = (EditText) findViewById(R.id.editText_CadastrarRepetirSenha);

        button_CadastrarEmail = (Button) findViewById(R.id.button_CadastrarEmail);
        button_VoltarCadastrar = (Button) findViewById(R.id.button_VoltarCadastrar);

        auth = FirebaseAuth.getInstance();

        button_CadastrarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editText_CadastrarEmail.getText().toString().trim();
                String senha = editText_CadastrarSenha.getText().toString().trim();
                String confirmaSenha = editText_CadastrarRepetirSenha.getText().toString().trim();

                if (email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {

                    Toast.makeText(getBaseContext(), "Error Preencha os campos"
                            ,Toast.LENGTH_LONG).show();

                }else{

                    //Verificação de possiveis erros
                    if (senha.contentEquals(confirmaSenha)){

                        if (UtilErrosVerificar.verificarInternet(getBaseContext())){

                            ConnectivityManager conexao = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                            //Criar Usuario(Codigo La em baixo)
                            criarUsuario(email,senha);

                        }else {

                            Toast.makeText(getBaseContext(), "Algo deu Errado! Verifique se o WIFI ou a Internet do aparelho",
                                    Toast.LENGTH_LONG).show();

                        }
                    }else{
                        Toast.makeText(getBaseContext(),"Senhas Diferentes",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }
            private void criarUsuario (String email, String senha) {

                auth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

                                    Toast.makeText(getBaseContext(),"Cadastro efetuado com Sucesso"
                                    ,Toast.LENGTH_LONG).show();

                                    finish();

                                }else{

                                    String resposta = task.getException().toString();
                                    UtilErrosVerificar.opcoesErro(getBaseContext(),resposta);
                                }


                            }
                        });
            }
        });
    }
}
