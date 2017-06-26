package com.anko.swiperefreshrecyclerview.common

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.Toast
import com.anko.swiperefreshrecyclerview.ui.fragment.ProgressFragment
import com.google.gson.*
import org.joda.time.DateTime
import java.lang.reflect.Type

class JodaGsonAdapter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
    override fun serialize(src: DateTime, srcType: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.millis)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): DateTime {
        try {
            return DateTime(json.asLong)
        } catch (e: IllegalArgumentException) {
            val date = context.deserialize<DateTime>(json, DateTime::class.java)
            return date
        }
    }
}

fun Activity.toast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(msgId: Int) {
    Toast.makeText(applicationContext, msgId, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showProgress() {
    val dialog = ProgressFragment.newInstance()
    dialog.show(supportFragmentManager, ProgressFragment::class.java.simpleName)
}

fun AppCompatActivity.dismissProgress() {
    (supportFragmentManager.findFragmentByTag(ProgressFragment::class.java.simpleName) as ProgressFragment?)?.dismiss()

}

fun AppCompatActivity.testDimen(): Pair<Int, Int> {
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    val width = dm.widthPixels
    val height = dm.heightPixels
    val density = dm.density
    val dpi = dm.densityDpi
    println("testDimen:$width * $height : $density : $dpi")

    val width_dip = (width / density + 0.5f).toInt()
    val height_dip = (height / density + 0.5f).toInt()
    println("testDimen: ${width_dip}dp * ${height_dip}dp")

    return Pair(width, height)
}
