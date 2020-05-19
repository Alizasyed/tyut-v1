package com.lumos.durshaltyut;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class signup extends AppCompatActivity {

    private TextInputEditText mPhone;
    private TextInputLayout mLayout;
    static String user_type = " ";
    TextView ed, welcome, logintext;

    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        logintext = findViewById(R.id.login_textview);
        mLayout = findViewById(R.id.mLayout);
        welcome = findViewById(R.id.wel_text);
        Bundle bundle = getIntent().getExtras();
        user_type = bundle.getString("type");

        ccp = (CountryCodePicker) findViewById(R.id.ccp);

//        if ((bundle.getString("type").equals("null")))
//        {
//            bundle.putString("type", "student");
//            user_type = bundle.getString("type");
//        }
//        else
//        {
//            user_type = bundle.getString("type");
//
//        }

        ed = findViewById(R.id.teachtxt);
        ed.setClickable(true);

        if (user_type == null || user_type.equals("student")) {
            Intent intent = new Intent();
            welcome.setText(R.string.start_learning_with_tyut);
            intent.putExtra("type", "student");
            ed.setText("Become an educator");


        } else if (user_type.equals("educator")) {
            Intent intent = new Intent();
            intent.putExtra("type", "educator");
            welcome.setText(R.string.start_earning_with_tyut);
            ed.setText("Need help with your studies? Learn with tyut");

        }

        // spinner = findViewById(R.id.spinnerCountries);
        //  spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,CountryData.countryNames));

        mPhone = findViewById(R.id.mPhone);

        findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = mPhone.getText().toString().trim();
                String code = "92";
                //       String code = ccp.getSelectedCountryCode();

                if (number.isEmpty() || number.length() < 10) {
                    mPhone.setError("Valid number required.");
                    mPhone.requestFocus();
                    return;
                }

                String phoneNumber = "+" + code + number;

                Intent intent = new Intent(signup.this, verifyphone.class);
                intent.putExtra("phonenumber", phoneNumber);
                intent.putExtra("type", user_type);
                startActivity(intent);
            }
        });


        ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user_type == null || user_type.equals("student")) {

                    Intent intent = new Intent(signup.this, signup.class);
                    intent.putExtra("type", "educator");
                    startActivity(intent);

                } else if (user_type.equals("educator")) {
                    Intent intent = new Intent(signup.this, signup.class);
                    intent.putExtra("type", "student");
                    startActivity(intent);
                }
            }
        });

    }


    public void login_new(View view) {

        if (user_type.equals("student")) {
            Intent i = new Intent(signup.this, login.class);
            i.putExtra("type", "student");
            startActivity(i);

        } else if (user_type.equals("educator")) {
            Intent i = new Intent(signup.this, login.class);
            i.putExtra("type", "educator");
            startActivity(i);


        }
    }

    public void educator(View view) {
        Intent i = new Intent(signup.this, signup.class);
        i.putExtra("type", "educator");
        startActivity(i);

    }
}
