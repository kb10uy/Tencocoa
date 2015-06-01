package org.kb10uy.tencocoa.adapters;

import android.view.View;

/**
 * Created by kb10uy on 2015/06/01.
 */
public interface GeneralListAdapterViewGenerator<TElement> {
    View generateView(View targetView, TElement item);
}