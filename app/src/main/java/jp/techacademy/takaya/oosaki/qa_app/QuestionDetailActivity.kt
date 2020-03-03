package jp.techacademy.takaya.oosaki.qa_app

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_question_detail.*

import java.util.HashMap
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.name
import android.media.ExifInterface


class QuestionDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private lateinit var mFavoriteRef: DatabaseReference
    private var ExistFlg = 0

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }
            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
        }
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        }
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
        }
        override fun onCancelled(databaseError: DatabaseError) {
        }
    }
    val fEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            ExistFlg = 1
            favoriteButton.setBackgroundResource(R.drawable.btn);
        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
        }
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }
        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        favoriteButton.setOnClickListener(this)
        ExistFlg = 0

        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras.get("question") as Question

        title = mQuestion.title

        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Questionを渡して回答作成画面を起動する
                // --- ここから ---
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
                // --- ここまで ---
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)

        //お気に入り
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null)
        {
            favoriteButton.setVisibility(View.INVISIBLE)
        }
        else{
            favoriteButton.setVisibility(View.VISIBLE)
            favoriteButton.setBackgroundResource(R.drawable.btn_pressed);
        }
        //
        val fuser = FirebaseAuth.getInstance().currentUser
        if(fuser != null) {
            val fdataBaseReference = FirebaseDatabase.getInstance().reference
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            mFavoriteRef = fdataBaseReference.child(FavoritePATH).child(userID).child(mQuestion.questionUid)
            mFavoriteRef.addChildEventListener(fEventListener)
        }
    }

    override fun onClick(v: View) {
        if( v == favoriteButton){
            if(ExistFlg == 1){
                mFavoriteRef.removeValue()
                ExistFlg = 0
                favoriteButton.setBackgroundResource(R.drawable.btn_pressed);
            }
            else{
                val data = HashMap<String, String>()
                data["genre"] = mQuestion.genre.toString()
                mFavoriteRef.setValue(data)
            }
        }
    }
}