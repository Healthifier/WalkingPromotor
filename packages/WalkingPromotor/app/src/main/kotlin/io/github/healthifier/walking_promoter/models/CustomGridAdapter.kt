package io.github.healthifier.walking_promoter.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.grid_view.view.*

//// customListはrecyclerViewのコンテンツとしてに表示するString配列のデータ
class CustomGridAdapter(private val customList: ArrayList<String>, private val customList2: ArrayList<String>) : RecyclerView.Adapter<CustomGridAdapter.CustomViewHolder>(){

    // リスナー格納変数
    lateinit var listener: OnItemClickListener

    // ViewHolderクラス(別ファイルに書いてもOK)
    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val sampleImg = view.imageView
        val sampleTxt = view.textView
    }

    // getItemCount onCreateViewHolder onBindViewHolderを実装
    // 上記のViewHolderクラスを使ってViewHolderを作成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.grid_view, parent, false)
        return CustomViewHolder(item)
    }

    // recyclerViewのコンテンツのサイズ
    override fun getItemCount(): Int {
        return customList.size
    }

    // ViewHolderに表示する画像とテキストを挿入
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.view.textView.text = customList[position]
        Glide.with(holder.view).load(customList2[position]).into(holder.view.imageView)
        // タップしたとき
        holder.view.setOnClickListener {
            listener.onItemClickListener(it, position, customList[position])
        }
    }

    //インターフェースの作成
    interface OnItemClickListener{
        fun onItemClickListener(view: View, position: Int, clickedText: String)
    }

    // リスナー
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
}
