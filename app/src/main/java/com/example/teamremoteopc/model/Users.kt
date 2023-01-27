package com.example.teamremoteopc.model

class Users{
   private var firstName =""
   private var dob : String=""

   constructor()
   constructor(firstName: String, dob: String) {
      this.firstName = firstName
      this.dob = dob
   }

   fun getFirstName():String{
      return firstName
   }
   fun getAge():String{
      return dob
   }

}