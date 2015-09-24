package com.swiftkaydevelopment.testing;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kevin Haines on 9/23/15.
 */
public class ContactCardAdapter extends BaseAdapter {

    Context context;
    List<MyDraggableItemAdapter.TitleValues> mlist;
    LayoutInflater inflater;

    public ContactCardAdapter(Context context, List<MyDraggableItemAdapter.TitleValues> mlist) {
        this.context = context;
        this.mlist = mlist;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.contact_card_list_item,parent,false);
        final ViewHolder holder = new ViewHolder();

        holder.iv = (ImageView) row.findViewById(R.id.ivcontact_card_list_item);
        holder.tvtitle = (TextView) row.findViewById(R.id.tvcontact_card_list_item_title);
        holder.tvvalue = (TextView) row.findViewById(R.id.tvcontact_card_list_item_value);

        row.setTag(holder);

        final MyDraggableItemAdapter.TitleValues val = mlist.get(position);

        if(val != null){
            holder.iv.setImageResource(val.imgloc);
            holder.tvtitle.setText(val.title);
            holder.tvvalue.setText(val.value);

        }


        return row;
    }

    class ViewHolder{
        ImageView iv;
        TextView tvtitle;
        TextView tvvalue;

    }
}
