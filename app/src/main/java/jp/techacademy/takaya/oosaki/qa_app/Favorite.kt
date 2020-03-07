package jp.techacademy.takaya.oosaki.qa_app

import java.io.Serializable

class Favorite(var uid: String, val questionUid: String, val genre: Int) : Serializable {

}