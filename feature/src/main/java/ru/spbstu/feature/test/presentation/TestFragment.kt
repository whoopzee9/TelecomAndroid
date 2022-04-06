package ru.spbstu.feature.test.presentation

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.zhihu.matisse.BuildConfig.APPLICATION_ID
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.ui.MatisseActivity
import ru.spbstu.common.BuildConfig
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.getFileName
import ru.spbstu.common.extenstions.getMimeType
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.CoilImageEngine
import ru.spbstu.common.utils.PermissionUtils
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTestBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import ru.spbstu.feature.files_helper.FilesHelper
import ru.spbstu.feature.test.presentation.adapter.FilesNamesAdapter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class TestFragment : ToolbarFragment<TestViewModel>(
    R.layout.fragment_test,
    R.string.error_connection,
    ToolbarType.EMPTY
) {

    override val binding by viewBinding(FragmentTestBinding::bind)

    private lateinit var adapter: FilesNamesAdapter

    override fun getToolbarLayout(): ViewGroup = binding.frgTestLayoutToolbar.root

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val downloadManager by lazy {
        requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun setupViews() {
        super.setupViews()
        initAdapter()
        binding.frgTestFabAdd.setDebounceClickListener {
            requestAndSelectPhoto()
        }
        viewModel.getFilesNames()
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != 0) {
                    val res = Matisse.obtainResult(result.data)
                    viewModel.filesList.clear()
                    res.forEach {
                        val parcelFileDescriptor =
                            requireContext().contentResolver.openFileDescriptor(it, "r", null)

                        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
                        val file = File(
                            requireContext().cacheDir,
                            requireContext().contentResolver.getFileName(it)
                        )
                        val outputStream = FileOutputStream(file)
                        inputStream.copyTo(outputStream)
                        viewModel.filesList.add(file)
                    }
                    viewModel.filesList.forEach {

                        viewModel.uploadFileInService(
                            it,
                            requireContext(),
                            viewLifecycleOwner
                        ) { progress, uploaded, total ->
                            binding.frgTestTvProgress.text = getString(
                                R.string.remaining,
                                (uploaded / (1024f * 1024)),
                                (total / (1024f * 1024)),
                                viewModel.currentFile,
                                viewModel.filesList.size
                            )
                            binding.frgTestProgressBar.progress = progress
                        }
                    }
//                    viewModel.uploadFile(viewModel.filesList.first()) { progress, uploaded, total ->
//                        binding.frgTestTvProgress.text = getString(
//                            R.string.remaining,
//                            (uploaded / (1024f * 1024)),
//                            (total / (1024f * 1024)),
//                            viewModel.currentFile,
//                            viewModel.filesList.size
//                        )
//                        binding.frgTestProgressBar.progress = progress
//                    }
                }
            }

    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .testComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun subscribe() {
        super.subscribe()
        viewModel.filesNames.observe {
            adapter.bindData(it)
        }
        viewModel.filesUploadState.observe {
            handleFilesUploadState(it)
        }
    }

    private fun handleFilesUploadState(state: TestViewModel.FileUploadState) {
        when (state) {
            TestViewModel.FileUploadState.FileLoadedSuccess -> {
                binding.frgTestProgressBar.visibility = View.VISIBLE
                binding.frgTestProgressBar.progress = 100
                binding.frgTestTvProgress.visibility = View.VISIBLE
                Toast.makeText(requireContext(), R.string.file_uploaded, Toast.LENGTH_SHORT).show()
            }
            TestViewModel.FileUploadState.Initial -> {
                binding.frgTestProgressBar.visibility = View.GONE
                binding.frgTestTvProgress.visibility = View.GONE
            }
            TestViewModel.FileUploadState.Processing -> {
                binding.frgTestProgressBar.visibility = View.VISIBLE
                binding.frgTestTvProgress.visibility = View.VISIBLE
            }
            is TestViewModel.FileUploadState.Failure -> {
                binding.frgTestProgressBar.visibility = View.VISIBLE
                binding.frgTestTvProgress.visibility = View.VISIBLE
                Toast.makeText(requireContext(), R.string.file_not_uploaded, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initAdapter() {
        adapter = FilesNamesAdapter {
            val cachePath = FilesHelper.createCacheFolder(requireContext())

            val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), it)

            val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.AUTHORITY, file)



            if (file.exists()) {
                openFileIntent(uri)
            } else {
                val downloadRequest =
                    DownloadManager.Request(Uri.parse("${BuildConfig.ENDPOINT}/file/download/$it"))
                        .setAllowedOverMetered(true)
                        .setTitle("Telecom")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                        .setVisibleInDownloadsUi(false)
                        .setDestinationUri(uri)
                        //.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, it)
                val downloadId = downloadManager.enqueue(downloadRequest)

                viewModel.saveDownloadId(it, downloadId)
            }
        }
        binding.frgTestRvList.adapter = adapter
    }

    private fun openFileIntent(uri: Uri) {
        val openFileIntent = Intent()
        openFileIntent.action = Intent.ACTION_VIEW
        openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        openFileIntent.setDataAndType(
            uri, requireContext().contentResolver.getType(uri)
        )
        openFileIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(openFileIntent, getString(R.string.select_application)))
    }

    private fun getDMStatus(downloadId: Long): Int? {
        val request = DownloadManager.Query()
            .setFilterById(downloadId)
        downloadManager.query(request).use {
            return if (it.count > 0) {
                it.getInt(it.getColumnIndex(DownloadManager.COLUMN_STATUS))
            } else null
        }
    }

    private fun checkDMStatus(fileName: String) {
        val downloadId = viewModel.getDownloadId(fileName)
        val status = getDMStatus(downloadId)
        when (status) {
            DownloadManager.STATUS_FAILED -> {
                //download again
            }
            DownloadManager.STATUS_
        }
    }

    private fun requestAndSelectPhoto() {
        PermissionUtils.checkStoragePermissions(this) {
            Matisse.from(this)
                .choose(
                    EnumSet.of(
                        MimeType.JPEG,
                        MimeType.MP4,
                        MimeType.PNG,
                        MimeType.BMP,
                        MimeType.WEBP
                    )
                )
                .countable(true)
                .maxSelectable(Int.MAX_VALUE) //Int.MAX_VALUE
                .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                .showSingleMediaType(true)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(1f)
                .imageEngine(CoilImageEngine())
                .theme(R.style.Theme_Matisse)

            val intent = Intent(requireContext(), MatisseActivity::class.java)

            resultLauncher.launch(intent)
        }
    }
}
