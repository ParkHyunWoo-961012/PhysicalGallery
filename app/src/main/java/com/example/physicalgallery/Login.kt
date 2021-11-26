package com.example.physicalgallery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class Login : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignIn : GoogleSignInClient? =null
    var GOOGLE_LOGIN_CODE = 9001
    val binding by lazy{ActivityLoginBinding.inflate(layoutInflater)}
    private var googleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!==null){ // 이미 로그인 되어있을시 바로 메인 액티비티로 이동
            moveMainPage(auth?.currentUser)
        }
    } //onStart End


    fun googlelogin() {
        var GoogleIntent = googleSignInClient?.signInIntent.run {
            startActivityForResult(this, GOOGLE_LOGIN_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthGoogle(account)
            }catch(e: ApiException){
                Log.w("TAH","Google sign in failed", e)
            }

        }
    }

    fun firebaseAuthGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth!!.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // Login
                    moveMainPage(task.result.user)
                }else{
                    //Show the error messaage
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(binding.emailEdittext.text.toString(),binding.passwordEdittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // Creating a user account
                    moveMainPage(task.result.user)
                }else if(task.exception?.message.isNullOrEmpty()){
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
                    moveMainPage(task.result.user)
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