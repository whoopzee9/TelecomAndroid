package ru.spbstu.feature.test.presentation

import android.content.Intent
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.ui.MatisseActivity
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.getFileName
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.common.utils.CoilImageEngine
import ru.spbstu.common.utils.PermissionUtils
import ru.spbstu.common.utils.ToolbarFragment
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentTestBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
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
                    viewModel.uploadFile(viewModel.filesList.first()) { progress, uploaded, total ->
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
            }

        MultipartUploadRequest().addFileToUpload().subs
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
                Toast.makeText(requireContext(), R.string.file_not_uploaded, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAdapter() {
        adapter = FilesNamesAdapter(viewModel::onItemClick)
        binding.frgTestRvList.adapter = adapter
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
