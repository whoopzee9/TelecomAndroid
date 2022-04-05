package ru.spbstu.common.utils

import android.Manifest
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tbruyelle.rxpermissions3.RxPermissions
import ru.spbstu.common.R

object PermissionUtils {
    fun checkStoragePermissions(frg: Fragment, onGranted: () -> Unit) {
        RxPermissions(frg)
            .request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .subscribe { granted ->
                if (granted) {
                    onGranted.invoke()
                } else {
                    Toast.makeText(frg.context, R.string.need_permissions, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}