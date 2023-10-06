package com.example.resell

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GoogleFragment : Fragment() {

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Google sign in failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    "Google sign in failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    reference = FirebaseDatabase.getInstance().getReference("Users")
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = user?.uid
                    val username = user?.displayName.toString()
                    val signIn = "GOOGLE"

                    if (userId != null) {
                        if (userId == user?.uid) {
                            if (username != null) {
                                reference.child(userId).child("signIn").setValue(signIn)
                            } else {
                                reference.child(userId).child("signIn").setValue(signIn)
                            }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Invalid User",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        Toast.makeText(
                            requireContext(),
                            "Sign Up Successfully, Login Again",
                            Toast.LENGTH_SHORT
                        ).show()
                       this.findNavController().navigate(R.id.action_googleFragment_to_coverPageFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}
