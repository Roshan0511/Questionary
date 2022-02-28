package com.roshan.questionary.Fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.SparseArray
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.Text
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.roshan.questionary.Dialogs.ChooseCameraOrScannerDialog
import com.roshan.questionary.R
import com.roshan.questionary.databinding.FragmentImageSearchBinding
import java.lang.Exception
import java.lang.StringBuilder

class ImageSearchFragment : Fragment() {

    var binding: FragmentImageSearchBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageSearchBinding.inflate(inflater, container, false)

        binding!!.snapToSolve.setOnClickListener {
            val dialog = ChooseCameraOrScannerDialog()
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            dialog.isCancelable = false
        }

        return binding!!.root
    }
}