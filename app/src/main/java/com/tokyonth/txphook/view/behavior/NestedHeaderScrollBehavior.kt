package com.tokyonth.txphook.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.iterator
import com.tokyonth.txphook.R

class NestedHeaderScrollBehavior(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    init {
        initView(context, attrs)
    }

    private var dependencyId: Int = 0

    private fun initView(context: Context, attrs: AttributeSet?) {
        val typedArray = context.resources.obtainAttributes(attrs, R.styleable.CrBehavior)
        dependencyId = typedArray.getResourceId(R.styleable.CrBehavior_anchorId, -1)
        typedArray.recycle()
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        // child: 当前 Behavior 所关联的 View，此处是 HeaderView
        // dependency: 待判断是否需要监听的其他子 View
        return dependency.id == dependencyId
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.translationY = dependency.translationY * 0.5f

        val viewId = R.id.tv_app_name
        val lineId = R.id.view_line
        val appName = child.findViewById<TextView>(viewId)
        val line = child.findViewById<View>(lineId)
        appName.translationY = dependency.translationY * 0.3f
        line.translationY = dependency.translationY * 0.28f

        val al = dependency.translationY / (child.height * 0.6f)
        for (view in (child as ViewGroup)) {
            if (view.id != viewId) {
                view.alpha = 1 + al
                if (view.id == lineId) {
                    line.alpha = -(1 + al) * 2
                }
            }
        }
        // 如果改变了 child 的大小位置必须返回 true 来刷新
        return true
    }

}
