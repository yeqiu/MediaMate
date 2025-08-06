package com.yeqiu.mediamate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yeqiu.mediamate.MediaMateManager
import com.yeqiu.mediamate.databinding.FragmentMediaSelectBinding
import com.yeqiu.mediamate.model.MediaFolder
import com.yeqiu.mediamate.model.MediaItemSelect
import com.yeqiu.mediamate.utils.AnimatorUtil
import com.yeqiu.mediamate.utils.Util
import com.yeqiu.mediamate.viewmodel.MediaSelectViewModel

/**
 * @project：AchillesArmory
 * @author：小卷子
 * @date 2025/7/27
 * @describe：
 * @fix：
 */
internal class MediaSelectFragment private constructor() : Fragment() {

    companion object {

        fun getInstance(): MediaSelectFragment {
            return MediaSelectFragment()
        }
    }

    private lateinit var binding: FragmentMediaSelectBinding

    private val viewModel by activityViewModels<MediaSelectViewModel>()

    private lateinit var backPressedCallback: OnBackPressedCallback

    private val mediaAdapter by lazy {
        MediaAdapter(onSelectListener)
    }

    private val folderAdapter by lazy {
        MediaFolderAdapter(onItemClickListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 创建回调
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }

        // 注册回调
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.onClickListener = this

        init()
    }


    fun onBackPressed() {

        if (binding.rvFolder.isVisible) {
            hideFolder()
        } else {
            requireActivity().finish()
        }
    }


    private val onItemClickListener: (MediaFolder) -> Unit = {

        viewModel.selectFolder(it)
        hideFolder()
    }

    private val onSelectListener: (MediaItemSelect) -> Unit = { it ->

        if (it.isSelect.get()) {
            it.isSelect.set(false)
        } else {
            if (viewModel.canSelect()) {
                it.isSelect.set(true)
            } else {
                Util.showToast(requireActivity(), "最多只能选择${viewModel.getMaxSize()}个文件")
            }
        }

        viewModel.updateSelectSize()


    }

    private fun init() {

        viewModel.initConfigArgument()

        binding.rvFolder.visibility = View.GONE
        binding.rvMedia.adapter = mediaAdapter
        binding.rvFolder.adapter = folderAdapter
        binding.rvMedia.addItemDecoration(GridDividerItemDecoration())

        viewModel.mediaList.observe(viewLifecycleOwner) {
            mediaAdapter.setList(it)
        }

        viewModel.folderList.observe(viewLifecycleOwner) {
            folderAdapter.setList(it)
        }

        viewModel.resultList.observe(viewLifecycleOwner) {
            MediaMateManager.onSelectResult(it)

            onBackPressed()
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                LoadingDialog.show(childFragmentManager)
            } else {
                LoadingDialog.dismiss(childFragmentManager)
            }
        }

    }


    fun clickShowFolder() {
        if (binding.rvFolder.isVisible) {
            hideFolder()
        } else {
            showFolder()
        }
    }


    private fun showFolder(){


        AnimatorUtil.expandedWithAnimator( binding.ivArrow,true)
        AnimatorUtil.showWhitAnim(binding.rvFolder,true)
    }

    private fun hideFolder() {
        AnimatorUtil.expandedWithAnimator( binding.ivArrow,false)
        AnimatorUtil.showWhitAnim(binding.rvFolder,false)
    }

    fun clickNext() {
        viewModel.doNext()
    }


}
