package com.myapp.mybutler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class FileViewer : Fragment() {

    private val args:DirectoryViewerArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fileviewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.txtStr).text=READFILE()
    }

    private fun READFILE():String{
        var str=""
        try{
            val file= File(context?.filesDir.toString()+args.DIRECTORY)
            val scan= Scanner(file)
            while(scan.hasNextLine()){
                str+=scan.nextLine()
                if(scan.hasNextLine())str+="\n"
            }
            return str
        }catch(e: FileNotFoundException){
            return ""
        }
    }


}