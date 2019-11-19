package com.jonnydev.bmp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jonnydev.bmp.adapter.PlaylistAdapter
import com.jonnydev.bmp.adapter.TrackAdapter
import com.jonnydev.bmp.adapter.TrackTouchHelperCallback
import com.jonnydev.bmp.adapter.TrackTouchHelperCallbackAdapter
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.viewmodel.PlaylistViewModel
import com.jonnydev.bmp.viewmodel.PlaylistViewModelFactory
import kotlinx.android.synthetic.main.fragment_track_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val PLAYLIST_ID = "playlist_id"

class PlaylistFragment :
    BaseTracksFragment(),
    PlaylistAdapter.PlaylistAdapterListener,
    CoroutineScope {
    
    private val mJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob
    private var mPlaylistId: Long? = null
    private lateinit var mAdapter: PlaylistAdapter
    private lateinit var mRecyclerView: RecyclerView
    
    private val mViewModel by lazy {
        ViewModelProvider(this, PlaylistViewModelFactory(mPlaylistId!!))
            .get(mPlaylistId.toString(), PlaylistViewModel::class.java)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPlaylistId = it.getLong(PLAYLIST_ID)
            mScrollToPosition = it.getInt(SCROLL_TO_POSITION)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = getRootView(inflater, container)
        
        mRecyclerView = rootView.track_list
        mPlaylistId?.let { id ->
            launch { activity?.title = mViewModel.getPlaylistTitle(id) }
            mAdapter = PlaylistAdapter(this, id)
            initRecyclerView(mRecyclerView, mAdapter)
            setOnLoadTrackListObserver()
            initTouchHelper(mAdapter, mRecyclerView)
        }
        
        return rootView
    }
    
    private fun setOnLoadTrackListObserver() {
        mViewModel.trackList.observe(viewLifecycleOwner, Observer { trackList ->
            trackList?.also {
                mAdapter.setTracks(it)
                scrollToPositionIfNeed(mRecyclerView)
            }
        })
    }
    
    private fun initTouchHelper(adapter: TrackTouchHelperCallbackAdapter, rv: RecyclerView) {
        val callback = TrackTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        
        touchHelper.attachToRecyclerView(rv)
    }
    
    override fun onSetTracks(adapter: TrackAdapter) {
        mListener?.onSetTracks(adapter)
    }
    
    override fun onTrackClick(track: Track, adapter: TrackAdapter) {
        mListener?.onTrackClick(track, adapter)
    }
    
    override fun onDeleteTrack(track: Track, tracksForUpdate: List<Track>) {
        mViewModel.deleteTrack(track, tracksForUpdate)
    }
    
    override fun onTrackDragged(tracksForUpdate: List<Track>) {
        mViewModel.updatePositionsAfterDragging(tracksForUpdate)
    }
    
    override fun onStopPlayer() {
        mListener?.onStopPlayer()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
    
    companion object {
        @JvmStatic
        fun newInstance(playlistId: Long, scrollToPosition: Int? = null) =
            PlaylistFragment().apply {
                arguments = Bundle().apply {
                    putLong(PLAYLIST_ID, playlistId)
                    scrollToPosition?.let { putInt(SCROLL_TO_POSITION, it) }
                }
            }
    }
}
