package org.kb10uy.tencocoa.adapters;

import android.view.View;

public interface GeneralListAdapterViewGenerator<TElement> {
    View generateView(View targetView, TElement item);
}