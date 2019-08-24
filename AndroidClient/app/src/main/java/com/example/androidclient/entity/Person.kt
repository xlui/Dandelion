package com.example.androidclient.entity

data class Person(var name: String, var phoneNumber: String)

class PersonArray(val data: List<Person>)