package com.example.androidplayground

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Px
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber


class RecyclerViewActy : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val snapHelper = CenterSnapHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        recyclerView = findViewById(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = MyAdapter(this, listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(CenterDecoration(0))
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Timber.d("onScrollStateChanged newState: $newState")
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    val view = snapHelper.findSnapView(layoutManager)
                    recyclerView.post {
                        Timber.d("onScrollStateChanged view: $view")
                        for (index in 0 until layoutManager.childCount) {
                            val itemView = layoutManager.getChildAt(index)
                            if (itemView != view) {
                                itemView?.findViewById<TextView>(R.id.textView)?.apply {
                                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                                    setTextColor(resources.getColor(R.color.purple_500))
                                }
                            } else {
                                itemView?.findViewById<TextView>(R.id.textView)?.apply {
                                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
                                    setTextColor(resources.getColor(R.color.black))
                                }
                            }
                        }
                    }
                }
            }
        })
        Timber.d("item count :${adapter.itemCount}")
//        snapHelper.scrollTo(8, false)
        (recyclerView.layoutManager as LinearLayoutManager).smoothScrollToPosition(recyclerView, RecyclerView.State(),  adapter.itemCount / 2 + 2)
    }
}

class MyAdapter(private val context: Context, private val dataList: List<String>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    private val displayMetrics = context.resources.displayMetrics
    private var screenWidth = 0

    init {
        screenWidth = displayMetrics.widthPixels;
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView

        init {
            textView = itemView.findViewById<TextView>(R.id.textView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)

        // 计算每个元素的宽度
        val itemWidth = screenWidth / 5

        // 设置元素的宽度
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.width = itemWidth
        view.layoutParams = layoutParams

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.textView.text = data
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}



class CenterDecoration(@Px private val spacing: Int) : RecyclerView.ItemDecoration() {

    private var firstViewWidth = -1
    private var lastViewWidth = -1

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapterPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val lm = parent.layoutManager as LinearLayoutManager
        if (adapterPosition == 0) {
            // Invalidate decorations when this view width has changed
            if (view.width != firstViewWidth) {
                view.doOnPreDraw { parent.invalidateItemDecorations() }
            }
            firstViewWidth = view.width
            outRect.left = parent.width / 2 - view.width / 2
            // If we have more items, use the spacing provided
            if (lm.itemCount > 1) {
                outRect.right = spacing / 2
            } else {
                // Otherwise, make sure this to fill the whole width with the decoration
                outRect.right = outRect.left
            }
        } else if (adapterPosition == lm.itemCount - 1) {
            // Invalidate decorations when this view width has changed
            if (view.width != lastViewWidth) {
                view.doOnPreDraw { parent.invalidateItemDecorations() }
            }
            lastViewWidth = view.width
            outRect.right = parent.width / 2 - view.width / 2
            outRect.left = spacing / 2
        } else {
            outRect.left = spacing / 2
            outRect.right = spacing / 2
        }
    }

}

/**
 * A LinearSnapHelper that ignores item decorations to determine a view's center
 */
class CenterSnapHelper : LinearSnapHelper() {

    private var verticalHelper: OrientationHelper? = null
    private var horizontalHelper: OrientationHelper? = null
    private var scrolled = false
    private var recyclerView: RecyclerView? = null
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE && scrolled) {
                if (recyclerView.layoutManager != null) {
                    val view = findSnapView(recyclerView.layoutManager)
                    if (view != null) {
                        val out = calculateDistanceToFinalSnap(recyclerView.layoutManager!!, view)
                        if (out != null) {
                            recyclerView.smoothScrollBy(out[0], out[1])
                        }
                    }
                }
                scrolled = false
            } else {
                scrolled = true
            }
        }
    }

    fun scrollTo(position: Int, smooth: Boolean) {
        if (recyclerView?.layoutManager != null) {
            val viewHolder = recyclerView!!.findViewHolderForAdapterPosition(position)
            if (viewHolder != null) {
                val distances = calculateDistanceToFinalSnap(
                    recyclerView!!.layoutManager!!,
                    viewHolder.itemView
                )
                if (smooth) {
                    recyclerView!!.smoothScrollBy(distances!![0], distances[1])
                } else {
                    recyclerView!!.scrollBy(distances!![0], distances[1])
                }
            } else {
                if (smooth) {
                    recyclerView!!.smoothScrollToPosition(position)
                } else {
                    recyclerView!!.scrollToPosition(position)
                }
            }
        }
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        if (layoutManager == null) {
            return null
        }
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        recyclerView?.addOnScrollListener(scrollListener)
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToCenter(layoutManager, targetView, getHorizontalHelper(layoutManager))
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToCenter(layoutManager, targetView, getVerticalHelper(layoutManager))
        } else {
            out[1] = 0
        }
        return out
    }

    private fun findCenterView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }
        var closestChild: View? = null
        val center: Int = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }
        var absClosest = Integer.MAX_VALUE

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childCenter = if (helper == horizontalHelper) {
                (child!!.x + child.width / 2).toInt()
            } else {
                (child!!.y + child.height / 2).toInt()
            }
            val absDistance = Math.abs(childCenter - center)

            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }
        return closestChild
    }

    private fun distanceToCenter(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        val childCenter = if (helper == horizontalHelper) {
            (targetView.x + targetView.width / 2).toInt()
        } else {
            (targetView.y + targetView.height / 2).toInt()
        }
        val containerCenter = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }
        return childCenter - containerCenter
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (verticalHelper == null || verticalHelper!!.layoutManager !== layoutManager) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper!!
    }

    private fun getHorizontalHelper(
        layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (horizontalHelper == null || horizontalHelper!!.layoutManager !== layoutManager) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return horizontalHelper!!
    }
}