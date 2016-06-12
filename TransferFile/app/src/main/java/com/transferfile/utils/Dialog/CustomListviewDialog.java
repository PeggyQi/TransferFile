package com.transferfile.utils.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.transferfile.R;
import com.transferfile.utils.Dialog.style.Circle;

public class CustomListviewDialog extends Dialog {
    Context mContext;
    private Circle mCircleDrawable;
    public CustomListviewDialog(Context context){
        super(context);
        mContext = context;
    }
    public CustomListviewDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View layout = LayoutInflater.from(mContext).
                inflate(R.layout.activity_dialoglistview, null);

        TextView textView = (TextView) layout.findViewById(R.id.text);
        textView.getBackground().setAlpha(100);
        mCircleDrawable = new Circle();
        mCircleDrawable.setBounds(0, 0, 100, 100);
        mCircleDrawable.setColor(Color.WHITE);
        textView.setCompoundDrawables(null, null, mCircleDrawable, null);
        mCircleDrawable.start();
        this.setContentView(layout);
    }
}
