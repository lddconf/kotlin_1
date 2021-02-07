package com.example.notes.ui.customviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.notes.R
import com.example.notes.dip
import com.firebase.ui.auth.ui.AppCompatBase
import java.lang.Integer.min

@Dimension(unit = DP)
private const val defStrokeWidthDp = 1

@Dimension(unit = DP)
private const val defSelectedStrokeWidthDp = defStrokeWidthDp + 2

@Dimension(unit = DP)
private const val defPaddingDp = 10

@Dimension(unit = DP)
private const val defWidthDp = 50


class CircleColorPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var strokeWidthDp: Int = defStrokeWidthDp
        set(value) {
            field = value
            invalidate()
        }

    var paddingDp: Int = defPaddingDp
        set(value) {
            field = value
            invalidate()
        }

    var lineWidthDp: Int = defWidthDp
        set(value) {
            field = value
            invalidate()
        }

    var selectedStrokeWidthDp = defSelectedStrokeWidthDp
        set(value) {
            field = value
            invalidate()
        }

    private val fillArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokeArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    var colors = mutableListOf<Int>()
        set(value) {
            field = value
            invalidate()
        }

    var selectedColor : Int? = null
        set(value) {
            field = value
            invalidate()
        }

    private var center : Pair<Int, Int> = 0 to 0

    private var outerRect =
        RectF(0f + defPaddingDp, 0f + defPaddingDp, 100f - defPaddingDp, 100f - defPaddingDp)

    private var innerRect = RectF(
        outerRect.left + defWidthDp,
        outerRect.top + defWidthDp, outerRect.right - defWidthDp, outerRect.bottom - defWidthDp
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = if (w < h) w else h
        outerRect =
            RectF(
                context.dip(defPaddingDp).toFloat(),
                context.dip(defPaddingDp).toFloat(),
                (size - context.dip(defPaddingDp)).toFloat(),
                (size - context.dip(defPaddingDp)).toFloat()
            )
        innerRect =
            RectF(
                outerRect.left + context.dip(defWidthDp),
                outerRect.top + context.dip(defWidthDp),
                outerRect.right - context.dip(defWidthDp),
                outerRect.bottom - context.dip(defWidthDp)
            )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        center = measuredWidth / 2 to measuredHeight / 2
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val arcSweepDegree = 360.toFloat() / colors.size
        var arcOffSetDegree = 0.toFloat()
        canvas?.apply {
            val path = Path()
            colors.forEach { color ->
                path.reset()
                path.arcTo(outerRect, arcOffSetDegree, arcSweepDegree)
                path.arcTo(innerRect, arcOffSetDegree + arcSweepDegree, -arcSweepDegree)
                path.close()

                canvas.drawPath(path, fillArcPaint)
                fillArcPaint.color = color

                strokeArcPaint.strokeWidth =
                    selectedColor?.let {
                        if ( color == it ) {
                            context.dip(selectedStrokeWidthDp).toFloat()
                        } else {
                            context.dip(strokeWidthDp).toFloat()
                        }
                    } ?: context.dip(strokeWidthDp).toFloat()

                strokeArcPaint.color = Color.BLACK
                canvas.drawPath(path, strokeArcPaint)
                arcOffSetDegree += arcSweepDegree
            }
        }
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleColorPicker, 0, 0)

        with(typedArray) {
            strokeWidthDp = getInt(R.styleable.CircleColorPicker_strokeWidthDp, defStrokeWidthDp)
            paddingDp = getInt(R.styleable.CircleColorPicker_padding, defPaddingDp)
            lineWidthDp = getInt(R.styleable.CircleColorPicker_lineWidth, defWidthDp)

            val colorsId =
                getResourceId(R.styleable.CircleColorPicker_colors, 0)

            if (colorsId != 0) {
                val reqColors = typedArray.resources.getIntArray(colorsId)
                reqColors.forEach {
                    colors.add(it)
                }
            } else {
                //No colors, no selection
                colors.add((background as ColorDrawable).color)
            }
        }
        typedArray.recycle()
    }

    init {
        initAttr(context, attrs)
    }

}