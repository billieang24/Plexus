package models

case class Comment(content: String, pageOwner: User, user:User, wallPost: WallPost, objectId:String)