package com.jonnydev.bmp

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jonnydev.bmp.adapter.PlaylistChooserAdapter
import com.jonnydev.bmp.adapter.PlaylistChooserTouchHelperCallback
import com.jonnydev.bmp.adapter.PlaylistChooserTouchHelperCallbackAdapter
import com.jonnydev.bmp.extension.hideKeyboard
import com.jonnydev.bmp.model.Playlist
import com.jonnydev.bmp.prefs.TrackPrefs
import com.jonnydev.bmp.service.MediaService
import com.jonnydev.bmp.util.RequestCodeGen
import com.jonnydev.bmp.viewmodel.PlaylistChooserViewModel
import kotlinx.android.synthetic.main.activity_playlist_chooser.*

const val EXTRA_RESULT_PLAYLIST_ID = "extra_result_playlist_id"
const val EXTRA_RESULT_PLAYLIST_TITLE = "extra_result_playlist_title"
val SHOW_CURRENT_TRACK_RESULT_CODE = RequestCodeGen.generateCode()

class PlaylistChooserActivity :
    AppCompatActivity(),
    View.OnClickListener,
    PlaylistChooserAdapter.PlaylistChooserAdapterListener {
    
    private lateinit var mPlaylistChooserAdapter: PlaylistChooserAdapter
    private var mMediaService: MediaService? = null
    private val mMediaServiceConnection = MediaServiceConnection()
    private var mShouldShowAllTracks = false
    private var mShouldBackToCurrentTrack = false
    private val mViewModel by lazy {
        ViewModelProvider(this)[PlaylistChooserViewModel::class.java]
    }
    private var mActivePlaylistId: Long = 0L
    private var mCurrentPlaylistId: Long = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)
        bindMediaService()
        
        mActivePlaylistId = intent.getLongExtra(EXTRA_ACTIVE_PLAYLIST_ID, 0L)
        mCurrentPlaylistId = intent.getLongExtra(EXTRA_CURRENT_PLAYLIST_ID, 0L)
        mPlaylistChooserAdapter = PlaylistChooserAdapter(this)
        
        initRecyclerView(rv_playlists)
        initTouchHelper(mPlaylistChooserAdapter, rv_playlists)
        btn_create.setOnClickListener(this)
    }
    
    override fun onClick(v: View?) {
        val playlistTitle = editText.text.toString()
        
        mViewModel.addPlayList(playlistTitle)
        
        editText.run {
            text.clear()
            clearFocus()
            hideKeyboard()
        }
    }
    
    private fun initRecyclerView(rv: RecyclerView) {
        with(rv) {
            layoutManager = LinearLayoutManager(context)
            adapter = mPlaylistChooserAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            setHasFixedSize(true)
        }
    }
    
    private fun initTouchHelper(
        adapter: PlaylistChooserTouchHelperCallbackAdapter,
        rv: RecyclerView
    ) {
        val callback = PlaylistChooserTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        
        touchHelper.attachToRecyclerView(rv)
    }
    
    private fun setOnLoadPlaylistsObserver() {
        mViewModel.playlists.observe(this, Observer {
            it?.let { playlists ->
                mPlaylistChooserAdapter.playlists = playlists
                scrollToPlaylistIfInserted(playlists)
            }
        })
    }
    
    private fun scrollToPlaylistIfInserted(playlists: MutableList<Playlist>) {
        mViewModel.insertedPlaylistId?.let { id ->
            val position = playlists.indexOfFirst { it.id == id }
            rv_playlists.smoothScrollToPosition(position)
            mViewModel.insertedPlaylistId = null
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.playlist_chooser, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all -> {
                if (mCurrentPlaylistId != 0L) {
                    when (mActivePlaylistId) {
                        0L -> mShouldBackToCurrentTrack = true
                        else -> onActivePlaylistDelete()
                    }
                }
                
                mViewModel.deleteAll()
            }
            android.R.id.home -> onBackPressed()
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    override fun onPlaylistDelete(playlist: Playlist, position: Int) {
        when (playlist.id) {
            mActivePlaylistId -> onActivePlaylistDelete()
            mCurrentPlaylistId -> mShouldBackToCurrentTrack = true
        }
        
        mViewModel.deletePlaylist(playlist)
    }
    
    private fun onActivePlaylistDelete() {
        mMediaService?.run { player.stop() }
        mShouldShowAllTracks = true
        TrackPrefs.clear().apply()
        mMediaService?.hasStartTrack = false
    }
    
    override fun onPlaylistClick(playlist: Playlist) {
        val intent = Intent().apply {
            putExtra(EXTRA_RESULT_PLAYLIST_ID, playlist.id)
            putExtra(EXTRA_RESULT_PLAYLIST_TITLE, playlist.title)
        }
        
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    
    private fun bindMediaService() {
        Intent(this, MediaService::class.java).also { intent ->
            bindService(intent, mMediaServiceConnection, 0)
        }
    }
    
    private fun onServiceBind(binder: MediaService.MediaBinder) {
        mMediaService = binder.getService()
        setOnLoadPlaylistsObserver()
    }
    
    override fun onBackPressed() {
        when {
            mShouldShowAllTracks -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            mShouldBackToCurrentTrack -> {
                setResult(SHOW_CURRENT_TRACK_RESULT_CODE)
                finish()
            }
            else -> super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mMediaService?.let { unbindService(mMediaServiceConnection) }
    }
    
    inner class MediaServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MediaService.MediaBinder ?: return
            
            onServiceBind(binder)
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {}
    }
}
