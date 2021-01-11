package com.townwang.yaohuo.ui.weight.wlc

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import java.util.*

class Particle(@JvmField var x: Float, @JvmField var y: Float, @JvmField var radius: Float)

class LineEvaluator : TypeEvaluator<Particle> {
    override fun evaluate(
        fraction: Float,
        startValue: Particle,
        endValue: Particle
    ): Particle {
        return Particle(
            startValue.x + (endValue.x - startValue.x) * fraction,
            startValue.y + (endValue.y - startValue.y) * fraction,
            startValue.radius + (endValue.radius - startValue.radius) * fraction
        )
    }
}

typealias ParticleAnimListener = () -> Unit

class ParticleView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val STATUS_MOTIONLESS = 0
    private val STATUS_PARTICLE_GATHER = 1
    private val STATUS_TEXT_MOVING = 2
    private val ROW_NUM = 10
    private val COLUMN_NUM = 10
    private val DEFAULT_MAX_TEXT_SIZE = sp2px(80f)
    private val DEFAULT_MIN_TEXT_SIZE = sp2px(30f)
    val DEFAULT_TEXT_ANIM_TIME = 1000
    val DEFAULT_SPREAD_ANIM_TIME = 300
    val DEFAULT_HOST_TEXT_ANIM_TIME = 800
    private var mHostTextPaint: Paint? = null
    private var mParticleTextPaint: Paint? = null
    private var mCirclePaint: Paint? = null
    private var mHostBgPaint: Paint? = null
    private var mWidth = 0
    private var mHeight = 0

    private val mParticles =
        Array(ROW_NUM) { arrayOfNulls<Particle>(COLUMN_NUM) }
    private val mMinParticles =
        Array(ROW_NUM) { arrayOfNulls<Particle>(COLUMN_NUM) }

    //背景色
    private var mBgColor = 0

    //粒子色
    private var mParticleColor = 0

    //默认粒子文案大小
    private var mParticleTextSize = DEFAULT_MIN_TEXT_SIZE
    private var mStatus = STATUS_MOTIONLESS

    var mParticleAnimListener: ParticleAnimListener? = null

    //粒子文案
    private var mParticleText = BuildConfig.VERSION_NAME

    //主文案
    private var mHostText: String? = null

    //扩散宽度
    private var mSpreadWidth = 0f

    //Host文字展现宽度
    private var mHostRectWidth = 0f

    //粒子文案的x坐标
    private var mParticleTextX = 0f

    //Host文字的x坐标
    private var mHostTextX = 0f

    //Text anim time in milliseconds
    private var mTextAnimTime = 0

    //Spread anim time in milliseconds
    private var mSpreadAnimTime = 0

    //HostText anim time in milliseconds
    private var mHostTextAnimTime = 0
    private var mStartMaxP: PointF? = null
    private var mEndMaxP: PointF? = null
    private var mStartMinP: PointF? = null
    private var mEndMinP: PointF? = null
    private fun initView(attrs: AttributeSet) {
        val typeArray =
            context.obtainStyledAttributes(attrs, R.styleable.ParticleView)
        mHostText =
            if (null == typeArray.getString(R.styleable.ParticleView_pv_host_text)) "" else typeArray.getString(
                R.styleable.ParticleView_pv_host_text
            )
        mParticleTextSize = typeArray.getDimension(
            R.styleable.ParticleView_pv_particle_text_size,
            DEFAULT_MIN_TEXT_SIZE.toFloat()
        ).toInt()
        val hostTextSize = typeArray.getDimension(
            R.styleable.ParticleView_pv_host_text_size,
            DEFAULT_MIN_TEXT_SIZE.toFloat()
        ).toInt()
        mBgColor = typeArray.getColor(
            R.styleable.ParticleView_pv_background_color,
            -0xf79855
        )
        mParticleColor = typeArray.getColor(
            R.styleable.ParticleView_pv_text_color,
            -0x310b03
        )
        mTextAnimTime = typeArray.getInt(
            R.styleable.ParticleView_pv_text_anim_time,
            DEFAULT_TEXT_ANIM_TIME
        )
        mSpreadAnimTime = typeArray.getInt(
            R.styleable.ParticleView_pv_text_anim_time,
            DEFAULT_SPREAD_ANIM_TIME
        )
        mHostTextAnimTime = typeArray.getInt(
            R.styleable.ParticleView_pv_text_anim_time,
            DEFAULT_HOST_TEXT_ANIM_TIME
        )
        typeArray.recycle()
        mHostTextPaint = Paint()
        mHostTextPaint!!.isAntiAlias = true
        mHostTextPaint!!.textSize = hostTextSize.toFloat()
        mParticleTextPaint = Paint()
        mParticleTextPaint!!.isAntiAlias = true
        mCirclePaint = Paint()
        mCirclePaint!!.isAntiAlias = true
        mHostBgPaint = Paint()
        mHostBgPaint!!.isAntiAlias = true
        mHostBgPaint!!.textSize = hostTextSize.toFloat()
        mParticleTextPaint!!.textSize = mParticleTextSize.toFloat()
        mCirclePaint!!.textSize = mParticleTextSize.toFloat()
        mParticleTextPaint!!.color = mBgColor
        mHostTextPaint!!.color = mBgColor
        mCirclePaint!!.color = mParticleColor
        mHostBgPaint!!.color = mParticleColor
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mStartMinP = PointF(
            mWidth / 2 - getTextWidth(
                mParticleText,
                mParticleTextPaint
            ) / 2f - dip2px(4f),
            mHeight / 2 + getTextHeight(
                mHostText,
                mHostTextPaint
            ) / 2 - getTextHeight(mParticleText, mParticleTextPaint) / 0.7f
        )
        mEndMinP = PointF(
            mWidth / 2 + getTextWidth(mParticleText, mParticleTextPaint) / 2f + dip2px(10f),
            mHeight / 2 + getTextHeight(mHostText, mHostTextPaint) / 2
        )
        for (i in 0 until ROW_NUM) {
            for (j in 0 until COLUMN_NUM) {
                mMinParticles[i][j] = Particle(
                    mStartMinP!!.x + (mEndMinP!!.x - mStartMinP!!.x) / COLUMN_NUM * j,
                    mStartMinP!!.y + (mEndMinP!!.y - mStartMinP!!.y) / ROW_NUM * i,
                    dip2px(0.8f).toFloat()
                )
            }
        }
        mStartMaxP = PointF(
            (mWidth / 2 - DEFAULT_MAX_TEXT_SIZE).toFloat(),
            (mHeight / 2 - DEFAULT_MAX_TEXT_SIZE).toFloat()
        )
        mEndMaxP = PointF(
            (mWidth / 2 + DEFAULT_MAX_TEXT_SIZE).toFloat(),
            (mHeight / 2 + DEFAULT_MAX_TEXT_SIZE).toFloat()
        )
        for (i in 0 until ROW_NUM) {
            for (j in 0 until COLUMN_NUM) {
                mParticles[i][j] = Particle(
                    mStartMaxP!!.x + (mEndMaxP!!.x - mStartMaxP!!.x) / COLUMN_NUM * j,
                    mStartMaxP!!.y + (mEndMaxP!!.y - mStartMaxP!!.y) / ROW_NUM * i,
                    getTextWidth(
                        mHostText + mParticleText,
                        mParticleTextPaint
                    ) / (COLUMN_NUM * 1.8f)
                )
            }
        }
        val linearGradient: Shader = LinearGradient(
            mWidth / 2 - getTextWidth(mParticleText, mCirclePaint) / 2f,
            mHeight / 2 - getTextHeight(mParticleText, mCirclePaint) / 2,
            mWidth / 2 - getTextWidth(mParticleText, mCirclePaint) / 2,
            mHeight / 2 + getTextHeight(mParticleText, mCirclePaint) / 2,
            intArrayOf(
                mParticleColor,
                Color.argb(120, getR(mParticleColor), getG(mParticleColor), getB(mParticleColor))
            ),
            null,
            Shader.TileMode.CLAMP
        )
        mCirclePaint!!.shader = linearGradient
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mStatus == STATUS_PARTICLE_GATHER) {
            for (i in 0 until ROW_NUM) {
                for (j in 0 until COLUMN_NUM) {
                    canvas.drawCircle(
                        mParticles[i][j]!!.x,
                        mParticles[i][j]!!.y,
                        mParticles[i][j]!!.radius,
                        mCirclePaint!!
                    )
                }
            }
        }
        if (mStatus == STATUS_TEXT_MOVING) {
            canvas.drawText(
                mHostText!!,
                mHostTextX,
                mHeight / 2 + getTextHeight(mHostText, mHostBgPaint) / 2,
                mHostBgPaint!!
            )
            canvas.drawRect(
                mHostTextX + mHostRectWidth,
                mHeight / 2 - getTextHeight(mHostText, mHostBgPaint) / 1.2f,
                mHostTextX + getTextWidth(mHostText, mHostTextPaint),
                mHeight / 2 + getTextHeight(mHostText, mHostBgPaint) / 1.2f,
                mHostTextPaint!!
            )
        }
        if (mStatus == STATUS_PARTICLE_GATHER) {
            canvas.drawRoundRect(
                RectF(
                    mWidth / 2 - mSpreadWidth,
                    mStartMinP!!.y,
                    mWidth / 2 + mSpreadWidth,
                    mEndMinP!!.y
                ), dip2px(2f).toFloat(), dip2px(2f).toFloat(), mHostBgPaint!!
            )
            canvas.drawText(
                mParticleText,
                mWidth / 2 - getTextWidth(mParticleText, mParticleTextPaint) / 2,
                mStartMinP!!.y + (mEndMinP!!.y - mStartMinP!!.y) / 2 + getTextHeight(
                    mParticleText,
                    mParticleTextPaint
                ) / 2,
                mParticleTextPaint!!
            )
        } else if (mStatus == STATUS_TEXT_MOVING) {
            canvas.drawRoundRect(
                RectF(
                    mParticleTextX - dip2px(4f),
                    mStartMinP!!.y,
                    mParticleTextX + getTextWidth(mParticleText, mParticleTextPaint) + dip2px(4f),
                    mEndMinP!!.y
                ), dip2px(2f).toFloat(), dip2px(2f).toFloat(), mHostBgPaint!!
            )
            canvas.drawText(
                mParticleText,
                mParticleTextX,
                mStartMinP!!.y + (mEndMinP!!.y - mStartMinP!!.y) / 2 + getTextHeight(
                    mParticleText,
                    mParticleTextPaint
                ) / 2,
                mParticleTextPaint!!
            )
        }
    }

    private fun startParticleAnim() {
        mStatus = STATUS_PARTICLE_GATHER
        val animList: MutableCollection<Animator> =
            ArrayList()
        val textAnim = ValueAnimator.ofInt(DEFAULT_MAX_TEXT_SIZE, mParticleTextSize)
        textAnim.setDuration((mTextAnimTime * 0.8f).toLong())
        textAnim.addUpdateListener { valueAnimator ->
            val textSize = valueAnimator.animatedValue as Int
            mParticleTextPaint!!.textSize = textSize.toFloat()
        }
        animList.add(textAnim)
        for (i in 0 until ROW_NUM) {
            for (j in 0 until COLUMN_NUM) {
                val animator = ValueAnimator.ofObject(
                    LineEvaluator(),
                    mParticles[i][j],
                    mMinParticles[i][j]
                )
                animator.duration =
                    mTextAnimTime + (mTextAnimTime * 0.02f).toInt() * i + ((mTextAnimTime * 0.03f).toInt() * j).toLong()
                animator.addUpdateListener { animation ->
                    mParticles[i][j] = animation.animatedValue as Particle
                    if (i == ROW_NUM - 1 && j == COLUMN_NUM - 1) {
                        invalidate()
                    }
                }
                animList.add(animator)
            }
        }
        val set = AnimatorSet()
        set.playTogether(animList)
        set.start()
        set.addListener(object : AnimListener() {
            override fun onAnimationEnd(animation: Animator) {
                startSpreadAnim()
            }
        })
    }

    private fun startSpreadAnim() {
        val animator = ValueAnimator.ofFloat(
            0f,
            getTextWidth(mParticleText, mParticleTextPaint) / 2 + dip2px(4f)
        )
        animator.duration = mSpreadAnimTime.toLong()
        animator.addUpdateListener { animation ->
            mSpreadWidth = animation.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : AnimListener() {
            override fun onAnimationEnd(animation: Animator) {
                startHostTextAnim()
            }
        })
        animator.start()
    }

    private fun startHostTextAnim() {
        mStatus = STATUS_TEXT_MOVING
        val animList: MutableCollection<Animator> =
            ArrayList()
        val particleTextXAnim = ValueAnimator.ofFloat(
            mStartMinP!!.x + dip2px(4f),
            mWidth / 2 - (getTextWidth(mHostText, mHostTextPaint) + getTextWidth(
                mParticleText,
                mParticleTextPaint
            )) / 2 + getTextWidth(mHostText, mHostTextPaint)
        )
        particleTextXAnim.addUpdateListener { animation ->
            mParticleTextX = animation.animatedValue as Float
        }
        animList.add(particleTextXAnim)
        val animator =
            ValueAnimator.ofFloat(0f, getTextWidth(mHostText, mHostTextPaint))
        animator.addUpdateListener { animation ->
            mHostRectWidth = animation.animatedValue as Float
        }
        animList.add(animator)
        val hostTextXAnim = ValueAnimator.ofFloat(
            mStartMinP!!.x,
            mWidth / 2 - (getTextWidth(mHostText, mHostTextPaint) + getTextWidth(
                mParticleText,
                mParticleTextPaint
            ) + dip2px(20f)) / 2
        )
        hostTextXAnim.addUpdateListener { animation ->
            mHostTextX = animation.animatedValue as Float
            invalidate()
        }
        animList.add(hostTextXAnim)
        val set = AnimatorSet()
        set.playTogether(animList)
        set.duration = mHostTextAnimTime.toLong()
        set.addListener(object : AnimListener() {
            override fun onAnimationEnd(animation: Animator) {
                    mParticleAnimListener?.invoke()
            }
        })
        set.start()
    }

    fun startAnim() {
        post { startParticleAnim() }
    }

    private abstract inner class AnimListener :
        Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }

//    interface ParticleAnimListener {
//        fun onAnimationEnd()
//    }

    private fun dip2px(dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    private fun sp2px(spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    private fun getTextHeight(text: String?, paint: Paint?): Float {
        val rect = Rect()
        paint!!.getTextBounds(text, 0, text!!.length, rect)
        return rect.height() / 1.1f
    }

    private fun getTextWidth(text: String?, paint: Paint?): Float {
        return paint!!.measureText(text)
    }

    private fun getR(color: Int): Int {
        return color shr 16 and 0xFF
    }

    private fun getG(color: Int): Int {
        return color shr 8 and 0xFF
    }

    private fun getB(color: Int): Int {
        return color and 0xFF
    }

    init {
        initView(attrs)
    }
}