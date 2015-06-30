package org.kb10uy.tencocoa.views;

/**
 * https://gist.github.com/STAR-ZERO/2934490
 *
 */
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * サイズ自動調整TextView
 *
 */
public class FontFitTextView extends TextView {

    private static final float MIN_TEXT_SIZE = 10f;

    public FontFitTextView(Context context) {
        super(context);
    }

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        resize();

    }

    private void resize() {

        Paint paint = new Paint();

        // Viewの幅
        int viewWidth = this.getWidth();
        // テキストサイズ
        float textSize = getTextSize();

        // Paintにテキストサイズ設定
        paint.setTextSize(textSize);
        // テキストの横幅取得
        float textWidth = paint.measureText(this.getText().toString());

        while (viewWidth <  textWidth) {
            // 横幅に収まるまでループ

            if (MIN_TEXT_SIZE >= textSize) {
                // 最小サイズ以下になる場合は最小サイズ
                textSize = MIN_TEXT_SIZE;
                break;
            }

            // テキストサイズをデクリメント
            textSize--;

            // Paintにテキストサイズ設定
            paint.setTextSize(textSize);
            // テキストの横幅を再取得
            textWidth = paint.measureText(this.getText().toString());

        }

        // テキストサイズ設定
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

}