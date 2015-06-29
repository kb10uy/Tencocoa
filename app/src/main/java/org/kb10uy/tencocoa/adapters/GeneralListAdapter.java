package org.kb10uy.tencocoa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by kb10uy on 2015/06/01.
 */
public class GeneralListAdapter<T> extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<T> list;
    GeneralListAdapterViewGenerator<T> viewGenerator;
    int layoutId;

    public GeneralListAdapter(Context ctx, int layout, GeneralListAdapterViewGenerator<T> generator) {
        context = ctx;
        layoutId = layout;
        viewGenerator = generator;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(List<T> l) {
        list = l;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list != null ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View sv = convertView == null ? inflater.inflate(layoutId, parent, false) : convertView;
        return viewGenerator.generateView(sv, (T) getItem(position));
    }
}