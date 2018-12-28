package de.michael.filebinmobile.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 * https://stackoverflow.com/a/29168276
 */
class GridViewItemDecorator(val gridSpacingPx: Int, val gridSize: Int) : RecyclerView.ItemDecoration() {
    var mNeedLeftSpacing = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val frameWidth = ((parent.width - gridSpacingPx * (gridSize - 1)) / gridSize)
        val padding = parent.width / gridSize - frameWidth
        val itemPosition = (view.getLayoutParams() as RecyclerView.LayoutParams).viewAdapterPosition
        if (itemPosition < gridSize) {
            outRect.top = 0
        } else {
            outRect.top = gridSpacingPx
        }
        if (itemPosition % gridSize == 0) {
            outRect.left = 0
            outRect.right = padding
            mNeedLeftSpacing = true
        } else if ((itemPosition + 1) % gridSize == 0) {
            mNeedLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        } else if (mNeedLeftSpacing) {
            mNeedLeftSpacing = false
            outRect.left = gridSpacingPx - padding
            if ((itemPosition + 2) % gridSize == 0) {
                outRect.right = gridSpacingPx - padding
            } else {
                outRect.right = gridSpacingPx / 2
            }
        } else if ((itemPosition + 2) % gridSize == 0) {
            mNeedLeftSpacing = false
            outRect.left = gridSpacingPx / 2
            outRect.right = gridSpacingPx - padding
        } else {
            mNeedLeftSpacing = false
            outRect.left = gridSpacingPx / 2
            outRect.right = gridSpacingPx / 2
        }
        outRect.bottom = 0
    }
}

/**
 * https://stackoverflow.com/a/29720777
 */
class BottomOffsetDecorator(private val offset: Int, private val condition: () -> Boolean) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val dataSize = state.itemCount
        val position = parent.getChildAdapterPosition(view)
        if (condition() && position == dataSize - 1) {
            outRect.set(0, 0, 0, offset)
        } else {
            outRect.set(0, 0, 0, 0)
        }

    }
}