package com.infnet.moviesinfnet.utils.ext

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.infnet.moviesinfnet.R
import com.infnet.moviesinfnet.utils.helper.Event
import com.infnet.moviesinfnet.utils.helper.Message


fun CoordinatorLayout.showSnackbar(
    message: String,
    buttonText: String? = null,
    action: () -> Unit = {},
    length: Int = Snackbar.LENGTH_LONG,
    gravity: Int = Gravity.TOP
)
{
    val s = Snackbar
        .make(this, message, length)

    buttonText?.let {
        s.setAction(it) {
            action()
        }
    }

    val params = s.view.layoutParams as CoordinatorLayout.LayoutParams
    params.gravity = gravity
    s.view.layoutParams = params
    s.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE

    s.show()
}

fun Activity.hideKeyboard()
{
    if (this.window != null)
    {
        val imm =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)

        //remove focus from EditText
        findViewById<View>(android.R.id.content).clearFocus()
    }
}

fun AppCompatActivity.setActionBarTitle(title: String)
{
    supportActionBar?.title = title
}

fun Fragment.hideKeyboard()
{
    requireActivity().hideKeyboard()
}

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun TextView.setTextOrGone(text: String?)
{
    isVisible = if (text == null)
    {
        false
    }
    else
    {
        setText(text)
        true
    }
}

fun Context.tryShowSnackbarOK(coordinatorLayout: CoordinatorLayout, eventMessage: Event<Message>)
{
    eventMessage.getContentIfNotHandled()?.let {
        coordinatorLayout.showSnackbar(
            message = it.getFormattedMessage(this),
            buttonText = getString(R.string.ok)
        )
    }
}
