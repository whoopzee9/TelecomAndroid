package ru.spbstu.telecom

import android.R
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.viewbinding.BuildConfig
import net.gotev.uploadservice.UploadServiceConfig
import net.gotev.uploadservice.data.RetryPolicyConfig
import net.gotev.uploadservice.data.UploadElapsedTime
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.data.UploadNotificationAction
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.data.UploadRate
import net.gotev.uploadservice.extensions.getCancelUploadIntent
import net.gotev.uploadservice.extensions.startNewUpload
import net.gotev.uploadservice.placeholders.Placeholder
import net.gotev.uploadservice.placeholders.PlaceholdersProcessor
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.telecom.di.app.AppComponent
import ru.spbstu.telecom.di.app.DaggerAppComponent
import ru.spbstu.telecom.di.deps.FeatureHolderManager
import ru.spbstu.telecom.log.ReleaseTree
import timber.log.Timber
import javax.inject.Inject

open class App : Application(), FeatureContainer {

    @Inject
    lateinit var featureHolderManager: FeatureHolderManager

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()

        appComponent.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        createNotificationChannel()

        UploadServiceConfig.initialize(
            context = this,
            defaultNotificationChannel = notificationChannelID,
            debug = BuildConfig.DEBUG
        )
        UploadServiceConfig.notificationConfigFactory = { context, uploadId ->
            val title = getString(ru.spbstu.telecom.R.string.uploading_files)
            UploadNotificationConfig(
                notificationChannelId = UploadServiceConfig.defaultNotificationChannel!!,
                isRingToneEnabled = true,
                progress = UploadNotificationStatusConfig(
                    title = title,
                    iconResourceID = ru.spbstu.common.R.drawable.ic_download_24,
                    iconColorResourceID = ContextCompat.getColor(context, ru.spbstu.common.R.color.background_primary),
                    largeIcon = BitmapFactory.decodeResource(resources, ru.spbstu.common.R.drawable.ic_download_24),
                    message = getString(ru.spbstu.telecom.R.string.loading),
                    actions = arrayListOf(
                        UploadNotificationAction(
                            icon = R.drawable.ic_menu_close_clear_cancel,
                            title = getString(ru.spbstu.telecom.R.string.cancel),
                            intent = context.getCancelUploadIntent(uploadId)
                        )
                    )
                ),
                success = UploadNotificationStatusConfig(
                    title = title,
                    iconResourceID = ru.spbstu.common.R.drawable.ic_download_24,
                    iconColorResourceID = ContextCompat.getColor(context, ru.spbstu.common.R.color.background_primary),
                    largeIcon = BitmapFactory.decodeResource(resources, ru.spbstu.common.R.drawable.ic_download_24),
                    message = getString(ru.spbstu.telecom.R.string.upload_completed_in)
                ),
                error = UploadNotificationStatusConfig(
                    title = title,
                    iconResourceID = ru.spbstu.common.R.drawable.ic_download_24,
                    iconColorResourceID = ContextCompat.getColor(context, ru.spbstu.common.R.color.background_primary),
                    largeIcon = BitmapFactory.decodeResource(resources, ru.spbstu.common.R.drawable.ic_download_24),
                    message = getString(ru.spbstu.telecom.R.string.upload_error),
//                    actions = arrayListOf(
//                        UploadNotificationAction(
//                            icon = R.drawable.ic_menu_close_clear_cancel,
//                            title = getString(ru.spbstu.telecom.R.string.retry),
//                            intent = context.startNewUpload()
//                        )
//                    )
                ),
                cancelled = UploadNotificationStatusConfig(
                    title = title,
                    iconResourceID = ru.spbstu.common.R.drawable.ic_download_24,
                    iconColorResourceID = ContextCompat.getColor(context, ru.spbstu.common.R.color.background_primary),
                    largeIcon = BitmapFactory.decodeResource(resources, ru.spbstu.common.R.drawable.ic_download_24),
                    message = getString(ru.spbstu.telecom.R.string.upload_canceled)
                )
            )
        }

        UploadServiceConfig.placeholdersProcessor = object : PlaceholdersProcessor {
            fun uploadElapsedTime(uploadElapsedTime: UploadElapsedTime) = when {
                uploadElapsedTime.minutes == 0 -> "${uploadElapsedTime.seconds} ??????"
                else -> "${uploadElapsedTime.minutes} ?????? ${uploadElapsedTime.seconds} ??????"
            }

            fun uploadRate(uploadRate: UploadRate): String {
                val suffix = when (uploadRate.unit) {
                    UploadRate.UploadRateUnit.BitPerSecond -> getString(ru.spbstu.telecom.R.string.bit_per_second)
                    UploadRate.UploadRateUnit.KilobitPerSecond -> getString(ru.spbstu.telecom.R.string.kilobit_per_second)
                    UploadRate.UploadRateUnit.MegabitPerSecond -> getString(ru.spbstu.telecom.R.string.mbit_per_second)
                }

                return "${uploadRate.value} $suffix"
            }

            fun uploadProgress(percent: Int) = "$percent %"

            fun uploadedFiles(uploadedFiles: Int) = "$uploadedFiles"

            fun remainingFiles(remainingFiles: Int) = "$remainingFiles"

            fun totalFiles(totalFiles: Int) = "$totalFiles"

            override fun processPlaceholders(message: String?, uploadInfo: UploadInfo): String {
                val safeMessage = message ?: return ""

                val uploadedFiles = uploadInfo.successfullyUploadedFiles
                val totalFiles = uploadInfo.files.size
                val remainingFiles = totalFiles - uploadedFiles

                return safeMessage
                    .replace(
                        Placeholder.ElapsedTime.value,
                        uploadElapsedTime(uploadInfo.elapsedTime)
                    )
                    .replace(Placeholder.UploadRate.value, uploadRate(uploadInfo.uploadRate))
                    .replace(Placeholder.Progress.value, uploadProgress(uploadInfo.progressPercent))
                    .replace(Placeholder.UploadedFiles.value, uploadedFiles(uploadedFiles))
                    .replace(Placeholder.RemainingFiles.value, remainingFiles(remainingFiles))
                    .replace(Placeholder.TotalFiles.value, totalFiles(totalFiles))
            }

        }
        UploadServiceConfig.retryPolicy = RetryPolicyConfig(
            initialWaitTimeSeconds = 10,
            maxWaitTimeSeconds = 100,
            multiplier = 2,
            defaultMaxRetries = 3
        )

    }

    override fun <T> getFeature(key: Class<*>): T {
        return featureHolderManager.getFeature<T>(key)!!
    }

    override fun releaseFeature(key: Class<*>) {
        featureHolderManager.releaseFeature(key)
    }

    override fun commonApi(): CommonApi {
        return appComponent
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                notificationChannelID,
                "Telecom Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val notificationChannelID = "TelecomChannel"
    }
}
