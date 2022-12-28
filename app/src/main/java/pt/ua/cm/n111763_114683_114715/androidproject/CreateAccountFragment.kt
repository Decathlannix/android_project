package pt.ua.cm.n111763_114683_114715.androidproject

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentCreateAccountBinding

class CreateAccountFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentCreateAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var emailInputText: TextInputEditText
    private lateinit var passwordInputText: TextInputEditText
    private lateinit var createAccountButton: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_account, container, false)
        auth = Firebase.auth

        createAccountButton = binding.createAccount
        emailInputText = binding.emailInput
        passwordInputText = binding.passwordInput

        createAccountButton.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.createAccount -> createAccount()
        }
    }

    private fun createAccount() {
        val email: String = emailInputText.text.toString()
        val password: String = passwordInputText.text.toString()
        if (email.isEmpty()) {
            emailInputText.error = "Email cannot be empty"
            emailInputText.requestFocus()
        } else if (password.isEmpty()) {
            passwordInputText.error = "Password cannot be empty"
            passwordInputText.requestFocus()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        // Create account success
                        updateUI(auth.currentUser!!)
                        findNavController().navigate(R.id.action_createAccountFragment_to_profileFragment)
                    } else {
                        Log.i(ContentValues.TAG, "createUserWithEmailandPassword failure", it.exception)
                    }
                }
        }
    }

    private fun updateUI(user: FirebaseUser) {
        Toast.makeText(requireActivity(), "Provider: ${user.providerData[0].providerId}, Email: ${user.providerData[0].email}, UID: ${user.providerData[0].uid}, Display name: ${user.providerData[0].displayName}, Photo URL: ${user.providerData[0].photoUrl}", Toast.LENGTH_LONG).show()
    }
}