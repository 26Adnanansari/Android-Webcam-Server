package com.adnanansari.webcam

/**
 * Camera foreground service implementation
 * Developed by Adnan Ansari
 * GitHub: github.com/26Adnanansari
 * Email: 26adnanansari@gmail.com
 */

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraService : Service() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var webSocketServer: SimpleWebSocketServer

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        cameraExecutor = Executors.newSingleThreadExecutor()
        webSocketServer = SimpleWebSocketServer(8080)
        webSocketServer.start()
        startForegroundService()
    }

    private fun startForegroundService() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "camera_channel")
            .setContentTitle("Camera Streaming")
            .setContentText("Streaming camera to connected devices")
            .setSmallIcon(R.drawable.ic_camera)
            .build()
        
        startForeground(1, notification)
        startCamera()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "camera_channel",
                "Camera Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        // Convert image to base64 and send via WebSocket
                        val base64 = image.toBase64String()
                        webSocketServer.broadcast(base64)
                        image.close()
                    }
                }
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        webSocketServer.stop()
    }
}

fun ImageProxy.toBase64String(): String {
    // Implementation to convert image to base64
    return "base64_placeholder" // Replace with actual conversion
}
