package com.atlanticcity.app.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.atlanticcity.app.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
       // setIndeterminate(true);
      //  setMessage(context.getResources().getString(R.string.please_wait));
      setContentView(R.layout.custom_dialog);
    }
}
