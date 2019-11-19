package com.jonnydev.bmp.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jonnydev.bmp.R
import com.jonnydev.bmp.adapter.TrackAdapter

const val SCROLL_TO_POSITION = "scroll_to_position"

abstract class BaseTracksFragment : Fragment() {
    protected var mListener: OnFragmentInteractionListener? = null
    protected var mScrollToPosition: Int? = null
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener.")
        }
    }
    
    protected fun initRecyclerView(rv: RecyclerView, trackAdapter: TrackAdapter) {
        with(rv) {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            setHasFixedSize(true)
        }
    }
    
    protected fun scrollToPositionIfNeed(rv: RecyclerView) {
        mScrollToPosition?.let { position ->
            rv.post { rv.smoothScrollToPosition(position) }
            mScrollToPosition = null
        }
    }
    
    protected fun getRootView(inflater: LayoutInflater, container: ViewGroup?) =
        inflater.inflate(R.layout.fragment_track_list, container, false)
    
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
}
