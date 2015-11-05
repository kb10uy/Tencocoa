package org.kb10uy.bhavaagra;

import android.view.View;

public interface GeneralListAdapterViewGenerator<TElement> {
    View generateView(View targetView, TElement item);
}