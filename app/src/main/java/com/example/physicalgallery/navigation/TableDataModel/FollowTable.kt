package com.example.physicalgallery.navigation.TableDataModel

data class FollowTable(
    var numfollower : Int = 0,
    var numfollwing : Int = 0,
    var followers : MutableMap<String,Boolean> = HashMap(),
    var followings : MutableMap<String,Boolean> = HashMap()
)