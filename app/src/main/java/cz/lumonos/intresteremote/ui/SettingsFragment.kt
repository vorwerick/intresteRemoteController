package cz.lumonos.intresteremote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import cz.lumonos.intresteremote.R
import cz.lumonos.intresteremote.data.intreste.IntresteRepository

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sortPanelsButton =
            view.findViewById<Button>(R.id.sort_panel_button).also {
                it.setOnClickListener {
                    IntresteRepository.instance.sortPanels()
                }
            }
        val testPanelsButton =
            view.findViewById<Button>(R.id.test_panels_button).also {
                it.setOnClickListener {
                    IntresteRepository.instance.runPanelTest()
                }
            }

        val testLedDisplayButton =
            view.findViewById<Button>(R.id.led_test_button).also {
                it.setOnClickListener {
                    IntresteRepository.instance.runLedDisplayTest()
                }
            }

        val panelsCountText =
            view.findViewById<TextView>(R.id.panels_connected_count_text)

        val coreTempText =
            view.findViewById<TextView>(R.id.intreste_core_temp_text)

        val versionNameText =
            view.findViewById<TextView>(R.id.intreste_version_count_text)
        IntresteRepository.instance.subscribeState {
            if(it != null){
                coreTempText.text = it.cpuTemp.toString() + " °C"
                versionNameText.text = it.version
                panelsCountText.text = it.panelCount.toString()
            }
        }
    }
}