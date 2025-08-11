package com.example.yarhooshmand

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.yarhooshmand.databinding.ActivitySmartBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.*

class SmartActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var b: ActivitySmartBinding
    private val client = OkHttpClient()
    private val prefsName = "yar_prefs"
    private var key: String? = null
    private var endpoint: String? = null
    private var tts: TextToSpeech? = null
    private val driveDefault = "https://drive.google.com/uc?export=download&id=17iwkjyGcxJeDgwQWEcsOdfbOxOah_0u0"

    private val recordPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) Toast.makeText(this, "اجازهٔ دسترسی به میکروفون لازم است", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySmartBinding.inflate(layoutInflater)
        setContentView(b.root)
        tts = TextToSpeech(this, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        key = prefs.getString("selected_key", null)
        endpoint = prefs.getString("selected_endpoint", null)
        b.tvStatus.text = if (key != null) "کلید: موجود" else "کلید: ندارد"
        b.tvFree.text = "استفادهٔ رایگان: " + prefs.getInt("free_uses_left", 5).toString()

        b.btnUpdateKeys.setOnClickListener {
            b.progress.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                val link = prefs.getString("drive_link", driveDefault) ?: driveDefault
                downloadAndSelectKey(link)
                withContext(Dispatchers.Main) {
                    b.progress.visibility = View.GONE
                    b.tvStatus.text = if (key != null) "کلید: موجود" else "کلید: ندارد"
                }
            }
        }

        b.btnSend.setOnClickListener {
            val text = b.etInput.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            if (prefs.getInt("free_uses_left", 5) <= 0) {
                Toast.makeText(this, "بخش هوشمند قفل است — لطفا خرید کنید.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            prefs.edit().putInt("free_uses_left", prefs.getInt("free_uses_left", 5) - 1).apply()
            b.tvFree.text = "استفادهٔ رایگان: " + prefs.getInt("free_uses_left", 5).toString()
            b.progress.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                val reply = callModel(text)
                withContext(Dispatchers.Main) {
                    b.progress.visibility = View.GONE
                    if (reply != null) {
                        b.tvChat.append("شما: " + text + "\nیار: " + reply + "\n\n")
                        tts?.speak(reply, TextToSpeech.QUEUE_ADD, null, "resp")
                    } else {
                        b.tvChat.append("خطا در تماس با مدل\n")
                    }
                }
            }
        }

        b.btnMic.setOnClickListener {
            startVoiceInput()
        }
    }

    private fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "تشخیص گفتار در دستگاه فعال نیست", Toast.LENGTH_SHORT).show()
            return
        }
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "صحبت کنید...")
        startActivityForResult(i, 901)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 901 && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                b.etInput.setText(matches[0])
            }
        }
    }

    private fun downloadAndSelectKey(url: String) {
        try {
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return
                val body = resp.body?.string() ?: return
                val lines = body.split(Regex("\\r?\\n")).map { it.trim() }.filter { it.isNotEmpty() }
                for (l in lines) {
                    val service = if (l.contains(":")) l.substringBefore(":") else "openai"
                    val rawKey = if (l.contains(":")) l.substringAfter(":") else l
                    endpoint = when (service.lowercase(Locale.getDefault())) {
                        "openrouter" -> "https://api.openrouter.ai/v1/chat/completions"
                        else -> "https://api.openai.com/v1/chat/completions"
                    }
                    key = rawKey
                    getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString("selected_key", key).putString("selected_endpoint", endpoint).apply()
                    return
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun callModel(prompt: String): String? {
        try {
            val json = JSONObject()
            val messages = org.json.JSONArray()
            val m = JSONObject()
            m.put("role", "user")
            m.put("content", prompt)
            messages.put(m)
            json.put("model", "gpt-3.5-turbo")
            json.put("messages", messages)
            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val req = Request.Builder().url(endpoint ?: return null).addHeader("Authorization", "Bearer " + (key ?: "")).post(body).build()
            client.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return null
                val s = r.body?.string() ?: return null
                val jo = JSONObject(s)
                val choices = jo.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val c0 = choices.getJSONObject(0)
                    val msg = c0.optJSONObject("message")?.optString("content") ?: c0.optString("text", null)
                    return msg
                }
                return jo.optString("answer", null)
            }
        } catch (e: Exception) { e.printStackTrace(); return null }
    }

    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts?.language = Locale("fa","IR") }
    override fun onDestroy() { super.onDestroy(); tts?.shutdown() }
}
