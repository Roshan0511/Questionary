package com.roshan.questionary.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.roshan.questionary.Activities.CommentActivity
import com.roshan.questionary.Models.PostModel
import com.roshan.questionary.Models.UserModel
import com.roshan.questionary.R
import com.roshan.questionary.databinding.HomepageQuestionViewBinding
import java.text.SimpleDateFormat
import java.util.*

class QuestionsAdapter(private val context: Context?, private val list: List<PostModel>) :
    RecyclerView.Adapter<QuestionsAdapter.MyViewHolder>() {

    private val database = FirebaseDatabase.getInstance()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): QuestionsAdapter.MyViewHolder {
        val binding = HomepageQuestionViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: QuestionsAdapter.MyViewHolder, position: Int) {
        val question: PostModel = list[position]

        holder.binding!!.question.text = question.questionTxt

        database.reference
            .child("Users")
            .child(question.userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                        if (context != null){
                            Glide.with(context.applicationContext)
                                .load(userModel!!.profilePic)
                                .placeholder(R.drawable.man)
                                .into(holder.binding.userProfilePic)
                        }
                    }
                    else{
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error : ${error.message}", Toast.LENGTH_SHORT).show()
                }

            })

        holder.binding.time.text = SimpleDateFormat("d MMM, h:mm aaa").format(Date((question.time.toString() + "").toLong()))

        holder.binding.giveAnswer.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", question.postId)
            intent.putExtra("userId", question.userId)
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(val binding: HomepageQuestionViewBinding?) : RecyclerView.ViewHolder(binding!!.root)
}