package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BottomRecyclerAdapter(
    private val context: Context,
    private var iconsList: ArrayList<RecyclerVDataC>,
    private val bottomRecyclerCallBack:(titleText: String)->Unit,
) : RecyclerView.Adapter<BottomRecyclerAdapter.EditorViewHolder>() {
    private var titleTextLast: TextView? = null
    private var iconIvLast: ImageView? = null

    inner class EditorViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var titleText: TextView = itemView.findViewById(R.id.bottom_text)
        var iconIv: ImageView = itemView.findViewById(R.id.bottom_icon)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EditorViewHolder {
        return EditorViewHolder(
            LayoutInflater.from(context).inflate(R.layout.bottom_rv_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return iconsList.size
    }


    override fun onBindViewHolder(holder: EditorViewHolder, position: Int) {
        if (position < iconsList.size && position >= 0) {
            val model: RecyclerVDataC = iconsList[position]

            holder.iconIv.setImageDrawable(model.images)
            holder.titleText.text = model.titles
            holder.itemView.setOnSingleClickListener({
                resetColors()
                titleTextLast = holder.titleText
                iconIvLast = holder.iconIv
                selectedColors()
                bottomRecyclerCallBack.invoke( model.titles)
            })
        }
    }

    private fun selectedColors() {
        iconIvLast?.setColorFilter(
            ContextCompat.getColor(
                context, R.color.primary_color_code
            )
        )
        titleTextLast?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.primary_color_code
            )
        )
    }

    private fun resetColors() {

        iconIvLast?.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.text_color
            )
        )
        titleTextLast?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.text_color
            )
        )
    }



}