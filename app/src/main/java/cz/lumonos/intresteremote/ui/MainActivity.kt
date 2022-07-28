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

    var menu: Menu? = null

    var connectedToIntreste = false
    val stringbuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.activity_main)

        val address = "00:22:04:00:27:80"
        if (!address.isNullOrEmpty()) {
            Log.i("AGAIN", "XXXXC")
            connectToBluetooth(address)
        } else {
            launchActivity()
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
        menu?.findItem(R.id.menu_item_bluetooth)?.isVisible = connectedToIntreste

        GlobalScope.launch(Dispatchers.Main) {
            while (true){
                delay(1500)
                connectedToIntreste = IntresteRepository.instance.isConnected()
                menu?.findItem(R.id.menu_item_bluetooth)?.isVisible = connectedToIntreste
            }
        }


        /* if (intresteViewModel.isConnectedToIntreste.value == true) {

         } else {
             intresteViewModel.connectToIntreste(
                 IntresteCommunicationService.MAC_ADDRESS,
                 BluetoothAdapter.getDefaultAdapter()
             )
         }*/

    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("GOGOL", "dXXX")
            Log.i("GOGOL", "CCC")
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device!!.name
                val deviceHardwareAddress = device.address // MAC address
                Log.i("GOGOL", "device $deviceName")
                Log.i("GOGOL", "hard$deviceHardwareAddress")
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        this.menu = menu
        val item = menu?.findItem(R.id.menu_item_bluetooth)

        item?.isVisible = IntresteRepository.instance.isConnected()

        return true
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

    fun launchActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1002
            )
        } else {
            val intent = Intent(this, ScanQRActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1002 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this, ScanQRActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "Please grant camera permission to use the QR Scanner",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
}