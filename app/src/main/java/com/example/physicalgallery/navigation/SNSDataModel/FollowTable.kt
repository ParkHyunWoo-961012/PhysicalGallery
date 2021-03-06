package com.example.physicalgallery.navigation.SNSDataModel

data class FollowTable(
    var numfollower : Int = 0,
    var numfollowing : Int = 0,
    var followers : MutableMap<String,Boolean> = HashMap(),
    var followings : MutableMap<String,Boolean> = HashMap()
)