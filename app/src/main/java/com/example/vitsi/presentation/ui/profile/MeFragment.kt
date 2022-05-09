package com.example.vitsi.presentation.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.vitsi.R
import com.example.vitsi.databinding.FragmentMeBinding
import com.example.vitsi.presentation.ui.profile.with_account.ProfileWithAccountFragment
import com.example.vitsi.presentation.ui.profile.without_account.ProfileWithoutAccountFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MeFragment : Fragment(R.layout.fragment_me) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().supportFragmentManager.beginTransaction()
            .add(
                R.id.frag_container,
                if (Firebase.auth.currentUser == null)
                    ProfileWithoutAccountFragment()
                else
                    //  Since we are retrieving the uid in ProfileWithAccountFragment using by navArgs(), lets include this bundle
                    ProfileWithAccountFragment().apply {
                        arguments = Bundle().also {
                            it.putString("uid", Firebase.auth.uid)
                        }
                    }
            )
            .commit()
    }
}
