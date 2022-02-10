package com.roshan.questionary.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.roshan.questionary.Dialogs.ShowingProfileDialog
import com.roshan.questionary.Models.UserModel
import com.roshan.questionary.R
import com.roshan.questionary.databinding.SuggestionsViewRvBinding

class SuggestionsAdapter(val context: Context, var list: ArrayList<UserModel>) : RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

     val auth = FirebaseAuth.getInstance()
     val database = FirebaseDatabase.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SuggestionsViewRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userModel = list[position]

        holder.binding.name.text = userModel.name

        Glide.with(context)
            .load(userModel.profilePic)
            .placeholder(R.drawable.placeholder)
            .into(holder.binding.profilePicSearch)

        holder.binding.profilePicSearch.setOnClickListener {
            val dialog = ShowingProfileDialog(userModel.userId)
            dialog.show((context as FragmentActivity).supportFragmentManager, dialog.tag)
            dialog.isCancelable = false
        }

        holder.binding.addFriend.setOnClickListener {
            Toast.makeText(context, "Clicked on position " + position+1, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: SuggestionsViewRvBinding) : RecyclerView.ViewHolder(binding.root)
}