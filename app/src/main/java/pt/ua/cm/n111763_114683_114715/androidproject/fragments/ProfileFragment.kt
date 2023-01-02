package pt.ua.cm.n111763_114683_114715.androidproject.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import pt.ua.cm.n111763_114683_114715.androidproject.R
import pt.ua.cm.n111763_114683_114715.androidproject.viewmodel.UserViewModel
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentProfileBinding
import java.util.*

class ProfileFragment : Fragment(), OnClickListener {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userImage: ImageView
    private lateinit var takePhotoButton: ImageButton
    private lateinit var choosePhotoButton: ImageButton
    private lateinit var userName: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var emailText: TextView
    private lateinit var leaderboardButton: ImageButton
    private lateinit var logoutButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var locationButton: ImageButton
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        firestore = FirebaseFirestore.getInstance()

        userImage = binding.userImage
        takePhotoButton = binding.takePhotoButton
        choosePhotoButton = binding.choosePhotoButton
        userName = binding.usernameInput
        saveButton = binding.saveButton
        emailText = binding.emailText
        leaderboardButton = binding.leaderboardButton
        logoutButton = binding.logoutButton
        playButton = binding.playButton
        locationButton = binding.locationButton

        takePhotoButton.setOnClickListener(this)
        choosePhotoButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)
        leaderboardButton.setOnClickListener(this)
        logoutButton.setOnClickListener(this)
        playButton.setOnClickListener(this)
        locationButton.setOnClickListener(this)
        emailText.text = viewModel.email

        viewModel.username.observe(viewLifecycleOwner) {
            it.let {
                userName.setText(it)
            }
        }

        viewModel.photoURI.observe(viewLifecycleOwner) {
            it.let {
                Glide.with(requireContext()).load(it).into(userImage)
            }
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.savePhotoURI(it.data?.data.toString())
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val granted = it.entries.all { it.value == true }
            if(granted){
                fetchLocation()
            }
        }

        updateLocation()

        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        // Location
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        val task: Task<Location> = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener {
            if (it != null) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses: MutableList<Address> =
                    geocoder.getFromLocation(it.latitude, it.longitude, 1)
                viewModel.saveCountry(addresses[0].countryCode)

            } else {
                val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val builder = AlertDialog.Builder(requireContext())
                    with(builder)
                    {
                        setTitle("Turn On Location")
                        setMessage("You need to turn on location to set your country")
                        setPositiveButton("Turn On") { _, _ ->
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                        setNegativeButton("Cancel", null)
                        show()
                    }
                }
            }
        }
    }


    private fun checkPermissions(permissionsToCheck: Array<String>): Boolean {
        val permissionsToRequest = mutableListOf<String>()
        for (permission in permissionsToCheck) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
            return false
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.takePhotoButton -> findNavController().navigate(R.id.action_profileFragment_to_cameraFragment)
            R.id.choosePhotoButton -> choosePhoto()
            R.id.saveButton -> saveProfile()
            R.id.leaderboardButton -> findNavController().navigate(R.id.action_profileFragment_to_leaderboardFragment)
            R.id.logoutButton -> logOut()
            R.id.playButton -> play()
            R.id.locationButton -> updateLocation()
        }
    }

    private fun updateLocation() {
        if (checkPermissions(
                arrayOf(
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        ) {
            fetchLocation()
        }
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun saveProfile() {
        if (userName.text.toString().isEmpty()) {
            userName.error = "Username cannot be empty"
            userName.requestFocus()
        } else {
            viewModel.saveProfileToFirestore(userName.text.toString())
        }
    }

    private fun logOut() {
        if (Firebase.auth.currentUser != null) {
            Log.i("TAG", "Firebase Log Out")
            Firebase.auth.signOut()
        } else {
            Log.i("TAG", "Google Log Out")
            val options: GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), options)
            googleSignInClient.signOut()
        }
        viewModel.clearProfileData()
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

    private fun play() {
        findNavController().navigate(R.id.action_profileFragment_to_playFragment)
    }


}