package com.roshan.questionnaire.Activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.roshan.questionnaire.Dialogs.ShowingImageDialog
import com.roshan.questionnaire.Models.AnswerModel
import com.roshan.questionnaire.Models.PostModel
import com.roshan.questionnaire.R
import com.roshan.questionnaire.databinding.ActivityQuestionCommentBinding
import java.util.*

class QuestionCommentActivity : AppCompatActivity() {

    var binding: ActivityQuestionCommentBinding?= null
    lateinit var postId: String
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionCommentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        postId = intent.getStringExtra("postId")!!

        setData()

        binding!!.questionImg.setOnClickListener {
            val dialog = ShowingImageDialog(postId)
            dialog.show(supportFragmentManager, dialog.tag)
            dialog.isCancelable = false
        }

        binding!!.answerBtn.setOnClickListener {
            binding!!.answerBtn.isClickable = false
            if (binding!!.answerEt.text.toString().trim().isEmpty()){
                if (binding!!.answerEt.text.toString().trim().isEmpty()){
                    binding!!.answerEt.error = "Required"
                }
                binding!!.answerBtn.isClickable = true
            } else {
                setAnswer()
            }
        }

        binding!!.backBtn.setOnClickListener {
            back()
        }
    }


    private fun setData()
    {
        database!!.reference.child("posts")
            .child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val post: PostModel ?= snapshot.getValue(PostModel::class.java)

                        binding!!.questionTxt.text = post!!.questionTxt
                        binding!!.subjectToolbar.text = post.subject
                        if (post.questionImage.isEmpty()){
                            binding!!.questionImg.visibility = View.GONE
                        } else {
                            binding!!.questionImg.visibility = View.VISIBLE
                            Glide.with(applicationContext)
                                .load(post.questionImage)
                                .placeholder(R.drawable.placeholder)
                                .into(binding!!.questionImg)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@QuestionCommentActivity, "Error : $error", Toast.LENGTH_SHORT).show()
                }

            })
    }


    private fun setAnswer(){

        dialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
        dialog!!.setMessage("Please wait...")
        dialog!!.setCancelable(false)
        dialog!!.show()

        val answer = AnswerModel()
        answer.answerText = binding!!.answerEt.text.toString()
        answer.explanationText = binding!!.explanationEt.text.toString()
        answer.answerAt = Date().time
        answer.answerBy = auth!!.uid.toString()

        database!!.reference.child("posts")
            .child(postId)
            .child("answers")
            .push()
            .setValue(answer)
            .addOnSuccessListener {
                database!!.reference.child("posts")
                    .child(postId)
                    .child("answerCount")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var answerCount = 0
                            if (snapshot.exists()){
                                val result = snapshot.value.toString()
                                answerCount = result.toInt()
                            }
                            database!!.reference.child("posts")
                                .child(postId)
                                .child("answerCount")
                                .setValue(answerCount+1)
                                .addOnSuccessListener {
                                    binding!!.answerEt.setText("")
                                    binding!!.explanationEt.setText("")

                                    binding!!.answerBtn.isClickable = true
                                    dialog!!.dismiss()
                                    finish()
                                }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            binding!!.answerBtn.isClickable = true
                            dialog!!.dismiss()
                            Toast.makeText(this@QuestionCommentActivity, "Error : $error", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
    }


    private fun back(){
        onBackPressed()
    }
}