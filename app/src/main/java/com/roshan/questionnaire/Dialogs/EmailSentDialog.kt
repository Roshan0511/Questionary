package com.roshan.questionnaire.Dialogs

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.roshan.questionnaire.Authentication.LoginActivity
import com.roshan.questionnaire.R
import com.roshan.questionnaire.databinding.EmailSentDialogBinding
import com.roshan.questionnaire.databinding.LogoutDialogBinding

class EmailSentDialog : DialogFragment() {
    var binding: EmailSentDialogBinding?= null
    var auth: FirebaseAuth?= null

    override fun onStart() {
        super.onStart()

        if (dialog!!.window != null){
            dialog!!.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            dialog!!.window!!.setGravity(Gravity.CENTER)
            dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
            dialog!!.window!!.setWindowAnimations(R.style.DialogAnimation)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EmailSentDialogBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding!!.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding!!.btnOk.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            dismiss()
        }

        return binding!!.root
    }
}