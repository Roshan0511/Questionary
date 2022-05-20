package com.roshan.questionnaire.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.roshan.questionnaire.Adapters.QuestionsAdapter
import com.roshan.questionnaire.Dialogs.ShowingProfileDialog
import com.roshan.questionnaire.Models.PostModel
import com.roshan.questionnaire.Models.UserModel
import com.roshan.questionnaire.R
import com.roshan.questionnaire.databinding.FragmentUserProfileDetailsBinding

class UserProfileDetails(val userId: String) : Fragment() {

    private lateinit var binding: FragmentUserProfileDetailsBinding
    private var database: FirebaseDatabase? = null
    private var pd: ProgressDialog? = null
    private var list: ArrayList<PostModel> = ArrayList()
    private var mAdapter: QuestionsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserProfileDetailsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        initView()
        pd!!.show()

        binding.profileImage.setOnClickListener {
            val dialog = ShowingProfileDialog(userId)
            dialog.isCancelable = false
            dialog.show((requireContext() as FragmentActivity).supportFragmentManager, dialog.tag)
        }

        binding.backBtn.setOnClickListener { pressBackButton()}

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (list.size != 0) {
            list.clear()
            setDataForPostAdapter()
        } else {
            setDataForPostAdapter()
        }

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                pressBackButton()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun initView() {
        database = FirebaseDatabase.getInstance()
        pd = ProgressDialog(requireContext(), R.style.AppCompatAlertDialogStyle)
        pd!!.setMessage("Please Wait...")
        pd!!.setCancelable(false)

        setUserProfileData()
    }

    private fun setUserProfileData(){
        database!!.reference
            .child("Users")
            .child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (pd!!.isShowing){
                        pd!!.dismiss()
                    }
                    if (snapshot.exists()) {
                        val user: UserModel? = snapshot.getValue(UserModel::class.java)

                        context?.let { it1 ->
                            Glide.with(it1)
                                .load(user!!.profilePic)
                                .placeholder(R.drawable.man)
                                .into(binding.profileImage)
                        }

                        binding.userName.text = user!!.name
                        binding.userEmail.text = user.email
                        binding.description.text = user.summary

                        if (binding.description.text.equals("")){
                            binding.description.text = "Empty!!"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (pd!!.isShowing){
                        pd!!.dismiss()
                    }
                    Toast.makeText(context, "Error ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun setDataForPostAdapter() {
        pd!!.show()
        database!!.reference
            .child("posts")
            .addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (pd!!.isShowing) {
                    pd!!.dismiss()
                }
                list.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val post = dataSnapshot.getValue(PostModel::class.java)!!
                        post.postId = dataSnapshot.key
                        if (post.userId == userId) {
                            list.add(post)
                        }
                    }
                    mAdapter = QuestionsAdapter(context, list)
                    binding.rvUserPost.adapter = mAdapter
                    binding.rvUserPost.layoutManager = LinearLayoutManager(context)

                    if (list.size == 0){
                        binding.tvQuestionAsked.visibility = View.GONE
                    }
                }
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                if (pd!!.isShowing) {
                    pd!!.dismiss()
                }
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun pressBackButton() {
        val fragment = SearchFragment()
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.linearLayout, fragment)
        transaction.commit()
    }
}