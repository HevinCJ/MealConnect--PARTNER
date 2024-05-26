package com.example.mealconnect.utils

 class UserData{
    var username:String?=null
    var phoneno:String?=null
    var email:String?=null
    var password:String?=null
    lateinit var profileimage:String


    constructor(){}

    constructor(username:String,email:String,password:String){
       this.username=username
       this.email=email
       this.password=password
       this.phoneno=""
       this.profileimage=""

    }
 }


