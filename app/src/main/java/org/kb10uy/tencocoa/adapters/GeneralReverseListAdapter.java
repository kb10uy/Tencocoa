package org.kb10uy.tencocoa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kb10uy on 2015/06/01.
 */
public class GeneralReverseListAdapter<T> extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<T> list;
    GeneralListAdapterViewGenerator<T> viewGenerator;
    int layoutId;

    public GeneralReverseListAdapter(Context ctx, int layout, GeneralListAdapterViewGenerator<T> generator) {
        context = ctx;
        layoutId = layout;
        viewGenerator = generator;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list = new ArrayList<>();
    }

    public void add(T obj) {
        list.add(obj);
        notifyDataSetChanged();
    }

    public void remove(T obj) {
        list.remove(obj);
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> l) {
        list = l;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get((list.size() - 1) - position);
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
