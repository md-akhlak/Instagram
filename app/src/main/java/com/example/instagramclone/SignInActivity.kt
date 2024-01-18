package com.example.instagramclone

import android.app.Instrumentation.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.instagramclone.Modals.User
import com.example.instagramclone.databinding.ActivitySignInBinding
import com.example.instagramclone.utils.USER_NODE
import com.example.instagramclone.utils.USER_PROFILE_FOLDER
import com.example.instagramclone.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignInActivity : AppCompatActivity() {

    val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    lateinit var user: User

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if (it == null) {

                } else {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        user = User()
        binding.signUnBtn.setOnClickListener {
            if (binding.name.editText?.text.toString()
                    .equals("") or binding.email.editText?.text.toString()
                    .equals("") or binding.pass.editText?.text.toString().equals("")
            ) {
                Toast.makeText(
                    this@SignInActivity, "please fill the required details", Toast.LENGTH_SHORT
                ).show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editText?.text.toString(), binding.pass.editText?.text.toString()
                ).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        user.name = binding.name.editText?.text.toString()
                        user.password = binding.pass.editText?.text.toString()
                        user.email = binding.email.editText?.text.toString()

                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this@SignInActivity, "Login", Toast.LENGTH_SHORT)
                                    .show()
                            }

                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            result.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        binding.plus.setOnClickListener {
            launcher.launch("image/*")
        }
    }
}