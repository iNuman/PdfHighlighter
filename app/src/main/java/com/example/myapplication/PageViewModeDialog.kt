package  com.example.myapplication

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatTextView
import com.example.myapplication.Constants.CONTINUE_MODE
import com.example.myapplication.Constants.DARK
import com.example.myapplication.Constants.EYES
import com.example.myapplication.Constants.HORIZONTAL
import com.example.myapplication.Constants.LIGHT
import com.example.myapplication.Constants.PAGE_MODE
import com.example.myapplication.Constants.VERTICAL
import com.example.myapplication.databinding.ContentPageModeDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PageViewModeDialog : BottomSheetDialogFragment() {

    private var pageViewModeListener: ((orientation: String) -> Unit)? = null
    private var _binding: ContentPageModeDialogBinding? = null
    private val binding get() = _binding!!
    private var selectedMode: String? = null
    private var selectedTheme: String? = null
    private var selectedPageMode: String? = null


    companion object {
        const val TAG = "PageViewModeDialog"
        private const val KEY_MODE = "KEY_MODE"
        private const val KEY_THEME = "KEY_THEME"
        private const val KEY_PAGE = "KEY_PAGE_SCROLL"

        fun newInstance(
            currentMode: String,
            currentTheme: String,
            currentPageMode: String,
            listener: (orientation: String) -> Unit
        ): PageViewModeDialog {
            val args = Bundle()
            args.putString(KEY_MODE, currentMode)
            args.putString(KEY_THEME, currentTheme)
            args.putString(KEY_PAGE, currentPageMode)
            val fragment = PageViewModeDialog()
            fragment.arguments = args
            fragment.pageViewModeListener = listener
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.StyleBottomSheetDialog)
        selectedMode = arguments?.getString(KEY_MODE)
        selectedTheme = arguments?.getString(KEY_THEME)
        selectedPageMode = arguments?.getString(KEY_PAGE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = ContentPageModeDialogBinding.inflate(inflater, container, false)

        }
        binding.setListenersToButtons()

        selectedMode?.let { mSelectedMode ->
            selectedTheme?.let { mSelectedTheme ->
                viewSelectionUnSelection(mSelectedMode, mSelectedTheme)
            }


        }

        selectedPageMode?.let {
            if(it == PAGE_MODE){
                Log.i("SWITHBTN", "setListenersToButtons: PAGE_MODE ---$it ")
                binding.switchController.isChecked = false
            }else if(it == CONTINUE_MODE){
                Log.i("SWITHBTN", "setListenersToButtons: CONTINUE_MODE ---$it ")
                binding.switchController.isChecked = true
            }
        }

        return binding.root
    }

    private fun viewSelectionUnSelection(mSelectedMode: String, mSelectedTheme: String) {
        if (mSelectedMode == HORIZONTAL && mSelectedTheme == DARK) {
            horizontalDarkSelection()
        } else if (mSelectedMode == VERTICAL && mSelectedTheme == LIGHT) {
            verticalLightSelection()
        } else if (mSelectedMode == HORIZONTAL && mSelectedTheme == LIGHT) {
            horizontalLightSelection()
        } else if (mSelectedMode == VERTICAL && mSelectedTheme == DARK) {
            verticalDarkSelection()
        } else if(mSelectedMode == HORIZONTAL && mSelectedTheme == EYES) {
            horizontalEyesMSelection()
        }else if(mSelectedMode == VERTICAL && mSelectedTheme == EYES){
            verticalEyesMSelection()
        }
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
    private fun disMissDialogSafely() {
        try {
            // dismiss with extension function
            activity?.dismissDialogIfVisible(dialog = dialog)
        } catch (ex: Exception) {
            Log.i("IDDIALOGPDF", "onPause: ")
        }
    }

    private fun ContentPageModeDialogBinding.setListenersToButtons() {
        horzontalModeBtn.setOnSingleClickListener ({
//            viewSelectionUnSelection(HORIZONTAL, currentTheme)

            pageViewModeListener?.invoke(Constants.HORIZONTAL_MODE)
            disMissDialogSafely()

        })
        verticalModeBtn.setOnSingleClickListener ({
//            viewSelectionUnSelection(VERTICAL, currentTheme)

            pageViewModeListener?.invoke(Constants.VERTICAL_MODE)
            disMissDialogSafely()
            //dismiss()
        })
        lightModeBtn.setOnSingleClickListener ({
//            viewSelectionUnSelection(currentMode, LIGHT)
            pageViewModeListener?.invoke(Constants.LIGHT_MODE)
            disMissDialogSafely()
            //dismiss()
        })
        eyesModeBtn.setOnSingleClickListener ({
//            viewSelectionUnSelection(currentMode, EYES)
            pageViewModeListener?.invoke(Constants.EYES_MODE)
            disMissDialogSafely()
        })
        darkModeBtn.setOnSingleClickListener ({

//            viewSelectionUnSelection(currentMode, DARK)

            pageViewModeListener?.invoke(Constants.NIGHT_MODE)
            disMissDialogSafely()
            //dismiss()
        })
        continueScrollBtn.setOnSingleClickListener ({

        })
        switchController.setOnCheckedChangeListener ({buttonView: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                pageViewModeListener?.invoke(CONTINUE_MODE)
                disMissDialogSafely()
                Log.i("SWITHBTN", "setListenersToButtons:---isChecked ")
            } else {
                pageViewModeListener?.invoke(PAGE_MODE)
                disMissDialogSafely()
                Log.i("SWITHBTN", "setListenersToButtons:---no checked ")
                // Do another thing
            }
        })

    }

    private fun horizontalDarkSelection() {
//        setSelectionUnSelection(
//            R.drawable.slider_horizontal_ic,
//            binding.horzontalModeBtn,
//            R.drawable.circle_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.slider_vertical,
//            binding.verticalModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.theme_ic,
//            binding.lightModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.theme_ic,
//            binding.eyesModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.darkModeBtn,
//            R.drawable.circle_selector
//        )

    }

    private fun horizontalEyesMSelection() {
//        setSelectionUnSelection(
//            R.drawable.slider_horizontal_ic,
//            binding.horzontalModeBtn,
//            R.drawable.circle_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.slider_vertical,
//            binding.verticalModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.theme_ic,
//            binding.lightModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.darkModeBtn,
//            R.drawable.circle_un_selector
//        )
//
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.eyesModeBtn,
//            R.drawable.circle_selector
//        )

    }

    private fun horizontalLightSelection() {
//        setSelectionUnSelection(
//            R.drawable.slider_horizontal_ic,
//            binding.horzontalModeBtn,
//            R.drawable.circle_selector,
//        )
//        setSelectionUnSelection(
//            R.drawable.slider_vertical,
//            binding.verticalModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.theme_ic,
//            binding.lightModeBtn,
//            R.drawable.circle_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.darkModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.eyesModeBtn,
//            R.drawable.circle_un_selector
//        )

    }

    private fun verticalLightSelection() {
//        setSelectionUnSelection(
//            R.drawable.slider_horizontal_ic,
//            binding.horzontalModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.slider_vertical,
//            binding.verticalModeBtn,
//            R.drawable.circle_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.theme_ic,
//            binding.lightModeBtn,
//            R.drawable.circle_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.darkModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.eyesModeBtn,
//            R.drawable.circle_un_selector
//        )

    }

    private fun verticalDarkSelection() {
//        setSelectionUnSelection(
//            R.drawable.slider_horizontal_ic,
//            binding.horzontalModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.slider_vertical,
//            binding.verticalModeBtn,
//            R.drawable.circle_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.theme_ic,
//            binding.lightModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.darkModeBtn,
//            R.drawable.circle_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.eyesModeBtn,
//            R.drawable.circle_un_selector
//        )

    }

    private fun verticalEyesMSelection() {
//        setSelectionUnSelection(
//            R.drawable.slider_horizontal_ic,
//            binding.horzontalModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.slider_vertical,
//            binding.verticalModeBtn,
//            R.drawable.circle_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.theme_ic,
//            binding.lightModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.darkModeBtn,
//            R.drawable.circle_un_selector
//        )
//        setSelectionUnSelection(
//            R.drawable.night_ic,
//            binding.eyesModeBtn,
//            R.drawable.circle_selector
//        )

    }


    private fun setSelectionUnSelection(
        drawableStart: Int,
        viewText: AppCompatTextView,
        drawableEnd: Int
    ) {
        try {
            viewText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawableStart,  // left drawable resource ID
                0,  // top drawable resource ID
                drawableEnd,  // right (end) drawable resource ID
                0   // bottom drawable resource ID
            )

        } catch (e: Resources.NotFoundException) {
            // Handle the case where the drawable resource is not found
        } catch (e: Exception) {
            // Handle other exceptions if needed
        }
    }


}