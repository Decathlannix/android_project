package pt.ua.cm.n111763_114683_114715.androidproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clears any downloaded files in the app's "cache" folder
        // This happens whenever the user enters the "Leaderboard" page
        this.cacheDir.deleteRecursively()
    }
}