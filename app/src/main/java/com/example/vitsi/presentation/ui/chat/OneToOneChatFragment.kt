package com.example.vitsi.presentation.ui.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vitsi.R
import com.example.vitsi.databinding.OneToOneChatFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OneToOneChatFragment : Fragment() {

    companion object {
        fun newInstance() = OneToOneChatFragment()
    }

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var mDbRef: DatabaseReference
    private val args by navArgs<OneToOneChatFragmentArgs>()

    var receiverRoom: String? = null
    var senderRoom: String? = null

    private lateinit var binding: OneToOneChatFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = OneToOneChatFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = args.name
        val receiverUid = args.uid


        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().reference

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        (activity as AppCompatActivity).supportActionBar?.title = name

        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        with(binding){
            chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            chatRecyclerView.adapter = messageAdapter
        }

        // recycler data

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        // sending to DB
        with(binding){
            sendButton.setOnClickListener {
                val message = messageBox.text.toString()
                val messageObject = Message(message, senderUid)

                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                messageBox.setText("")
            }
        }
    }
    }
