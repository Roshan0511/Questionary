package com.roshan.questionary.Dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.roshan.questionary.R
import com.roshan.questionary.databinding.ChooseCameraOrScannerDialogBinding

class ChooseCameraOrScannerDialog : DialogFragment() {

    var binding: ChooseCameraOrScannerDialogBinding? = null

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

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ChooseCameraOrScannerDialogBinding.inflate(inflater, container, false)

        binding!!.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding!!.scanText.setOnClickListener {
            val capture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(capture, 101)
        }

        return binding!!.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == Activity.RESULT_OK){
            val bundle = data!!.extras
            val bitmap: Bitmap = bundle!!.get("data") as Bitmap

            val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
            val firebaseVision: FirebaseVision = FirebaseVision.getInstance()
            val textRecognizer: FirebaseVisionTextRecognizer = firebaseVision.onDeviceTextRecognizer
            val task: Task<FirebaseVisionText> = textRecognizer.processImage(image)

            task.addOnSuccessListener {
                // Do what you want when task is successful
                Toast.makeText(requireContext(), "Text : ${it.text}", Toast.LENGTH_LONG).show()
            }
            task.addOnFailureListener {
                Toast.makeText(requireContext(), "Error occurred!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}