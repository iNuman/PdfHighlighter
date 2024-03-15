package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ContentColorRecyclerBinding


class ColorAdapter(
    private val colorItems: List<ColorItem>,
    private val onColorItemClickListener: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {
    private var drawable: GradientDrawable? = null
    var mSelectedItem:Int = -1
    var previousItemPosition = -1
//    private var selectedItemPosition: Int = RecyclerView.NO_POSITION


   inner class ViewHolder(private val binding: ContentColorRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(colorItem: ColorItem) {
            drawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.parseColor(colorItem.colorCode), Color.parseColor(colorItem.colorCode))
            )
            drawable?.cornerRadius = 3f
            binding.colorCircle.background = drawable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContentColorRecyclerBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return colorItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val colorItem = colorItems[position]
        holder.bind(colorItem)
        if (position == mSelectedItem) {
            holder.itemView.setBackgroundResource(R.drawable.color_bg_selected_text)
        } else {
            holder.itemView.setBackground(null)
        }

        holder.itemView.setOnClickListener {
            mSelectedItem = position
            notifyItemChanged(previousItemPosition)
            previousItemPosition = mSelectedItem
            notifyItemChanged(mSelectedItem)
                onColorItemClickListener.invoke(colorItem.colorCode)
            }
        }
    }




