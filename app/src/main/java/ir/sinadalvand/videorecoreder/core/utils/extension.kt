/*
 *
 *   Created by Sina Dalvand on 1/21/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

/*
 *
 *   Created by Sina Dalvand on 8/7/2019
 *   Copyright (c) 2019 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.core.utils

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import java.math.BigInteger
import java.security.MessageDigest


fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun TextView.setFont(font: String, bold: Boolean = false) {
    val face = Typeface.createFromAsset(context.assets, "fonts/${font}.ttf")
    if (bold) typeface = face else setTypeface(face, Typeface.BOLD)
}

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

