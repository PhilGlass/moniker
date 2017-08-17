package glass.phil.monzo.presentation.transactions;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import glass.phil.monzo.R;

final class TransactionDividerDecoration extends RecyclerView.ItemDecoration {
  private final TransactionsAdapter adapter;
  private final int height;
  private final int leftInset;
  private final int rightInset;
  private final Paint paint = new Paint();

  TransactionDividerDecoration(Context context, TransactionsAdapter adapter) {
    this.adapter = adapter;

    final Resources resources = context.getResources();
    height = resources.getDimensionPixelSize(R.dimen.transactions_divider_height);

    final int logoSize = resources.getDimensionPixelSize(R.dimen.transactions_logo_size);
    final int horizontalSpacing = resources.getDimensionPixelSize(R.dimen.transactions_horizontal_spacing);
    final int inset = logoSize + (2 * horizontalSpacing);
    final boolean ltr = resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
    leftInset = ltr ? inset : 0;
    rightInset = ltr ? 0 : inset;

    paint.setColor(context.getColor(R.color.divider));
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    outRect.set(0, 0, 0, shouldDrawDividerBelow(parent.getChildAdapterPosition(view)) ? height : 0);
  }

  @Override public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    final RecyclerView.LayoutManager lm = parent.getLayoutManager();
    for (int i = 0; i < parent.getChildCount(); i++) {
      final View child = parent.getChildAt(i);
      if (shouldDrawDividerBelow(parent.getChildAdapterPosition(child))) {
        final int bottom = lm.getDecoratedBottom(child);
        canvas.drawRect(lm.getDecoratedLeft(child) + leftInset, bottom - height,
            lm.getDecoratedRight(child) - rightInset, bottom, paint);
      }
    }
  }

  private boolean shouldDrawDividerBelow(int position) {
    final boolean validPosition = position != RecyclerView.NO_POSITION;
    final boolean lastItem = position == (adapter.getItemCount() - 1);
    return validPosition && !lastItem && adapter.getHeaderId(position) == adapter.getHeaderId(position + 1);
  }
}
