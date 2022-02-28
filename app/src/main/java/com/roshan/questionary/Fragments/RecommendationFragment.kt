package com.roshan.questionary.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.roshan.questionary.Adapters.SuggestionsAdapter
import com.roshan.questionary.Models.UserModel
import com.roshan.questionary.R
import com.roshan.questionary.databinding.FragmentRecommendationBinding

class RecommendationFragment : Fragment() {

    var binding: FragmentRecommendationBinding ?= null
    var adapter: SuggestionsAdapter ?= null
    var auth: FirebaseAuth ?= null
    var database: FirebaseDatabase ?= null
    var list = ArrayList<UserModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setDataForRv()

        adapter = SuggestionsAdapter(requireContext(), list)

        binding!!.recyclerViewForSuggestions.layoutManager = GridLayoutManager(requireContext(), 2)
        binding!!.recyclerViewForSuggestions.adapter = adapter
        binding!!.recyclerViewForSuggestions.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL))
        binding!!.recyclerViewForSuggestions.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        binding!!.backBtn.setOnClickListener {
            pressBackButton()
        }

        return binding!!.root
    }

    private fun setDataForRv() {
        binding!!.progressBar6.visibility = View.VISIBLE

        database!!.reference.child("Users")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    if (snapshot.exists()){
                        for (data in snapshot.children){
                            val user : UserModel ?= data.getValue(UserModel::class.java)
                            user!!.userId = data.key
                            if (!user.userId.equals(auth!!.uid)){
                                list.add(user)
                            }
                        }
                        adapter!!.notifyDataSetChanged()
                        binding!!.progressBar6.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    private fun pressBackButton() {
        val fragment = ProfileFragment()
        assert(fragmentManager != null)
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.linearLayout, fragment)
        transaction.commit()
    }


    override fun onResume() {
        super.onResume()

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                pressBackButton()
                return@setOnKeyListener true
            }
            false
        }
    }
}