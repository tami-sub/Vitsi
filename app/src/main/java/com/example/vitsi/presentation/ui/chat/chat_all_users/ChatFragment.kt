package com.example.vitsi.presentation.ui.chat.chat_all_users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vitsi.databinding.ChatFragmentBinding
import com.example.vitsi.domain.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {


    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter

    private lateinit var mAuth:FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var binding: ChatFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        mAuth = FirebaseAuth.getInstance()
        mDbRef = Firebase.database.reference

        userList = ArrayList()

        binding = ChatFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UserAdapter(requireContext(), userList, view)
        with(binding){
            userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            userRecyclerView.adapter = adapter
        }

        mDbRef.child("users").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnap in snapshot.children){

                    Toast.makeText(requireContext(), postSnap.child("basic-data").value.toString(), Toast.LENGTH_SHORT).show()

                    val currentUser = postSnap.child("basic-data").getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
//        mDbRef.child("users").addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                userList.clear()
//                for(postSnapshot in snapshot.children){
//                    val goka = postSnapshot.children
//                    Log.d("joka",goka.toString())
//                    val currentUser = postSnapshot.child("basic-data").getValue(User::class.java)
//
//                    //
//                    if (mAuth.currentUser?.uid != currentUser?.uid){
//                        userList.add(currentUser!!)
//                    }
//                    //
//                    //userList.add(currentUser!!)
//                }
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
    }

    companion object {
        fun newInstance() = ChatFragment()
    }
}