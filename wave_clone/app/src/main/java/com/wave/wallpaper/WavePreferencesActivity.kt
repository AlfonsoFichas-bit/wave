package com.wave.wallpaper

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class WavePreferencesActivity : Activity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(WaveRenderer.PREFS_NAME, 0)
        val listView = ListView(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ColorInfo.all.map { it.label })
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            prefs.edit().putInt(WaveRenderer.PREFS_KEY_COLOR, position).apply()
            finish()
        }
        setContentView(listView)
    }
}
PY