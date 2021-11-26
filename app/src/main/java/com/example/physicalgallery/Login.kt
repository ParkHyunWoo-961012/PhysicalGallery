package com.example.physicalgallery

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class Login : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignIn : GoogleSignInClient? =null
    var GOOGLE_LOGIN_CODE = 1007
    val binding by lazy{ActivityLoginBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        binding.emailLoginButton.setOnClickListener {
            signinAndSignup()
        }
        binding.googleSignButton.setOnClickListener{
            googlelogin()
        }
        var google = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("361991051449-d13a6j16rk7j85o6pdb8jucub35kqgk5.apps.googleusercontent.com").requestEmail().build()
        googleSignIn = GoogleSignIn.getClient(this,google)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                var account = result.signInAccount
                firebaseAuthGoogle(account)
            }
        }
    }

    fun firebaseAuthGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
    }
    fun googlelogin(){
        var GoogleIntent = googleSignIn?.signInIntent.run{
            startActivityForResult(this,GOOGLE_LOGIN_CODE)
        }
    }
    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(binding.emailEdittext.text.toString(),binding.passwordEdittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // Creating a user account
                    moveMainPage(auth?.currentUser)
                }else if(!task.exception?.message.isNullOrEmpty()){
                    // Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }else{
                    //Login if you have account
                    signinEmail()
                }
            }
    }
    fun signinEmail(){
        auth?.signInWithEmailAndPassword(binding.emailEdittext.text.toString(),binding.passwordEdittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // Login
                    moveMainPage(auth?.currentUser)
                }else{
                    //Show the error messaage
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }
    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

}