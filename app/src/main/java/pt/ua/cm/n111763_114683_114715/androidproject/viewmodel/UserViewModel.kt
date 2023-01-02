package pt.ua.cm.n111763_114683_114715.androidproject.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlin.random.Random

class UserViewModel : ViewModel() {
    private var _auth = Firebase.auth
    val auth: FirebaseAuth
        get() = _auth

    private var _username = MutableLiveData("")
    val username: LiveData<String>
        get() = _username

    private lateinit var _email: String
    val email: String
        get() = _email

    private lateinit var _uid: String

    private var _photoURI = MutableLiveData("")
    val photoURI: LiveData<String>
        get() = _photoURI

    private lateinit var _country: String

    private var _usersLeaderboardInfo = MutableLiveData<MutableList<UserInfo>>()
    val usersLeaderboardInfo: LiveData<MutableList<UserInfo>>
        get() = _usersLeaderboardInfo

    fun loadProfileData(email: String, uid: String) {
        _email = email
        _uid = uid

        // Gets the username saved in Firestore
        // User's first time (no document exists): creates one with his email, a default username and no country
        // User has been in the app (document exists): retrieves the saved country and username
        FirebaseFirestore.getInstance().collection("users").document(_uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (!task.result.exists()) {
                        val data = hashMapOf(
                            "country" to "N/A",
                            "email" to _email,
                            "name" to _email,
                            "score" to Random.nextInt(1, 21).toString() // RETIRAR!!
                        )
                        task.result.reference.set(data)
                        _country = "N/A"
                        _username.value = _email
                    } else {
                        _country = task.result.getString("country")!!
                        _username.value = task.result.getString("name")
                    }
                }
            }

        // Gets the image saved in Storage
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


        val picturePathRef = FirebaseStorage.getInstance().reference.child("images/${_uid}/avatar.jpg")
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
                                document.getString("country")!!,
                                ""
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
                    user.setImage(Uri.fromFile(localFile).toString())
                    _usersLeaderboardInfo.value = auxList
                }
        }
    }

    fun isEmailPasswordLogged(): Boolean = auth.currentUser != null

    fun emailPasswordSignOut() {
        auth.signOut()
    }

    fun googleSignOut(googleSignInClient: GoogleSignInClient) {
        googleSignInClient.signOut()
    }

    fun clearProfileData() {
        _username.value = ""
        _photoURI.value = ""
    }
}