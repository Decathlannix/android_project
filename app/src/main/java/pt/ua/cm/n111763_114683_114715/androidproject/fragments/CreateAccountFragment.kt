package pt.ua.cm.n111763_114683_114715.androidproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import pt.ua.cm.n111763_114683_114715.androidproject.R
import pt.ua.cm.n111763_114683_114715.androidproject.viewmodel.UserViewModel
import pt.ua.cm.n111763_114683_114715.androidproject.databinding.FragmentCreateAccountBinding
import timber.log.Timber

class CreateAccountFragment : Fragment(), View.OnClickListener {

    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentCreateAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_account, container, false)

        binding.createAccount.setOnClickListener(this)
        binding.cancel.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.createAccount -> createAccount()
            R.id.cancel -> findNavController().navigate(R.id.action_createAccountFragment_to_loginFragment)
        }
    }

    private fun createAccount() {
        val email: String = binding.emailInput.text.toString()
        val password: String = binding.passwordInput.text.toString()

        if (email.isEmpty()) {
            binding.emailInput.error =  getString(R.string.email_empty)
            binding.emailInput.requestFocus()
        } else if (password.isEmpty()) {
            binding.passwordInput.error =  getString(R.string.password_empty)
            binding.passwordInput.requestFocus()
        } else {
            viewModel.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        viewModel.loadProfileData(viewModel.auth.currentUser!!.providerData[0].email!!, viewModel.auth.currentUser!!.providerData[0].uid)
                        findNavController().navigate(R.id.action_createAccountFragment_to_profileFragment)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.account_creation_failed), Toast.LENGTH_LONG).show()
                        Timber.e("createAccount: ${it.exception.toString()}")
                    }
                }
        }
    }
}