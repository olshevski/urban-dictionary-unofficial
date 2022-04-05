package dev.olshevski.udu.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.res.use
import androidx.recyclerview.widget.RecyclerView
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import dev.olshevski.udu.R
import kotlin.properties.Delegates

class ExtendedRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private var _dividerHeight by Delegates.notNull<Int>()
    private var _dividerColor by Delegates.notNull<Int>()
    private var dividerItemDecoration: ItemDecoration? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ExtendedRecyclerView).use {
            _dividerHeight = it.getDimensionPixelSize(
                R.styleable.ExtendedRecyclerView_dividerHeight,
                0
            )
            _dividerColor = it.getColor(
                R.styleable.ExtendedRecyclerView_dividerColor,
                Color.TRANSPARENT
            )
        }
        updateDividerItemDecoration()
    }

    var dividerHeight
        @Px get() = _dividerHeight
        set(@Px height: Int) {
            if (_dividerHeight != height) {
                _dividerHeight = height
                updateDividerItemDecoration()
            }
        }

    var dividerColor
        @ColorInt get() = _dividerColor
        set(@ColorInt color: Int) {
            if (_dividerColor != color) {
                _dividerColor = color
                updateDividerItemDecoration()
            }
        }

    private fun updateDividerItemDecoration() {
        dividerItemDecoration?.let { removeItemDecoration(it) }
        if (_dividerHeight != 0) {
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                .size(_dividerHeight)
                .color(_dividerColor)
                .build()
                .also { dividerItemDecoration = it }
            )
        }
    }

}