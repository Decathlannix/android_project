package pt.ua.cm.n111763_114683_114715.androidproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentLeaderboardBinding
    private lateinit var leaderboardList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_leaderboard, container, false)
        val adapter = LeaderboardAdapter()

        binding.leaderboardList.adapter = adapter

        viewModel.usersLeaderboardInfo.observe(viewLifecycleOwner) {
            it.let {
                adapter.usersLeaderboardInfo = it
                adapter.usersLeaderboardInfo.sortByDescending { user -> user.score }
            }
        }

        viewModel.loadLeaderboardInfoFromFirebase()

        return binding.root
    }
}