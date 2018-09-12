package com.kotdroid.sociallogins.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast

class TestService : Service() {
    override fun onBind(p0: Intent?): IBinder {
        return null!!
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val handler = Handler()

        val context = this

        Thread(Runnable {
            handler.post {
                Toast.makeText(context, "TestingBackgroundService in Oreo", Toast.LENGTH_SHORT).show()
            }
        }).start()

        return START_STICKY
    }
}