package com.example.shakeit

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null

    private lateinit var textViewContador: TextView
    private lateinit var textViewMensagem: TextView
    private lateinit var buttonZerar: Button

    private var contador = 0
    private var ultimoMovimento = 0L

    private val limite = 15f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        textViewContador = findViewById(R.id.textViewContador)
        textViewMensagem = findViewById(R.id.textViewMensagem)
        buttonZerar = findViewById(R.id.buttonZerar)

        sensorManager =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager

        acelerometro =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (acelerometro == null) {
            Toast.makeText(
                this,
                "Acelerômetro não disponível.",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }

        buttonZerar.setOnClickListener {
            contador = 0
            atualizarTela()
        }
    }

    override fun onResume() {
        super.onResume()

        acelerometro?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val aceleracao = sqrt(
                x * x + y * y + z * z
            )

            val agora = System.currentTimeMillis()

            if (aceleracao > limite &&
                agora - ultimoMovimento > 500
            ) {

                contador++

                ultimoMovimento = agora

                atualizarTela()
            }
        }
    }

    private fun atualizarTela() {

        textViewContador.text = "Agitações: $contador"

        textViewMensagem.text = when {
            contador == 0 -> "Vamos começar!"
            contador < 5 -> "Continue!"
            contador < 10 -> "Boa!"
            contador < 20 -> "Mandou bem!"
            else -> "VOCÊ NÃO PARA! 🔥"
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}