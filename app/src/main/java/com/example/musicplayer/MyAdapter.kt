package com.example.musicplayer

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

class MyAdapter (private val context : Activity, private val arrayList : ArrayList<Song>) : ArrayAdapter<Song>(context,
                R.layout.list_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.list_item, null)

        val imageView : ImageView = view.findViewById(R.id.song_pic)
        val songName : TextView = view.findViewById(R.id.songName)
        val artistName : TextView = view.findViewById(R.id.artistName)
        val songDuration : TextView = view.findViewById(R.id.song_duration)

        imageView.setImageResource(arrayList[position].imageId)
        songName.text = arrayList[position].songName
        artistName.text = arrayList[position].artistName
        songDuration.text = arrayList[position].songDuration

        return view
    }
}