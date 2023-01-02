package pt.ua.cm.n111763_114683_114715.androidproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import pt.ua.cm.n111763_114683_114715.androidproject.adapters.LeaderboardAdapter
import pt.ua.cm.n111763_114683_114715.androidproject.R
import pt.ua.cm.n111763_114683_114715.androidproject.viewmodel.UserViewModel
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentLeaderboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_leaderboard, container, false)

        val adapter = LeaderboardAdapter()

        binding.leaderboardList.adapter = adapter

        viewModel.usersLeaderboardInfo.observe(viewLifecycleOwner) {
            it.let {
                adapter.usersLeaderboardInfo = it
                // Organize the players from the highest score to the lowest
                adapter.usersLeaderboardInfo.sortByDescending { user -> user.score }
            }
        }

        viewModel.loadLeaderboardInfoFromFirebase()

        return binding.root
    }
}