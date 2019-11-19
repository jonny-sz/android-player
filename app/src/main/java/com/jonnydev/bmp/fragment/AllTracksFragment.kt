package com.jonnydev.bmp.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.jonnydev.bmp.*
import com.jonnydev.bmp.action.MultiSelectionMode
import com.jonnydev.bmp.adapter.AllTracksAdapter
import com.jonnydev.bmp.adapter.TrackAdapter
import com.jonnydev.bmp.model.Track
import com.jonnydev.bmp.util.App
import com.jonnydev.bmp.util.RequestCodeGen
import com.jonnydev.bmp.util.TrackList
import com.jonnydev.bmp.viewmodel.AllTracksViewModel
import kotlinx.android.synthetic.main.fragment_track_list.view.*

private val SELECT_PLAYLIST_REQUEST_CODE by lazy { RequestCodeGen.generateCode() }

class AllTracksFragment :
    BaseTracksFragment(),
    AllTracksAdapter.AllTracksAdapterListener,
    MultiSelectionMode.MultiSelectionModeListener {
    
    private val mViewModel by lazy {
        ViewModelProvider(this)[AllTracksViewModel::class.java]
    }
    private lateinit var mAdapter: AllTracksAdapter
    private lateinit var mMultiSelectionMode: MultiSelectionMode
    private lateinit var mRecyclerView: RecyclerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
        activity?.title = getString(R.string.label_all_tracks)
        mAdapter = AllTracksAdapter(this)
        mMultiSelectionMode = MultiSelectionMode(this)
        
        if (mViewModel.isMultiSelectionMode)
            mMultiSelectionMode.start(activity!!)
        
        initRecyclerView(mRecyclerView, mAdapter)
        setOnLoadTrackListObserver()
        
        return rootView
    }
    
    private fun setOnLoadTrackListObserver() {
        mViewModel.trackList.observe(viewLifecycleOwner, Observer<TrackList> { trackList ->
            trackList?.also {
                it.sortByFolder()
                mAdapter.setTracks(it)
                scrollToPositionIfNeed(mRecyclerView)
            }
        })
    }
    
    private fun getMultiSelectionModeTitle(): String {
        return "${mViewModel.selectedTracks.size} ${App.context.getString(R.string.selected)}"
    }
    
    override fun onAddSelectedTrack(track: Track) {
        mViewModel.addSelectedTrack(track)
        mMultiSelectionMode.title = getMultiSelectionModeTitle()
    }
    
    override fun onRemoveSelectedTrack(track: Track) {
        mViewModel.removeSelectedTrack(track)
        
        if (mViewModel.selectedTracks.isEmpty()) {
            mMultiSelectionMode.finish()
        } else {
            mMultiSelectionMode.title = getMultiSelectionModeTitle()
        }
    }
    
    override fun onStartMultiSelectionMode() {
        mMultiSelectionMode.start(activity!!)
    }
    
    override fun onSetTracks(adapter: TrackAdapter) {
        mListener?.onSetTracks(adapter)
    }
    
    override fun onTrackClick(track: Track, adapter: TrackAdapter) {
        mListener?.onTrackClick(track, adapter)
    }
    
    override fun onStopPlayer() {
        mListener?.onStopPlayer()
    }
    
    override fun onCreateActionMode(mode: ActionMode?) {
        mViewModel.isMultiSelectionMode = true
        mode?.title = getMultiSelectionModeTitle()
    }
    
    override fun onActionItemClicked() {
        val intent = Intent(activity, PlaylistChooserActivity::class.java).apply {
            putExtra(EXTRA_CURRENT_PLAYLIST_ID, mAdapter.id)
        }
        startActivityForResult(intent, SELECT_PLAYLIST_REQUEST_CODE)
    }
    
    override fun onDestroyActionMode() {
        mViewModel.uncheckSelectedTracks()
        mAdapter.notifyDataSetChanged()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SELECT_PLAYLIST_REQUEST_CODE -> {
                data?.let { intent ->
                    if (resultCode == Activity.RESULT_OK) {
                        val playlistId = intent.getLongExtra(EXTRA_RESULT_PLAYLIST_ID, 0L)
                        val playlistTitle = intent.getStringExtra(EXTRA_RESULT_PLAYLIST_TITLE)
                        
                        if (playlistId != 0L) {
                            mViewModel.addSelectedTracksToPlaylist(playlistId, playlistTitle)
                        }
                        
                        mMultiSelectionMode.finish()
                    }
                }
            }
        }
        
        super.onActivityResult(requestCode, resultCode, data)
    }
    
    companion object {
        @JvmStatic
        fun newInstance(scrollToPosition: Int?) =
            AllTracksFragment().apply {
                arguments = Bundle().apply {
                    scrollToPosition?.let { putInt(SCROLL_TO_POSITION, it) }
                }
            }
    }
}
