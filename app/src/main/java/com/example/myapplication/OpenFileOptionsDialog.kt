package com.example.myapplication

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.Constants.DOC_BOOKMARK
import com.example.myapplication.Constants.DOC_TABLE_CONTENT
import com.example.myapplication.databinding.ContentOpenFileOptionDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OpenFileOptionsDialog: BottomSheetDialogFragment() {
    private var openedOptionListener: ((selectedOption: String) -> Unit)? = null

    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        fun newInstance(title: String, listener: (selectedOption: String) -> Unit): OpenFileOptionsDialog {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            val fragment = OpenFileOptionsDialog()
            fragment.arguments = args
            fragment.openedOptionListener = listener
            return fragment
        }

    }

    private var _binding: ContentOpenFileOptionDialogBinding? = null
    private val binding get() = _binding!!
    private var documentName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.StyleBottomSheetDialog)
        documentName = arguments?.getString(KEY_TITLE)
        Log.i("FILEOP", "onCreate:--IF$documentName")


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = ContentOpenFileOptionDialogBinding.inflate(inflater, container, false)

        }
       documentName?.let {
           binding.titleTv.text = it
       }
        binding.setListenersToButtons()
        return binding.root
    }




    override fun onPause() {
        super.onPause()
        try {
            // dismiss with extension function
            activity?.dismissDialogIfVisible(dialog = dialog)
        } catch (ex: Exception) {
            Log.i("TAG", "onPause: ")
        }
    }

    private fun ContentOpenFileOptionDialogBinding.setListenersToButtons() {

        tableContentBtn.setOnSingleClickListener ({
            openedOptionListener?.invoke(DOC_TABLE_CONTENT)
            runDismiss()

        })
        addBookmarkBtn.setOnSingleClickListener ({
            openedOptionListener?.invoke(DOC_BOOKMARK)
            runDismiss()

        })


    }

    private fun runDismiss() {
        dialog?.dismiss()
    }


}

fun Context.dismissDialogIfVisible(dialog: Dialog?) {
    dialog?.apply {
        if (isShowing) dismiss()
    }
}