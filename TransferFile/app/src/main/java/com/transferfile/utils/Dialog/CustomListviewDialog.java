package com.transferfile.utils.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.transferfile.R;
import com.transferfile.adapter.DialogAdapter;
import com.transferfile.utils.Dialog.style.Circle;

import java.util.ArrayList;
import java.util.List;

public class CustomListviewDialog extends Dialog {
    Context mContext;
    private Circle mCircleDrawable;
    static DialogAdapter adapter;
    ListView listView;
   static public DialogAdapter getAdapter() {
        return adapter;
    }

    public CustomListviewDialog(Context context) {
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
        mCircleDrawable = new Circle();
        mCircleDrawable.setBounds(-310,5, 40, 40);
        mCircleDrawable.setColor(Color.GRAY);
        textView.setCompoundDrawables(null, null, mCircleDrawable, null);
        mCircleDrawable.start();

        listView = (ListView) layout.findViewById(R.id.dialoglistview);
        adapter = new DialogAdapter(mContext);
        listView.setAdapter(adapter);
        this.setContentView(layout);
    }
    private List<String> getData(){

        List<String> data = new ArrayList<String>();
        data.add("测试数据1");
        data.add("测试数据2");
        data.add("测试数据3");
        data.add("测试数据4");
        data.add("测试数据1");
        data.add("测试数据2");
        data.add("测试数据3");
        data.add("测试数据4");
        return data;
    }
}
