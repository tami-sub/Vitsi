package com.example.vitsi.presentation.ui.audio


import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.vitsi.domain.local.LocalVideo
import com.example.vitsi.databinding.AudioFragmentBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import java.io.IOException
import java.util.*

class AudioFragment : Fragment() {

    companion object {
        fun newInstance() = AudioFragment()
    }

    private lateinit var viewModel: AudioViewModel

    private lateinit var binding: AudioFragmentBinding
    private lateinit var mFileName: String
    private var mRecorder: MediaRecorder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AudioFragmentBinding.inflate(layoutInflater)

        mFileName = requireActivity().externalCacheDir!!.absolutePath
        mFileName += "/" + UUID.randomUUID().toString()
        //+ ".mp3"


        with(binding) {
            btnStart.setOnClickListener {
                checkPermission()
                it.isEnabled = false
                btnStop.isEnabled = true
            }
            btnStop.setOnClickListener {
                stopRecording()
                it.isEnabled = false
                btnStart.isEnabled = true
            }
        }

        return binding.root
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mRecorder!!.setOutputFile(mFileName)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        Log.d("joka", mFileName)
        try {
            mRecorder!!.prepare()
        } catch (e: IOException) {
            Log.e("joka", "prepare() failed")
        }

        mRecorder!!.start()
    }

    private fun stopRecording() {
        mRecorder!!.stop()
        mRecorder!!.release()
        mRecorder = null

        Log.d("boka", mFileName)
        mFileName?.let {
            findNavController().navigate(
                AudioFragmentDirections.actionAudioFragmentToPostAudioFragment(
                    LocalVideo(
                        mFileName,
                        9,
                        ""
                    )
                )
            )
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            startRecording()
        }
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission == true) {
                startRecording()
            } else {
//                showAlert()   I'll do later, maybe in MS
            }
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AudioViewModel::class.java)
        // TODO: Use the ViewModel
    }

}