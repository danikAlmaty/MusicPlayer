package com.example.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.databinding.ActivitySongBinding
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var bindingSong : ActivitySongBinding
    private lateinit var songArrayList : ArrayList<Song>

    private lateinit var mediaPlayer: MediaPlayer

    private var statusPlaying: Boolean = true

    val imageIdArr = intArrayOf(
        R.drawable.aceofbase, R.drawable.bumbum, R.drawable.alexia,
        R.drawable.donomar, R.drawable.kaira, R.drawable.kaira,
        R.drawable.nent, R.drawable.kaira, R.drawable.desiigner
    )

    val songNameArray = arrayOf(

        "All That She Wants",
        "Я тебя бум-бум-бум",
        "The summer is crazy",
        "Danza kuduro",
        "Кайда кайда",
        "Маскунем",
        "Nentori",
        "Сени Суйем",
        "Timmy Turner"
    )

    val artistNameArray = arrayOf(
        "Ace Of Base",
        "BIFFGUYZ",
        "Alexia",
        "Don Omar",
        "Kairat Nurtas",
        "kairat Nurtas",
        "Arilena Ara",
        "Kairat Nurtas",
        "Desiigner"
    )

    val songIdArray = intArrayOf(
        R.raw.allthatshewants, R.raw.bum_bum, R.raw.crazysummer,
        R.raw.danzakuduro, R.raw.kaidakaida, R.raw.maskunem,
        R.raw.nentori, R.raw.senisuyem, R.raw.timmy
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingSong = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)




        songArrayList = ArrayList()

        for (i in songNameArray.indices){

            mediaPlayer = MediaPlayer.create(this, songIdArray[i])
            val duration = mediaPlayer.duration
            val durationLong = duration.toLong()
            val song = Song(songNameArray[i], artistNameArray[i], imageIdArr[i], songIdArray[i], durationConverter(durationLong))
            songArrayList.add(song)

        }

        binding.listView.isClickable = true
        binding.listView.adapter = MyAdapter(this, songArrayList)

        binding.listView.setOnItemClickListener { parent, view, position, id ->


                mediaPlayer.stop()
                mediaPlayer = MediaPlayer.create(this, songArrayList[position].songId)
                openSong(position, mediaPlayer, statusPlaying)



        }






    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                val newPosition = data!!.getIntExtra("newPosition", R.drawable.aceofbase)                // Get the result from intent
                val result = data!!.getIntExtra("result", R.drawable.aceofbase)
                statusPlaying = data!!.getBooleanExtra("isPlaying", true)

                mediaPlayer = MediaPlayer.create(this, songArrayList[newPosition].songId)
                mediaPlayer.seekTo(result)
                statusPlaying = if(statusPlaying){
                    mediaPlayer.start()
                    binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
                    true
                }else{
                    binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
                    false
                }
                binding.musController.visibility = View.VISIBLE
                binding.imageId.setImageResource(songArrayList[newPosition].imageId)

                binding.musController.isClickable = true

                binding.play.setOnClickListener {
                    playSong()
                }

                binding.musController.setOnClickListener {
                    openSong(newPosition, mediaPlayer, statusPlaying)
                }
            }
        }
    }

    private fun playSong(){
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            statusPlaying = true
        }
        else{
            mediaPlayer!!.pause()
            binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
            statusPlaying = false
        }
    }


    private fun durationConverter(duration : Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(duration)
                    )
        )
    }

    private fun openSong(position : Int, mediaPlayer: MediaPlayer, statusPlaying : Boolean){

        val duration = mediaPlayer.duration
        val durationLong = duration.toLong()

        val songName = songNameArray[position]
        val artistName = artistNameArray[position]
        val imageId = imageIdArr[position]
        val songId = songIdArray[position]


        var i = Intent(this, SongActivity::class.java)
        i.putExtra("songName", songName)
        i.putExtra("artistName", artistName)
        i.putExtra("imageId", imageId)
        i.putExtra("songId", songId)
        i.putExtra("songDuration", durationConverter(durationLong))
        i.putExtra("position", position)
        i.putExtra("result", mediaPlayer!!.currentPosition)
        i.putExtra("imageArray", imageIdArr)
        i.putExtra("songNameArray", songNameArray)
        i.putExtra("artistNameArray", artistNameArray)
        i.putExtra("songIdArray", songIdArray)
        i.putExtra("statusPlaying", statusPlaying)



        startActivityForResult(i, 0)

        if(mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
    }

}