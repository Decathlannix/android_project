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
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.OnClickListener
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
import pt.ua.cm.n111763_114683_114715.androidproject.R
import pt.ua.cm.n111763_114683_114715.androidproject.viewmodel.UserViewModel
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentProfileBinding
import pt.ua.cm.n111763_114683_114715.androidproject.utils.Utils.LOCATION_REQUIRE_PERMISSIONS
import timber.log.Timber
import java.util.*

class ProfileFragment : Fragment(), OnClickListener {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        setHasOptionsMenu(true)

        binding.takePhotoButton.setOnClickListener(this)
        binding.choosePhotoButton.setOnClickListener(this)
        binding.saveButton.setOnClickListener(this)
        binding.emailText.text = viewModel.email

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.savePhotoURI(it.data?.data.toString())
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { permission -> permission.value }
            if (granted) {
                fetchLocation()
            }
        }

        // Observe the viewmodel's live data to change the UI whenever changes happen
        viewModel.username.observe(viewLifecycleOwner) {
            it.let {
                binding.usernameInput.setText(it)
            }
        }
        viewModel.photoURI.observe(viewLifecycleOwner) {
            it.let {
                Glide.with(requireContext()).load(it).into(binding.userImage)
            }
        }

        // Perform initial location fetching to save user's country code
        updateLocation()

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.takePhotoButton -> findNavController().navigate(R.id.action_profileFragment_to_cameraFragment)
            R.id.choosePhotoButton -> choosePhoto()
            R.id.saveButton -> saveProfile()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.playButton -> {
                play()
                return true
            }
            R.id.updateLocationButton -> {
                updateLocation()
                return true
            }
            R.id.leaderboardButton -> {
                findNavController().navigate(R.id.action_profileFragment_to_leaderboardFragment)
                return true
            }
            R.id.aboutUsButton -> {
                findNavController().navigate(R.id.action_profileFragment_to_aboutUsFragment)
                return true
            }
            R.id.logOutButton -> {
                logOut()
                return true
            }
        }
        return false
    }

    private fun updateLocation() {
        if (checkPermissions(LOCATION_REQUIRE_PERMISSIONS)) {
            fetchLocation()
        }
    }

    private fun checkPermissions(permissionsToCheck: Array<String>): Boolean {
        val permissionsToRequest = mutableListOf<String>()
        for (permission in permissionsToCheck) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission") // All permissions are granted at this point
    private fun fetchLocation() {
        // Obtain user's last location
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val task: Task<Location> = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses: MutableList<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                // Save the country code, using the FlagKit library
                viewModel.saveCountry(addresses[0].countryCode)
            } else {
                // In case the location isn't enabled on the device, a dialog box appears
                // asking the user to enable it and sends him to the appropriate settings page
                val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val builder = AlertDialog.Builder(requireContext())
                    with(builder)
                    {
                        setTitle(getString(R.string.dialog_title))
                        setMessage(getString(R.string.dialog_message))
                        setPositiveButton(getString(R.string.dialog_positive)) { _, _ ->
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                        setNegativeButton(getString(R.string.dialog_negative), null)
                        show()
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        if (binding.usernameInput.text.toString().isEmpty()) {
            binding.usernameInput.error = getString(R.string.email_empty)
            binding.usernameInput.requestFocus()
        } else {
            viewModel.saveProfileToFirestore(binding.usernameInput.text.toString())
        }
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = getString(R.string.photo_type)
        launcher.launch(intent)
    }

    private fun logOut() {
        if (viewModel.isEmailPasswordLogged()) {
            Timber.i("(Email & password) Log Out")
            viewModel.emailPasswordSignOut()
        } else {
            Timber.i("(Google) Log Out")
            val options: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), options)
            viewModel.googleSignOut(googleSignInClient)
        }
        viewModel.clearProfileData()
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

    private fun play() {
        findNavController().navigate(R.id.action_profileFragment_to_playFragment)
    }
}