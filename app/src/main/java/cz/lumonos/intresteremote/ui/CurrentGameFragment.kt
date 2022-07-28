package cz.lumonos.intresteremote.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cz.lumonos.intresteremote.R
import cz.lumonos.intresteremote.data.intreste.IntresteRepository
import cz.lumonos.intresteremote.service.CurrentGame
import cz.lumonos.intresteremote.service.GameState
import cz.lumonos.intresteremote.service.log.L
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CurrentGameFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_current_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        IntresteRepository.instance.subscribeCurrentGame {

            L.info("Observable " + it?.gameName)
            if (it == null) {
                //cancel game
            }
            val currentGame: CurrentGame = it!!

            val gameStateTextView =
                view.findViewById<AppCompatTextView>(R.id.game_status_text_view)
            val gameTimeoutTextView =
                view.findViewById<AppCompatTextView>(R.id.game_timeout_text_view)
            val gameScoreTextView =
                view.findViewById<AppCompatTextView>(R.id.game_score_text_view)
            val gameLastStateTextView =
                view.findViewById<AppCompatTextView>(R.id.game_last_state_text_view)

            when (currentGame.gameState) {
                GameState.PREPARED.toString() -> gameStateTextView.text =
                    "Připraveno k zahájení"
                GameState.STARTING.toString() -> gameStateTextView.text = "Hra začíná"
                GameState.PROGRESS.toString() -> gameStateTextView.text = "Hra probíhá"
                GameState.FINISHED.toString() -> gameStateTextView.text = "Hra dohraná"
            }

            gameTimeoutTextView.text = "Zbývající čas ${currentGame.timeout}s"
            gameScoreTextView.text = "Skóre ${currentGame.score}"

            val builder = StringBuilder()


            if (it.hitPanelId != -1 && it.hitPanelIndex != -1) {
                builder.append("Zasažený panel ID").append(it.hitPanelId).append(":").append("\n")
                    .append("Pozice seřazení ").append(it.hitPanelIndex)
            }

            gameLastStateTextView.text = builder.toString()
        }

        val cancelGameCardView = view.findViewById<CardView>(R.id.cancel_game_card)
        cancelGameCardView.setOnClickListener {
            showCancelDialog()
        }

        val restartGameCardView = view.findViewById<CardView>(R.id.restart_game_card)
        restartGameCardView.setOnClickListener {
            showRestartDialog()
        }


    }

    fun showRestartDialog() {
        AlertDialog.Builder(context)
            .setTitle("Restart")
            .setMessage("Opravdu chcete hru restartovat?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Ano",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                   // IntresteRepository.instance.startGame()

                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton("Ne") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    fun showCancelDialog() {
        AlertDialog.Builder(context)
            .setTitle("Ukončit")
            .setMessage("Opravdu ukončit hru?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Ano",
                DialogInterface.OnClickListener { dialog, which ->
                    GlobalScope.launch {
                        delay(3000)
                        IntresteRepository.instance.requestCurrentGame()
                    }
                    IntresteRepository.instance.cancelGame()
                    dialog.cancel()
                    findNavController().navigate(R.id.action_nav_current_game_to_nav_menu)
                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton("Ne") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }
}