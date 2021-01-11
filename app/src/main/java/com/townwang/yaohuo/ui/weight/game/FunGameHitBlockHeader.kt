package com.townwang.yaohuo.ui.weight.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.util.SmartUtil
import com.townwang.yaohuo.R
import java.util.*

class FunGameHitBlockHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FunGameView<Any?>(context, attrs, 0) {
    /**
     * 矩形砖块的高度、宽度
     */
    private var blockHeight = 0f
    private var blockWidth = 0f

    /**
     * 小球半径
     */
    private var BALL_RADIUS: Float
    private var blockPaint: Paint
    private var blockLeft = 0f
    private var racketLeft = 0f
    private var cx = 0f
    private var cy = 0f
    private var pointList: MutableList<Point>? = null
    private var isLeft = false
    private var angle = 0
    private var blockHorizontalNum: Int
    private var speed: Int
    override fun onInitialized(
        kernel: RefreshKernel,
        height: Int,
        maxDragHeight: Int
    ) {
        val thisView: View = this
        val measuredWidth = thisView.measuredWidth
        blockHeight =
            1f * height / BLOCK_VERTICAL_NUM - Companion.DIVIDING_LINE_SIZE
        blockWidth = measuredWidth * BLOCK_WIDTH_RATIO
        blockLeft = measuredWidth * BLOCK_POSITION_RATIO
        racketLeft = measuredWidth * RACKET_POSITION_RATIO
        controllerSize = (blockHeight * 1.6f).toInt()
        super.onInitialized(kernel, height, maxDragHeight)
    }

    //</editor-fold>
    //<editor-fold desc="游戏控制">
    override fun resetConfigParams() {
        cx = racketLeft - 3 * BALL_RADIUS
        cy = (mHeaderHeight * .5f)
        controllerPosition = Companion.DIVIDING_LINE_SIZE
        angle = DEFAULT_ANGLE
        isLeft = true
        if (pointList == null) {
            pointList = ArrayList()
        } else {
            pointList!!.clear()
        }
    }

    /**
     * 检查小球是否撞击到挡板
     *
     * @param y 小球当前坐标Y值
     * @return 小球位于挡板Y值区域范围内：true，反之：false
     */
    private fun checkTouchRacket(y: Float): Boolean {
        var flag = false
        val diffVal = y - controllerPosition
        if (diffVal >= 0 && diffVal <= controllerSize) { // 小球位于挡板Y值区域范围内
            flag = true
        }
        return flag
    }

    /**
     * 检查小球是否撞击到矩形块
     *
     * @param x 小球坐标X值
     * @param y 小球坐标Y值
     * @return 撞击到：true，反之：false
     */
    private fun checkTouchBlock(x: Float, y: Float): Boolean {
        var columnX = ((x - blockLeft - BALL_RADIUS - speed) / blockWidth).toInt()
        columnX = if (columnX == blockHorizontalNum) columnX - 1 else columnX
        var rowY = (y / blockHeight).toInt()
        rowY = if (rowY == BLOCK_VERTICAL_NUM) rowY - 1 else rowY
        val p = Point()
        p[columnX] = rowY
        var flag = false
        for (point in pointList!!) {
            if (point.equals(p.x, p.y)) {
                flag = true
                break
            }
        }
        if (!flag) {
            pointList!!.add(p)
        }
        return !flag
    }

    //</editor-fold>
    //<editor-fold desc="绘制方法">
    override fun drawGame(canvas: Canvas?, width: Int, height: Int) {
        val thisView: View = this
        drawColorBlock(canvas)
        drawRacket(canvas)
        if (status == STATUS_GAME_PLAY || status == STATUS_GAME_FINISHED || status == STATUS_GAME_FAIL || thisView.isInEditMode
        ) {
            drawBallPath(canvas, width)
        }
    }

    /**
     * 绘制挡板
     *
     * @param canvas 默认画布
     */
    private fun drawRacket(canvas: Canvas?) {
        mPaint.color = rModelColor
        canvas!!.drawRect(
            racketLeft,
            controllerPosition,
            racketLeft + blockWidth,
            controllerPosition + controllerSize,
            mPaint
        )
    }

    /**
     * 绘制并处理小球运动的轨迹
     *
     * @param canvas 默认画布
     * @param width  视图宽度
     */
    private fun drawBallPath(canvas: Canvas?, width: Int) {
        mPaint.color = mModelColor
        if (cx <= blockLeft + blockHorizontalNum * blockWidth + (blockHorizontalNum - 1) * Companion.DIVIDING_LINE_SIZE + BALL_RADIUS) { // 小球进入到色块区域
            if (checkTouchBlock(cx, cy)) { // 反弹回来
                isLeft = false
            }
        }
        if (cx <= blockLeft + BALL_RADIUS) { // 小球穿过色块区域
            isLeft = false
        }
        if (cx + BALL_RADIUS >= racketLeft && cx - BALL_RADIUS < racketLeft + blockWidth) { //小球当前坐标X值在挡板X值区域范围内
            if (checkTouchRacket(cy)) { // 小球与挡板接触
                if (pointList!!.size == blockHorizontalNum * BLOCK_VERTICAL_NUM) { // 矩形块全部被消灭，游戏结束
                    status = STATUS_GAME_OVER
                    return
                }
                isLeft = true
            }
        } else if (cx > width) { // 小球超出挡板区域
            status = STATUS_GAME_OVER
        }
        if (cy <= BALL_RADIUS + Companion.DIVIDING_LINE_SIZE) { // 小球撞到上边界
            angle = 180 - DEFAULT_ANGLE
        } else if (cy >= mHeaderHeight - BALL_RADIUS - Companion.DIVIDING_LINE_SIZE) { // 小球撞到下边界
            angle = 180 + DEFAULT_ANGLE
        }
        if (isLeft) {
            cx -= speed.toFloat()
        } else {
            cx += speed.toFloat()
        }
        cy -= Math.tan(Math.toRadians(angle.toDouble())).toFloat() * speed
        canvas!!.drawCircle(cx, cy, BALL_RADIUS, mPaint)
        val thisView: View = this
        thisView.invalidate()
    }

    /**
     * 绘制矩形色块
     *
     * @param canvas 默认画布
     */
    private fun drawColorBlock(canvas: Canvas?) {
        var left: Float
        var top: Float
        var column: Int
        var row: Int
        for (i in 0 until blockHorizontalNum * BLOCK_VERTICAL_NUM) {
            row = i / blockHorizontalNum
            column = i % blockHorizontalNum
            var flag = false
            for (point in pointList!!) {
                if (point.equals(column, row)) {
                    flag = true
                    break
                }
            }
            if (flag) {
                continue
            }
            blockPaint.color = ColorUtils.setAlphaComponent(lModelColor, 255 / (column + 1))
            left =
                blockLeft + column * (blockWidth + Companion.DIVIDING_LINE_SIZE)
            top =
                Companion.DIVIDING_LINE_SIZE + row * (blockHeight + Companion.DIVIDING_LINE_SIZE)
            canvas!!.drawRect(left, top, left + blockWidth, top + blockHeight, blockPaint)
        }
    }

    companion object {
        /**
         * 默认矩形块竖向排列的数目
         */
         const val BLOCK_VERTICAL_NUM = 5

        /**
         * 默认矩形块横向排列的数目
         */
         const val BLOCK_HORIZONTAL_NUM = 3

        /**
         * 矩形块的宽度占屏幕宽度比率
         */
         const val BLOCK_WIDTH_RATIO = .01806f

        /**
         * 挡板所在位置占屏幕宽度的比率
         */
         const val RACKET_POSITION_RATIO = .8f

        /**
         * 矩形块所在位置占屏幕宽度的比率
         */
         const val BLOCK_POSITION_RATIO = .08f

        /**
         * 小球默认其实弹射角度
         */
         const val DEFAULT_ANGLE = 30

        /**
         * 分割线默认宽度大小
         */
        const val DIVIDING_LINE_SIZE = 1f

        /**
         * 小球移动速度
         */
         const val SPEED = 3
    }

    init {
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.FunGameHitBlockHeader
        )
        speed = ta.getInt(
            R.styleable.FunGameHitBlockHeader_srlBallSpeed,
            SmartUtil.dp2px(SPEED.toFloat())
        )
        blockHorizontalNum = ta.getInt(
            R.styleable.FunGameHitBlockHeader_srlBlockHorizontalNum,
            BLOCK_HORIZONTAL_NUM
        )
        if (ta.hasValue(R.styleable.FunGameHitBlockHeader_fghBallSpeed)) {
            speed = ta.getInt(
                R.styleable.FunGameHitBlockHeader_fghBallSpeed,
                SmartUtil.dp2px(SPEED.toFloat())
            )
        }
        if (ta.hasValue(R.styleable.FunGameHitBlockHeader_fghBlockHorizontalNum)) {
            blockHorizontalNum = ta.getInt(
                R.styleable.FunGameHitBlockHeader_fghBlockHorizontalNum,
                BLOCK_HORIZONTAL_NUM
            )
        }
        ta.recycle()
        blockPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        blockPaint.style = Paint.Style.FILL
        BALL_RADIUS = SmartUtil.dp2px(4f).toFloat()
    }
}