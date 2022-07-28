package cz.lumonos.intresteremote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cz.lumonos.intresteremote.R

class NewGameFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val game1CardView = view.findViewById<CardView>(R.id.game_1_card)
        game1CardView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_new_game_to_nav_config_game)
        }

    }
}