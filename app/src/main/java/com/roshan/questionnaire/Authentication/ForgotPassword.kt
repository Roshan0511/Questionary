package com.roshan.questionnaire.Authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.roshan.questionnaire.Dialogs.EmailSentDialog
import com.roshan.questionnaire.databinding.FragmentForgotPasswordBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

class ForgotPassword : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.btnSend.setOnClickListener {
            if (binding.etEmail.editText!!.text.trim().isEmpty()) {
                binding.etEmail.editText!!.error = "Required!!"
            } else {
                auth!!.sendPasswordResetEmail(binding.etEmail.editText!!.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val dialog = EmailSentDialog()
                            dialog.isCancelable = false
                            dialog.show((context as FragmentActivity).supportFragmentManager, dialog.tag)
                        } else {
                            Toast.makeText(context,
                                "Something went wrong!!",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        return binding.root
    }
}