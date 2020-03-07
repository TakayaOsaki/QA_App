package jp.techacademy.takaya.oosaki.qa_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.activity_question_detail.*

class FavoriteActivity : AppCompatActivity() {
    private lateinit var mFavoriteRef: DatabaseReference
    private lateinit var mFavoriteArrayList: ArrayList<Favorite>
    private lateinit var mAdapter: FavoriteListAdapter
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mListView: ListView

    var userID = ""
    private var mGenreRef: DatabaseReference? = null
    private lateinit var mDatabaseReference: DatabaseReference
    private var mGenre = 0

    private var Genre1Flg = 0
    private var Genre2Flg = 0
    private var Genre3Flg = 0
    private var Genre4Flg = 0
    private var EventFlg = 0

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            for (favorite in mFavoriteArrayList) {
                if (dataSnapshot.key.equals(favorite.questionUid)) {
                    val map = dataSnapshot.value as Map<String, String>

                    val ftitle = map["title"] ?: ""
                    val body = map["body"] ?: ""
                    val name = map["name"] ?: ""
                    val uid = map["uid"] ?: ""
                    val imageString = map["image"] ?: ""
                    val bytes =
                        if (imageString.isNotEmpty()) {
                            Base64.decode(imageString, Base64.DEFAULT)
                        } else {
                            byteArrayOf()
                        }
                    val answerArrayList = ArrayList<Answer>()
                    mGenre = favorite.genre
                    val question = Question(
                        ftitle, body, name, uid, dataSnapshot.key ?: "",
                        mGenre, bytes, answerArrayList
                    )
                    mQuestionArrayList.add(question)
                    mAdapter.notifyDataSetChanged()

                    break

                }
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onCancelled(p0: DatabaseError) {
        }
    }

    val fEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val genre = map["genre"] ?: ""
            mGenre = genre.toInt()
            val favorite = Favorite(userID, dataSnapshot.key ?: "", genre.toInt())
            mFavoriteArrayList.add(favorite)

            if (mGenre == 1 && Genre1Flg == 0) {
                EventFlg = 1
                Genre1Flg = 1
            } else if (mGenre == 2 && Genre2Flg == 0) {
                EventFlg = 1
                Genre2Flg = 1
            } else if (mGenre == 3 && Genre3Flg == 0) {
                EventFlg = 1
                Genre3Flg = 1
            } else if (mGenre == 4 && Genre4Flg == 0) {
                EventFlg = 1
                Genre4Flg = 1
            } else {
                EventFlg = 0
            }
            //Search question in genre
            if (EventFlg == 1) {
                mDatabaseReference = FirebaseDatabase.getInstance().reference
                mGenreRef = mDatabaseReference.child(ContentsPATH).child(genre)
                mGenreRef!!.addChildEventListener(mEventListener)

            }
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
        setContentView(R.layout.activity_favorite)
        title = "お気に入り"
        Genre1Flg = 0
        Genre2Flg = 0
        Genre3Flg = 0
        Genre4Flg = 0
        EventFlg = 0

        mFavoriteArrayList = ArrayList<Favorite>()
        mQuestionArrayList = ArrayList<Question>()

        mFavoriteArrayList.clear()
        mQuestionArrayList.clear()

        // ListViewの準備
        mListView = findViewById(R.id.FlistView)
        mAdapter = FavoriteListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()

        val fuser = FirebaseAuth.getInstance().currentUser
        if(fuser != null) {
            val fdataBaseReference = FirebaseDatabase.getInstance().reference
            userID = FirebaseAuth.getInstance().currentUser!!.uid
            mFavoriteRef = fdataBaseReference.child(FavoritePATH).child(userID)
            mFavoriteRef.addChildEventListener(fEventListener)
        }

        mListView.setOnItemClickListener { parent, view, position, id ->
            // Questionのインスタンスを渡して質問詳細画面を起動する
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        Genre1Flg = 0
        Genre2Flg = 0
        Genre3Flg = 0
        Genre4Flg = 0
        EventFlg = 0
        mFavoriteArrayList.clear()
        mQuestionArrayList.clear()

        mAdapter.setQuestionArrayList(mQuestionArrayList)
        mListView.adapter = mAdapter

        // 選択したジャンルにリスナーを登録する
        if (mFavoriteRef != null) {
            mFavoriteRef!!.removeEventListener(fEventListener)
        }
        val fuser = FirebaseAuth.getInstance().currentUser
        if(fuser != null) {
            val fdataBaseReference = FirebaseDatabase.getInstance().reference
            userID = FirebaseAuth.getInstance().currentUser!!.uid
            mFavoriteRef = fdataBaseReference.child(FavoritePATH).child(userID)
            mFavoriteRef.addChildEventListener(fEventListener)
        }

    }

    override fun onResume() {
        super.onResume()


    }
}
