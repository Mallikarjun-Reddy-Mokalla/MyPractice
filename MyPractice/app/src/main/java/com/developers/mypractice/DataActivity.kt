package com.developers.mypractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DataActivity : AppCompatActivity() {
    val myList: ArrayList<String> = ArrayList()
    val myList2: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        val recyclerView = findViewById<RecyclerView>(R.id.recyler_view_data)

        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_1.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_2.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_3.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_4.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_5.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_6.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_7.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_8.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_9.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_10.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_11.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_12.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_13.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_14.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_15.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_16.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_17.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_18.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_19.jpg")
        myList.add("https://crazyconsole.s3.us-west-2.amazonaws.com/natureframes/single/single_th_20.jpg")
        myList2.add(R.drawable.single_th_1)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)
        myList2.add(R.drawable.single_th_1)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)
        myList2.add(R.drawable.single_th_1)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)
        myList2.add(R.drawable.single_th_2)
        myList2.add(R.drawable.single_th_3)
        myList2.add(R.drawable.single_th_4)

        val adapter = ImageAdapter(myList2)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        recyclerView.adapter = adapter

    }
}