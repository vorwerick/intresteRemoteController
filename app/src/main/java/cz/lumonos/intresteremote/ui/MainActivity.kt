package cz.lumonos.intresteremote.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import cz.lumonos.intresteremote.App
import cz.lumonos.intresteremote.R
import cz.lumonos.intresteremote.data.intreste.IntresteRepository
import cz.lumonos.intresteremote.service.log.LoggingListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), LoggingListener {

    var connectedToIntreste = false
    val stringbuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.activity_main)

        if (!IntresteRepository.instance.isConnected()) {
            val address = "00:22:04:00:27:80"
            if (!address.isNullOrEmpty()) {
                Log.i("AGAIN", "XXXXC")
                connectToBluetooth(address)
            }
        }
        IntresteRepository.instance.subscribeConnectionStatus { status, address ->
            if (status != null) {
                when (status) {
                    1 -> {
                        //connected
                        findViewById<LinearLayout>(R.id.connection_status_view).setBackgroundColor(
                            getColor(
                                R.color.correct_hit_color_text
                            )
                        )
                    }
                    2 -> {
                        findViewById<LinearLayout>(R.id.connection_status_view).setBackgroundColor(
                            getColor(
                                R.color.timeout_color
                            )
                        )
                    }
                    else -> {
                        //disconnected
                        findViewById<LinearLayout>(R.id.connection_status_view).setBackgroundColor(
                            getColor(
                                R.color.error_color
                            )
                        )

                    }
                }

            }
        }


        App.loggingService.loggingListener = this

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { view ->
            getNavController().navigateUp()
        }

        val appBarConfiguration = AppBarConfiguration(getNavController().graph)
        setupActionBarWithNavController(getNavController(), appBarConfiguration)

        connectedToIntreste = IntresteRepository.instance.isConnected()


        /* if (intresteViewModel.isConnectedToIntreste.value == true) {

         } else {
             intresteViewModel.connectToIntreste(
                 IntresteCommunicationService.MAC_ADDRESS,
                 BluetoothAdapter.getDefaultAdapter()
             )
         }*/

    }


    private fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    override fun onLogMessage(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            stringbuilder.insert(0, message + "\n")
            findViewById<TextView>(R.id.console).text = stringbuilder.toString()
        }
    }

    private fun connectToBluetooth(address: String) {
        Toast.makeText(this, address, Toast.LENGTH_LONG).show()
        IntresteRepository.instance.connect(
            address,
            BluetoothAdapter.getDefaultAdapter()
        )
    }

    private fun getPreferences(context: Context): String? {
        return context.getSharedPreferences("APP", Context.MODE_PRIVATE).getString("address", "")
    }
}