package com.example.isepchat.activities.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.isepchat.databinding.FragmentNotificationsBinding
import com.example.isepchat.R
import com.example.isepchat.activities.AuthentificationActivity
import com.example.isepchat.activities.SettingsActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val btn3 = root.findViewById<Button>(R.id.btn3)
        btn3.setOnClickListener {
            val auth = Firebase.auth
            auth.signOut()
            Intent(activity, AuthentificationActivity::class.java).also {
                startActivity(it)
            }
            activity?.finish()
        }

        val btn2 = root.findViewById<Button>(R.id.btn2)
        btn2.setOnClickListener {
            Intent(activity, SettingsActivity::class.java).also {
                startActivity(it)
            }
        }

        notificationsViewModel.text.observe(viewLifecycleOwner) {
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}