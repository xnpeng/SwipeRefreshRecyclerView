package com.anko.swiperefreshrecyclerview.ui.activity

import com.anko.swiperefreshrecyclerview.ui.fragment.SweetFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : android.support.v7.app.AppCompatActivity() {

    var lastIndex = -1
    var lastFragment: android.support.v4.app.Fragment? = null
    var sweetFragment: SweetFragment? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.anko.swiperefreshrecyclerview.R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                com.anko.swiperefreshrecyclerview.R.id.action_sweet -> changeTab(0)
            }
            true
        }
        changeTab(0)
    }

    fun changeTab(position: Int) {

        if (lastIndex == position) return

        lastIndex = position

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (lastFragment != null) fragmentTransaction.hide(lastFragment)


        when (position) {
            0 -> {
                sweetFragment = fragmentManager.findFragmentByTag(SweetFragment::class.java.simpleName) as SweetFragment?

                if (sweetFragment == null) {
                    sweetFragment = SweetFragment.Companion.newInstance()
                    fragmentTransaction.add(com.anko.swiperefreshrecyclerview.R.id.container, sweetFragment, SweetFragment::class.java.simpleName)
                } else {
                    fragmentTransaction.show(sweetFragment)
                }

                lastFragment = sweetFragment
            }
        }

        fragmentTransaction.commit()
    }


}
