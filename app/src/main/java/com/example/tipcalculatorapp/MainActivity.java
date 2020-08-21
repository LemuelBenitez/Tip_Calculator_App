package com.example.tipcalculatorapp;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener,RadioGroup.OnClickListener{

    //declare your variables for the widgets
    EditText editTextBillAmount;
    SeekBar tipSeek;
    TextView textViewBillAmount;
    TextView showPercentage;
    TextView amountOfTip;
    TextView perAmount;
    TextView perPerson;
    public static final String TAG = "MainActivity";
    private String spinnerLabel = "";
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    RadioGroup radioGroup;
    RadioButton keep;
    RadioButton tip;
    RadioButton total;
    Button button;


    //declare the variables for the calculations
    private double billAmount = 0.0;
    private double percent = .00;

    //set the number formats to be used for the $ amounts , and % amounts
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //add Listeners to Widgets
        editTextBillAmount = (EditText)findViewById(R.id.editText_BillAmount);
 /*  Note: each View that will be retrieved using findViewById needs to map to a View with the matching id
Example: editTextBillAmount
Needs to map to a View with the following: android:id="@+id/editText_BillAmount
*/      perAmount = (TextView)findViewById(R.id.perPersonTotal);
        tipSeek = (SeekBar)findViewById(R.id.seekBar);
        textViewBillAmount = findViewById(R.id.totalAmount);
        amountOfTip = (TextView) findViewById(R.id.tipAmount);
        showPercentage = (TextView)findViewById(R.id.precentage);
        perPerson = (TextView)findViewById(R.id.perPersonTotal);
        perPerson.addTextChangedListener((TextWatcher) this);
        showPercentage.addTextChangedListener((TextWatcher) this);
        editTextBillAmount.addTextChangedListener((TextWatcher) this);
        tipSeek.setOnSeekBarChangeListener(this);
        spinner = (Spinner)findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.numPerson, android.R.layout.simple_spinner_item);
        if(spinner != null){
            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
        }
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        keep = (RadioButton)findViewById(R.id.radio_keep);
        tip = (RadioButton)findViewById(R.id.radio_tip);
        total = (RadioButton)findViewById(R.id.radio_total);
        button = (Button)findViewById(R.id.button_refresh);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    /*
    Note:   int i, int i1, and int i2
            represent start, before, count respectively
            The charSequence is converted to a String and parsed to a double for you
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("MainActivity", "inside onTextChanged method: charSequence= "+charSequence);

        try {
            //surround risky calculations with try catch (what if billAmount is 0 ?
            //charSequence is converted to a String and parsed to a double for you
            billAmount = Double.parseDouble(charSequence.toString());// 100;
            Log.d("MainActivity", "Bill Amount = " + billAmount);
            //setText on the textView
            textViewBillAmount.setText(currencyFormat.format(billAmount));
            perPerson.setText(currencyFormat.format(billAmount));
            //perform tip and total calculation and update UI by calling calculate
            calculate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        //try {
            Log.d("msg", "seekBar");
            percent = progress / 100.0; //calculate percent based on seeker value
            calculate();
        //}catch (Exception e){
          //  e.printStackTrace();
       // }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }



    // calculate and display tip and total amounts
    private void calculate() {
        Log.d("MainActivity", "inside calculate method");
        //uncomment below

       // format percent and display in percentTextView
      showPercentage.setText(percentFormat.format(percent));

       // calculate the tip and total
       double tip = billAmount * percent;
       double total = billAmount + tip;
      //use the tip example to do the same for the Total

       // display tip and total formatted as currency
       //user currencyFormat instead of percentFormat to set the textViewTip
       amountOfTip.setText(currencyFormat.format(tip));
        textViewBillAmount.setText(currencyFormat.format(total));
       if(radioGroup.callOnClick() || tipSeek.callOnClick()|| editTextBillAmount.callOnClick()){
           switch(radioGroup.getCheckedRadioButtonId()){
               case (R.id.radio_keep):
                   keepTip();
                   break;
               case (R.id.radio_tip):
                   tipTip();
                   break;
               case (R.id.radio_total):
                   totalTip();
                   break;
               default:
                   perPerson.setText(currencyFormat.format(total));
           }
       }
        //use the tip example to do the same for the Total
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.share_menu:
              try {
                  double tip = billAmount * percent;
                  double total = billAmount + tip;
                  Intent sendIntent = new Intent();
                  sendIntent.setAction(Intent.ACTION_SEND);
                  sendIntent.putExtra(Intent.EXTRA_TEXT,"The bill amount is being split evenly");
                  sendIntent.setType("text/plain");

                  Intent shareIntent = Intent.createChooser(sendIntent, null);
                  startActivity(shareIntent);
                  break;
              }catch (Exception e){
                  e.printStackTrace();
                  break;
              }
            case R.id.info_menu:
                openDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialog(){
     infoDialog show = new infoDialog();
     show.show(getSupportFragmentManager(),"info dialog");
    }
    int num;
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d(TAG,"start of spinner");
        spinnerLabel = adapterView.getItemAtPosition(position).toString();
        this.num = Integer.valueOf(spinnerLabel);
        Log.d(TAG,"start of spinner");
    }

    public int getNum(){
        return this.num;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
      switch(view.getId()){
          case(R.id.radio_keep):
              Toast toast = Toast.makeText(this,"You Pressed Keep",Toast.LENGTH_SHORT);
              toast.show();
              keepTip();
              break;
          case(R.id.radio_tip):
              Toast toe = Toast.makeText(this,"You Pressed Tip",Toast.LENGTH_SHORT);
              toe.show();
              tipTip();
              break;
          case(R.id.radio_total):
              Toast total = Toast.makeText(this,"You Pressed Total",Toast.LENGTH_SHORT);
              total.show();
              totalTip();
              break;
      }


    }
    public void keepTip(){
        double tip = (billAmount * percent);
        double total = (billAmount + tip)/getNum();

        perPerson.setText(currencyFormat.format(total));
    }

    public void  tipTip(){
        double tip = (billAmount * percent);
        tip = Math.round(tip);
        double total = (billAmount + tip)/getNum();

        perPerson.setText(currencyFormat.format(total));
    }
    public void  totalTip(){
        double tip = (billAmount * percent);
        double total = (billAmount + tip)/getNum();
        total = Math.round(total);
        perPerson.setText(currencyFormat.format(total));
    }
   /* Expand your TipCalculator App
    //first page//Add a Spinner to select the number of people splitting the total (1,2,3,4…) 1 is the default value
    //Use switch-case for this and have in a method// first page// Add Radio Buttons to select rounding options. (the bill will always remain exact)
            “No” (the exact bill, tip, total will be used in calculations)
            “Tip” (the tip will be rounded up before added to the bill to calculate the exact total)
            “Total” (the bill and tip remain exact, but the total will be rounded up)
    Add a TextView to display the Per Person Total amount calculated
   //first page// Add an Options Menu to the App Bar to present the user with 2 options (Share and Info)
    Share
    //second page; when clicked// with a return button//Sends a message containing the bill,tip,total and per person info to friend(s).
    Info
   //small box when hovered over or clicked// Displays a Dialog explaining the Spinner is used to split the total among friends.*/


}

