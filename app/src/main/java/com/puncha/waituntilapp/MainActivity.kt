package com.puncha.waituntilapp

import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.puncha.waituntilapp.databinding.ActivityMainBinding
import java.lang.reflect.Field
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var isPageLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.webview.apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.d("PCH", Thread.currentThread().toString())
                    super.onPageFinished(view, url)
                    isPageLoaded = true
                }
            }
        }
        binding.fab.setOnClickListener {
            this.startResearching(binding.webview)
        }
    }

    private fun startResearching(webView: WebView) {
        Log.d("PCH", "Researching started...")
        webView.loadUrl("https://www.baidu.com")
        this.waitUntil { isPageLoaded }
        binding.webview.evaluateJavascript("1+2") {
            Toast.makeText(this.applicationContext, "1 + 2 = $it", Toast.LENGTH_LONG).show()
        }
        Log.d("PCH", "Researching is done!")
    }

    private fun waitUntil(predicate: ()->Boolean) {
        Log.d("PCH", "waitUntil - started")
        val queue = Looper.myQueue()
        val nextMethod: Method? = queue.javaClass!!.getDeclaredMethod("next", *arrayOf())
        nextMethod?.isAccessible = true
        while (!predicate()) {
            val msg: Message? = nextMethod!!.invoke(queue) as Message?
            if (msg != null) {
                Log.d("PCH", "Picked a message: $msg")
                val target = msg.target
                if (target == null) {
                    // No target is a magic identifier for the quit message.
                    break
                }
                target.dispatchMessage(msg)
                try {
                    msg.recycle()
                } catch (_: java.lang.Exception) {
                }
            }
        }
        Log.d("PCH", "waitUntil - ended")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}