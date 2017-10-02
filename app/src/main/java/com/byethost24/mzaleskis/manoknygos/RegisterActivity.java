package com.byethost24.mzaleskis.manoknygos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byethost24.mzaleskis.manoknygos.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends Activity {

    // kelias byethost iki register.php failiuko
    private static final String REGISTER_URL = "http://mzaleskis.byethost24.com/demo/mobile/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText regName = (EditText) findViewById(R.id.regusername);
        final EditText regPass = (EditText) findViewById(R.id.reguserpswd);
        final EditText regMail = (EditText) findViewById(R.id.reguseremail);
        regName.setError(null);

        Button regSend = (Button) findViewById(R.id.regusersubmit);
        regSend.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                boolean cancel = false;
                View focusView = null;

                //Registracija(String regiVardas, String regiEmail, String regiSlaptazodis)
                //Registracija naujaReg = new Registracija(regName.getText().toString(), regMail.getText().toString(), regPass.getText().toString());

                if (TextUtils.isEmpty(regName.getText().toString())) {
                    regName.setError("Neįvestas vardas");
                    focusView = regName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(regPass.getText().toString())) {
                    regPass.setError("Neįvestas slaptažodis");
                    focusView = regPass;
                    cancel = true;
                }
                if (TextUtils.isEmpty(regMail.getText().toString())) {
                    regMail.setError("Neįvestas el. paštas");
                    focusView = regMail;
                    cancel = true;
                }
                if (cancel) {
                    focusView.requestFocus();
                }else {

                    registerUser(regName.getText().toString(), regPass.getText().toString(), regMail.getText().toString());
                    //Toast.makeText(RegisterActivity.this, naujaReg.getRegiVardas() + "\n" + naujaReg.getRegiEmail(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);

                    Intent goToLoginWindow = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(goToLoginWindow);

                }
            }

        });
    }

    private void registerUser(String username, String password, String email) {
        String urlSuffix = "?username="+username+"&password="+password+"&email="+email;
        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this, "Prašome palaukti",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(REGISTER_URL+s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    // byethost naudoja antibot sistema, todel reikia kiekvienam rankutėmis suvesti cookie turinį,
                    // kuris pas kiekviena bus skirtingas. kaip tai padaryti zemiau nuoroda
                    // http://stackoverflow.com/questions/31912000/byethost-server-passing-html-values-checking-your-browser-with-json-string
                    con.setRequestProperty("Cookie", "__test=7a4d917e220fbf9a55cab3046bd1a3b7; expires=2038 m. sausio 1 d., penktadienis 01:55:55; path=/");
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                }catch(Exception e){
                    return null;
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute(urlSuffix);
    }
}
