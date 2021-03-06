package com.twentytwo.textit.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.twentytwo.textit.R
import com.twentytwo.textit.models.User

class ContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = "Contacts"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val fromUid = firebaseUser.uid
            val rootRef = FirebaseFirestore.getInstance()
            val uidRef = rootRef.collection("users").document(fromUid)
            uidRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {
                            val fromUser = document.toObject(User::class.java)
                            val userContactsRef = rootRef.collection("contacts").document(fromUid)
                                .collection("userContacts")
                            userContactsRef.get().addOnCompleteListener { t ->
                                if (t.isSuccessful) {
                                    val listOfToUserNames = ArrayList<String>()
                                    val listOfToUsers = ArrayList<User>()
                                    val listOfRooms = ArrayList<String>()
                                    for (d in t.result!!) {
                                        val toUser = d.toObject(User::class.java)
                                        listOfToUserNames.add(toUser.userName)
                                        listOfToUsers.add(toUser)
                                        listOfRooms.add(d.id)
                                    }

                                    val arrayAdapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_list_item_1,
                                        listOfToUserNames
                                    )

                                    val list_viw = findViewById<ListView>(R.id.list_viw)
                                    list_viw.adapter = arrayAdapter
                                    list_viw.onItemClickListener =
                                        AdapterView.OnItemClickListener { _, _, position, _ ->
                                            val intent = Intent(this, ChatActivity::class.java)
                                            intent.putExtra("fromUser", fromUser)
                                            intent.putExtra("toUser", listOfToUsers[position])
                                            intent.putExtra("roomId", "noRoomId")
                                            startActivity(intent)
                                            finish()
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(menuItem)
        }
    }
}