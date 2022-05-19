package com.roshan.questionnaire.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.roshan.questionnaire.Activities.CommentActivity
import com.roshan.questionnaire.Models.PostModel
import com.roshan.questionnaire.Models.UserModel
import com.roshan.questionnaire.R
import com.roshan.questionnaire.databinding.HomepageQuestionViewBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class QuestionsAdapter(private val context: Context?, private var list: List<PostModel>) :
    RecyclerView.Adapter<QuestionsAdapter.MyViewHolder>() {
    constructor() : this(null, emptyList())

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
        holder.binding.category.text = question.subject

        if (question.questionImage.isEmpty()){
            holder.binding.imageChecker.visibility = View.GONE
        } else {
            holder.binding.imageChecker.visibility = View.VISIBLE
        }

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

        holder.binding.imageChecker.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", question.postId)
            intent.putExtra("userId", question.userId)
            context!!.startActivity(intent)
        }

        holder.binding.giveAnswer.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", question.postId)
            intent.putExtra("userId", question.userId)
            context!!.startActivity(intent)
        }

        auth.uid?.let {
            database.reference
                .child("bookmarks")
                .child(it)
                .child(question.postId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            holder.binding.bookmark.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_bookmark_added_24,
                                0, 0, 0)
                        }

                        else {
                            holder.binding.bookmark.setOnClickListener {
                                database.reference
                                    .child("bookmarks")
                                    .child(auth.uid!!)
                                    .child(question.postId)
                                    .setValue(true)
                                    .addOnSuccessListener {
                                        holder.binding.bookmark.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_bookmark_added_24,
                                            0, 0, 0)

                                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error : ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(val binding: HomepageQuestionViewBinding?) : RecyclerView.ViewHolder(binding!!.root)


    @SuppressLint("NotifyDataSetChanged")
    fun filteredList(filterList: ArrayList<PostModel>){
        list = filterList
        notifyDataSetChanged()
    }
}