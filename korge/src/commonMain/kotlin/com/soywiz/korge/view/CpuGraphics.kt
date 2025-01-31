package com.soywiz.korge.view

import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korim.vector.Context2d
import com.soywiz.korim.vector.Shape
import com.soywiz.korim.vector.ShapeBuilder
import com.soywiz.korim.vector.buildShape
import com.soywiz.korma.geom.BoundsBuilder
import com.soywiz.korma.geom.shape.Shape2d
import com.soywiz.korma.geom.shape.toShape2d
import com.soywiz.korma.geom.vector.VectorPath
import kotlin.jvm.JvmOverloads

inline fun Container.cpuGraphics(autoScaling: Boolean = false, callback: ShapeBuilder.(CpuGraphics) -> Unit = {}): CpuGraphics = CpuGraphics(autoScaling).addTo(this).also { graphics ->
    graphics.updateShape { callback(this, graphics) }
    graphics.redrawIfRequired()
}

@KorgeExperimental
inline fun Container.cpuGraphics(
    build: ShapeBuilder.() -> Unit,
    antialiased: Boolean = true,
    callback: @ViewDslMarker CpuGraphics.() -> Unit = {}
) = CpuGraphics(buildShape { build() }, antialiased).addTo(this, callback)

@KorgeExperimental
inline fun Container.cpuGraphics(
    shape: Shape,
    antialiased: Boolean = true,
    callback: @ViewDslMarker CpuGraphics.() -> Unit = {}
) = CpuGraphics(shape, antialiased).addTo(this, callback)

fun CpuGraphics(
    shape: Shape,
    antialiased: Boolean = true,
): CpuGraphics = CpuGraphics().apply {
    this.antialiased = antialiased
    this.shape = shape
}

open class CpuGraphics @JvmOverloads constructor(
    autoScaling: Boolean = false
) : BaseGraphics(autoScaling) {
    //internal val vectorPathPool = Pool(reset = { it.clear() }) { VectorPath() }
    private var shapeVersion = 0
    var shape: Shape? = null
        set(value) {
            field = value
            shapeVersion++
            dirty()
        }
	//@PublishedApi
	//internal var currentPath = vectorPathPool.alloc()

    // @TODO: Not used but to have same API as GpuShapeView
    var antialiased: Boolean = true

    inline fun updateShape(redrawNow: Boolean = false, block: ShapeBuilder.(CpuGraphics) -> Unit): CpuGraphics {
        this.shape = buildShape { block(this@CpuGraphics) }
        if (redrawNow) this.redrawIfRequired()
        return this
    }

    private var hitShapeVersion = -1
    private var hitShape2dVersion = -1

    private var tempVectorPaths = arrayListOf<VectorPath>()
    private var customHitShape2d: Shape2d? = null
    private var customHitShapes: List<VectorPath>? = null

    override var hitShape: VectorPath?
        set(value) { customHitShapes = value?.let { listOf(it) } }
        get() = hitShapes?.firstOrNull()

    @PublishedApi
    internal inline fun dirty(callback: () -> Unit): CpuGraphics {
        dirty()
        callback()
        return this
    }

    override var hitShapes: List<VectorPath>?
        set(value) {
            customHitShapes = value
        }
        get() {
            if (customHitShapes != null) return customHitShapes
            if (hitShapeVersion != shapeVersion) {
                hitShapeVersion = shapeVersion
                tempVectorPaths.clear()

                // @TODO: Try to combine polygons on KorGE 2.0 to have a single hitShape
                //when (shape) {
                //is StyledShape -> shape.path?.let { tempVectorPaths.add(it) }
                //else ->
                tempVectorPaths.add(shape?.getPath() ?: VectorPath())
            }

            return tempVectorPaths
        }

    override var hitShape2d: Shape2d
        set(value) {
            customHitShape2d = value
        }
        get() {
            if (customHitShape2d != null) return customHitShape2d!!
            if (hitShape2dVersion != shapeVersion) {
                hitShape2dVersion = shapeVersion
                customHitShape2d = hitShapes!!.toShape2d()
            }
            return customHitShape2d!!
        }

    init {
        hitTestUsingShapes = true
    }

    override fun drawShape(ctx: Context2d) {
        shape?.draw(ctx)
    }

    override fun getShapeBounds(bb: BoundsBuilder, includeStrokes: Boolean) {
        shape?.addBounds(bb, includeStrokes)
    }
}
