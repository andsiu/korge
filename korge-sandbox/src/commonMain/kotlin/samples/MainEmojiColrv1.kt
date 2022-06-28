package samples

import com.soywiz.klock.measureTime
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.container
import com.soywiz.korge.view.graphics
import com.soywiz.korge.view.text
import com.soywiz.korge.view.vector.gpuGraphics
import com.soywiz.korge.view.vector.gpuShapeView
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.font.SystemFont
import com.soywiz.korim.font.asFallbackOf
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korim.font.withFallback
import com.soywiz.korim.text.text
import com.soywiz.korim.vector.Context2d
import com.soywiz.korim.vector.ShapeBuilder
import com.soywiz.korim.vector.buildShape
import com.soywiz.korim.vector.toSvg
import com.soywiz.korio.file.std.resourcesVfs

class MainEmojiColrv1 : Scene() {
    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["twemoji-glyf_colr_1.ttf"].readTtfFont(preload = false).asFallbackOf(DefaultTtfFont)
        val font2 = measureTime({resourcesVfs["noto-glyf_colr_1.ttf"].readTtfFont(preload = false).asFallbackOf(DefaultTtfFont)}) {
            println("Read font in... $it")
        }
        val font3 = SystemFont.getEmojiFont().asFallbackOf(DefaultTtfFont)

        println("font=$font, font2=$font2, font3=$font3")

        //val font = DefaultTtfFont.withFallback()
        val str = "HELLO! 😀😁🤤👨‍🦳👨🏻‍🦳👨🏻‍🦳👩🏽‍🦳⛴🔮🤹‍♀️😇🥹🍦💩🥜🥝🌄🏞"
        //val str = "😶"
        //val str = "🌄"

        fun Context2d.buildText() {
            fillText(str, font = font, textSize = 50.0, x = 22.0, y = 0.0, color = Colors.WHITE)
            fillText(str, font = font2, textSize = 50.0, x = 22.0, y = 75.0, color = Colors.WHITE)
            //fillText(str, font = font3, textSize = 50.0, x = 22.0, y = 150.0, color = Colors.WHITE)
        }

        container {
            xy(0, 0)
            text(str, font = font, textSize = 50.0, color = Colors.WHITE).xy(x = 22.0, y = 0.0)
            text(str, font = font2, textSize = 50.0, color = Colors.WHITE).xy(x = 22.0, y = 75.0)
        }

        val shape = buildShape { buildText() }
        //println(shape.toSvg())

        println("native rendered in..." + measureTime {
            graphics {
                it.xy(0, 200)
                it.useNativeRendering = true
                buildText()
            }.also {
                it.redrawIfRequired()
            }
        })
        println("non-native rendered in..." + measureTime {
            graphics {
                it.xy(0, 350)
                it.useNativeRendering = false
                buildText()
            }.also {
                it.redrawIfRequired()
            }
        })
        gpuGraphics {
            it.xy(0, 500)
            buildText()
        }
        //text(str, font = font, textSize = 50.0).xy(64, 100)
        //text(str, font = font2, textSize = 50.0).xy(64, 200)
        //text(str, font = font3, textSize = 50.0).xy(64, 300)

        //text("👨‍🦳", font = font, textSize = 64.0).xy(64, 100)
        //text("\uD83D\uDC68\u200D", font = font, textSize = 64.0).xy(64, 100)
        //text("HELLO! 😀😁🤤", font = font, textSize = 64.0).xy(64, 100)
        //text("HELLO! 😀\uD83D\uDE01\uD83E\uDD24a", font = font, textSize = 64.0).xy(64, 100)
        //text("😀a", font = font, textSize = 64.0).xy(64, 100)
        //text("😀", font = font, textSize = 64.0).xy(64, 100)
        //text("HELLO! \uD83D\uDE00", font = font, textSize = 64.0).xy(50, 100)
    }
}
