package com.myapp.mybutler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.FileNotFoundException


class DirectoryViewer : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_directoryviewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val directory=context?.filesDir.toString()

        val list=READFILELIST(directory)

        ListAddItems(list,view)

        view.findViewById<TextView>(R.id.txtDir).text=directory

    }

    fun READFILELIST(dir:String):Array<String>?{
        try{
            val file= File(dir)
            return file.list()
        }catch(e:FileNotFoundException){
            return null
        }
    }

    fun ListAddItems(f_list:Array<String>?,view:View){

        val SV=view.findViewById<View>(R.id.scrollview)as ViewGroup

        SV.removeAllViews()

        if(f_list==null)return

        var count=0

        //f_listの件数分ループする
        for(i in f_list.indices){
            //f_list[i]がtxtだった場合
            if(f_list[i].lastIndexOf(".txt")!=-1){
                layoutInflater.inflate(R.layout.layout_file,SV)
                val layout=SV.getChildAt(count)as ConstraintLayout
                count++
                layout.tag = count
                (layout.getChildAt(1)as TextView).text=hideExtention(f_list[i])
                layout.setOnClickListener{
                    findNavController().navigate(R.id.action_DirectoryViewer_self)
                }
            }
            //f_list[i]がpngだった場合
            else if(f_list[i].lastIndexOf(".png")!=-1){

            }
            //f_list[i]がfolderだった場合
            else{
                layoutInflater.inflate(R.layout.layout_folder,SV)
                val layout=SV.getChildAt(count)as ConstraintLayout
                count++
                layout.tag = count
                (layout.getChildAt(1)as TextView).text=hideExtention(f_list[i])
                layout.setOnClickListener{
                    findNavController().navigate(R.id.action_DirectoryViewer_self)
                }
            }
        }
    }

    fun hideExtention(str:String):String{
        val point:Int=str.lastIndexOf(".")
        if(point!=-1){
            return str.substring(0,point)
        }
        return str
    }

}