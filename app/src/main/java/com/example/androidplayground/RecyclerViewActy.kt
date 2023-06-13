package com.example.androidplayground

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber


class RecyclerViewActy : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var snapHelper: LinearSnapHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        recyclerView = findViewById(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter =
            MyAdapter(this, listOf("0", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "0", "0"))
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        // find third visible item of recycler view
        recyclerView.post {
            val firstPosition = layoutManager.findFirstVisibleItemPosition()
            val middlePosition = firstPosition + 2
            layoutManager.getChildAt(middlePosition)?.findViewById<TextView>(R.id.textView)?.apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
                setTextColor(resources.getColor(R.color.black))
            }
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Timber.d("onScrollStateChanged newState: $newState")
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    val view = snapHelper.findSnapView(layoutManager)
                    recyclerView.post {
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
        }
        )
    }
}

class MyAdapter(private val context: Context, private val dataList: List<String>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private val fakePosition = listOf<Int>(0,1,12,13)

    private val displayMetrics = context.resources.displayMetrics
    private var screenWidth = 0

    init {
        screenWidth = displayMetrics.widthPixels;
    }

    enum class ViewType {
        NORMAL,
        FAKE
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class FakeViewHolder(itemView: View) : ViewHolder(itemView) {

    }

    class RealViewHolder(itemView: View) : ViewHolder(itemView) {
        var textView: TextView

        init {
            textView = itemView.findViewById<TextView>(R.id.textView)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View = when (viewType) {
                ViewType.FAKE.ordinal -> {
                    LayoutInflater.from(parent.context).inflate(R.layout.item_layout_fake, parent, false)
                }
                else -> {
                    LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
                }
        }

        // 计算每个元素的宽度
        val itemWidth = screenWidth / 5

        // 设置元素的宽度
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.width = itemWidth
        view.layoutParams = layoutParams

        return when (viewType) {
            ViewType.FAKE.ordinal -> {
                FakeViewHolder(view)
            }
            else -> {
                RealViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        if (holder is RealViewHolder) {
            holder.textView.text = data
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (fakePosition.contains(position)) {
            ViewType.FAKE.ordinal
        } else {
            ViewType.NORMAL.ordinal
        }
    }
}
