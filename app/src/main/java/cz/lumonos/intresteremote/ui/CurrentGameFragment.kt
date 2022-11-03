package cz.lumonos.intresteremote.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cz.lumonos.intresteremote.R
import cz.lumonos.intresteremote.data.intreste.IntresteRepository
import cz.lumonos.intresteremote.service.CurrentGame
import cz.lumonos.intresteremote.service.GameState
import cz.lumonos.intresteremote.service.ObjectGameConfig
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

            val gameName = view.findViewById<AppCompatTextView>(R.id.game_status_name)
            val gameHitCount =  view.findViewById<AppCompatTextView>(R.id.game_hit_count_text)
            val gameMissCount =  view.findViewById<AppCompatTextView>(R.id.game_misses_count_text)

            val gameHitPointsCount =  view.findViewById<AppCompatTextView>(R.id.game_hit_points_text)

            val gameMissPointsCount =  view.findViewById<AppCompatTextView>(R.id.game_miss_points_text)

            val gameStateTextView =
                view.findViewById<AppCompatTextView>(R.id.game_status_text_view)
            val gameTimeoutTextView =
                view.findViewById<AppCompatTextView>(R.id.game_timeout_text_view)
            val gameScoreTextView =
                view.findViewById<AppCompatTextView>(R.id.game_score_text_view)
            val gameLastStateTextView =
                view.findViewById<AppCompatTextView>(R.id.game_last_state_text_view)

            gameName.text = "Jméno hry: " + currentGame.gameName
            when (currentGame.gameState) {
                GameState.PREPARED.toString() -> gameStateTextView.text =
                    "Stav: Připraveno k zahájení"
                GameState.STARTING.toString() -> gameStateTextView.text = "Stav: Hra začíná"
                GameState.PROGRESS.toString() -> gameStateTextView.text = "Stav: Hra probíhá"
                GameState.FINISHED.toString() -> gameStateTextView.text = "Stav: Hra dohraná"
            }
            gameHitCount.text = "Zásahů: " + currentGame.hits.toString()
            gameMissCount.text = "Netrefení: " + currentGame.misses.toString()
            gameHitPointsCount.text = "Bodů za zásah: " + currentGame.hitPoints.toString()
            gameMissPointsCount.text = "Bodů za netrefení: " + currentGame.missesPoints.toString()
            gameTimeoutTextView.text = "Zbývající čas: ${currentGame.timeout}s"
            gameScoreTextView.text = "Skóre: ${currentGame.score}"

            val builder = StringBuilder()


            if (it.hitPanelId != -1 && it.hitPanelIndex != -1) {
                builder.append("Poslední zasažený panel č. ").append(it.hitPanelIndex)
            }

            gameLastStateTextView.text = builder.toString()
        }

        val restartGameCardView = view.findViewById<CardView>(R.id.restart_game_card)
        restartGameCardView.setOnClickListener {
            showRestartDialog()
        }
        val newGameCardView = view.findViewById<CardView>(R.id.new_game_card)
        newGameCardView.setOnClickListener {
            showNewGameDialog()
        }


    }

    private fun showNewGameDialog() {
        val builderSingle = AlertDialog.Builder(context)
        builderSingle.setTitle("Vyberte hru")

        val arrayAdapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_item )
        arrayAdapter.add("Zasáhni co nejvíc")

        builderSingle.setNegativeButton(
            "Zrušit"
        ) { dialog, which -> dialog.dismiss() }

        builderSingle.setAdapter(
            arrayAdapter
        ) { dialog, which ->
            val builderInner = ConfigureGameFragment()
            builderInner.show(childFragmentManager, builderInner.tag)
        }
        builderSingle.show()
    }

    fun showRestartDialog() {
        AlertDialog.Builder(context)
            .setTitle("Restart")
            .setMessage("Opravdu chcete hru restartovat?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Ano",
                DialogInterface.OnClickListener { dialog, which ->
                    IntresteRepository.instance.restartGame()
                    dialog.cancel()


                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton("Ne") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }


}