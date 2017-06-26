package com.anko.swiperefreshrecyclerview.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anko.swiperefreshrecyclerview.R
import com.anko.swiperefreshrecyclerview.common.toast
import com.anko.swiperefreshrecyclerview.model.Sweet
import com.anko.swiperefreshrecyclerview.service.SweetApi
import com.anko.swiperefreshrecyclerview.ui.activity.MainActivity
import com.anko.swiperefreshrecyclerview.ui.activity.SweetActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sweet.*

open class SweetFragment : Fragment() {

    val TAG: String = SweetFragment::class.java.simpleName

    val pageSize = 10
    var pageNumber = 1
    var activity: MainActivity? = null
    var adapter: SweetAdapter? = null
    var isRefresh: Boolean = false

    companion object {
        fun newInstance(): SweetFragment {
            return SweetFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_sweet, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SweetAdapter(activity!!.applicationContext, R.layout.item_sweet)
        adapter!!.setOnItemClickListener { adapter, _, position ->
            start2Detail(adapter.data[position] as Sweet)
        }
        adapter!!.setOnLoadMoreListener({
            pageNumber++
            isRefresh = false
            loadData(pageSize, pageNumber)
        }, recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)

        swipeLayout.setOnRefreshListener({
            Handler().postDelayed({
                pageNumber = 1
                isRefresh = true
                loadData(pageSize, pageNumber)
            }, 2000)
        })

        loadData(pageSize, pageNumber)
    }

    private fun loadData(mPageSize: Int, mPageNumber: Int) {
        val api = SweetApi.create()
        api.latestSweet(mPageSize, mPageNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    Log.d(TAG, "loadData:${result.size}")
                    if (isRefresh) {
                        Log.d(TAG, "refresh")
                        adapter!!.setNewData(result)
                        swipeLayout.isRefreshing = false
                    } else {
                        Log.d(TAG, "loadmore")
                        adapter!!.addData(result)
                        adapter!!.loadMoreComplete()
                        if (result.size < pageSize) adapter!!.loadMoreEnd()
                    }
                }, { error ->
                    swipeLayout!!.isRefreshing = false
                    activity!!.toast(error.localizedMessage)
                })
    }

    override fun onAttach(context: Activity?) {
        super.onAttach(context)
        this.activity = context as MainActivity?
    }

    override fun onDetach() {
        super.onDetach()
        this.activity = null
    }

    private fun start2Detail(sweet: Sweet) {
        val intent = Intent(activity, SweetActivity::class.java)
        intent.putExtra("sweet", sweet)
        startActivity(intent)
    }

    class SweetAdapter(var context: Context, layoutId: Int) : BaseQuickAdapter<Sweet, BaseViewHolder>(layoutId) {
        override fun convert(viewHolder: BaseViewHolder, sweet: Sweet) {
            viewHolder.setText(R.id.userId, sweet.userId)
            viewHolder.setText(R.id.date, DateUtils.getRelativeTimeSpanString(sweet.date.millis))
            val len = if (sweet.text.length < 100) sweet.text.length else 100
            viewHolder.setText(R.id.content, sweet.text.substring(0, len))
        }

    }

}