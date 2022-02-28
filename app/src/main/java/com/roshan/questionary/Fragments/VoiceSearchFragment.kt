package com.roshan.questionary.Fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.roshan.questionary.databinding.FragmentVoiceSearchBinding
import java.util.*
import kotlin.collections.ArrayList

class VoiceSearchFragment : Fragment() {

    private var binding: FragmentVoiceSearchBinding? = null
    private val REQUEST_CODE_SPEECH_INPUT: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVoiceSearchBinding.inflate(inflater, container, false)

        binding!!.recordBtn.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception){
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        return binding!!.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK){
            if (data != null){
                val result: ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                binding!!.recordText.text = result[0]
                binding!!.recordText.visibility = View.VISIBLE
                binding!!.uploadBtn.visibility = View.VISIBLE
            }
        }
    }
}