package com.example.leonardodruid.uailistar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.Principal;

public class MainActivity extends AppCompatActivity {

    private Button button_LoginEmail, button_CadastrarEmail;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_LoginEmail = (Button) findViewById(R.id.button_LoginEmail);
        button_CadastrarEmail = (Button) findViewById(R.id.button_CadastrarEmail);

        auth = FirebaseAuth.getInstance();

        //Verificar se o usuario esta Logado( Codigo la em baixo)

        estadoAuthenticacao();

        //Logar em Activity
        button_LoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user= auth.getCurrentUser();

                if (user == null) {

                    Intent intent_LoginEmail = new Intent(getApplicationContext(),Login_Email.class);
                    startActivity(intent_LoginEmail);

                }else{

                    Intent intent_LoginTelaPrincipal = new Intent(getApplicationContext(),PrincipalUaiListar_Activity .class);
                    startActivity(intent_LoginTelaPrincipal);
                }
            }
        });

        button_CadastrarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_CadastrarEmail = new Intent(getApplicationContext(), Cadastrar_Email.class);
                startActivity(intent_CadastrarEmail);

            }
        });

    }

    //Verificar se o usuario esta logado
    private void estadoAuthenticacao(){

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){

                    Toast.makeText(getBaseContext(), "Usuario: " + user.getEmail() +
                            " Está Logado", Toast.LENGTH_LONG).show();

                }else{


                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null) {

            auth.removeAuthStateListener(authStateListener);
        }
    }
}
