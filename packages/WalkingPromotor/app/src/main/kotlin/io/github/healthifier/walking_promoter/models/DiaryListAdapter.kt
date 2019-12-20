package io.github.healthifier.walking_promoter.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.list.view.*

class DiaryListAdapter(context: Context, diaries: List<DiaryData>) : ArrayAdapter<DiaryData>(context, 0, diaries){
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val thumbnail_image = Thumbnail_Image()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        var holder: ViewHolder

        if (view == null) {
            view = layoutInflater.inflate(R.layout.list, parent, false)
            holder = ViewHolder(
                view?.titleTextView!!,
                view.dayTextView,
                view.photoImageView
            )
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val diary = getItem(position) as DiaryData
        holder.titleTextView.text = diary.title
        holder.dayTextView.text = diary.day
        //holder.photoTextView.text = diary.photo
        holder.photoImageView.setImageBitmap(thumbnail_image.decodeSampledBitmapFromResource(diary.photo, 72, 54))
        return view
    }
}
