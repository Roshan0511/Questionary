package com.roshan.questionnaire.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.roshan.questionnaire.Adapters.QuestionsAdapter
import com.roshan.questionnaire.Models.PostModel
import com.roshan.questionnaire.R
import com.roshan.questionnaire.databinding.FragmentQuestionSearchBinding
import java.util.*

class QuestionSearchFragment : Fragment() {

    private lateinit var binding: FragmentQuestionSearchBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var list: ArrayList<PostModel>? = null
    private lateinit var mAdapter: QuestionsAdapter
    private var pd: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQuestionSearchBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }

    private fun initView(){
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        list = ArrayList()

        pd = ProgressDialog(requireContext(), R.style.AppCompatAlertDialogStyle)
        pd!!.setMessage("Please Wait...")
        pd!!.setCancelable(false)
    }

    override fun onResume() {
        super.onResume()

        binding.searchQuestionRv.visibility = View.GONE

        if (list!!.size != 0) {
            list!!.clear()
            filterList()
            setDataForPostAdapter()
        } else {
            filterList()
            setDataForPostAdapter()
        }
    }

    private fun updateUI(){
        mAdapter = list?.let { QuestionsAdapter(context, it) }!!
        binding.searchQuestionRv.layoutManager = LinearLayoutManager(context)
        binding.searchQuestionRv.adapter = mAdapter
    }



    private fun setDataForPostAdapter() {
        pd!!.show()

        database.reference
            .child("posts")
            .addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (pd!!.isShowing) {
                    pd!!.dismiss()
                }
                list!!.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val post = dataSnapshot.getValue(PostModel::class.java)!!
                        post.postId = dataSnapshot.key
                        list!!.add(post)
                        updateUI()
                    }
                }
                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                if (pd!!.isShowing) {
                    pd!!.dismiss()
                }
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterList() {
        pd!!.show()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                if (pd!!.isShowing){
                    pd!!.dismiss()
                }

                if (query.isNotEmpty()){
                    binding.searchQuestionRv.visibility = View.VISIBLE
                } else {
                    binding.searchQuestionRv.visibility = View.GONE
                }

                val filteredList: MutableList<PostModel> = ArrayList()
                for (item in list!!) {
                    if (item.questionTxt.lowercase(Locale.getDefault())
                            .contains(query.lowercase(Locale.getDefault()))
                    ) {
                        filteredList.add(item)
                    }
                }
                mAdapter.filteredList(filteredList as ArrayList<PostModel>)

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun getData() {
        database.reference
            .child("posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (dataSnapshot in snapshot.children){
                            database.reference
                                .child("posts")
                                .child(dataSnapshot.key!!)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val post: PostModel? = snapshot.getValue(PostModel::class.java)
                                        list!!.add(post!!)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(context, "Error : ${error.message}", Toast.LENGTH_LONG).show()
                                    }

                                })
                        }
                    }
                    Toast.makeText(context, list!!.size.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error : ${error.message}", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun getList(text : String) : ArrayList<PostModel>{
        val arrayList: ArrayList<PostModel> = ArrayList()

        for (i in 0 until list!!.size){
            if (text == list!![i].questionTxt){
                arrayList.add(list!![i])
            }
        }

        Toast.makeText(context, arrayList.size.toString(), Toast.LENGTH_SHORT).show()

        return arrayList
    }
}