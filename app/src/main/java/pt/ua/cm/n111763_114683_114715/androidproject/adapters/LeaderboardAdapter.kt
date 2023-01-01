package pt.ua.cm.n111763_114683_114715.androidproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.ua.cm.n111763_114683_114715.androidproject.R
import pt.ua.cm.n111763_114683_114715.androidproject.viewmodel.UserInfo

class LeaderboardAdapter: RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userScore: TextView = itemView.findViewById(R.id.userScore)
    }

    var usersLeaderboardInfo = mutableListOf<UserInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.leaderboard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = "${usersLeaderboardInfo[position].name} (${usersLeaderboardInfo[position].email})"
        Glide.with(holder.itemView).load(usersLeaderboardInfo[position].image).into(holder.userImage)
        holder.userScore.text = "Highest score: ${usersLeaderboardInfo[position].score}"
    }

    override fun getItemCount() = usersLeaderboardInfo.size
}