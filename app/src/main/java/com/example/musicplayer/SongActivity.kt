package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivitySongBinding
import java.util.concurrent.TimeUnit

class SongActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongBinding

    private var mediaPlayer: MediaPlayer? = null

    private var seekLength : Int = 0

    private var statusPlaying: Boolean = true

    private var position: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var songName = intent.getStringExtra("songName")
        var artistName = intent.getStringExtra("artistName")
        var imageId = intent.getIntExtra("imageId", R.drawable.bumbum)
        var songId = intent.getIntExtra("songId", R.raw.bum_bum)
        var songDuration = intent.getStringExtra("songDuration")
        val result = intent.getIntExtra("result", R.drawable.bumbum)
        position = intent.getIntExtra("position", 0)


        val imageArray = intent.getIntArrayExtra("imageArray")
        val songNameArray = intent.getStringArrayExtra("songNameArray")
        val artistNameArray = intent.getStringArrayExtra("artistNameArray")
        val songIdArray = intent.getIntArrayExtra("songIdArray")
        statusPlaying = intent.getBooleanExtra("statusPlaying", true)

        binding.songName.text = songName
        binding.artistName.text  =artistName
        binding.imageId.setImageResource(imageId)
        binding.songDuration.text = songDuration

        mediaPlayer = MediaPlayer.create(this, songId)
        mediaPlayer!!.seekTo(result)
        if (statusPlaying) {

            mediaPlayer!!.start()
            binding.play.setBackgroundColor(R.drawable.baseline_pause_24)
            statusPlaying = true
        }else{
            statusPlaying = false
            binding.play.setBackgroundColor(R.drawable.baseline_play_arrow_24)
        }
        updateSeekBar()
        binding.play.setOnClickListener {
            playSong()
        }

        binding.forward.setOnClickListener {
            mediaPlayer!!.release()
            mediaPlayer = null

            if (songNameArray!!.indexOf(songName) < 8) {
                songName = songNameArray!![songNameArray.indexOf(songName) + 1]
                artistName = artistNameArray!![artistNameArray.indexOf(artistName) + 1]
                imageId = imageArray!![imageArray.indexOf(imageId) + 1]
                songId = songIdArray!![songIdArray.indexOf(songId) + 1]
            } else{
                songName = songNameArray!![0]
                artistName = artistNameArray!![0]
                imageId = imageArray!![0]
                songId = songIdArray!![0]
            }

            binding.songName.text = songNameArray!![songNameArray.indexOf(songName)]
            binding.artistName.text  = artistNameArray!![artistNameArray.indexOf(artistName)]
            binding.imageId.setImageResource(imageArray!![imageArray.indexOf(imageId)])
            mediaPlayer = MediaPlayer.create(this, songId!!)
            val duration = mediaPlayer!!.duration
            val durationLong = duration.toLong()
            binding.songDuration.text = durationConverter(durationLong)
            mediaPlayer!!.start()
            updateSeekBar()


        }

        binding.rewind.setOnClickListener {
            mediaPlayer!!.release()
            mediaPlayer = null

            if (songNameArray!!.indexOf(songName) > 0) {
                songName = songNameArray!![songNameArray.indexOf(songName) - 1]
                artistName = artistNameArray!![artistNameArray.indexOf(artistName) - 1]
                imageId = imageArray!![imageArray.indexOf(imageId) - 1]
                songId = songIdArray!![songIdArray.indexOf(songId) - 1]
            } else{
                songName = songNameArray!![songNameArray.size - 1]
                artistName = artistNameArray!![songNameArray.size - 1]
                imageId = imageArray!![songNameArray.size - 1]
                songId = songIdArray!![songNameArray.size - 1]
            }
                binding.songName.text = songNameArray!![songNameArray.indexOf(songName)]
                binding.artistName.text  = artistNameArray!![artistNameArray.indexOf(artistName)]
                binding.imageId.setImageResource(imageArray!![imageArray.indexOf(imageId)])
                mediaPlayer = MediaPlayer.create(this, songId!!)
                val duration = mediaPlayer!!.duration
                val durationLong = duration.toLong()
                binding.songDuration.text = durationConverter(durationLong)
                mediaPlayer!!.start()
                updateSeekBar()

            }
        }




    private fun updateSeekBar(){

        if (mediaPlayer!= null){
            binding.songTime.text = durationConverter(
                mediaPlayer!!.currentPosition.toLong()
            )
        }

        seekBarSetup()
        Handler().postDelayed(runnable, 50)
    }

    private var runnable = Runnable { updateSeekBar() }

    private fun seekBarSetup() {

        if (mediaPlayer!= null){
            binding.seekbar.progress = mediaPlayer!!.currentPosition
            binding.seekbar.max = mediaPlayer!!.duration
        }

        binding.seekbar.setOnSeekBarChangeListener(
            @SuppressLint(/* ...value = */ "AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2){
                    mediaPlayer!!.seekTo(p1)
                    binding.songTime.text = durationConverter(p1.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if(mediaPlayer != null && mediaPlayer!!.isPlaying){

                    if (p0 != null){
                        mediaPlayer!!.seekTo(p0.progress)
                    }
                }

            }

        })
    }

    private fun playSong(){
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.seekTo(seekLength)
            mediaPlayer!!.start()
            binding.play.setBackgroundResource(R.drawable.baseline_pause_24)
            updateSeekBar()
        }
        else{
            mediaPlayer!!.pause()
            seekLength = mediaPlayer!!.currentPosition
            binding.play.setBackgroundResource(R.drawable.baseline_play_arrow_24)
        }

    }

    private fun clearMediaPlayer() {
        if(mediaPlayer!!.isPlaying){
            mediaPlayer!!.stop()
        }
        mediaPlayer!!.release()
        mediaPlayer = null
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

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }
    override fun onBackPressed() {
        intent.putExtra("result", mediaPlayer!!.currentPosition)
        intent.putExtra("newPosition", position)
        intent.putExtra("isPlaying", statusPlaying)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}