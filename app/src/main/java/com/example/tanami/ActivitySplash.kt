package com.example.tanami

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.VideoView

class ActivitySplash : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activitysplash)

        val videoView = findViewById<VideoView>(R.id.videoSplash)

        val videoPath = "android.resource://" + packageName + "/" + R.raw.animasi_login
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        videoView.start()

        videoView.setOnCompletionListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

        videoView.start()
    }
}