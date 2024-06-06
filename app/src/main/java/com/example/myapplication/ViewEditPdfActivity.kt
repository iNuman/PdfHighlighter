package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.artifex.mupdfdemo.*
import com.artifex.mupdfdemo.Annotation
import com.artifex.mupdfdemo.Ext.Companion.onClick
import com.example.myapplication.Constants.CONTINUE_MODE
import com.example.myapplication.Constants.DARK
import com.example.myapplication.Constants.DATA_CLASS_BUNDLE
import com.example.myapplication.Constants.DIRECT_DOC_EDIT_OPEN
import com.example.myapplication.Constants.DOC_BOOKMARK
import com.example.myapplication.Constants.DOC_ID
import com.example.myapplication.Constants.DOC_NAME
import com.example.myapplication.Constants.DOC_TABLE_CONTENT
import com.example.myapplication.Constants.EYES
import com.example.myapplication.Constants.EYES_MODE
import com.example.myapplication.Constants.FROM_ADD_BOOKMARK
import com.example.myapplication.Constants.FROM_ITEM_BTN
import com.example.myapplication.Constants.HORIZONTAL
import com.example.myapplication.Constants.LIGHT
import com.example.myapplication.Constants.LIGHT_MODE
import com.example.myapplication.Constants.NIGHT_MODE
import com.example.myapplication.Constants.PAGE_MODE
import com.example.myapplication.Constants.PDF_FILE_PATH
import com.example.myapplication.Constants.VERTICAL
import com.example.myapplication.databinding.ActivityViewPdfBinding
import com.google.android.material.slider.Slider
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.CharSequence
import kotlin.Exception
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.also
import kotlin.apply
import kotlin.arrayOf
import kotlin.getValue
import kotlin.lazy
import kotlin.let
import kotlin.run
import kotlin.takeIf
import kotlin.toString


//    suspend fun getPdfDocumentsForAndroid11AndAbove(
//        context: Context, sharedPref: SharedPreferences, sorting: String
//    ) = flow {
//        try {
//
//            if (checkExternalStoragePermission(context)) {
//                Log.i("TOATALANDORID10", "FUNCTION START")
//                val contentResolver = context.contentResolver
//                val collection = if (SDK_INT >= Build.VERSION_CODES.Q) {
//                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
//                } else {
//                    MediaStore.Files.getContentUri("external")
//                }
//                val projection = arrayOf(
//                    MediaStore.Files.FileColumns._ID,
//                    MediaStore.Files.FileColumns.DATA,
//                    MediaStore.Files.FileColumns.DISPLAY_NAME,
//                    MediaStore.Files.FileColumns.MIME_TYPE,
//                    MediaStore.Files.FileColumns.SIZE,
//                    MediaStore.Files.FileColumns.DATE_MODIFIED
//                )
//                //val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
//                // val selectionArgs = arrayOf("application/pdf")
//                val mimeTypePdf = "application/pdf"
//                val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + mimeTypePdf + "')"
//                // val selectionArgs = arrayOf("application/epub+zip")
//                // val selectionArgs = arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//                val sortOrder = getSortOrder(sharedPref.getString("sortingOp", sorting)
//                )
//                val pdfDocuments = mutableListOf<PDFEntity>()
//                //  Log.i("TOATALANDORID10", "Above Cursor")
//                contentResolver.query(
//                    collection, projection, whereClause, null, sortOrder
//                )?.use { cursor ->
//                    //  Log.i("TOATALANDORID10", "Inside Cursor")
//                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
//                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
//                    val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
//                    val pathCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
//                    while (cursor.moveToNext()) {
//                        //  Log.i("TOATALANDORID10", "MOVE NEXT Cursor")
//                        val fileId = cursor.getLong(idColumn)
//                        val name = cursor.getString(nameColumn) // not fetch name in lower devices
//                        val size = cursor.getLong(sizeColumn)
//                        val dateModified = cursor.getLong(dateColumn)
//                        val contentUri = ContentUris.withAppendedId(collection, fileId)
//                        // Use a method to get the file path from the contentUri
//                        val filePath = cursor.getString(pathCol)
//                        // val filePath = getFilePathFromContentUri(context, contentUri)
//                        val fileExist = filePath?.let {
//                            File(it)
//                        }
//                        try {
//                            fileExist?.let {
//                                if (filePath != null && it.exists()) {
//                                    val entity = PDFEntity(
//                                        docId = fileId,
//                                        docTitle = it.name,
//                                        docPath = filePath,
//                                        docUri = contentUri.toString(),
//                                        docSize = size,
//                                        docDateModified = dateModified,
//                                    )
//                                    val updatedEntity = t//isFavouriteInDb(entity)
//                                    pdfDocuments.add(updatedEntity)
//                                    // Log.i("OBONEBYONE", "getPdfDocumentsForAndroid11AndAbove: ${updatedEntity.docTitle}")
//                                }
//                            }
//
//                        } catch (ex: Exception) {
//                            Log.i("error", "MODELCLASS${ex.message.toString()}")
//                        }
//                    }
//                }
//                emit(pdfDocuments)
//
//            } else {
//                //  null
//                Log.i("error", "READ_EXTERNAL_STORAGE permission denied")
//            }
//        } catch (ex: Exception) {
//            Log.i("exception", "getPdfDocumentsForAndroid11AndAbove:$ex ")
//        }
//        //  null
//    }.onCompletion {
//        Log.i("error", "Successfully completed")
//    }.flowOn(Dispatchers.IO)





class ViewEditPdfActivity : AppCompatActivity(), OnPageChangeListener {
    private val binding by lazy {
        ActivityViewPdfBinding.inflate(layoutInflater)
    }
    private var muPDFReaderViewN: MuPDFReaderView? = null
    private var path: String? = null
    private var currentDocId: Long? = null
    private var muPDFCore: MuPDFCore? = null
    private var receivedData: PDFEntity? = null
    private var fileName: String? = null
    private var mTopBarMode: TopBarMode = TopBarMode.Main
    private var mAcceptMode: AcceptMode? = null
    private var mSearchTask: SearchTask? = null // search thread
    private var mAlertBuilder: AlertDialog.Builder? = null // pop up box
    private val colorItemList = mutableListOf<ColorItem>()
    var mMode: MuPDFReaderView.Mode? = null
    var documentName: String? = null
    var isDirectEditOpen = false

    var seekBar: VerticalSeekBar? = null
    var thumbView: View? = null
    var thumbViewHorizontal: View? = null
    var pdfUrl: String? = null
    var resultt: SearchTaskResult?= null


    companion object {
        var currentMode: String = VERTICAL
        var currentTheme: String = LIGHT
        var currentPageMode: String = PAGE_MODE
        var currentBgColor = Color.WHITE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateWholeCode()

    }

    private fun onCreateWholeCode() {
        setContentView(binding.root)
        mAlertBuilder = AlertDialog.Builder(this)
        SharedPreferencesUtil.init(application)
        binding.initViews()
        setBottomRecyclerData()
        getIntentDataOpenPdf()
        binding.setTextButtonClicks()
        initPdfCore()
        checkingOutlineAndSetData()
        settingClicksToSearch()
        initColorAdapter()
        setBrushSize()
        thumbView = LayoutInflater.from(this).inflate(R.layout.layout_seekbar_thumb, null, false)
        thumbViewHorizontal =
            LayoutInflater.from(this).inflate(R.layout.layout_h_seekbar_thumb, null, false)
//        sideSeekbarScroller()
//        horizontalModeSeekScroller()
        if (isDirectEditOpen) {
            bottomRecyclerCallBack("Edit")
        }
        binding.activityBackBtn.setOnSingleClickListener({
            onBackPressedCallback.handleOnBackPressed()
        }, false)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun updatePageNumView(index: Int) {
        if (muPDFCore == null) return
        Log.i("TAG", "updatePageNumView: ${muPDFCore?.countPages()}")
        Log.i("TAG", "updatePageNumView: ${index + 1}")
        muPDFCore?.let {
            //if (index + 1 <= it.countPages() && index > 0)
            // binding.currentPageTv.text = (index + 1).toString()
            binding.currentPageTv.text = (index + 1).toString()
            val totalPages = (it.countPages())
            binding.totalPageTv.text = "of " + totalPages.toString()
        }


    }

    private fun getThumb(progress: Int): Drawable? {
        thumbView?.let { thumb ->
            val progressTextView = thumb.findViewById<TextView>(R.id.tvProgress)
            progressTextView.text = progress.toString() + ""
            progressTextView.rotation = 270f // Set rotation to 0 degrees (horizontal)
            thumb.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val bitmap = Bitmap.createBitmap(
                thumb.measuredWidth,
                thumb.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            thumb.layout(0, 0, thumb.measuredWidth, thumb.measuredHeight)
            thumb.draw(canvas)
            return BitmapDrawable(resources, bitmap)
        }
        return null

    }

    private fun getThumbHorizontal(progress: Int): Drawable? {
        thumbViewHorizontal?.let { thumb ->
            val progressTextView = thumb.findViewById<TextView>(R.id.tvProgress)
            progressTextView.text = progress.toString() + ""
            progressTextView.rotation = 270f // Set rotation to 0 degrees (horizontal)
            thumb.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val bitmap = Bitmap.createBitmap(
                thumb.measuredWidth,
                thumb.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            thumb.layout(0, 0, thumb.measuredWidth, thumb.measuredHeight)
            thumb.draw(canvas)
            return BitmapDrawable(resources, bitmap)
        }
        return null

    }


    private fun deleteAnnotation() {
//        if (mTopBarMode == TopBarMode.Delete) {
        binding.deleteAnnotationBtn.show()
//        } else {
//            binding.deleteAnnotationBtn.hide()
//        }

    }


    private fun setListenerToReaderView() {


        muPDFReaderViewN?.setListener(object : MuPDFReaderViewListener {
            override fun onMoveToChild(i: Int) {
                if (muPDFCore == null) return

            }

            override fun onTapMainDocArea() {
                Log.d("DELETEANNOT", "onHit:  ")
            }

            override fun onDocMotion() {
                Log.e("DELETEANNOT", "onHit:  ")

            }

            override fun onHit(item: Hit) {
                when (mTopBarMode) {
                    TopBarMode.Annot -> {
                        if (item == Hit.Annotation) {
                            //   mTopBarMode = TopBarMode.Delete
                            //deleteAnnotation()
                        }
                    }

                    TopBarMode.Delete -> {
                        //  mTopBarMode = TopBarMode.Annot
                    }

                    else -> {

                    }
                }
            }

            override fun onLongPress() {
                Log.i("LONGPRESS", "onLongPress: hitting in Activity")
                bottomRecyclerCallBack("Edit")
                highlightText()
//                getPageViewMupdf()?.let { pageView ->
//                    var visible = false
//                    onClick = { x, y, currentY, action, bolean ->
//                        visible = bolean
//                        if (mAcceptMode == AcceptMode.Highlight) {
//                            Log.i("TETXTTOUCH", "showCopyRect: yes I'm in PageView with $x $y")
//                            if (action) {
//                                copyButtonOnScreen(x, y, currentY)
//                                hideShowSecondCopyBtn(visible)
//                            } else {
//                                hideShowSecondCopyBtn(false)
//                            }
//                        }
//                    }
//                    Log.i("TETXTTOUCH", "hideShowSecondCopyBtn: BEFORE")
//                }

            }

        })

    }

    private var mPageSliderRes = 0
    private fun initPdfCore() {
        muPDFCore?.let { muPDFCore ->
            if (muPDFCore.countPages() > 0) {
                sideSeekHandling()

            }
            // Set up the page slider
            val smax = Math.max(muPDFCore.countPages() - 1, 1)
            mPageSliderRes = (10 + smax - 1) / smax * 2
            Log.d("PROGRESSSEEK", "sliderInitial:$mPageSliderRes")
            // Update page number text and slider
            muPDFReaderViewN?.let { readerView ->
                if (currentMode == HORIZONTAL) {
                    readerView.setHorizontalScrolling(true)
                } else {
                    readerView.setHorizontalScrolling(false)
                }
                val index: Int = readerView.displayedViewIndex
                updatePageNumView(index * mPageSliderRes)
                binding.mySeekBar.max = (muPDFCore.countPages() - 1) * mPageSliderRes
                binding.mySeekBar.progress = index * mPageSliderRes

                binding.horizontalSeekSlider.max = (muPDFCore.countPages() - 1) * mPageSliderRes
                binding.horizontalSeekSlider.progress = index * mPageSliderRes
                Log.d(
                    "PROGRESSSEEK",
                    "sliderInitialMax:${(muPDFCore.countPages() - 1) * mPageSliderRes} "
                )
                Log.d("PROGRESSSEEK", "sliderInitialProgress:${index * mPageSliderRes}")

            }

            setListenerToReaderView()
            //last commented code will be here
            muPDFReaderViewN?.adapter = MuPDFPageAdapter(this, muPDFCore, this)
            //Set the background color of the view
            muPDFReaderViewN?.setBackgroundColor(currentBgColor)
//                ContextCompat.getColor(this, R.color.muPDFReaderView_bg)
//            )
        }?.run {
            return
        }
        //for deselection
        //  onDeleteButtonClick()
    }

    private fun bottomRecyclerCallBack(titleText: String) {
        when (titleText) {
            "Edit" -> {
                binding.disableApplyButton()
                binding.tittleBar.hideIfVisible()
                binding.border.hideIfVisible()
                hideUndoRedoButtons()
                binding.toolsLayout.editingButtonsLy.showIfNotVisible()
                binding.mySeekBar.hideIfVisible()
                binding.horizontalSeekSlider.hideIfVisible()
                binding.layoutBackView.showIfNotVisible()
            }

            "Page" -> {
                gotoPage()
            }

            "Reading Mode" -> {
                readingModes()
            }

            "Comment" -> {
                //perform annotation deselection
            }

            "Read Aloud" -> {
            }

        }
    }

    private fun drawTextOnPage() {
        binding.drawLayout.showIfNotVisible()
        mTopBarMode = TopBarMode.Accept
        binding.toolsLayout.switcher.displayedChild = mTopBarMode.ordinal
        mAcceptMode = AcceptMode.Ink
        muPDFReaderViewN?.setMode(MuPDFReaderView.Mode.Drawing)
        documentName?.let {
            binding.toolsLayout.annotType.text = it
        }
        showInfo(getString(com.artifex.mupdfdemo.R.string.pdf_tools_draw_annotation))

    }

    private fun initColorAdapter() {
        colorListInflating()
        val colorAdapter = ColorAdapter(colorItemList) { colorCode ->
            setInkColor(Color.parseColor(colorCode))
        }
        binding.colorRecycler.setHasFixedSize(true)
        binding.colorRecycler.adapter = colorAdapter

    }

    private fun setInkColor(color: Int) {
        muPDFReaderViewN?.setInkColor(color)

    }

    private fun readingModes() {
        try {
            PageViewModeDialog.newInstance(
                currentMode, currentTheme, currentPageMode
            ) { viewMode ->
                applyViewMode(viewMode)
            }.show(supportFragmentManager, PageViewModeDialog.TAG)
        } catch (_: Exception) {

        }
    }

    override fun onPause() {
        super.onPause()
        if (mSearchTask != null) {
            mSearchTask?.stop()
        }
    }

    private fun applyViewMode(viewMode: String) {
        muPDFReaderViewN?.let { pdfReaderView ->
            when (viewMode) {
                Constants.HORIZONTAL_MODE -> {
                    pdfReaderView.setHorizontalScrolling(true)
                    animateChangeMode(pdfReaderView, false)
                    currentMode = HORIZONTAL
                    sideSeekHandling()
                    //binding.mySeekBar.hide()
                    //binding.horizontalSeekSlider.show()
                }

                Constants.VERTICAL_MODE -> {
                    pdfReaderView.setHorizontalScrolling(false)
                    animateChangeMode(pdfReaderView, true)
                    currentMode = VERTICAL
                    sideSeekHandling()
                   // binding.mySeekBar.show()
                   // binding.horizontalSeekSlider.hide()

                }

                NIGHT_MODE -> {
                    // set view backgroundColor
                    pdfReaderView.let {
                        sideSeekHandling()
                        currentBgColor = Color.BLACK
                        // currentBgColor = Color.LTGRAY
                        it.setBackgroundColor(currentBgColor)
                        //  it.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                        getPageViewMupdf()?.let { pageView ->
//                            pageView.setPageMode("DARK")
                            //pageView.setPageMode(true)
                            update3PagesMode()
                            currentTheme = DARK
                        }


                    }


                }

                LIGHT_MODE -> {
                    pdfReaderView.let { muPdf ->
                        sideSeekHandling()
                        currentBgColor = Color.WHITE
                        // currentBgColor = Color.MAGENTA
                        muPdf.setBackgroundColor(currentBgColor)
                        getPageViewMupdf()?.let { pageView ->
                            //pageView.setPageMode(false)
//                            pageView.setPageMode("LIGHT")
                            update3PagesMode()
                            currentTheme = LIGHT
                        }
                    }
                }

                EYES_MODE -> {
                    pdfReaderView.let { muPdf ->
                        sideSeekHandling()
                        currentBgColor = ContextCompat.getColor(
                            this@ViewEditPdfActivity, com.artifex.mupdfdemo.R.color.colorAccentTrans
                        )
                        // Color.GRAY//
                        muPdf.setBackgroundColor(currentBgColor)
                        getPageViewMupdf()?.let { pageView ->
                            //pageView.setPageMode(false)
//                            pageView.setPageMode("EYES")
                            update3PagesMode()
                            currentTheme = EYES
                        }
                    }
                }

                CONTINUE_MODE -> {
                    getPageViewMupdf()?.let { pageView ->
                        sideSeekHandling()
//                        pageView.setContinuousPageScroll(true)
                        currentPageMode = CONTINUE_MODE
                    }
                }

                PAGE_MODE -> {
                    getPageViewMupdf()?.let { pageView ->
                        sideSeekHandling()
//                        pageView.setContinuousPageScroll(false)
                        currentPageMode = PAGE_MODE
                    }
                }

                else -> {
                    // Handle other cases or do nothing
                }
            }
        }

    }

    private fun getPageViewMupdf(): MuPDFView? {
        return try {
            muPDFReaderViewN?.let {
                val pageView = it.displayedView
                if (pageView != null && pageView is MuPDFView) {
                    return pageView
                }
            }
            null
        } catch (e: Exception) {
            // Handle any other exceptions that may occur
            Log.i("error", "getPageViewMupdf:${e.message.toString()} ")
            null
        }

    }

    private fun update3PagesMode(): MuPDFView? {
        return try {
            muPDFReaderViewN?.let {
                val currentPageNumber = it.displayedViewIndex
                val prevoiusIndex = currentPageNumber - 1
                val futureIndex = currentPageNumber + 1
                /* update previous if exist */
                if (prevoiusIndex > 0) {
                    val previousView = it.getView(prevoiusIndex)
                    if (previousView is MuPDFView) previousView?.update()
                } else {
                    val additionIndex = currentPageNumber + 2
                    muPDFCore?.countPages()?.let { totalPages ->
                        if (additionIndex > 0 && additionIndex < totalPages) {
                            val additionView = it.getView(additionIndex)
                            if (additionView is MuPDFView) additionView?.update()
                        }
                    }

                }

                muPDFCore?.countPages()?.let { totalPages ->
                    if (futureIndex > 0 && futureIndex < totalPages) {
                        val futureView = it.getView(futureIndex)
                        if (futureView is MuPDFView) futureView?.update()
                    }
                }

            }
            null
        } catch (e: Exception) {
            // Handle any other exceptions that may occur
            Log.i("error", "getPageViewMupdf:${e.message.toString()} ")
            null
        }

    }

    private fun animateChangeMode(view: View, horizontalMode: Boolean, duration: Long = 500L) {
        try {
            val animatorSet = AnimatorSet()
            val translationXEnd = if (horizontalMode) 0f else view.width.toFloat()
            val translationYEnd = if (horizontalMode) view.height.toFloat() else 0f
            val translationXAnimator =
                ObjectAnimator.ofFloat(view, "translationX", view.translationX, translationXEnd)
            val translationYAnimator =
                ObjectAnimator.ofFloat(view, "translationY", view.translationY, translationYEnd)
            animatorSet.play(translationXAnimator)
            animatorSet.play(translationYAnimator)
            animatorSet.duration = duration
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    view.translationX = 0f
                    view.translationY = 0f
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            })
            animatorSet.start()
        } catch (_: Exception) {

        }
    }

    private fun ActivityViewPdfBinding.setTextButtonClicks() {
        deleteAnnotationBtn.setOnSingleClickListener({
            getPageViewMupdf()?.let { pageView ->
                pageView.deleteSelectedAnnotation()
            }
        })
        optionMenuBtn.setOnSingleClickListener({
            openFilesOptionDialog(documentName)
        })

        toolsLayout.closeButton.setOnSingleClickListener({
            hideShowSecondCopyBtn(false)
            onTextCancelButtonClick()
        })
        toolsLayout.copyButton.setOnSingleClickListener({
            hideUndoRedoButtons()
            binding.enableApplyButton()
            selectAndCopyText()
            getPageViewMupdf()?.let { pageView ->
                var visible = false
                onClick = { x, y, rectF ->
//                    visible = bolean
                    if (mAcceptMode == AcceptMode.CopyText) {
                        Log.i("TETXTTOUCH", "showCopyRect: yes I'm in PageView with $x $y")
//                        if (action) {
                            copyButtonOnScreen(x, y, y)
                            hideShowSecondCopyBtn(visible)
                        } else {
                            hideShowSecondCopyBtn(false)
                        }
//                    }
                }
                Log.i("TETXTTOUCH", "hideShowSecondCopyBtn: BEFORE")
            }
        })
        toolsLayout.underlineBtn.setOnSingleClickListener({
            hideShowSecondCopyBtn(false)
            hideUndoRedoButtons()
            binding.enableApplyButton()
            underlineText()
        })
        toolsLayout.highlightButton.setOnSingleClickListener({
            hideShowSecondCopyBtn(false)
            hideUndoRedoButtons()
            binding.enableApplyButton()
            highlightText()
        })
        toolsLayout.strikeBtn.setOnSingleClickListener({
            hideShowSecondCopyBtn(false)
            hideUndoRedoButtons()
            binding.enableApplyButton()
            strikethroughText()
        })
        toolsLayout.signatureBtn.setOnSingleClickListener({
            hideShowSecondCopyBtn(false)
            showUndoRedoButtons()
            binding.enableApplyButton()
            drawTextOnPage()
        })
        toolsLayout.acceptButton.setOnSingleClickListener({
            onAcceptTextButtonClick()
        })
        copyBtnSecond.setOnSingleClickListener({
            onAcceptTextButtonClick()
        })

        toolsLayout.cancelAcceptButton.setOnSingleClickListener({
            onTextCancelButtonClick()
        })



        toolsLayout.undoDrawButton.setOnSingleClickListener({
            // val pageView = muPDFReaderViewN?.displayedView as MuPDFView
            getPageViewMupdf()?.let { pageView ->
//                pageView.undoDraw()
            }
        })

        toolsLayout.redoDrawButton.setOnSingleClickListener({
            // val pageView = muPDFReaderViewN?.displayedView as MuPDFView
            getPageViewMupdf()?.let { pageView ->
//                pageView.redoDraw()

            }
        })
        binding.textSearchBtn.setOnSingleClickListener({
            binding.tittleBar.hideIfVisible()
            binding.border.hideIfVisible()
            binding.bottomOptionRecycler.hideIfVisible()
            binding.searchActionLy.searchLy.showIfNotVisible()
            searchModeOn()
            searchTaskClicks()

        })


    }

    var word = ""
    private fun extractText() {
        muPDFReaderViewN?.let { readerView ->
            muPDFCore?.let {
                val textWord = it.textLines(readerView.displayedViewIndex)
                for (z in textWord.indices) {
                    for (j in textWord[z].indices) {
                        word += "${textWord[z][j].w} "

                    }
                }
                //  Log.i("CUURENTTEXT", "extractText:$word")
            }
        }


    }



    private fun copyButtonOnScreen(xPos: Float, yPos: Float, currentY: Float) {
        Log.i(
            "TETXTTOUCH",
            "copyButtonOnScreen--FINAL VALUES , xPOS---$xPos  , yPOS---$yPos, currentY----D $currentY"
        )
            if (currentY != -1f) {
                binding.copyBtnSecond.x = xPos
                binding.copyBtnSecond.y = currentY - binding.copyBtnSecond.height
                binding.copyBtnSecond.invalidate()
            } else {
                binding.copyBtnSecond.x = xPos
                binding.copyBtnSecond.y = yPos - binding.copyBtnSecond.height
                binding.copyBtnSecond.invalidate()
            }

        //  extractText() extract whole page text
    }

    private fun hideShowSecondCopyBtn(bol: Boolean) {
        if (bol) {
            Log.i("TETXTTOUCH", "hideShowSecondCopyBtn: IF")
            binding.copyBtnSecond.showIfNotVisible()
        } else {
            Log.i("TETXTTOUCH", "hideShowSecondCopyBtn: ELSE")
            binding.copyBtnSecond.hideIfVisible()
        }

    }


    private fun openFilesOptionDialog(documentNameChange: String?) {
        Log.i("documentName", "openFilesOptionDialog: --BEFORE try")
        try {
            documentNameChange?.let {
                Log.i("documentName", "openFilesOptionDialog: --LET BLOCK")
                val dialogFragment = OpenFileOptionsDialog.newInstance(it) { selectedOption ->
                    callBackFromOpenDialog(selectedOption)
                }
                dialogFragment.show(supportFragmentManager, "FILE_OPTION")
            }

        } catch (ex: Exception) {
            Log.i("error", "openFilesOptionDialog: ")
        }
    }

    private fun callBackFromOpenDialog(selectedOption: String) {
        when (selectedOption) {
            DOC_TABLE_CONTENT -> {
                opnShowBookMarkDialog()
            }

            DOC_BOOKMARK -> {
                addPageBookmark()
            }
        }

    }

    private fun checkingOutlineAndSetData() {
        muPDFCore?.let { mupdfBase ->
            if (mupdfBase.hasOutline()) {
                val outline = mupdfBase.outline
                outline?.let { outlineNew ->
                    OutlineActivityData.get().items = outlineNew
                }
            }


        }
    }

    private fun opnShowBookMarkDialog() {
//        try {
//            currentDocId?.let { documentId ->
//                val showBookmarkDialog =
//                    ShowBookMarkContentDialog.newInstance(documentId) { pageNumber, selectedOption ->
//                        navigateMarkedPage(pageNumber, selectedOption)
//                    }
//                showBookmarkDialog?.let {
//                    it.show(supportFragmentManager, ShowBookMarkContentDialog.TAG)
//                }
//            }
//        } catch (ex: Exception) {
//            Log.i("error", "openFilesOptionDialog: ")
//        }
    }

    private fun navigateMarkedPage(pageNumber: Long, selectedOption: String) {
        try {
            when (selectedOption) {
                FROM_ITEM_BTN -> {
                    muPDFReaderViewN?.displayedViewIndex = pageNumber.toInt()
                }

                DOC_TABLE_CONTENT -> {
                    muPDFReaderViewN?.displayedViewIndex = pageNumber.toInt()
                }

                FROM_ADD_BOOKMARK -> {
                    removeBookmark(pageNumber)
                }
            }

        } catch (_: Exception) {

        }

    }

    private fun removeBookmark(bookmarkId: Long) {
//        pdfViewModel.removeBookMark(bookmarkId)
    }


    private fun addPageBookmark() {
//        val currentPageNumber = muPDFReaderViewN?.displayedViewIndex?.toLong() ?: -1L
//        currentDocId?.let { pdfEntityID ->
//            if (currentPageNumber != -1L) {
//                val bookmarks = BookmarkEntity(
//                    pdfDocId = pdfEntityID, pageNumber = currentPageNumber, pageTitle = "Page"
//                )
//                try {
//
//                    pdfViewModel.addPageBookmark(bookmarks)
//                    receivedData?.let {
//                        addPdfToBookmark(it)
//                    }
//
//                } catch (ex: Exception) {
//                    Log.i(
//                        "BOOKDBError",
//                        "addPageBookmark: ---Activity ${ex.message.toString()} "
//                    )
//                }
//            }
//
//        }

    }

    private fun addPdfToBookmark(selectedPdfModel: PDFEntity) {
        lifecycleScope.launch(IO) {
            try {
//                pdfViewModel.addToBookmarkData(selectedPdfModel)
//                withContext(Main) {
//                    Toast.makeText(
//                        this@ViewEditPdfActivity,
//                        "Bookmark Added",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                }
            } catch (_: Exception) {
            }
        }


    }

    private fun searchTaskClicks() {
        // Create a search task
        mSearchTask = object : SearchTask(this, muPDFCore) {
            override fun onTextFound(result: SearchTaskResult) {
                resultt = result
                SearchTaskResult.set(result)
                Log.i("TAG", "TotalWordCount:${result.txt.count()} ")
                // Ask the ReaderView to move to the resulting page
                muPDFReaderViewN?.displayedViewIndex = result.pageNumber
                Log.e("TAG", "onTextFound: ${result.pageNumber}")
                // Make the ReaderView act on the change to SearchTaskResult
                // via overridden onChildSetup method.
                muPDFReaderViewN?.resetupChildren()
            }

            override fun onTextNotFound(result: String?) {
                try {
                    result?.let {
                        val searchDismissDialog =
                            SearchDismissDialog.newInstance(it) { selectedOption ->
                                cbSearchDialog(selectedOption)
                            }
                        searchDismissDialog.show(supportFragmentManager, "SEARCH_DISMISS")
                    }

                } catch (_: Exception) {

                }


            }
        }
        // Search invoking buttons are disabled while there is no text specified
        binding.searchActionLy.searchBack.isEnabled = false
        binding.searchActionLy.searchForward.isEnabled = false
        binding.searchActionLy.searchBack.setColorFilter(Color.argb(0xFF, 250, 250, 250))
        binding.searchActionLy.searchForward.setColorFilter(Color.argb(0xFF, 250, 250, 250))


    }

    private fun cbSearchDialog(selectedOption: String) {
    }


    private fun searchModeOn() {
        if (mTopBarMode != TopBarMode.Search) {
            mTopBarMode = TopBarMode.Search
            //Focus on EditTextWidget
            binding.searchActionLy.searchText.requestFocus()
            showKeyboard()
            binding.toolsLayout.switcher.displayedChild = mTopBarMode.ordinal
        }
    }


    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchActionLy.searchText, 0)
    }


    private fun settingClicksToSearch() {
        var haveText = false
        binding.searchActionLy.searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                haveText = s.toString().isNotEmpty()
                setButtonEnabled(binding.searchActionLy.searchBack, haveText)
                setButtonEnabled(binding.searchActionLy.searchForward, haveText)
                //Remove any previous search results
                if (SearchTaskResult.get() != null && binding.searchActionLy.searchText.text.toString() != SearchTaskResult.get().txt) {
                    SearchTaskResult.set(null)
                    muPDFReaderViewN?.resetupChildren()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })
        //React to Done button on keyboard
        binding.searchActionLy.searchText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE && haveText) search(1)

            false
        }
//        binding.searchActionLy.searchText.setOnEditorActionListener { textView, actionId, keyEvent ->
//            if(actionId == EditorInfo.IME_ACTION_NEXT && haveText) search(1)
//            false
//        }
        binding.searchActionLy.searchText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) search(
                1
            )
            false
        }
        // Activate search invoking buttons
        binding.searchActionLy.searchBack.setOnSingleClickListener({
            try {
                search(-1)
            } catch (ex: Exception) {
                // Log.e(TAG, "settingClickToSearch: $ex")
            }
        })
        binding.searchActionLy.searchForward.setOnSingleClickListener({
            try {
                search(1)
            } catch (ex: Exception) {

            }

        })
        binding.searchActionLy.cancelSearch.setOnSingleClickListener({
            hideShowView()
        })
        binding.searchActionLy.backSearchBtn.setOnSingleClickListener({
            hideShowView()
        })

    }

    private fun hideShowView() {

        binding.searchActionLy.searchLy.hideIfVisible()
        binding.tittleBar.showIfNotVisible()
        binding.border.showIfNotVisible()
        binding.bottomOptionRecycler.showIfNotVisible()
        binding.layoutBackView.hideIfVisible()
        searchModeOff()
    }


    private fun searchModeOff() {
        hideKeyboard()
        binding.searchActionLy.searchText.text = null
        binding.searchActionLy.searchText.hint = "Enter Text"
        SearchTaskResult.set(null)
        // Make the ReaderView act on the change to mSearchTaskResult
        // via overridden onChildSetup method.
        muPDFReaderViewN?.resetupChildren()
    }

    private fun search(direction: Int) {
        hideKeyboard()
        val displayPage = muPDFReaderViewN?.displayedViewIndex
        val r = SearchTaskResult.get()
        val searchPage = r?.pageNumber ?: -1
        displayPage?.let { page ->
            mSearchTask?.go(
                binding.searchActionLy.searchText.text.toString(), direction, page, searchPage
            )
            Log.i("TAG", "search: $page")

        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchActionLy.searchText.windowToken, 0)
    }

    private fun setButtonEnabled(button: ImageView, enabled: Boolean) {
        button.isEnabled = enabled
        button.setColorFilter(
            if (enabled) com.artifex.mupdfdemo.R.color.colorAccentTrans else com.artifex.mupdfdemo.R.color.colorDarkTrans
        )
    }

    private fun onTextCancelButtonClick() {
        // val pageView = muPDFReaderViewN?.displayedView as MuPDFView
        getPageViewMupdf()?.let { pageView ->
            if (pageView != null) {
                pageView.deselectText()
                pageView.cancelDraw()
            }
        }
        muPDFReaderViewN?.setMode(MuPDFReaderView.Mode.Viewing)
        hideEditingViews()
        binding.toolsLayout.switcher.displayedChild = mTopBarMode.ordinal
    }

    private fun onAcceptTextButtonClick() {
        //val pageView = muPDFReaderViewN?.displayedView as MuPDFView
        getPageViewMupdf()?.let { pageView ->
            var success = false
            when (mAcceptMode) {
                AcceptMode.CopyText -> {
                    success = pageView.copySelection()
                    hideEditingViews()
                    // mTopBarMode = TopBarMode.Accept
//                    showInfo(
//                        if (success) getString(com.artifex.mupdfdemo.R.string.copied_to_clipboard) else getString(
//                            com.artifex.mupdfdemo.R.string.no_text_selected
//                        )
//                    )
                }

                AcceptMode.Highlight -> {
                    success = pageView.markupSelection(Annotation.Type.HIGHLIGHT)
                    //    mTopBarMode = TopBarMode.Accept
                    hideEditingViews()
                    if (success) {

                        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        Log.i("TEXTHIGHLIGHT", "Highlight: not case ")
                        Toast.makeText(this@ViewEditPdfActivity, clipboard.getText(), Toast.LENGTH_SHORT).show()
//                        showInfo(getString(com.artifex.mupdfdemo.R.string.no_text_selected))
                    }
                }

                AcceptMode.Underline -> {
                    success = pageView.markupSelection(Annotation.Type.UNDERLINE)
                    hideEditingViews()
                    if (!success) showInfo(getString(com.artifex.mupdfdemo.R.string.no_text_selected))
                }

                AcceptMode.StrikeOut -> {
                    success = pageView.markupSelection(Annotation.Type.STRIKEOUT)
                    //   mTopBarMode = TopBarMode.Accept
                    hideEditingViews()
                    if (!success) showInfo(getString(com.artifex.mupdfdemo.R.string.no_text_selected))
                }

                AcceptMode.Ink -> {
                    success = pageView.saveDraw()
                    hideEditingViews()
                    // mTopBarMode = TopBarMode.Accept
                    if (!success) showInfo(getString(com.artifex.mupdfdemo.R.string.nothing_to_save))
                }

                else -> {

                }
            }
        }

        binding.toolsLayout.switcher.displayedChild = mTopBarMode.ordinal
        muPDFReaderViewN?.setMode(MuPDFReaderView.Mode.Viewing)
    }

    private fun hideEditingViews() {
        sideSeekHandling()
        binding.toolsLayout.editingButtonsLy.hideIfVisible()
        binding.drawLayout.hideIfVisible()
        binding.layoutBackView.hideIfVisible()
        binding.tittleBar.showIfNotVisible()
        binding.border.showIfNotVisible()
        hideShowSecondCopyBtn(false)
    }

    private fun strikethroughText() {
        mTopBarMode = TopBarMode.Accept
        binding.toolsLayout.switcher.displayedChild = mTopBarMode.ordinal
        mAcceptMode = AcceptMode.StrikeOut
        muPDFReaderViewN?.setMode(MuPDFReaderView.Mode.Selecting)
        documentName?.let {
            binding.toolsLayout.annotType.text = it
        }
        showInfo(getString(com.artifex.mupdfdemo.R.string.select_text))
    }

    private fun highlightText() {
        mTopBarMode = TopBarMode.Accept
        binding.toolsLayout.switcher.displayedChild = mTopBarMode.ordinal
        mAcceptMode = AcceptMode.Highlight
        muPDFReaderViewN?.setMode(MuPDFReaderView.Mode.Selecting)
        documentName?.let {
            binding.toolsLayout.annotType.text = it
        }
        showInfo(getString(com.artifex.mupdfdemo.R.string.select_text))


    }

    private fun underlineText() {
        mTopBarMode = TopBarMode.Accept
        binding.toolsLayout.switcher.displayedChild = mTopBarMode.ordinal
        mAcceptMode = AcceptMode.Underline
        muPDFReaderViewN?.setMode(MuPDFReaderView.Mode.Selecting)
        mMode = MuPDFReaderView.Mode.Selecting
        documentName?.let {
            binding.toolsLayout.annotType.text = it
        }
        showInfo(getString(com.artifex.mupdfdemo.R.string.select_text))
    }

    private fun selectAndCopyText() {
        mTopBarMode = TopBarMode.Accept
        binding.toolsLayout.switcher.displayedChild = mTopBarMode.ordinal
        mAcceptMode = AcceptMode.CopyText
        muPDFReaderViewN?.setMode(MuPDFReaderView.Mode.Selecting)
        documentName?.let {
            binding.toolsLayout.annotType.text = it
        }
        showInfo(getString(com.artifex.mupdfdemo.R.string.select_text))
    }

    private fun gotoPage() {
        try {
            /*comment for time being */
            //muPDFReaderViewN?.displayedViewIndex = 0 //mPageSliderRes
            // Create a new instance of the dialog fragment.
            muPDFCore?.let { pdfCore ->
                if (pdfCore.countPages() > 1) {
                    val dialogFragment =
                        GoToPageDialog.newInstance(pdfCore.countPages()) { resultPageNumber ->
                            openRequiredPage(resultPageNumber)
                        }

                    dialogFragment.show(supportFragmentManager, "go_to_page_dialog")
                } else {
                    Toast.makeText(
                        this@ViewEditPdfActivity, "All pages are displayed", Toast.LENGTH_LONG
                    ).show()
                }

            }
        } catch (_: Exception) {
            Log.i("error", "gotoPage: Exception raised")
        }


    }


    private fun openRequiredPage(resultPageNumber: Int) {
        muPDFReaderViewN?.displayedViewIndex = resultPageNumber - 1
        setSeekProgressWhenChange(resultPageNumber - 1)
        binding.textPageNum.showIfNotVisible()
        muPDFCore?.let { pdfCore ->
            binding.textPageNum.text =
                "Current page $resultPageNumber / ${pdfCore.countPages()}"
        }
        //toast(this@ViewEditPdfActivity, "current page $resultPageNumber")
        lifecycleScope.launch {
            delay(1500)
            binding.textPageNum.visibility = View.GONE
        }


    }


    private fun showInfo(text: String) {
        try {
//            val inflater = layoutInflater
//            val binding = CustomToastBinding.inflate(inflater)
//            binding.toastMessageCustom.text = text
//            val toast = Toast(applicationContext)
//            //generating error for gravity
//            // toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
//            toast.duration = Toast.LENGTH_SHORT
//            toast.view = binding.root
//            toast.show()
        } catch (ex: Exception) {
            Log.e("error", "showInfoException:${ex.message.toString()} ")
        }


    }


    private fun ActivityViewPdfBinding.initViews() {
        muPDFReaderViewN = pdfReaderRenderViewN
    }

    private fun sideSeekHandling() {
        if(muPDFCore?.countPages() != 1){
            if (currentMode == HORIZONTAL) {
                binding.mySeekBar.hide()
                binding.horizontalSeekSlider.show()
            } else if (currentMode == VERTICAL) {
                binding.mySeekBar.show()
                binding.horizontalSeekSlider.hide()
            }
        }else{
            binding.mySeekBar.hide()
            binding.horizontalSeekSlider.hide()
        }



    }

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }


    private fun getIntentDataOpenPdf() {
        pdfUrl = intent.data?.toString()
        if (pdfUrl == null) {
            path = intent.getStringExtra(PDF_FILE_PATH)
            currentDocId = intent.getLongExtra(DOC_ID, -1)
            isDirectEditOpen = intent.getBooleanExtra(DIRECT_DOC_EDIT_OPEN, false)
            val receivedBundle: Bundle? = intent.extras
            receivedData = receivedBundle?.getParcelable(DATA_CLASS_BUNDLE)
            documentName = intent.getStringExtra(DOC_NAME)
            documentName?.let {
                binding.titleTv.text = it
                binding.toolsLayout.annotType.text = it

            }

            openPdfFile(path)
        }
        if (pdfUrl != null) {
            var check = false
            val docId = getFileIdFromPdfUri(Uri.parse(pdfUrl))
            Log.i("URLDEEPLINK", "getIntentDataOpenPdf---TRY BLOCK --ID ___PDFURL:${docId} ")
            val pathDeepLink = try {
                check = true
                Log.i(
                    "URLDEEPLINK",
                    "getIntentDataOpenPdf---TRY BLOCK ___PDFURL:${Uri.parse(pdfUrl)} "
                )
                Log.i(
                    "URLDEEPLINK",
                    "getIntentDataOpenPdf---TRY BLOCK:${
                        UtilsClass().getRealPathFromURI(
                            this@ViewEditPdfActivity,
                            Uri.parse(pdfUrl)
                        )
                    } "
                )
                UtilsClass().getRealPathFromURI(this@ViewEditPdfActivity, Uri.parse(pdfUrl))
            } catch (ex: java.lang.Exception) {
                Log.i("checkList", "setList: $ex")
                try {
                    check = false
                    Log.i(
                        "URLDEEPLINK", "getIntentDataOpenPdf---TRY BLOCK:${
                            UtilsClass().getAbsolutePathFromUri(
                                this@ViewEditPdfActivity,
                                Uri.parse(pdfUrl)
                            )
                        } "
                    )
                    UtilsClass().getAbsolutePathFromUri(
                        this@ViewEditPdfActivity,
                        Uri.parse(pdfUrl)
                    )
                } catch (ex: java.lang.Exception) {
                    Log.i("getAbsolutePathFromUri", "getAbsolutePathFromUri: $ex")
                    null
                }
            }
            pathDeepLink?.let { fPath ->
                docId?.let { docId ->
                    val deepLinkObj = PDFEntity(
                        docId = docId,
                        docTitle = File(fPath).name,
                        docPath = fPath
                    )
                    receivedData = deepLinkObj
                    currentDocId = docId
                    path = fPath
                    Log.i("DEEPLINKID", "getIntentDataOpenPdf:${deepLinkObj.docId} ")
                    documentName = File(fPath).name
                    Log.i("DEEPLINKTITEL", "getIntentDataOpenPdf:${deepLinkObj.docTitle} ")
                    binding.titleTv.text = documentName
                    binding.toolsLayout.annotType.text = documentName
                    //  openFilesOptionDialog(documentName)

                }
            }
            openPdfFile(path)
        }


    }

    private fun getFileIdFromPdfUri(pdfUri: Uri): Long? {
        val pathSegments = pdfUri.pathSegments
        val fileIdSegment =
            pathSegments.lastOrNull { it == "file" }?.let { pathSegments.indexOf(it) + 1 }

        val fileId: Long? = fileIdSegment?.takeIf { it < pathSegments.size }?.let {
            pathSegments[it].toLongOrNull()
        }
        return fileId
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }


    private fun openPdfFile(pathGet: String?) {
        pathGet?.let { path ->
            Log.i("FILEPATH", "getIntentDataOpenPdf:$path")
            displayPdf(path)

        }
    }

    private fun ActivityViewPdfBinding.disableApplyButton() {
//        toolsLayout.acceptButton.isEnabled = false
//        toolsLayout.acceptButton.backgroundTintList =
//            ContextCompat.getColorStateList(this@ViewEditPdfActivity, R.color.disable_color)
    }

    private fun ActivityViewPdfBinding.enableApplyButton() {
//        toolsLayout.acceptButton.isEnabled = true
//        toolsLayout.acceptButton.backgroundTintList = ContextCompat.getColorStateList(this@ViewEditPdfActivity, R.color.button_selected_color)
    }

    private fun displayPdf(path: String) {
        val file = File(path)
        if (file.exists()) {
            muPDFCore = openFile(path)
        }


    }

    private fun copyFileFromAssetsToInternal(context: Context, assetFileName: String): String? {
        val inputStream: InputStream
        val outputStream: OutputStream
        try {
            inputStream = context.assets.open(assetFileName)
            val outputFile = File(context.filesDir, assetFileName)
            outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            return outputFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

//    private fun openFile(path: String): MuPDFCore? {
//        val lastSlashPos = path.lastIndexOf('/')
//        fileName =
//            (if (lastSlashPos == -1) path else path.substring(lastSlashPos + 1)).toString()
//        Log.e("openFile", "filename = $fileName")
//        Log.e("openFile", "Trying to open $path")
//        try {
//            muPDFCore = MuPDFCore(this, path)
//            // New: delete old directory data
//            OutlineActivityData.set(null)
//        } catch (e: Exception) {
//            Log.e("openFile", "openFile catch:$e")
//            return null
//        } catch (e: OutOfMemoryError) {
//            //  out of memory is not an Exception, so we catch it separately.
//            Log.e("openFile", "openFile catch: OutOfMemoryError $e")
//            return null
//        }
//        return muPDFCore
//    }

    private fun openFile(path: String): MuPDFCore? {
        val lastSlashPos = path.lastIndexOf('/')
        val fileName = if (lastSlashPos == -1) path else path.substring(lastSlashPos + 1)
        Log.e("openFile", "filename = $fileName")
        Log.e("openFile", "Trying to open $path")
        try {
            val muPDFCore = MuPDFCore(this, path)
            // New: delete old directory data
            OutlineActivityData.set(null)
            return muPDFCore
        } catch (e: Exception) {
            Log.e("openFile", "Error opening file", e)
            return null
        } catch (e: OutOfMemoryError) {
            //  out of memory is not an Exception, so we catch it separately.
            Log.e("openFile", "OutOfMemoryError", e)
            return null
        }
    }

    private fun setBottomRecyclerData() {
        try {
            val bottomRvModelList: ArrayList<RecyclerVDataC> = ArrayList()
            bottomRvModelList.add(
                RecyclerVDataC(
                    ContextCompat.getDrawable(
                        this@ViewEditPdfActivity, R.drawable.edit_doc_ic
                    ), resources.getString(R.string.edit)
                )
            )
            bottomRvModelList.add(
                RecyclerVDataC(
                    ContextCompat.getDrawable(
                        this@ViewEditPdfActivity, R.drawable.page_go_to_ic
                    ), resources.getString(R.string.page)
                )
            )
            bottomRvModelList.add(
                RecyclerVDataC(
                    ContextCompat.getDrawable(
                        this@ViewEditPdfActivity, R.drawable.reading_mode_ic
                    ), resources.getString(R.string.reading_mode)
                )
            )
            bottomRvModelList.add(
                RecyclerVDataC(
                    ContextCompat.getDrawable(
                        this@ViewEditPdfActivity, R.drawable.comment_ic
                    ), resources.getString(R.string.comment)
                )
            )
            bottomRvModelList.add(
                RecyclerVDataC(
                    ContextCompat.getDrawable(
                        this@ViewEditPdfActivity, R.drawable.read_aloud_ic
                    ), resources.getString(R.string.read_aloud)
                )
            )
            val bottomRvAdapter = BottomRecyclerAdapter(
                this@ViewEditPdfActivity, bottomRvModelList
            ) { titleText ->
                bottomRecyclerCallBack(titleText)
            }
            binding.bottomOptionRecycler.apply {
                adapter = bottomRvAdapter
                setHasFixedSize(true)
            }
        } catch (ex: java.lang.Exception) {
            Log.e("error", "setBottomRecyclerVData: $ex")
        }
    }


    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                muPDFCore?.let { muPDFCoreN ->
                    if (muPDFCoreN.hasChanges()) {
                    }
                }
            }
        }


    fun setLastModifiedToCurrentTime(filePath: String) {
        val file = File(filePath)
        val currentTime = Date()
        file.setLastModified(currentTime.time)
        Log.i(
            "LASTMODIFIEDE",
            "handleOnBackPressed:--LET ---TIME${file.setLastModified(currentTime.time)} "
        )
    }

    private fun scanFile(context: Context, filePath: String) {
        try {
            MediaScannerConnection.scanFile(context,
                arrayOf(filePath),
                null,
                object : MediaScannerConnection.OnScanCompletedListener {
                    override fun onScanCompleted(path: String, uri: Uri?) {
                    }
                })
        } catch (_: Exception) {
        }
    }


    override fun onDestroy() {
        Log.i("LIFECYCL", "onDestroy: called ")
        if (binding.pdfReaderRenderViewN != null) {
            binding.pdfReaderRenderViewN.applyToChildren(object : ReaderView.ViewMapper() {
                override fun applyToView(view: View) {
                    (view as MuPDFView).releaseBitmaps()
                }
            })
        }
        if (muPDFCore != null) muPDFCore?.onDestroy()
        /* if (mAlertTask != null) {
             mAlertTask.cancel(true)
             mAlertTask = null
         }*/
        muPDFCore = null
        super.onDestroy()
    }

    private fun colorListInflating() {
        val colors = arrayOf(
            "#D4D4D4", "#4E4E4E", "#3A3A3A", "#000000", "#FFCCD5",
            "#FFB3C1", "#FF4D6D", "#C9184A", "#A4133C", "#800F2F", "#590D22", "#FFEA00",
            "#FFDD00", "#FFDD00", "#FFD000", "#FFAA00", "#FF9500", "#FF8800", "#FFB703",
            "#4CC9F0", "#4895EF", "#4361EE", "#3F37C9", "#3A0CA3", "#480CA8", "#7209B7",
            "#B5179E", "#F72585", "#8338EC", "#EF476F", "#06D6A0", "#EF476F", "#073B4C",
            "#5F0F40", "#CCFF33", "#9EF01A", "#38B000", "#008000", "#006400", "#004B23",
            "#F7D1CD", "#A47148", "#892B64", "#3D2645", "#0d3b66", "#faf0ca", "#f4d35e",
            "#ee964b", "#f95738", "#6fffe9", "#5bc0be", "#41ead4", "#011627", "#f7af9d",
            "#c08497", "#b0d0d3", "#ffcad4", "#456990", "#FFFFFF",
        )
        for (i in colors.indices) {
            val textColorModel = ColorItem(colors[i])
            colorItemList.add(textColorModel)
        }
    }

    private fun setBrushSize() {
        binding.brushSizeSlider.addOnSliderTouchListener(touchListener)
    }

    private val touchListener: Slider.OnSliderTouchListener =
        object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                muPDFReaderViewN?.setPaintStrockWidth(slider.value)
            }
        }

    private fun hideUndoRedoButtons() {
        binding.drawLayout.hideIfVisible()
        binding.toolsLayout.undoDrawButton.hideIfVisible()
        binding.toolsLayout.redoDrawButton.hideIfVisible()
    }

    private fun showUndoRedoButtons() {
        binding.toolsLayout.undoDrawButton.showIfNotVisible()
        binding.toolsLayout.redoDrawButton.showIfNotVisible()
    }

    override fun onPageChanged(page: Int) {
        Log.e("PageIs", "CurrentPageIS:${page}")
        setSeekProgressWhenChange(page + 1)


    }

    override fun onPageChanged(page: Int, currentPageView: View?) {
        TODO("Not yet implemented")
    }

    private fun setSeekProgressWhenChange(index: Int) {
        try {
            binding.mySeekBar.progress = index
            seekBar?.thumb = getThumb(index)
            binding.horizontalSeekSlider.progress = index
            binding.horizontalSeekSlider.thumb = getThumbHorizontal(index)

        } catch (ex: Exception) {
            Log.e("error", "onPageChanged: ")
        }
    }


}
