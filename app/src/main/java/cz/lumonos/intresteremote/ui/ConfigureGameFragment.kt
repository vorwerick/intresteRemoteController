package cz.lumonos.intresteremote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cz.lumonos.intresteremote.R
import cz.lumonos.intresteremote.data.intreste.IntresteRepository
import cz.lumonos.intresteremote.service.ObjectGameConfig

class ConfigureGameFragment: Fragment() {

    var timout = 60
    var missesPoints = -2
    var hitPoints = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_configure_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         timout = 60
         missesPoints = -2
         hitPoints = 1


        val readyGameCardView = view.findViewById<CardView>(R.id.ready_game_card)
        readyGameCardView.setOnClickListener {
            IntresteRepository.instance.startGame(ObjectGameConfig(timout, hitPoints, missesPoints))
            findNavController().navigate(R.id.action_nav_config_game_to_nav_current_game)
        }

        val timeoutMinusButton = view.findViewById<Button>(R.id.game_config_timeout_minus_button)
        timeoutMinusButton.setOnClickListener {
            timout -= 10
            if(timout <= 10){
                timout = 10
            }
            updateTextView()

        }
        val timeoutPlusButton = view.findViewById<Button>(R.id.game_config_timeout_plus_button)
        timeoutPlusButton.setOnClickListener {
            timout += 10
            updateTextView()

        }
        val hitsMinusButton = view.findViewById<Button>(R.id.game_config_hits_minus_button)
        hitsMinusButton.setOnClickListener {
            hitPoints -= 1
            if(hitPoints <= 0){
                hitPoints = 0
            }
            updateTextView()

        }
        val hitsPlusButton = view.findViewById<Button>(R.id.game_config_hits_plus_button)
        hitsPlusButton.setOnClickListener {
            hitPoints += 1
            updateTextView()

        }
        val missesMinusButton = view.findViewById<Button>(R.id.game_config_misses_minus_button)
        missesMinusButton.setOnClickListener {
            missesPoints -= 1
            if(missesPoints <= -9){
                missesPoints = -9
            }
            updateTextView()

        }
        val missesPlusButton = view.findViewById<Button>(R.id.game_config_misses_plus_button)
        missesPlusButton.setOnClickListener {
            missesPoints += 1
            if(missesPoints >= 0){
                missesPoints = 0
            }
            updateTextView()
        }


        updateTextView()

    }

    private fun updateTextView() {
        val timeoutTextView = requireView().findViewById<AppCompatTextView>(R.id.game_config_timeout_text)
        val hitsTextView = requireView().findViewById<AppCompatTextView>(R.id.game_config_hits_text)
        val missesTextView = requireView().findViewById<AppCompatTextView>(R.id.game_config_misses_text)
        timeoutTextView.text = timout.toString()
        hitsTextView.text = hitPoints.toString()
        missesTextView.text = missesPoints.toString()
    }
}