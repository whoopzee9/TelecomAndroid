package ru.spbstu.common.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.zhihu.matisse.engine.ImageEngine

class CoilImageEngine : ImageEngine {
    override fun loadThumbnail(
        context: Context?,
        resize: Int,
        placeholder: Drawable?,
        imageView: ImageView?,
        uri: Uri?
    ) {
        imageView?.load(uri) {
            placeholder(placeholder)
            scale(Scale.FILL)
            transformations(RoundedCornersTransformation(0f))
            size(resize)
        }
    }

    override fun loadGifThumbnail(
        context: Context?,
        resize: Int,
        placeholder: Drawable?,
        imageView: ImageView?,
        uri: Uri?
    ) {
        imageView?.load(uri) {
            placeholder(placeholder)
            size(resize)
        }
    }

    override fun loadImage(
        context: Context?,
        resizeX: Int,
        resizeY: Int,
        imageView: ImageView?,
        uri: Uri?
    ) {
        imageView?.load(uri) {
            size(resizeX, resizeY)
        }
    }

    override fun loadGifImage(
        context: Context?,
        resizeX: Int,
        resizeY: Int,
        imageView: ImageView?,
        uri: Uri?
    ) {
        imageView?.load(uri) {
            size(resizeX, resizeY)
        }
    }

    override fun supportAnimatedGif(): Boolean {
        return false
    }
}
