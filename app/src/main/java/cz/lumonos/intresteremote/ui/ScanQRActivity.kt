package cz.lumonos.intresteremote.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ScanQRActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this) // Programmatically initialize the scanner view
        setContentView(mScannerView) // Set the scanner view as the content view
    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView?.startCamera() // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        setPreferences(this, rawResult.text)
        val intent = Intent(this, MainActivity::class.java)
       // startActivity(intent)


    }

    private fun setPreferences(context: Context, address: String): Boolean? {
        return context.getSharedPreferences("APP", Context.MODE_PRIVATE).edit()
            ?.putString("address", address)?.commit()
    }
}