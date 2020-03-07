package jp.techacademy.takaya.oosaki.qa_app

import android.content.Context

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

class FavoriteListAdapter(context: Context) : BaseAdapter()  {

    private var fLayoutInflater: LayoutInflater
    private var fQuestionArrayList = ArrayList<Question>()

    init {
        fLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return fQuestionArrayList.size
    }

    override fun getItem(position: Int): Any {
        return fQuestionArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        var GenreStr = ""

        if (convertView == null) {
            convertView = fLayoutInflater.inflate(R.layout.list_favorite_question, parent, false)
        }

        val titleText = convertView!!.findViewById<View>(R.id.FtitleTextView) as TextView
        titleText.text = fQuestionArrayList[position].title

        val nameText = convertView.findViewById<View>(R.id.FnameTextView) as TextView
        nameText.text = fQuestionArrayList[position].name

        val genreText = convertView.findViewById<View>(R.id.FgenreTextView) as TextView
        val genreNum = fQuestionArrayList[position].genre
        if(genreNum == 1){
            GenreStr = "趣味"
        }else if(genreNum == 2){
            GenreStr ="生活"
        }else if(genreNum == 3){
            GenreStr ="健康"
        }else if(genreNum == 4){
            GenreStr ="コンピューター"
        }
        genreText.text = GenreStr

        val bytes = fQuestionArrayList[position].imageBytes
        if (bytes.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
            val imageView = convertView.findViewById<View>(R.id.FimageView) as ImageView
            imageView.setImageBitmap(image)
        }

        return convertView
    }

    fun setQuestionArrayList(questionArrayList: ArrayList<Question>) {
        fQuestionArrayList = questionArrayList
    }



}