package com.example.myapplication

import android.os.Bundle
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.DialogFragment
import com.example.myapplication.databinding.ContentPageNavigationDialogBinding

class GoToPageDialog : DialogFragment() {
//    var pageNavigationListener: GoToPageDialogListener? = null
    private var pageNavigationListener: ((pageNumber: Int) -> Unit)? = null
    private var _binding: ContentPageNavigationDialogBinding? = null
    private val binding get() = _binding!!
    private var totalPagesCount: Int? = null
    var pageNumber = 0

    companion object {
        fun newInstance(totalPageCount: Int ,listener: (pageNumber: Int) -> Unit) : GoToPageDialog {
            val args = Bundle()
            args.putInt(Constants.TOTAL_PAGE_COUNT, totalPageCount)
            val fragment = GoToPageDialog()
            fragment.arguments = args
            fragment.pageNavigationListener = listener
            return fragment
        }

    }


    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        (activity)?.let {
            it.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            dialog?.window?.setLayout((width * .95).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.setCancelable(true)
            dialog?.setCanceledOnTouchOutside(true)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(STYLE_NORMAL, R.style.StyleBottomSheetDialogPage)
        arguments?.let {
            totalPagesCount = it.getInt(Constants.TOTAL_PAGE_COUNT)
            Log.d("PagesCount", "$totalPagesCount")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = ContentPageNavigationDialogBinding.inflate(inflater, container, false)

        }
        totalPagesCount?.let { count ->
            binding.pageNumberEditText.hint = "Enter page number (1-${count})"
            Log.i("HINTCOLR", "onCreateView:$count ")

        }
        val editText = binding.pageNumberEditText
        // input type of the edit text to accept numbers only.
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.setListenersToButtons()
        editText.requestFocus()
        //Open the numeric keyboard when the dialog is open.
        openNumericKeyboard()
        return binding.root
    }

    private fun openNumericKeyboard() {
        binding.pageNumberEditText.setOnEditorActionListener(object : OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    jumpToPage()
                    return true
                }
                return false
            }

        })


    }

    override fun onPause() {
        super.onPause()
        try {
            activity?.dismissDialogIfVisible(dialog = dialog)
        } catch (ex: Exception) {
            Log.i("TAG", "onPause: ")
        }
    }

    private fun ContentPageNavigationDialogBinding.setListenersToButtons() {
        cancelButton.setOnSingleClickListener ({
            disMissDialogSafely()
        })
        okButton.setOnSingleClickListener ({
            jumpToPage()
        })

    }
    private fun disMissDialogSafely() {
        try {
            // dismiss with extension function
            activity?.dismissDialogIfVisible(dialog = dialog)
        } catch (ex: Exception) {
            Log.i("IDDIALOGPDF", "onPause: ")
        }
    }
    private fun jumpToPage(){
        totalPagesCount?.let { totalPages ->
            val regex = Regex("^[1-$totalPages]$")
            // Get the input from the edit text.
            val input = binding.pageNumberEditText.text.toString()
            // Check if the input is valid.
            if (!regex.matches(input)) {
                // Show an error message.
                binding.pageNumberEditText.error =
                    "Page number must be between 1 and $totalPages."
            } else {
                // The input is valid.
                pageNumber = input.toInt()
            }
            // Dismiss the dialog if the input is valid.
            if(binding.pageNumberEditText.text?.isNotEmpty()== true){
                pageNumber = binding.pageNumberEditText.text.toString().toInt()
                if (pageNumber in 1..totalPages) {
                    pageNavigationListener?.invoke( pageNumber)
                    dismiss()
                }
            }

        }

    }


}