package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EmptyRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context, attrs
    )

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    )

    private var mEmptyView: View? = null
    private val mEmptyObserver: AdapterDataObserver = EmptyRecyclerDataObserver(this)

    private var mCallback: Callback? = null

    fun setCallback(callback: Callback?) {
        mCallback = callback
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(mEmptyObserver)
        mEmptyObserver.onChanged()
    }

    fun setEmptyView(mEmptyView: View?) {
        this.mEmptyView = mEmptyView
    }

    private class EmptyRecyclerDataObserver(val recyclerView: EmptyRecyclerView) : AdapterDataObserver() {

        override fun onChanged() {
            val adapter = recyclerView.adapter
            if (adapter != null && recyclerView.mEmptyView != null) {
                if (adapter.itemCount == 0) {
                    recyclerView.mEmptyView!!.visibility = VISIBLE
                    recyclerView.visibility = GONE
                } else {
                    recyclerView.mEmptyView!!.visibility = GONE
                    recyclerView.visibility = VISIBLE
                    if (recyclerView.mCallback != null) recyclerView.mCallback!!.onEmpty()
                }
            }
        }
    }

    interface Callback {
        fun onEmpty()
    }
}