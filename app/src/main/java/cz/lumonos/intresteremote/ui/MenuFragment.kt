package cz.lumonos.intresteremote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cz.lumonos.intresteremote.R
import cz.lumonos.intresteremote.data.intreste.IntresteRepository

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

        val newGameCardView = view.findViewById<CardView>(R.id.new_game_card)
        newGameCardView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_nav_new_game)
        }

        val currentGameCardView = view.findViewById<CardView>(R.id.current_game_card)
        currentGameCardView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_nav_current_game)
        }

        val sortPanelsCardView = view.findViewById<CardView>(R.id.sort_panels_card)
        sortPanelsCardView.setOnClickListener {
            IntresteRepository.instance.sortPanels()
        }

        val statisticsCardView = view.findViewById<CardView>(R.id.statistics_card)
        statisticsCardView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_nav_statistics)
        }

        val gameStoreCardView = view.findViewById<CardView>(R.id.game_store_card)
        gameStoreCardView.setOnClickListener {
            Toast.makeText(requireContext(), "Todo", Toast.LENGTH_SHORT).show()
        }

        val settingsCardView = view.findViewById<CardView>(R.id.settings_card)
        settingsCardView.setOnClickListener {
            Toast.makeText(requireContext(), "Todo", Toast.LENGTH_SHORT).show()
        }

        val accountAndLicenceCardView = view.findViewById<CardView>(R.id.account_and_licence_card)
        accountAndLicenceCardView.setOnClickListener {
            Toast.makeText(requireContext(), "Todo", Toast.LENGTH_SHORT).show()
        }

        IntresteRepository.instance.subscribeCurrentGame {
            //changeCurrentGameCardViewVisibility(currentGameCardView, it != null)
        }
        IntresteRepository.instance.requestCurrentGame()
        changeCurrentGameCardViewVisibility(currentGameCardView, false)
    }

    private fun changeCurrentGameCardViewVisibility(currentGameCardView: CardView, show: Boolean) {
        if (show) {
            currentGameCardView.visibility = View.VISIBLE
        } else {
            currentGameCardView.visibility = View.GONE
        }
    }
}