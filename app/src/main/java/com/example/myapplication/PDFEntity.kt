package com.example.myapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PDFEntity(
    var docId: Long = 0L,
    var docTitle: String = "",
    var docPath: String = "",
    var docUri: String = "",
    var docSize: Long = 0L,
    var docDateModified: Long = 0L,
    var docFavourite: Boolean = false,
    var docRecentOpen: Boolean = false,
    var isDocBookmarked: Boolean = false,
    var isItemChecked: Boolean = false
): Parcelable