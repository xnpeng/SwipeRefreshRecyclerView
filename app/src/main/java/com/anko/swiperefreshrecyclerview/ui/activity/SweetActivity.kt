package com.anko.swiperefreshrecyclerview.ui.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.anko.swiperefreshrecyclerview.R
import com.anko.swiperefreshrecyclerview.common.dismissProgress
import com.anko.swiperefreshrecyclerview.common.testDimen
import com.anko.swiperefreshrecyclerview.common.toast
import com.anko.swiperefreshrecyclerview.model.Media
import com.anko.swiperefreshrecyclerview.model.Sweet
import com.anko.swiperefreshrecyclerview.service.SweetApi
import com.anko.swiperefreshrecyclerview.service.apiBaseUrl
import com.anko.swiperefreshrecyclerview.service.glideHeader
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sweet.*
import kotlinx.android.synthetic.main.content_main.*

var img_width: Int = 120
var img_height: Int = 90

class SweetActivity : AppCompatActivity() {

    val TAG: String = SweetActivity::class.java.simpleName

    lateinit var title: String
    lateinit var sweet: Sweet
    lateinit var mSweetAdapter: SweetAdapter
    var headList: List<List<Media>> = emptyList()
    var bodyList: List<Sweet> = emptyList()
    var footList: List<Sweet> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sweet)

        initData()
        initView()

        calcDemin()
    }

    private fun calcDemin() {
        val p = testDimen()
        img_width = (p.first - 40) / 3
        img_height = (img_width * 3) / 4
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun initData() {
        sweet = intent.getParcelableExtra("sweet")
        title = sweet.userId + "@" + DateUtils.getRelativeTimeSpanString(sweet.date.millis)
        bodyList = listOf(sweet)

        loadMedias(sweet.id)
        loadComments(sweet.id)
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        sweetRecyclerView.itemAnimator = DefaultItemAnimator()

        val mLayoutManager = LinearLayoutManager(this)
        sweetRecyclerView.layoutManager = mLayoutManager

        mSweetAdapter = SweetAdapter(this, headList, bodyList, footList)
        sweetRecyclerView.adapter = mSweetAdapter

        sweetRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastCompletelyVisibleItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastCompletelyVisibleItemPosition + 1 == mSweetAdapter.itemCount) {
                    Handler().postDelayed({
                        loadComments(sweet.id)
                    }, 5000)
                    Log.e(TAG, "到底了" + lastCompletelyVisibleItemPosition)
                }

                val firstCompletelyVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstCompletelyVisibleItemPosition == 0 && dy < 0) {
                    Handler().postDelayed({
                        loadComments(sweet.id)
                    }, 5000)
                    Log.e(TAG, "开始刷新")
                }
            }
        })

    }

    private fun loadMedias(refId: Int) {
        val api = SweetApi.create()
        api.getMedias(refId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result ->
                    Log.d(TAG, "medias:${result.size}")
                    mSweetAdapter.headList = listOf(result)
                    mSweetAdapter.notifyDataSetChanged()
                }, { error ->
                    this.toast(error.localizedMessage)
                })
    }

    private fun loadComments(refId: Int) {
        val api = SweetApi.create()
        api.getReplies(refId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result ->
                    Log.d(TAG, "comments:${result.size}")
                    mSweetAdapter.footList = result
                    mSweetAdapter.notifyDataSetChanged()
                }, { error ->
                    this.toast(error.localizedMessage)
                }, { dismissProgress() })
    }


    class SweetAdapter(var context: Context, var headList: List<List<Media>>, var bodyList: List<Sweet>, var footList: List<Sweet>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val TAG: String = SweetAdapter::class.java.simpleName

        private val HEAD_TYPE = 1
        private val BODY_TYPE = 2
        private val FOOT_TYPE = 3

        override fun getItemViewType(position: Int): Int {
            val viewType: Int
            if (position < headList.size) {
                viewType = HEAD_TYPE
            } else if (position > headList.size + bodyList.size - 1) {
                viewType = FOOT_TYPE
            } else {
                viewType = BODY_TYPE
            }
            return viewType
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

            if (holder is HeadHolder) {
                Log.d(TAG, "headholder")

                val listHeadView = listOf(holder.mIvHead1, holder.mIvHead2, holder.mIvHead3, holder.mIvHead4, holder.mIvHead5, holder.mIvHead6, holder.mIvHead7, holder.mIvHead8, holder.mIvHead9)

                val medias = headList[position]
                if (!medias.isEmpty())
                    for (i in 0..medias.size - 1) {
                        val media = medias[i]
                        val imgUri = apiBaseUrl + "/media/${media.fileName}/${media.fileType}"
                        val url = GlideUrl(imgUri, glideHeader)

                        val iv = listHeadView[i]

                        Glide.with(context).load(url).into(iv)
                        iv.setOnClickListener({
                            Log.d(TAG, "head clicked")
                            (context as Activity).toast("You clicked me!")
                            //todo show big image list
                        })

                        iv.visibility = VISIBLE
                    }
            }

            if (holder is BodyHolder) {
                Log.d(TAG, "bodyHolder")

                holder.mTvBody.text = bodyList.get(position - headList.size).text
                //holder.mTvBody.setOnClickListener {
                //    Log.d(TAG, "body clicked")
                //(context as Activity).toast(holder.adapterPosition)
                //}
            }

            if (holder is FootHolder) {
                val sweet = footList[position - headList.size - bodyList.size]
                //val imgUri = apiBaseUrl + "/media/${sweet.userId}/avatar"
                //holder.mIvAuthorImage.setImageURI(Uri.parse(imgUri))
                //holder.mIvAuthorImage.setOnClickListener({
                //    (context as Activity).toast(holder.adapterPosition.toString())
                //})

                val author = sweet.userId + "@" + DateUtils.getRelativeTimeSpanString(sweet.date.millis)
                holder.mTvAuthorName.text = author
                holder.mTvContent.text = sweet.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
            val inflater = LayoutInflater.from(context)
            val view: View?
            when (viewType) {
                HEAD_TYPE -> {
                    view = inflater.inflate(R.layout.item_head, parent, false)
                    return HeadHolder(view)
                }
                BODY_TYPE -> {
                    view = inflater.inflate(R.layout.item_body, parent, false)
                    return BodyHolder(view)
                }
                FOOT_TYPE -> {
                    view = inflater.inflate(R.layout.item_foot, parent, false)
                    return FootHolder(view)
                }
            }
            return null
        }

        override fun getItemCount(): Int {
            return headList.size + bodyList.size + footList.size
        }

    }

    internal class HeadHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvHead1: ImageView = itemView.findViewById(R.id.sweet_image1) as ImageView
        val mIvHead2: ImageView = itemView.findViewById(R.id.sweet_image2) as ImageView
        val mIvHead3: ImageView = itemView.findViewById(R.id.sweet_image3) as ImageView
        val mIvHead4: ImageView = itemView.findViewById(R.id.sweet_image4) as ImageView
        val mIvHead5: ImageView = itemView.findViewById(R.id.sweet_image5) as ImageView
        val mIvHead6: ImageView = itemView.findViewById(R.id.sweet_image6) as ImageView
        val mIvHead7: ImageView = itemView.findViewById(R.id.sweet_image7) as ImageView
        val mIvHead8: ImageView = itemView.findViewById(R.id.sweet_image8) as ImageView
        val mIvHead9: ImageView = itemView.findViewById(R.id.sweet_image9) as ImageView

        init {
            val lp = LinearLayout.LayoutParams(img_width, img_height)
            lp.setMargins(10, 10, 0, 0)
            mIvHead1.layoutParams = lp
            mIvHead2.layoutParams = lp
            mIvHead3.layoutParams = lp
            mIvHead4.layoutParams = lp
            mIvHead5.layoutParams = lp
            mIvHead6.layoutParams = lp
            mIvHead7.layoutParams = lp
            mIvHead8.layoutParams = lp
            mIvHead9.layoutParams = lp
        }
    }

    internal class BodyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvBody: TextView = itemView.findViewById(R.id.content) as TextView
    }

    internal class FootHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val mIvAuthorImage: ImageView = itemView.findViewById(R.id.author_image) as ImageView
        val mTvAuthorName: TextView = itemView.findViewById(R.id.author_name) as TextView
        val mTvContent: TextView = itemView.findViewById(R.id.comment) as TextView
    }

}