package com.merttoptas.chaptatutorial

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.safetynet.SafetyNet
import com.merttoptas.chaptatutorial.databinding.ActivityMainBinding
import com.tomasznajda.rxrecaptcha.ReCaptcha
import org.json.JSONObject
import javax.crypto.Cipher.SECRET_KEY


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        queue = Volley.newRequestQueue(applicationContext)

        binding.btnChapta.setOnClickListener {
            getCaptcha()
        }

    }

    private fun reChapta(){
        ReCaptcha().verify(this, "")

    }

    private fun getCaptcha() {
        SafetyNet.getClient(this@MainActivity)
            .verifyWithRecaptcha("6LccVGcdAAAAAM8c8HQOArsm6WpTvZk2-Nvmpn9h")
            .addOnSuccessListener { recaptchaTokenResponse ->

                val captchaToken = recaptchaTokenResponse.tokenResult
                if (captchaToken != null) {
                    if (captchaToken.isNotEmpty()) {
                        Toast.makeText(
                            this@MainActivity, captchaToken.toString(), Toast.LENGTH_SHORT
                        ).show()
                        Log.d("deneme1", "buraya girdi 1 " + captchaToken.toString())
                                //handleVerify(captchaToken)
                    } else {
                        Toast.makeText(
                            this@MainActivity, "Invalid Captcha Response", Toast.LENGTH_SHORT
                        ).show()
                        Log.d("deneme1", "buraya girdi 3 ")

                    }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this@MainActivity, "Failed to Load Captcha", Toast.LENGTH_SHORT
                ).show()
                Log.d("deneme1", "buraya girdi 4 ")

            }
    }

    private fun handleVerify(responseToken: String) {
        val url = "https://igbeta.mertercan.com/api/other/captcha"
        Log.d("deneme1", "buraya girdi 2 ")

        val request: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    Toast.makeText(
                        applicationContext,
                        jsonObject.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    if (jsonObject.getBoolean("success")) { //code logic when captcha returns true Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getBoolean("success")),Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(
                            applicationContext,
                            jsonObject.getString("error-codes").toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (ex: Exception) {
                    Log.d("deneme1", "JSON exception: " + ex.message)
                }
            }, Response.ErrorListener { error -> Log.d("deneme1", "Error message: " + error.message) }) {
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["secret"] = "6LccVGcdAAAAAKJvjOtsNEhJm7S8VFl639ndv3vc"
                    params["response"] = responseToken
                    return params
                }
            }
        request.retryPolicy = DefaultRetryPolicy(
            50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(request)

    }
}