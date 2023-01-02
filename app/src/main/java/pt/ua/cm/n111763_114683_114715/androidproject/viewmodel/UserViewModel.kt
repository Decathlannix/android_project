package pt.ua.cm.n111763_114683_114715.androidproject.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlin.random.Random

class UserViewModel : ViewModel() {
    private lateinit var _email: String
    val email: String
        get() = _email
    private lateinit var _uid: String
    val uid: String
        get() = _uid
    private lateinit var _country: String
    val country: String
        get() = _country
    private var _username = MutableLiveData("")
    val username: LiveData<String>
        get() = _username
    private var _photoURI = MutableLiveData("")
    val photoURI: LiveData<String>
        get() = _photoURI
    private var _usersLeaderboardInfo = MutableLiveData<MutableList<UserInfo>>()
    val usersLeaderboardInfo: LiveData<MutableList<UserInfo>>
        get() = _usersLeaderboardInfo

    fun saveLoginData(email: String, uid: String) {
        _email = email
        _uid = uid
        loadProfileDataFromFirestore()
    }

    private fun loadProfileDataFromFirestore() {
        FirebaseFirestore.getInstance().collection("users").document(_uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (!task.result.exists()) {
                        val data = hashMapOf(
                            "email" to _email,
                            "score" to Random.nextInt(1, 21).toString()
                        )
                        task.result.reference.set(data)
                    } else {
                        if (!task.result.getString("name").isNullOrBlank())
                            _username.value = task.result.getString("name")
                    }
                }
            }

        val localFile = File.createTempFile("avatar", ".jpg")

        FirebaseStorage.getInstance().reference.child("images/${_uid}/avatar.jpg")
            .getFile(localFile)
            .addOnSuccessListener {
                _photoURI.value = Uri.fromFile(localFile).toString()
            }
    }

    fun savePhotoURI(photoURI: String) {
        _photoURI.value = photoURI
    }

    fun saveCountry(country: String) {
        _country = country

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(_uid)
            .update("country", _country)
    }

    fun saveProfileToFirestore(username: String) {
        _username.value = username
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(_uid)
            .update("name", _username.value)


        val picturePathRef =
            FirebaseStorage.getInstance().reference.child("images/${_uid}/avatar.jpg")
        picturePathRef.putFile(_photoURI.value!!.toUri())
    }

    fun loadLeaderboardInfoFromFirebase() {
        val auxList = mutableListOf<UserInfo>()

        fetchFirestoreProfileData(auxList)
    }

    private fun fetchFirestoreProfileData(auxList: MutableList<UserInfo>) {
        FirebaseFirestore.getInstance().collection("users").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (!document.getString("score").isNullOrBlank()) {
                        auxList.add(
                            UserInfo(
                                document.id,
                                document.getString("name")!!,
                                document.getString("email")!!,
                                document.getString("score")!!.toInt(),
                                document.getString("country")!!
                            )
                        )
                    }
                }
                fetchFirebaseStorageProfileData(auxList)
            }
    }

    private fun fetchFirebaseStorageProfileData(auxList: MutableList<UserInfo>) {
        for (user in auxList) {
            val localFile = File.createTempFile("avatar", ".jpg")
            FirebaseStorage.getInstance().reference.child("images/${user.uid}/avatar.jpg")
                .getFile(localFile)
                .addOnSuccessListener {
                    user.setImagePath(Uri.fromFile(localFile).toString())
                    Log.i(
                        "fetchStorage",
                        "${user.name}, ${user.email}, ${user.uid}, ${user.score}, ${user.image}"
                    )
                    _usersLeaderboardInfo.value = auxList
                }
        }
    }

    fun clearProfileData() {
        _username.value = ""
        _photoURI.value = ""
    }
}