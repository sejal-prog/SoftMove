package com.example.softmove.ViewModel
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import com.example.softmove.Repository.CredentialValidationRepo
//
//class LoginViewModel: AndroidViewModel {
//    private var credentialvalidationrepo: CredentialValidationRepo
//
//    constructor(application: Application) : super(application){
//        credentialvalidationrepo = CredentialValidationRepo(application)
//    }
//
//    fun validateCredentials(email:String,passWord:String): LiveData<String> {
//        return credentialvalidationrepo.validateCredentials(email,passWord)
//    }
//}