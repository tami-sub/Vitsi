package com.example.vitsi.presentation.ui.chat.chat_all_users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.vitsi.R
import com.example.vitsi.models.user.User

class UserAdapter(val context: Context, val userList: ArrayList<User>, val view:View):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.chat_user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.username

        holder.itemView.setOnClickListener {
            findNavController(view).navigate(ChatFragmentDirections.actionChatFragmentToOneToOneChatFragment(
                currentUser.username,
                currentUser.uid))
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }
}