package pt.ua.cm.n111763_114683_114715.androidproject

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userImage: ImageView
    private lateinit var takePhotoButton : ImageButton
    private lateinit var choosePhotoButton : ImageButton
    private lateinit var userName: TextInputEditText
    private lateinit var saveButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        firestore = FirebaseFirestore.getInstance()

        userImage = binding.userImage
        takePhotoButton = binding.takePhotoButton
        choosePhotoButton = binding.choosePhotoButton
        userName = binding.usernameInput
        saveButton = binding.saveButton

        takePhotoButton.setOnClickListener(this)
        choosePhotoButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)
        // loadCurrentProfile()

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.takePhotoButton -> takePhoto()
            R.id.choosePhotoButton -> choosePhoto()
            R.id.saveButton -> saveProfile()
        }
    }

    private fun takePhoto() {
        findNavController().navigate(R.id.action_profileFragment_to_cameraFragment)
    }

    private fun choosePhoto() {
    }

    private fun saveProfile() {
        val username: String = userName.text.toString()
        if (username.isEmpty()) {
            userName.error = "Username cannot be empty"
            userName.requestFocus()
        } else {
            val data = hashMapOf(
                "name" to username,
                "imagePath" to username) // MUDAR!!!
            firestore.collection("users").document(Firebase.auth.currentUser!!.uid).set(data)
        }
    }

    private fun loadCurrentProfile() {
        firestore.collection("users").document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener { document ->
            userName.setText(document.data?.get("name").toString())
            Glide.with(requireContext()).load(document.data?.get("imagePath").toString()).into(userImage)
        }
    }
}