package com.jonnydev.bmp

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.jonnydev.bmp.adapter.TrackAdapter
import com.jonnydev.bmp.databinding.ActivityMainBinding
import com.jonnydev.bmp.delegates.MediaServiceDelegate
import com.jonnydev.bmp.delegates.TrackAdapterDelegate
import com.jonnydev.bmp.extension.seconds
import com.jonnydev.bmp.extension.toTimeFormat
import com.jonnydev.bmp.fragment.AllTracksFragment
import com.jonnydev.bmp.fragment.OnFragmentInteractionListener
import com.jonnydev.bmp.fragment.PlaylistFragment
import com.jonnydev.bmp.fragment.SettingDialogFragment
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.prefs.SettingPrefs
import com.jonnydev.bmp.prefs.TrackPrefs
import com.jonnydev.bmp.service.MediaService
import com.jonnydev.bmp.util.ConnMediator
import com.jonnydev.bmp.util.Player
import com.jonnydev.bmp.util.RequestCodeGen
import com.jonnydev.bmp.util.Settings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

private val READ_STORAGE_REQUEST_CODE by lazy { RequestCodeGen.generateCode() }
private val SELECT_PLAYLIST_REQUEST_CODE by lazy { RequestCodeGen.generateCode() }
private const val ALL_TRACKS_FRAGMENT_TAG = "all_songs_fragment_tag"
const val EXTRA_CURRENT_PLAYLIST_ID = "current_playlist_id"
const val EXTRA_ACTIVE_PLAYLIST_ID = "active_playlist_id"

class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    SeekBar.OnSeekBarChangeListener,
    Player.OnPlayerUpdateListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener,
    OnFragmentInteractionListener {
    
    private val mConnMediator = ConnMediator()
    private var mMediaService by MediaServiceDelegate(mConnMediator)
    private var mTrackAdapter by TrackAdapterDelegate(mConnMediator)
    private lateinit var mActivityMainBinding: ActivityMainBinding
    private val mMediaServiceConnection = MediaServiceConnection()
    private var mIsReadExtStoragePermission = false
    private var mCurrentPlaylistId: Long? = null
    private var mOnActivityResulted: (() -> Unit)? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)
        equalizer.stopBars()
        
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        
        mConnMediator.onConnectionsReady = { onFirstStart() }
        checkSdkAndPermissions()
        initPlayerButtons()
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        seekBar.setOnSeekBarChangeListener(this)
        current_track_container.setOnClickListener { onCurrentTrackClick() }
    }
    
    override fun onResume() {
        super.onResume()
        mOnActivityResulted?.let {
            it()
            mOnActivityResulted = null
        }
    }
    
    private fun onCurrentTrackClick() {
        val adapter = mTrackAdapter
        val playlistId = adapter?.currentTrack?.playlistId
        val path = adapter?.currentTrack?.path
        
        path?.let {
            if (!File(path).exists()) {
                updateAllTracks(true)
            }
        }
        
        playlistId?.let { listId ->
            when (listId) {
                0L -> when {
                    !isFragmentAdded(ALL_TRACKS_FRAGMENT_TAG) -> {
                        replaceFragment(
                            AllTracksFragment.newInstance(adapter.currentIndex),
                            ALL_TRACKS_FRAGMENT_TAG
                        )
                    }
                    else -> findViewById<RecyclerView>(R.id.track_list)
                        .smoothScrollToPosition(adapter.currentIndex)
                }
                else -> when {
                    !isFragmentAdded(listId.toString()) -> replaceFragment(
                        PlaylistFragment.newInstance(listId, adapter.currentIndex),
                        listId.toString()
                    )
                    else -> findViewById<RecyclerView>(R.id.track_list)
                        .smoothScrollToPosition(adapter.currentIndex)
                }
            }
        }
    }
    
    private fun onFirstStart() {
            mMediaService!!.run {
                if (!hasStartTrack) {
                    prepareFirstTrack()
                    hasStartTrack = true
                } else {
                    mTrackAdapter!!.run {
                        if (isNotEmpty()) {
                            mActivityMainBinding.currentTrack = currentTrack
                        }
                    }
                    if (player.isPlaying)
                        equalizer.animateBars()
                }
                if (player.isNotStopped) {
                    mActivityMainBinding.mp = player
                }
            }
    }
    
    private fun addFragment(fragment: Fragment, tag: String) {
        loadSettings()
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment, tag)
            .commit()
    }
    
    private fun isFragmentAdded(tag: String) =
        supportFragmentManager.findFragmentByTag(tag)?.isAdded ?: false
    
    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.run {
            beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit()
        }
    }
    
    private fun checkSdkAndPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> checkPermission()
            else -> onPermissionChecked()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission() {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        
        if (permission != PackageManager.PERMISSION_GRANTED) {
            when (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                true -> showPermissionDialog(this)
                else -> makeRequest()
            }
        } else {
            onPermissionChecked()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionDialog(context: Context) {
        AlertDialog.Builder(context).apply {
            setMessage(getString(R.string.permission_message))
            setPositiveButton(getString(R.string.permission_positive_btn)) { _, _ ->
                makeRequest()
            }
                .create()
                .show()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    private fun makeRequest() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_STORAGE_REQUEST_CODE
        )
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionChecked()
                }
            }
        }
    }
    
    private fun onPermissionChecked() {
        mIsReadExtStoragePermission = true
        
        if (supportFragmentManager.fragments.isEmpty()) {
            when (val playlistId = TrackPrefs.playlistId) {
                0L -> addFragment(AllTracksFragment(), ALL_TRACKS_FRAGMENT_TAG)
                else -> addFragment(PlaylistFragment.newInstance(playlistId), playlistId.toString())
            }
        }
        
        bindMediaService()
    }
    
    private fun loadSettings() {
        Settings.playbackModeRadioId = SettingPrefs.radioId
        Settings.fastForwardTime = SettingPrefs.fastForwardTime
    }
    
    private fun bindMediaService() {
        Intent(this, MediaService::class.java).also { intent ->
            startService(intent)
            bindService(intent, mMediaServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    
    override fun onSetTracks(adapter: TrackAdapter) {
        if (mTrackAdapter == null || adapter.id == mTrackAdapter!!.id) {
            mTrackAdapter?.let {
                val currentTrack = mActivityMainBinding.currentTrack
                
                if (it.currentIndex != -1 && currentTrack != null) {
                    adapter.selectTrackByPosition(it.currentIndex)
                }
            }
            
            mTrackAdapter = adapter
        } else {
            mCurrentPlaylistId = adapter.id
        }
    }
    
    override fun onTrackClick(track: Track, adapter: TrackAdapter) {
        mTrackAdapter?.let { currentAdapter ->
            if (adapter.id != currentAdapter.id) {
                currentAdapter.currentTrack?.let { it.isSelected = false }
                mTrackAdapter = adapter
            }
        }
        mMediaService?.onTrackClick(track)
    }
    
    private fun initPlayerButtons() {
        btn_play.setOnClickListener { mMediaService?.playOrPause() }
        btn_next.setOnClickListener { mTrackAdapter?.let { mMediaService?.next(it) } }
        btn_prev.setOnClickListener { mTrackAdapter?.let { mMediaService?.prev(it) } }
        btn_forward.setOnClickListener { mMediaService?.forward() }
        btn_back.setOnClickListener { mMediaService?.back() }
    }
    
    private fun prepareFirstTrack() {
        mTrackAdapter?.run {
            if (isNotEmpty()) {
                val playerPosition = TrackPrefs.playerPosition
                val trackId = TrackPrefs.trackId
                val startTrack = getTrackById(trackId) ?: getTrackByPosition(0)
                val startPosition =
                    if (id != 0L) startTrack.playlistPosition else getPositionByTrack(startTrack)
                
                selectTrackByPosition(startPosition)
                mMediaService?.setTrack(startTrack)
                
                if (playerPosition > 0) {
                    mMediaService?.seekTo(playerPosition)
                }
                if (TrackPrefs.isPlaying) {
                    mMediaService?.play()
                    TrackPrefs.isPlaying = false
                    TrackPrefs.apply()
                } else {
                    mActivityMainBinding.currentTrack = startTrack
                }
            }
        }
    }
    
    override fun onClickPlay() {
        btn_play.setImageResource(R.drawable.ic_pause_48dp)
        equalizer.animateBars()
        mTrackAdapter?.let { adapter ->
            adapter.currentTrack?.let { track ->
                if (!File(track.path).exists()) return
            }
            
            mActivityMainBinding.currentTrack = adapter.currentTrack
        }
    }
    
    override fun onClickPause() {
        btn_play.setImageResource(R.drawable.ic_play_48dp)
        equalizer.stopBars()
    }
    
    override fun updateProgress(playerCurrentPosition: Int) {
        seekBar.progress = playerCurrentPosition.seconds
        cur_time.text = playerCurrentPosition.toTimeFormat()
    }
    
    override fun onTrackError(track: Track) {
        updateAllTracks(true)
    }
    
    override fun onPrepared(mp: MediaPlayer) {
        seekBar.max = mp.duration.seconds
        total_time.text = mp.duration.toTimeFormat()
        cur_time.text = mp.currentPosition.toTimeFormat()
    }
    
    override fun onCompletion(mp: MediaPlayer) {
        mTrackAdapter?.let { adapter ->
            if (adapter.isNotEmpty()) {
                mMediaService?.let { ms ->
                    if (ms.next(adapter) == null) {
                        onClickPause()
                    }
                }
            }
        }
    }
    
    override fun onStopPlayer() {
        val time = 0.toTimeFormat()
        
        mMediaService?.stop()
        cur_time.text = time
        total_time.text = time
        seekBar.progress = 0
        seekBar.max = 0
        onClickPause()
        mActivityMainBinding.currentTrack = null
        TrackPrefs.clear().apply()
    }
    
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_all_tracks -> showAllTracksFragment()
            R.id.nav_playlists -> startPlaylistChooserActivity()
            R.id.nav_manage -> showSettingsDialog()
            R.id.nav_update -> updateAllTracks(false)
        }
        
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    
    private fun showAllTracksFragment() {
        if (mIsReadExtStoragePermission) {
            if (!isFragmentAdded(ALL_TRACKS_FRAGMENT_TAG)) {
                replaceFragment(AllTracksFragment(), ALL_TRACKS_FRAGMENT_TAG)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showPermissionDialog(this)
            }
        }
    }
    
    private fun startPlaylistChooserActivity() {
        val activePlaylistId = mTrackAdapter?.id
        val intent = Intent(this, PlaylistChooserActivity::class.java).apply {
            activePlaylistId?.let { putExtra(EXTRA_ACTIVE_PLAYLIST_ID, it) }
            mCurrentPlaylistId?.let { putExtra(EXTRA_CURRENT_PLAYLIST_ID, it) }
        }
        
        startActivityForResult(intent, SELECT_PLAYLIST_REQUEST_CODE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SELECT_PLAYLIST_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> data?.let { intent ->
                        val playlistId = intent.getLongExtra(EXTRA_RESULT_PLAYLIST_ID, 0L)
                        
                        if (playlistId != 0L) {
                            mOnActivityResulted = { onPlaylistSelect(playlistId) }
                        }
                    }
                    SHOW_CURRENT_TRACK_RESULT_CODE -> mOnActivityResulted = { onCurrentTrackClick() }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
    
    private fun onPlaylistSelect(playlistId: Long) {
        val fragmentTag = playlistId.toString()
        
        if (!isFragmentAdded(fragmentTag)) {
            val fragment = PlaylistFragment.newInstance(playlistId)
            replaceFragment(fragment, fragmentTag)
        }
    }
    
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        mTrackAdapter?.let { adapter ->
            if (fromUser && adapter.isNotEmpty()) {
                mMediaService?.seekTo(progress * 1000)
                cur_time.text = mMediaService?.currentPosition?.toTimeFormat()
            }
        }
    }
    
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    
    private fun showSettingsDialog() {
        val dialog = SettingDialogFragment()
        
        dialog.show(supportFragmentManager, "SettingDialogFragment")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mMediaService?.let { unbindService(mMediaServiceConnection) }
    }
    
    private fun updateAllTracks(isError: Boolean) {
        mMediaService?.run {
            TrackPrefs.isUpdating = true
            TrackPrefs.isPlaying = if (isError) true else isPlaying
            TrackPrefs.commit()
            stop()
            hasStartTrack = false
        }
        startActivity(Intent(this, SplashScreenActivity::class.java))
    }
    
    inner class MediaServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MediaService.MediaBinder ?: return
            val mediaService = binder.getService()
            
            mediaService.run {
                player.setOnPlayerUpdateListener(this@MainActivity)
                player.setOnPreparedListener(this@MainActivity)
                player.setOnCompletionListener(this@MainActivity)
            }
            mMediaService = mediaService
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            mMediaService = null
        }
    }
}
