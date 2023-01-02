package pt.ua.cm.n111763_114683_114715.androidproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.murgupluoglu.flagkit.FlagKit
import pt.ua.cm.n111763_114683_114715.androidproject.R
import pt.ua.cm.n111763_114683_114715.androidproject.viewmodel.UserInfo

class LeaderboardAdapter: RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userScore: TextView = itemView.findViewById(R.id.userScore)
        val countryImage: ImageView = itemView.findViewById(R.id.countryImage)
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
        holder.userName.text = holder.itemView.context.getString(R.string.leaderboard_name_email, usersLeaderboardInfo[position].name, usersLeaderboardInfo[position].email)
        holder.userScore.text = holder.itemView.context.getString(R.string.leaderboard_score, usersLeaderboardInfo[position].score)
        if (usersLeaderboardInfo[position].image == "") {
            Glide.with(holder.itemView).load(AppCompatResources.getDrawable(holder.itemView.context, R.drawable.ic_default_image)).into(holder.userImage)
        } else {
            Glide.with(holder.itemView).load(usersLeaderboardInfo[position].image).into(holder.userImage)
        }
        if (usersLeaderboardInfo[position].country == holder.itemView.context.getString(R.string.default_country)) {
            Glide.with(holder.itemView).load(AppCompatResources.getDrawable(holder.itemView.context, R.drawable.ic_default_flag)).into(holder.countryImage)
        } else {
            Glide.with(holder.itemView).load(FlagKit.getDrawable(holder.itemView.context, usersLeaderboardInfo[position].country)).into(holder.countryImage)
        }
    }

    override fun getItemCount() = usersLeaderboardInfo.size
}