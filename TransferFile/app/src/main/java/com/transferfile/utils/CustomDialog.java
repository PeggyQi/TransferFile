package com.transferfile.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.transferfile.R;

public class CustomDialog extends Dialog {
    Context mContext;
    public CustomDialog (Context context){
        super(context);
        mContext = context;
    }
    public CustomDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View layout = LayoutInflater.from(mContext).
                inflate(R.layout.activity_dialog, null);

        ImageView imageView= (ImageView) layout.findViewById(R.id.guard_view);
        imageView.getBackground().setAlpha(200);
        LoadingDrawable mGuardDrawable;//绘制工具
        /**初始化绘制工具**/
        mGuardDrawable = new LoadingDrawable(new GuardLoadingRenderer(mContext));
        imageView.setImageDrawable(mGuardDrawable);
        mGuardDrawable.start();
        this.setContentView(layout);
    }
}
