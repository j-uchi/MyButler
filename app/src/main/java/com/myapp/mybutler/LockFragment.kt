package com.myapp.mybutler

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import java.lang.ClassCastException

class LockFragment : Fragment() {

    private var _activityListener:OnLockListener?=null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _activityListener?.onUnlockDialog()
    }

    interface OnLockListener{
        fun onUnlockDialog()
    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        try{
            _activityListener=context as OnLockListener
        }catch(e: ClassCastException){
        }
    }
}