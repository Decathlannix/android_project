package pt.ua.cm.n111763_114683_114715.androidproject

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentLoginBinding

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var logInGoogleButton: SignInButton
    private lateinit var logInEmailPasswordButton: Button
    private lateinit var emailInputText: TextInputEditText
    private lateinit var passwordInputText: TextInputEditText
    private lateinit var createAccountButton: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        auth = Firebase.auth

        val options: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), options)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleSignInResult(task)
            }
        }

        logInGoogleButton = binding.logInGoogle
        logInEmailPasswordButton = binding.logInEmailPassword
        createAccountButton = binding.createAccount
        emailInputText = binding.emailInput
        passwordInputText = binding.passwordInput

        logInGoogleButton.setOnClickListener(this)
        createAccountButton.setOnClickListener(this)
        logInEmailPasswordButton.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.logInGoogle -> logInGoogle()
            R.id.logInEmailPassword -> logInEmailPassword()
            R.id.createAccount -> findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }
    }

    private fun logInGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val user: GoogleSignInAccount = task.result
            updateUIGoogle(user)
            findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
        } else {
            Log.i(TAG, "handleSignInResult failure: ${task.exception.toString()}")
        }
    }

    private fun logInEmailPassword() {
        val email: String = emailInputText.text.toString()
        val password: String = passwordInputText.text.toString()
        if (email.isEmpty()) {
            emailInputText.error = "Email cannot be empty"
            emailInputText.requestFocus()
        } else if (password.isEmpty()) {
            passwordInputText.error = "Password cannot be empty"
            passwordInputText.requestFocus()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        updateUI(auth.currentUser!!)
                        emailInputText.text?.clear()
                        passwordInputText.text?.clear()
                        findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                    } else {
                        Log.i(TAG, "createUserWithEmailandPassword failure: ${it.exception.toString()}")
                    }
                }
        }
    }

    private fun updateUIGoogle(user: GoogleSignInAccount) {
        Toast.makeText(requireActivity(), "UID: ${user.id}, Email: ${user.email}, Display name: ${user.displayName}, Photo URL: ${user.photoUrl}", Toast.LENGTH_LONG).show()
    }

    private fun updateUI(user: FirebaseUser) {
        Toast.makeText(requireActivity(), "UID: ${user.providerData[0].uid}, Email: ${user.providerData[0].email}, Display name: ${user.providerData[0].displayName}, Photo URL: ${user.providerData[0].photoUrl}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
        googleSignInClient.signOut()
    }

}