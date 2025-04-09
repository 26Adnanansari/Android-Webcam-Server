package com.adnanansari.webcam

/**
 * Main Activity for Mobile Camera Connect
 * Developed by Adnan Ansari
 * GitHub: github.com/26Adnanansari
 * Email: 26adnanansari@gmail.com
 */

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adnanansari.webcam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var server: SimpleWebSocketServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            startService(Intent(this, CameraService::class.java))
            server = SimpleWebSocketServer(8080)
            server.start()
            updateUI(true)
        }

        binding.btnStop.setOnClickListener {
            stopService(Intent(this, CameraService::class.java))
            server.stop()
            updateUI(false)
        }

        binding.tvIp.text = getLocalIpAddress()
    }

    private fun updateUI(isRunning: Boolean) {
        binding.btnStart.isEnabled = !isRunning
        binding.btnStop.isEnabled = isRunning
        binding.tvStatus.text = if (isRunning) "Server running" else "Server stopped"
    }

    private fun getLocalIpAddress(): String {
        // Implementation to get device IP address
        return "192.168.x.x" // Replace with actual IP fetching logic
    }
}
