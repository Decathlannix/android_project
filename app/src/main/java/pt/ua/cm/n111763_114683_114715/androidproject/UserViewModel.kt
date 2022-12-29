package pt.ua.cm.n111763_114683_114715.androidproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel: ViewModel() {
    private lateinit var _email: String
    val email: String
        get() = _email
    private lateinit var _uid: String
    val uid: String
        get() = _uid
    private var _username = MutableLiveData("")
    val username: LiveData<String>
        get() = _username
    private var _photoURI = MutableLiveData("")
    val photoURI: LiveData<String>
        get() = _photoURI
    private lateinit var _scores: List<Int>
    val scores: List<Int>
        get() = _scores

    fun saveLoginData(email: String, uid: String) {
        _email = email
        _uid = uid
        loadProfileDataFromFirestore()
    }

    private fun loadProfileDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("users").document(_uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!task.result.exists()) {
                    val data = hashMapOf("email" to _email)
                    task.result.reference.set(data)
                } else {
                    if (!task.result.getString("name").isNullOrEmpty()) _username.value = task.result.data?.get("name").toString()
                    if (!task.result.getString("imagePath").isNullOrEmpty()) _photoURI.value = task.result.data?.get("imagePath").toString()
                }
            }
        }
    }

    fun savePhotoURI(photoURI: String) {
        _photoURI.value = photoURI
    }

    fun saveProfileToFirestore(username: String) {
        _username.value = username
        FirebaseFirestore.getInstance().collection("users")
            .document(_uid)
            .update("name", _username.value,
                "imagePath", _photoURI.value)
    }

    fun clearProfileData() {
        _username.value = ""
        _photoURI.value = ""
    }
}