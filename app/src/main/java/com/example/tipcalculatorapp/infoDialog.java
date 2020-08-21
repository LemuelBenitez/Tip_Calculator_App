package com.example.tipcalculatorapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;

public class infoDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Information")
               .setMessage("A Spinner is used to split the total amongst friends.");
         return builder.create();

    }

}