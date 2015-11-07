package org.kb10uy.bhavaagra;

import android.view.View;

interface GeneralListAdapterViewGenerator<TElement> {
    View generateView(View targetView, TElement item);
}