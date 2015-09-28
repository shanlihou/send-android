package com.example.send;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * Created by root on 15-9-14.
 */
public class ContactAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    public ContactAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return HandleContact.getInstance().getSize();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null)
        {
            view = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.alpha = (TextView) view.findViewById(R.id.alpha);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.number =
                    (TextView) view.findViewById(R.id.number);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        Contacter contacter = HandleContact.getInstance().getContact(i);
        holder.name.setText(contacter.getUserName());
        holder.number.setText(contacter.getPhoneNumber());
        String currentStr = HandleContact.getInstance().getAlpha(contacter.getSortKey());
        String previewStr = (i - 1) >= 0 ? HandleContact.getInstance().getAlpha(
                HandleContact.getInstance().getContact(i - 1).getSortKey()) : " ";

        if (!previewStr.equals(currentStr))
        {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
        }
        else
        {
            holder.alpha.setVisibility(View.GONE);
        }

        //
        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return HandleContact.getInstance().getContact(i);
    }
    private class ViewHolder{
        TextView alpha;
        TextView name;
        TextView number;
    }
}
