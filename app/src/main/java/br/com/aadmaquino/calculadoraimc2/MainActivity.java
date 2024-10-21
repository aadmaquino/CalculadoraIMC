package br.com.aadmaquino.calculadoraimc2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText peso, altura;
    private RelativeLayout quadroresult;
    private TextView resultado, situacao;
    private ScrollView scroll;
    protected Button calcular, limpar;

    private double varpeso = 0, varaltura = 0;
    private String resultstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Mostrar ícone do aplicativo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.drawable.calculadoraimc_32);
            getSupportActionBar().setTitle("  " + getText(R.string.app_name));
        }

        // Esconder teclado android na iniciliazação
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Consultar o AdView como um recurso e carregar uma solicitação.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Inicialização da Aplicação
        peso = findViewById(R.id.TxtPeso);
        altura = findViewById(R.id.TxtAltura);
        calcular = findViewById(R.id.BtnCalcular);
        quadroresult = findViewById(R.id.RelativeLayoutResult);
        resultado = findViewById(R.id.LblResultado);
        situacao = findViewById(R.id.LblSituacao);
        limpar = findViewById(R.id.BtnLimpar);
        scroll = findViewById(R.id.ScrollViewMain);

        // Evento do botão Calcular
        calcular.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Esconder teclado android após clicar em Calcular IMC
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                // Validação dos campos
                if (peso.getText().toString().isEmpty() || altura.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.message_error)
                        .setMessage(getText(R.string.message_empty_value).toString())
                        .setCancelable(true)
                        .setNegativeButton(getText(R.string.button_ok_message).toString(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (peso.getText().toString().isEmpty()) {
                                    peso.requestFocus();
                                } else {
                                    altura.requestFocus();
                                }
                            }
                        });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (peso.getText().toString().equals(".") || altura.getText().toString().equals(".")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.message_error)
                        .setMessage(getText(R.string.message_format_value).toString())
                        .setCancelable(true)
                        .setNegativeButton(getText(R.string.button_ok_message).toString(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (peso.getText().toString().equals(".")) {
                                    peso.setText("", TextView.BufferType.NORMAL);
                                    peso.requestFocus();
                                } else {
                                    altura.setText("", TextView.BufferType.NORMAL);
                                    altura.requestFocus();
                                }
                            }
                        });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    // Criação das variáveis
                    varpeso = Double.parseDouble(peso.getText().toString());
                    varaltura = Double.parseDouble(altura.getText().toString());

                    // Verificar se altura é igual a zero
                    if (varaltura == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.message_error)
                            .setMessage(getText(R.string.message_height_zero).toString())
                            .setCancelable(true)
                            .setNegativeButton(getText(R.string.button_ok_message).toString(), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    altura.setText("", TextView.BufferType.NORMAL);
                                    altura.requestFocus();
                                }
                            });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        // Campos - OK. Cálculo do IMC
                        double imc = (varpeso / (varaltura * varaltura));
                        String result = String.format(Locale.US, "%.2f", imc);
                        result = result.replace(".", ",");

                        // Resultado e Situação
                        if (imc < 17) {
                            quadroresult.setBackgroundColor(Color.parseColor("#FF0000"));
                            resultado.setBackgroundColor(Color.parseColor("#FF0000"));
                            resultado.setTextColor(Color.parseColor("#FFFFFF"));
                            resultado.setText(result, TextView.BufferType.NORMAL);
                            situacao.setBackgroundColor(Color.parseColor("#FF0000"));
                            situacao.setTextColor(Color.parseColor("#FFFFFF"));
                            resultstring = getText(R.string.label_situation).toString() + " " + getText(R.string.label_situation_veryunderweight).toString();
                            situacao.setText(resultstring, TextView.BufferType.NORMAL);
                            scroll.fullScroll(View.FOCUS_DOWN);
                        } else if (imc >= 17 && imc <= 18.49) {
                            quadroresult.setBackgroundColor(Color.parseColor("#FFFF00"));
                            resultado.setBackgroundColor(Color.parseColor("#FFFF00"));
                            resultado.setTextColor(Color.parseColor("#000000"));
                            resultado.setText(result, TextView.BufferType.NORMAL);
                            situacao.setBackgroundColor(Color.parseColor("#FFFF00"));
                            situacao.setTextColor(Color.parseColor("#000000"));
                            resultstring = getText(R.string.label_situation).toString() + " " + getText(R.string.label_situation_underweight).toString();
                            situacao.setText(resultstring, TextView.BufferType.NORMAL);
                            scroll.fullScroll(View.FOCUS_DOWN);
                        } else if (imc >= 18.5 && imc <= 24.99) {
                            quadroresult.setBackgroundColor(Color.parseColor("#11772D"));
                            resultado.setBackgroundColor(Color.parseColor("#11772D"));
                            resultado.setTextColor(Color.parseColor("#FFFFFF"));
                            resultado.setText(result, TextView.BufferType.NORMAL);
                            situacao.setBackgroundColor(Color.parseColor("#11772D"));
                            situacao.setTextColor(Color.parseColor("#FFFFFF"));
                            resultstring = getText(R.string.label_situation).toString() + " " + getText(R.string.label_situation_normalweight).toString();
                            situacao.setText(resultstring, TextView.BufferType.NORMAL);
                            scroll.fullScroll(View.FOCUS_DOWN);
                        } else if (imc >= 25 && imc <= 29.99) {
                            quadroresult.setBackgroundColor(Color.parseColor("#FFFF00"));
                            resultado.setBackgroundColor(Color.parseColor("#FFFF00"));
                            resultado.setTextColor(Color.parseColor("#000000"));
                            resultado.setText(result, TextView.BufferType.NORMAL);
                            situacao.setBackgroundColor(Color.parseColor("#FFFF00"));
                            situacao.setTextColor(Color.parseColor("#000000"));
                            resultstring = getText(R.string.label_situation).toString() + " " + getText(R.string.label_situation_overweight).toString();
                            situacao.setText(resultstring, TextView.BufferType.NORMAL);
                            scroll.fullScroll(View.FOCUS_DOWN);
                        } else if (imc >= 30 && imc <= 34.99) {
                            quadroresult.setBackgroundColor(Color.parseColor("#FF0000"));
                            resultado.setBackgroundColor(Color.parseColor("#FF0000"));
                            resultado.setTextColor(Color.parseColor("#FFFFFF"));
                            resultado.setText(result, TextView.BufferType.NORMAL);
                            situacao.setBackgroundColor(Color.parseColor("#FF0000"));
                            situacao.setTextColor(Color.parseColor("#FFFFFF"));
                            resultstring = getText(R.string.label_situation).toString() + " " + getText(R.string.label_situation_obesity1weight).toString();
                            situacao.setText(resultstring, TextView.BufferType.NORMAL);
                            scroll.fullScroll(View.FOCUS_DOWN);
                        } else if (imc >= 35 && imc <= 39.99) {
                            quadroresult.setBackgroundColor(Color.parseColor("#505050"));
                            resultado.setBackgroundColor(Color.parseColor("#505050"));
                            resultado.setTextColor(Color.parseColor("#FFFFFF"));
                            resultado.setText(result, TextView.BufferType.NORMAL);
                            situacao.setBackgroundColor(Color.parseColor("#505050"));
                            situacao.setTextColor(Color.parseColor("#FFFFFF"));
                            resultstring = getText(R.string.label_situation).toString() + " " + getText(R.string.label_situation_obesity2weight).toString();
                            situacao.setText(resultstring, TextView.BufferType.NORMAL);
                            scroll.fullScroll(View.FOCUS_DOWN);
                        } else {
                            quadroresult.setBackgroundColor(Color.parseColor("#000000"));
                            resultado.setBackgroundColor(Color.parseColor("#000000"));
                            resultado.setTextColor(Color.parseColor("#FFFFFF"));
                            resultado.setText(result, TextView.BufferType.NORMAL);
                            situacao.setBackgroundColor(Color.parseColor("#000000"));
                            situacao.setTextColor(Color.parseColor("#FFFFFF"));
                            resultstring = getText(R.string.label_situation).toString() + " " + getText(R.string.label_situation_obesity3weight).toString();
                            situacao.setText(resultstring, TextView.BufferType.NORMAL);
                            scroll.fullScroll(View.FOCUS_DOWN);
                        }
                    }
                }
            }
        });

        // Evento do botão Limpar
        limpar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scroll.fullScroll(View.FOCUS_UP);
                peso.setText("", TextView.BufferType.NORMAL);
                altura.setText("", TextView.BufferType.NORMAL);
                quadroresult.setBackgroundColor(Color.parseColor("#17283A"));
                resultado.setText("", TextView.BufferType.NORMAL);
                situacao.setText("", TextView.BufferType.NORMAL);
                resultado.setBackgroundColor(Color.parseColor("#17283A"));
                situacao.setBackgroundColor(Color.parseColor("#17283A"));
                peso.requestFocus();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_exit);
        builder.setMessage(R.string.action_exit_message);
        builder.setPositiveButton(R.string.button_yes_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.button_no_message, null);
        builder.show();
    }

    // Métodos do Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Informações
        if (id == R.id.action_info) {
            ImageView tableinfoImageView = new ImageView(getApplicationContext());
            tableinfoImageView.setImageResource(R.drawable.tableinfo);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getText(R.string.action_info).toString())
                .setView(tableinfoImageView)
                .setMessage("")
                .setCancelable(true)
                .setNegativeButton(getText(R.string.button_ok_message).toString(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        // Sobre
        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final TextView websiteTextView = new TextView(builder.getContext());
            final SpannableString webText = new SpannableString("aadmaquino.com.br");
            Linkify.addLinks(webText, Linkify.WEB_URLS);
            websiteTextView.setText(webText);
            websiteTextView.setMovementMethod(LinkMovementMethod.getInstance());
            websiteTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            builder.setTitle(getText(R.string.action_about).toString());
            if (Build.VERSION.SDK_INT >= 24) {
                builder.setMessage(Html.fromHtml("<br><b>" + getText(R.string.app_name).toString() + "<br><br>" + getText(R.string.action_about_message_version).toString() + "</b> " + BuildConfig.VERSION_NAME + "<br><b>" + getText(R.string.action_about_message_developedby).toString() + "</b> " + getText(R.string.action_about_message_author).toString() + "<br>", Html.FROM_HTML_MODE_COMPACT));
            } else {
                builder.setMessage(Html.fromHtml("<b>" + getText(R.string.app_name).toString() + "<br><br>" + getText(R.string.action_about_message_version).toString() + "</b> " + BuildConfig.VERSION_NAME + "<br><b>" + getText(R.string.action_about_message_developedby).toString() + "</b> " + getText(R.string.action_about_message_author).toString() + "<br>"));
            }

            builder.setView(websiteTextView);
            builder.setPositiveButton(R.string.button_ok_message, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Não faça nada
                }
            });
            builder.show();
            return true;
        }

        // Sair
        if (id == R.id.action_exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.action_exit);
            builder.setMessage(R.string.action_exit_message);
            builder.setPositiveButton(R.string.button_yes_message, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton(R.string.button_no_message, null);
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}