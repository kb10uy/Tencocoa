package org.kb10uy.bhavaagra;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.List;

public final class BhavaAgra {
    public static RhapsodyBuilder from(Context context, int result) {
        return new RhapsodyBuilder(context, result);
    }

    public static List<Uri> parse(Intent intent) {
        return null;
    }
}
