package com.byethost24.mzaleskis.manoknygos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private Button registerme, submit;
    private EditText vardas, password;

    private CheckBox rememberMeCheckBox;

    private static final String REGISTER_URL = "http://mzaleskis.byethost24.com/demo/mobile/LoginConfirmation.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vardas = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.userpswd);

        RegisterButton();
        Authentication();

    }

    public void RegisterButton() {
        registerme = (Button) findViewById(R.id.userregister);
        registerme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) //kas bus paspaudus Registruotis mygtuka
            {
                Intent goToRegisterWindow = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(goToRegisterWindow);

            } //public void onClick
        }); //submit.setOnClickListener
    }

    public void Authentication() {

        rememberMeCheckBox = (CheckBox) findViewById(R.id.remember_me_id);

        final VartotojoRegistracija dude = new VartotojoRegistracija(getApplicationContext());

        rememberMeCheckBox.setChecked(dude.isRemembered());

        if (dude.isRemembered()) {
            vardas.setText(dude.getUsernameForLogin(), TextView.BufferType.EDITABLE);
            password.setText(dude.getPasswordForLogin(), TextView.BufferType.EDITABLE);
        }else {
            vardas.setText("", TextView.BufferType.EDITABLE);
            password.setText("", TextView.BufferType.EDITABLE);
        }


        vardas.setError(null);
        password.setError(null);

        submit = (Button) findViewById(R.id.usersubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) //kas bus paspaudus submit mygtuka
            {
                String username = vardas.getText().toString();
                String password2 = password.getText().toString();

               /* Toast.makeText(getApplicationContext(),
                        "username:"+username+"\n"+
                        "password:"+password, Toast.LENGTH_SHORT).show();*/

                boolean cancel = false;
                View focusView = null;

                if (!IsValid(username)) {

                    vardas.setError(getString(R.string.login_invalid_username));
                    focusView = vardas;
                    cancel = true;
                }

                if (!IsValid(password2)) {
                    password.setError(getString(R.string.login_invalid_password));
                    focusView = password;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {

                    if (rememberMeCheckBox.isChecked()){
                        dude.setUsernameLogin(username);
                        dude.setPasswordForLogin(password2);
                        dude.setRemembered(true);
                    }else {
                        dude.setUsernameLogin(username);
                        dude.setPasswordForLogin(password2);
                        dude.setRemembered(false);
                    }
                    postToDatabase(dude);
                }
            } //public void onClick
        }); //submit.setOnClickListener

    }

    private boolean IsValid(String credentials) {
        final String CREDENTIALS_PATTERN = "^([0-9a-zA-Z]{3,15})+$";
        Pattern pattern = Pattern.compile(CREDENTIALS_PATTERN);

        Matcher matcher = pattern.matcher (credentials);
        return matcher.matches();
    }

    private void postToDatabase (final VartotojoRegistracija dude){
        class NewEntry extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            DB database = new DB();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Prašome palaukti",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                if (s.equals("202")){
                    Intent myIntent = new Intent(LoginActivity.this, SearchActivity.class);
                    //myIntent.putExtra("key", value); //Optional parameters
                    LoginActivity.this.startActivity(myIntent);
                    Toast.makeText(getApplicationContext(),"paejo " + s,Toast.LENGTH_LONG).show();
                }else if (s.equals("203")){
                    Toast.makeText(getApplicationContext(),"Tokio vartotojo vardo su slaptažodžiu nėra",Toast.LENGTH_LONG).show();
                }
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                // Pirmas string raktas, antras string reiksme
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("username",params[0]);
                data.put("password",params[1]);

                String result = database.sendPostRequest(REGISTER_URL,data);

                return  result;
            }
        }

        NewEntry ru = new NewEntry();
        ru.execute(dude.getUsernameForLogin(), dude.getPasswordForLogin());
    }//onCreate
} //class


