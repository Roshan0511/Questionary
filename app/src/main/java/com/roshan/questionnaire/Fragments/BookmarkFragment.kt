package com.roshan.questionnaire.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.roshan.questionnaire.Adapters.BookmarkAdapter
import com.roshan.questionnaire.R
import com.roshan.questionnaire.databinding.FragmentBookmarkBinding

class BookmarkFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var pd: ProgressDialog
    private lateinit var list: ArrayList<String>
    private lateinit var mAdapter: BookmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        initView()

        binding.backBtn.setOnClickListener { pressBackButton() }

        return binding.root
    }

    private fun initView() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        list = ArrayList()
        pd = ProgressDialog(context, R.style.AppCompatAlertDialogStyle)
        pd.setMessage("Please Wait...")
        pd.setCancelable(false)
    }

    private fun updateUI(){
        mAdapter = BookmarkAdapter(context, list)
        binding.rvBookmark.layoutManager = LinearLayoutManager(context)
        binding.rvBookmark.adapter = mAdapter
    }

    private fun setData() {
        pd.show()

        auth.uid?.let {
            database.reference
                .child("bookmarks")
                .child(it)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (pd.isShowing) {
                            pd.dismiss()
                        }

                        list.clear()
                        if (snapshot.exists()){
                            for (dataSnapshot in snapshot.children){
                                list.add(dataSnapshot.key!!.toString())

                                updateUI()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (pd.isShowing) {
                            pd.dismiss()
                        }
                        Toast.makeText(context, "Error ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun pressBackButton() {
        val fragment = MyProfileFragment()
        assert(fragmentManager != null)
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.linearLayout, fragment)
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()

        if (list.size != 0) {
            list.clear()
            setData()
        } else {
            setData()
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
}