package wee.digital.fpa.ui.plash

import android.view.animation.AnticipateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.splash.*
import wee.digital.fpa.util.SimpleLifecycleObserver
import wee.digital.fpa.util.SimpleTransitionListener
import wee.digital.library.extension.setHyperText
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SplashView(private val v: SplashFragment) {

    private val paymentInterval: Int = 90 // second

    private val viewTransition = ChangeBounds().apply {
        interpolator = AnticipateInterpolator(1.2f)
        duration = 600
    }

    private var disposable: Disposable? = null

    private fun onBindRemainingText(second: Int) {
        val text = "Thời gian còn lại: <b>%02d:%02d</b>".format(second / 60, second % 60)
        v.splashTextViewRemaining.setHyperText(text)
    }

    fun animateStartRemaining(onAnimEnd: () -> Unit) {
        onBindRemainingText(paymentInterval)
        viewTransition.addListener(object : SimpleTransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                viewTransition.removeListener(this)
                onAnimEnd()
            }
        })
        val logoId = v.splashImageViewLogo.id
        onViewAnimate {
            constrainHeight(logoId, v.splashImageViewLogo.height / 2)
            setVerticalBias(logoId, 0.1f)
        }
    }

    fun animateStopRemaining() {
        val logoId = v.splashImageViewLogo.id
        onViewAnimate {
            constrainHeight(logoId, v.splashImageViewLogo.height * 2)
            setVerticalBias(logoId, 0.5f)
        }
    }

    private fun onViewAnimate(block: ConstraintSet.() -> Unit) {
        TransitionManager.beginDelayedTransition(v.viewContent, viewTransition)
        ConstraintSet().also {
            it.clone(v.viewContent)
            it.block()
            it.applyTo(v.viewContent)
        }
    }

    fun onViewInit() {
        v.viewLifecycleOwner.lifecycle.addObserver(object : SimpleLifecycleObserver() {
            override fun onDestroy() {
                disposable?.dispose()
            }
        })
    }

    fun startPaymentRemaining(onRemainingEnd: () -> Unit) {
        val waitingCounter = AtomicInteger(paymentInterval)
        disposable?.dispose()
        disposable = Observable
                .interval(1, 1, TimeUnit.SECONDS)
                .map {
                    waitingCounter.decrementAndGet()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it > 0) {
                        onBindRemainingText(it)
                    } else {
                        disposable?.dispose()
                        onRemainingEnd()
                    }
                }, {})

    }

    fun stopPaymentRemaining() {
        disposable?.dispose()
        v.splashTextViewRemaining.text = null
    }

}