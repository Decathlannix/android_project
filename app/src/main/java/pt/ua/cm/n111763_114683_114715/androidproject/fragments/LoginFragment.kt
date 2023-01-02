package pt.ua.cm.n111763_114683_114715.androidproject.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import pt.ua.cm.n111763_114683_114715.androidproject.R
import pt.ua.cm.n111763_114683_114715.androidproject.viewmodel.UserViewModel
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentLoginBinding
import timber.log.Timber

class LoginFragment : Fragment(), View.OnClickListener {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                saveLoginData(task)
            }
        }

        binding.logInGoogle.setOnClickListener(this)
        binding.createAccount.setOnClickListener(this)
        binding.logInEmailPassword.setOnClickListener(this)

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
        val options: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), options)

        val signInIntent: Intent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun logInEmailPassword() {
        val email: String = binding.emailInput.text.toString()
        val password: String = binding.passwordInput.text.toString()

        if (email.isEmpty()) {
            binding.emailInput.error = getString(R.string.email_empty)
            binding.emailInput.requestFocus()
        } else if (password.isEmpty()) {
            binding.passwordInput.error = getString(R.string.password_empty)
            binding.passwordInput.requestFocus()
        } else {
            viewModel.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        viewModel.loadProfileData(viewModel.auth.currentUser!!.providerData[0].email!!, viewModel.auth.currentUser!!.providerData[0].uid)
                        findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.email_password_login_failed), Toast.LENGTH_LONG).show()
                        Timber.e("logInEmailPassword: ${it.exception.toString()}")
                    }
                }
        }
    }

    private fun saveLoginData(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val user: GoogleSignInAccount = task.result
            viewModel.loadProfileData(user.email!!, user.id!!)
            findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
        } else {
            Toast.makeText(requireContext(),  getString(R.string.google_login_failed), Toast.LENGTH_LONG).show()
            Timber.e("saveLoginData: ${task.exception.toString()}")
        }
    }
}