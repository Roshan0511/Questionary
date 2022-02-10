package com.roshan.questionary.Dialogs

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.roshan.questionary.Activities.MainActivity
import com.roshan.questionary.Authentication.LoginActivity
import com.roshan.questionary.R
import com.roshan.questionary.databinding.LogoutDialogBinding

class LogOutDialog : DialogFragment() {
    var binding: LogoutDialogBinding ?= null
    var auth: FirebaseAuth ?= null

    override fun onStart() {
        super.onStart()

        if (dialog!!.window != null){
            dialog!!.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            dialog!!.window!!.setGravity(Gravity.CENTER)
            dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogoutDialogBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding!!.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding!!.logOutBtn.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            auth!!.signOut()
            startActivity(intent)
            requireActivity().finish()
            dismiss()
        }

        return binding!!.root
    }
}