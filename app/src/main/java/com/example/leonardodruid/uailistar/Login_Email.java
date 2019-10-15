package com.example.leonardodruid.uailistar;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class Login_Email extends AppCompatActivity {


    private EditText editText_EmailLogin, editText_SenhaEmail;
    private Button button_EntrarEmail, button_CadastrarEmail;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;
    private CardView cardView_LoginGoogle, cardView_LoginFacebook, cardView_LoginAnonimo;

    private FirebaseAuth.AuthStateListener authStateListener;

    private CallbackManager callbackManager;

    //Text Button para recuperar senha-------------------------------------------------------------

    public void onClick_EsqueceuLogin(View view){

        Intent intent_RecuperarSenha = new Intent(getApplicationContext(),RecuperarSenha_Activity.class);
        startActivity(intent_RecuperarSenha);

    }


    //---------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__email);

        editText_EmailLogin = (EditText) findViewById(R.id.editText_EmailLogin);
        editText_SenhaEmail = (EditText) findViewById(R.id.editText_SenhaEmail);

        button_EntrarEmail = (Button) findViewById(R.id.button_EntrarEmail);
        button_CadastrarEmail = (Button) findViewById(R.id.button_CadastrarEmail);
        cardView_LoginGoogle= (CardView) findViewById(R.id.cardView_LoginGoogle);
        cardView_LoginFacebook = (CardView) findViewById(R.id.cardView_LoginFacebook);
        cardView_LoginAnonimo = (CardView) findViewById(R.id.cardView_LoginAnonima);

        auth = FirebaseAuth.getInstance();

        //Verificar se o usuario esta logado
        servicoAuthenticacao();

        //Entra com o Google
        servicosGoogle();

        //Entrar com o Facebook
        servicosFacebook();

        //Logar se o usuario estiver Logado
        user= auth.getCurrentUser();

        /*GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);



        if (account == null){


        }else{

            Intent intent_LoginTelaPrincipal = new Intent(getApplicationContext(),PrincipalUaiListar_Activity.class);
            startActivity(intent_LoginTelaPrincipal);

            finish();
        }


        if (user == null) {


        }else{
            Intent intent_LoginTelaPrincipal = new Intent(getApplicationContext(),PrincipalUaiListar_Activity.class);
            startActivity(intent_LoginTelaPrincipal);

            finish();

        }*/

        //--------------------------------------TRATAMENTO DE CLICKS-----------------------------------

        button_CadastrarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_CadastrarEmail = new Intent(getApplicationContext(), Cadastrar_Email.class);
                startActivity(intent_CadastrarEmail);

            }
        });

        cardView_LoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInGoogle();

            }
        });

        cardView_LoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInFacebook();

            }
        });

        cardView_LoginAnonimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInAnonimo();

            }
        });



        button_EntrarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editText_EmailLogin.getText().toString().trim();
                String senha = editText_SenhaEmail.getText().toString().trim();

                if (email.isEmpty() || senha.isEmpty()) {

                    Toast.makeText(getBaseContext(), "Insira Email ou Senha",
                            Toast.LENGTH_LONG).show();

                }else{

                    if (UtilErrosVerificar.verificarInternet(getBaseContext())){

                        ConnectivityManager conexao = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                        //Confirmar Login e senha(Codigo mais abaixo)
                        confirmarLoginEmail(email, senha);

                    }else{

                        Toast.makeText(getBaseContext(), "Algo deu Errado! Verifique se o WIFI ou a Internet do aparelho",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            //----------------------------------------CONFIRMAÇÃO DE E-MAIL COM POSSIVEIS ERROS---------------------

            private void confirmarLoginEmail(String email, String senha) {

                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                           startActivity(new Intent(getBaseContext(), PrincipalUaiListar_Activity.class));
                           Toast.makeText(getBaseContext(), "Usuario Logado com Sucesso",
                                   Toast.LENGTH_LONG).show();
                           finish();

                        }else{

                            String resposta = task.getException().toString();

                            //Criar opções de erro com resposta (Codigo mais abaixo)
                            opcoesErro(resposta);
                        }
                    }
                });
            }
            private void opcoesErro(String resposta){

                if (resposta.contains("interrupted connection")) {

                    Toast.makeText(getBaseContext(), "Sem conexão Tente novamente mais tarde",
                            Toast.LENGTH_LONG).show();

                }else{

                    Toast.makeText(getBaseContext(), "Algo deu ERRADO! Email ou senha incorretos",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //---------------------------------------------SERVIÇOS LOGIN--------------------------------

    private void servicoAuthenticacao(){

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){

                    Toast.makeText(getBaseContext(), "Usuario: " +user.getEmail() + "Está Logado",Toast.LENGTH_LONG).show();

                    startActivity(new Intent(getBaseContext(),PrincipalUaiListar_Activity.class));

                }else{


                }
            }
        };

    }

    //Iniciar serviços autenticação google
    private void servicosGoogle(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                //.requestServerAuthCode(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void servicosFacebook(){

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                AdicionarContaFacebookaoFirebase(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

                Toast.makeText(getBaseContext(), "Cancelado", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {

                String resultado = error.getMessage();

                UtilErrosVerificar.opcoesErro(getBaseContext(),resultado);

            }
        });

    }

    //-------------------------------------------------METODOS DE LOGIN---------------------------

    private void signInAnonimo(){

        acessarContaAnonimaaoFirebase();

    }

    private void signInFacebook(){

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"));

    }


    private void signInGoogle(){

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account == null){

        Intent intent_Google = googleSignInClient.getSignInIntent();

        startActivityForResult(intent_Google, 555);

        }else{

            //já existe alguem conectado pelo google
            Toast.makeText(getBaseContext(), "Usuario já esta Logado", Toast.LENGTH_LONG).show();


        }

    }

//-----------------------------------------------AUTENTICAÇÃO NO FIREBASE------------------------------------

    private void acessarContaAnonimaaoFirebase(){

        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            startActivity(new Intent(getBaseContext(), PrincipalUaiListar_Activity.class));
                            finish();

                        } else {

                            String resultado = task.getException().toString();
                            UtilErrosVerificar.opcoesErro(getBaseContext(),resultado);

                        }
                    }
                });

    }

    private void AdicionarContaFacebookaoFirebase(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            startActivity(new Intent(getBaseContext(), PrincipalUaiListar_Activity.class));
                            finish();

                        } else {

                            String resultado = task.getException().toString();
                            UtilErrosVerificar.opcoesErro(getBaseContext(),resultado);

                        }

                        // ...
                    }
                });
    }

    private void AdicionarContaGoogleaoFirebase(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            startActivity(new Intent(getBaseContext(), PrincipalUaiListar_Activity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getBaseContext(), "Erro ao Criar Conta Google",Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

//-----------------------------------------------METODOS DA ACTIVITY-------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 555){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);



            try {


                GoogleSignInAccount account = task.getResult(ApiException.class);

                AdicionarContaGoogleaoFirebase(account);






            }catch (ApiException e){

                String resultado = e.getMessage();

                UtilErrosVerificar.opcoesErro(getBaseContext(),resultado);

        }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null){

            auth.removeAuthStateListener(authStateListener);
        }
    }
}
