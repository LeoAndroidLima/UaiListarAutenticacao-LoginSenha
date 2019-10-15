package com.example.leonardodruid.uailistar;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RecuperarSenha_Activity extends AppCompatActivity {

    private Button button_ContinuarRecuperarSenha;
    private EditText editText_RecuperarSenha;

    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha_);

        auth = FirebaseAuth.getInstance();

        button_ContinuarRecuperarSenha = (Button) findViewById(R.id.button_ContinuarRecuperarSenha);
        editText_RecuperarSenha = (EditText) findViewById(R.id.editText_RecuperarSenha);


        button_ContinuarRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editText_RecuperarSenha.getText().toString().trim();

                if (email.isEmpty()){

                    Toast.makeText(getBaseContext(), "Insira seu e-mail para Recuperar sua senha",
                            Toast.LENGTH_LONG).show();
                }else{

                    enviarEmail(email);
                }

            }

            private void enviarEmail(String email){

                auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getBaseContext(), "Enviamos uma mensagem para o seu e-mail com um link para você refefinir a sua senha",
                                Toast.LENGTH_LONG).show();

                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String erro = e.toString();

                        UtilErrosVerificar.opcoesErro(getBaseContext(),erro);
                    }
                });
            }

        });

    }
}
