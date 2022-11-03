package cz.lumonos.intresteremote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import cz.lumonos.intresteremote.R
import cz.lumonos.intresteremote.data.intreste.IntresteRepository
import cz.lumonos.intresteremote.service.GameState

class MenuFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        IntresteRepository.instance.requestCurrentGame()


        val currentGameCardView = view.findViewById<CardView>(R.id.current_game_card)
        currentGameCardView.setOnClickListener {
            if (IntresteRepository.instance.isConnected()) {
                findNavController().navigate(R.id.action_nav_menu_to_nav_current_game)
            } else {
                Snackbar.make(
                    view,
                    "Intreste není připojeno, nelze zahájit hru.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }


        val statisticsCardView = view.findViewById<CardView>(R.id.statistics_card)
        statisticsCardView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_nav_statistics)
        }


        val settingsCardView = view.findViewById<CardView>(R.id.settings_card)
        settingsCardView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_nav_settings)
        }

        val connectionStatusText =
            view.findViewById<AppCompatTextView>(R.id.text_connect_with_intreste)
        val addressText =
            view.findViewById<AppCompatTextView>(R.id.text_address_with_intreste)
        val info1Text = view.findViewById<AppCompatTextView>(R.id.text_info1)
        val info2Text = view.findViewById<AppCompatTextView>(R.id.text_info2)

        connectionStatusText.text = "Odpojeno"
        addressText.text = ""
        info1Text.visibility = View.GONE
        info2Text.visibility = View.GONE

        IntresteRepository.instance.subscribeConnectionStatus { status, address ->
            if (status != null) {
                addressText.text = "Adresa zařízení "+ address

                when (status) {
                    1 -> {
                        //connected
                        connectionStatusText.text = "Připojeno"
                    }
                    2 -> {
                        connectionStatusText.text = "Připojování"
                    }
                    else -> {
                        //disconnected
                        connectionStatusText.text = "Odpojeno"

                    }
                }
            }
        }

        IntresteRepository.instance.subscribeState {
            if (it != null) {
                info1Text.visibility = View.VISIBLE
                info2Text.visibility = View.VISIBLE
                if (it.intresteConnected) {
                    info1Text.text = "Připojeno k panelům (" + it.panelCount + ")"
                } else {
                    info1Text.text = "Panely nejsou připojeny"
                }
                if (it.ledPanelConnected) {
                    info2Text.text = "LED displej připojen"
                } else {
                    info2Text.text = "LED displej není připojen"
                }

            } else {
                info1Text.visibility = View.GONE
                info2Text.visibility = View.GONE
            }
        }


        val titleText = view.findViewById<AppCompatTextView>(R.id.game_title_text)
        val infoText = view.findViewById<AppCompatTextView>(R.id.game_info_text)
        val statusText = view.findViewById<AppCompatTextView>(R.id.game_status_text)
        val nameText = view.findViewById<AppCompatTextView>(R.id.game_name_text)
        infoText.visibility = View.GONE
        nameText.visibility = View.GONE
        titleText.text = "Zahájit hru"
        statusText.text = "Žádná hra vybraná"

        IntresteRepository.instance.subscribeCurrentGame {
            if (it != null) {
                infoText.visibility = View.VISIBLE
                nameText.visibility = View.VISIBLE
                titleText.text =
                    ("Hra " + it.timeout.toString() + "s")
                infoText.text =
                    (it.hits.toString() + "x zásah " + it.misses.toString() + "x vedle, " + "celkem " + it.score + " bodů")
                when (it.gameState) {
                    GameState.PREPARED.toString() -> {
                        statusText.text =
                            "Připraveno k zahájení"
                        titleText.text =
                            ("Hra čeká na dvojúder")
                    }
                    GameState.STARTING.toString() -> statusText.text = "Hra začíná"
                    GameState.PROGRESS.toString() -> statusText.text = "Hra běží"
                    GameState.FINISHED.toString() -> statusText.text = "Hra dohraná"
                }
                nameText.text = it.gameName

            } else {
                infoText.visibility = View.GONE

                nameText.visibility = View.GONE
                titleText.text = "Zahájit hru"
                statusText.text = "Žádná hra vybraná"
            }
        }
        IntresteRepository.instance.requestCurrentGame()

    }


}