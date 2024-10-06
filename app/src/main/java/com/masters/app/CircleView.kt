package com.masters.app // Replace with your actual package

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var number: Int = 0

    init {
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 50f
    }

    fun setNumber(number: Int) {
        this.number = number
        invalidate() // Redraw the view when the number changes
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Set the color based on the number
        paint.color = when (number) {
            in 0..10 -> Color.RED
            in 11..50 -> Color.parseColor("#FFA500") // Orange
            in 51..90 -> Color.GREEN
            else -> Color.CYAN
        }

        // Draw the circle
        val radius = minOf(width, height) / 3f
        val cx = (width / 2).toFloat()
        val cy = (height / 2).toFloat()
        canvas.drawCircle(cx, cy, radius, paint)

        // Set the paint color for the number text
        paint.color = Color.BLACK
        canvas.drawText("$number", cx, cy + paint.textSize / 3, paint)
    }

    companion object {
        const val DEFAULT_RADIUS = 100
        const val DEFAULT_COLOR = Color.GRAY
    }
}
