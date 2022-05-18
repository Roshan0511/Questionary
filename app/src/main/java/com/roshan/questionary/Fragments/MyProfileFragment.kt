package com.roshan.questionary.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.roshan.questionary.Activities.EditProfileActivity
import com.roshan.questionary.Dialogs.LogOutDialog
import com.roshan.questionary.Dialogs.ShowingProfileDialog
import com.roshan.questionary.Models.UserModel
import com.roshan.questionary.R
import com.roshan.questionary.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {

    private var binding : FragmentMyProfileBinding? = null
    private var auth: FirebaseAuth? = null
    private var databse: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var pd: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        initView()

        pd!!.show()

        setProfileData()

        binding!!.profileImage.setOnClickListener {
            val dialog = ShowingProfileDialog(auth!!.uid)
            dialog.isCancelable = false
            dialog.show((requireContext() as FragmentActivity).supportFragmentManager, dialog.tag)
        }

        binding!!.uploadImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 11)
        }

        binding!!.myQuestionTxt.setOnClickListener { setFragment(MyQuestionsFragment()) }
        binding!!.savedQuestion.setOnClickListener { setFragment(BookmarkFragment()) }

        binding!!.recommendation.setOnClickListener { setFragment(SearchFragment()) }

        binding!!.logout.setOnClickListener {
            val dialog = LogOutDialog()
            dialog.isCancelable = false
            dialog.show((requireContext() as FragmentActivity).supportFragmentManager, dialog.tag)
        }

        binding!!.tvEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            requireContext().startActivity(intent)

        }
        return binding!!.root
    }

    private fun initView() {
        auth = FirebaseAuth.getInstance()
        databse = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        pd = ProgressDialog(context, R.style.AppCompatAlertDialogStyle)
        pd!!.setMessage("Please Wait...")
        pd!!.setCancelable(false)
    }


    private fun setProfileData() {
        auth!!.uid?.let {
            databse!!.reference
                .child("Users")
                .child(it)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (pd!!.isShowing){
                            pd!!.dismiss()
                        }
                        if (snapshot.exists()){
                            val user: UserModel? = snapshot.getValue(UserModel::class.java)

                            context?.let { it1 ->
                                Glide.with(it1)
                                    .load(user!!.profilePic)
                                    .placeholder(R.drawable.placeholder)
                                    .into(binding!!.profileImage)
                            }

                            binding!!.userName.text = user!!.name
                            binding!!.userEmail.text = user.email
                            binding!!.description.text = user.summary

                            if (binding!!.description.text.equals("")){
                                binding!!.description.text = "Add your description"
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
    }


    private fun setFragment(fragment : Fragment) {
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.linearLayout, fragment)
        transaction.commit()
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            pd!!.show()

            if (data != null) {
                val uri = data.data
                binding!!.profileImage.setImageURI(uri)

                val reference : StorageReference? = auth!!.uid?.let {
                    storage!!.reference.child("profile_image").child(it)
                }

                if (uri != null) {
                    reference!!.putFile(uri).addOnSuccessListener {
                        Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()

                        reference.downloadUrl.addOnSuccessListener {
                            auth!!.uid?.let { it1 ->
                                databse!!.reference
                                    .child("Users")
                                    .child(it1)
                                    .child("profilePic")
                                    .setValue(it.toString())

                                pd!!.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }
}