package com.devstudioworks.customChipGroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.MarginLayoutParamsCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CustomChipGroup : ChipGroup {
    var additionalChipPosition: Int = Int.MAX_VALUE;
    private val DEF_STYLE_RES =
        com.google.android.material.R.style.Widget_MaterialComponents_ChipGroup
    private var maxRow = Int.MAX_VALUE
    var maxRowDef = Int.MAX_VALUE
    private var additionalChipTitle: String? = null
    private var additionalChipColor = -0xeee
    private var additionalChipColorPressed = -0xc5c5c5
    private var additionalChipTextColor = -0x000
    var additionalChipListener: AdditionalChipListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        com.google.android.material.R.attr.chipGroupStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CustomChipGroup,
            defStyleAttr,
            DEF_STYLE_RES
        )
        maxRowDef = a.getInt(R.styleable.CustomChipGroup_maxRow, maxRow)
        if (maxRowDef > 0) isSingleLine = false
        maxRow = maxRowDef
        additionalChipTitle = a.getString(R.styleable.CustomChipGroup_additionalChipMore)
        additionalChipColor = a.getColor(R.styleable.CustomChipGroup_additionalChipColor, -0xeee)
        additionalChipColorPressed =
            a.getColor(R.styleable.CustomChipGroup_additionalChipColorPressed, -0xc5c5c5)
        additionalChipTextColor =
            a.getColor(R.styleable.CustomChipGroup_additionalTextColor, -0x000)

    }

    private var row = 0

    override fun onLayout(sizeChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            return
        }
        row = 1
        val paddingStart = paddingRight
        val paddingEnd = paddingLeft
        val childTop = paddingTop
        val maxChildEnd = right - left - paddingEnd
        inflateChipItemsToLayout(paddingStart, maxChildEnd, paddingStart, childTop, childTop)
    }

    private fun inflateChipItemsToLayout(
        childStart: Int,
        maxChildEnd: Int,
        paddingStart: Int,
        childTop: Int,
        childBottom: Int
    ) {
        var childEnd1: Int
        var childStart1: Int = childStart
        var childTop1 = childTop
        var childBottom1 = childBottom
        for (i in 0 until childCount) {
            if (getChildAt(i) != null) {
                val child = getChildAt(i) as Chip

                if (child.visibility == GONE) {
                    continue
                }

                val lp = child.layoutParams
                var startMargin = 0
                var endMargin = 0
                if (lp is MarginLayoutParams) {
                    startMargin = MarginLayoutParamsCompat.getMarginStart(lp)
                    endMargin = MarginLayoutParamsCompat.getMarginEnd(lp)
                }
                childEnd1 = childStart1 + startMargin + child.measuredWidth
                if (!isSingleLine && (childEnd1 > maxChildEnd)) {
                    childStart1 = paddingStart
                    childTop1 = childBottom1
                    updateAdditionalChip(i)
                    row++
                }
                child.visibility = if (row > maxRow) GONE else VISIBLE
                childEnd1 = childStart1 + startMargin + child.measuredWidth
                childBottom1 = childTop1 + child.measuredHeight
                child.layout(childStart1 + startMargin, childTop1, childEnd1, childBottom1)
                childStart1 += startMargin + endMargin + child.measuredWidth
            }
        }
    }

    private fun updateAdditionalChip(i: Int) {
        if (row == maxRow && (i - 1) > 0) {
            additionalChipPosition = i - 1
            val additionalChip = (getChildAt(i - 1) as Chip)
            additionalChip.text = additionalChipTitle ?: ""
            additionalChip.isCheckable = false
            additionalChip.setOnClickListener {
                additionalChipListener?.onAdditionalChipClick(it, context)
            }
        }
    }

    private fun updateShowMoreChip(
        childEnd1: Int,
        maxChildEnd: Int,
        childStart1: Int,
        paddingStart: Int,
        childTop1: Int,
        childBottom1: Int,
        i: Int
    ): Pair<Int, Int> {
        var childStart11 = childStart1
        var childTop11 = childTop1
        if (!isSingleLine && (childEnd1 > maxChildEnd)) {
            childStart11 = paddingStart
            childTop11 = childBottom1

            if (row == maxRow && (i - 1) > 0) {
                additionalChipPosition = i - 1
                val showChip = (getChildAt(i - 1) as Chip)
                showChip.text = additionalChipTitle ?: ""
                showChip.isCheckable = false
                showChip.setOnClickListener {
                    additionalChipListener?.onAdditionalChipClick(it, context)
                }
            }
            row++
        }
        return Pair(childStart11, childTop11)
    }

    fun setAdditionalChipClickListener(listener: AdditionalChipListener) {
        additionalChipListener = listener
    }

}

interface AdditionalChipListener {
    fun onAdditionalChipClick(view: View, context: Context)
}