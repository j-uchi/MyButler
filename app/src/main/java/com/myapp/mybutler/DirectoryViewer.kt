package com.myapp.mybutler

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileNotFoundException
import java.lang.ClassCastException


class DirectoryViewer : Fragment() {

    private val args:DirectoryViewerArgs by navArgs()

    private var _activityListener:OnActivityListener?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_directoryviewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.txtDir).text=args.DIRECTORY

        view.findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener{

        }
        view.findViewById<FloatingActionButton>(R.id.fab_close).setOnClickListener{

        }
        view.findViewById<FloatingActionButton>(R.id.fab_folder).setOnClickListener{
            CreateFolderDialog()
        }
        view.findViewById<FloatingActionButton>(R.id.fab_file).setOnClickListener{
            CreateFileDialog()
        }
        view.findViewById<FloatingActionButton>(R.id.fab_picture).setOnClickListener{
            _activityListener?.openGallery()
        }

    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        try{
            _activityListener=context as OnActivityListener
        }catch(e:ClassCastException){

        }
    }

    interface OnActivityListener{
        fun openGallery()
    }

    override fun onResume(){
        super.onResume()
        Reflesh()
    }

    fun READFILELIST(dir:String):Array<String>?{
        try{
            val file= File(dir)
            return file.list()
        }catch(e:FileNotFoundException){
            return null
        }
    }

    fun ListAddItems(f_list:Array<String>?,view:View?){

        if(f_list==null)return

        if(view==null)return

        val SV=view.findViewById<View>(R.id.scrollview)as ViewGroup

        SV.removeAllViews()

        var count=0

        //f_listの件数分ループする
        for(i in f_list.indices){
            //f_list[i]がtxtだった場合
            if(f_list[i].lastIndexOf(".txt")!=-1){
                layoutInflater.inflate(R.layout.layout_file,SV)
                val layout=SV.getChildAt(count)as ConstraintLayout
                count++
                layout.tag = count
                (layout.getChildAt(1)as TextView).text=adjustLength(hideExtention(f_list[i]))
                layout.setOnClickListener{
                }
                layout.setOnLongClickListener{
                    CreateOptionDialog(f_list[i])
                    true
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
                (layout.getChildAt(1)as TextView).text=adjustLength(hideExtention(f_list[i]))
                layout.setOnClickListener{
                    val action=DirectoryViewerDirections.actionDirectoryViewerSelf(args.DIRECTORY+"/"+f_list[i])
                    findNavController().navigate(action)
                }
                layout.setOnLongClickListener{
                    CreateOptionDialog(f_list[i])
                    true
                }
            }
        }
    }

    fun adjustLength(str:String):String{
        var filename:String=str
        if(filename.length>14){
            filename=filename.substring(0,14)
            filename+="…"
        }
        return filename
    }

    fun hideExtention(str:String):String{
        val point:Int=str.lastIndexOf(".")
        if(point!=-1){
            return str.substring(0,point)
        }
        return str
    }

    fun getExtention(str:String):String{
        val point:Int=str.lastIndexOf(".")
        if(point!=-1){
            return str.substring(point)
        }
        return ""
    }

    fun CreateFolderDialog(){
        val myedit = EditText(context)

        AlertDialog.Builder(context)
                .setTitle("新規フォルダ作成")
                .setView(myedit)
                .setPositiveButton("作成"){_,_ ->
                    CreateFolder(myedit.text.toString())
                    Reflesh()
                }
                .setNegativeButton("キャンセル"){_,_->
                }
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_format_list_bulleted_24)
                .show()
    }

    fun CreateFolder(str:String){
        if(str!=""){
            val file=File(context?.filesDir.toString()+args.DIRECTORY+"/"+str)
            file.mkdir()
        }
    }

    fun CreateFileDialog(){
        val myedit = EditText(context)

        AlertDialog.Builder(context)
                .setTitle("新規ファイル作成")
                .setView(myedit)
                .setPositiveButton("作成"){_,_ ->
                    CreateFile(myedit.text.toString())
                    Reflesh()
                }
                .setNegativeButton("キャンセル"){_,_->
                }
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_text_snippet_24)
                .show()
    }

    fun CreateFile(str:String){
        if(str!=""){
            val file=File(context?.filesDir.toString()+args.DIRECTORY+"/"+str+".txt")
            file.writeText(str)
        }
    }

    fun CreateOptionDialog(f_name:String){
        var title=f_name
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage("削除または名称変更しますか？")
                .setPositiveButton("削除"){_,_->
                    DeleteFile(f_name)
                    Reflesh()
                }
                .setNegativeButton("キャンセル"){_,_->
                }
                .setNeutralButton("名称変更"){_,_->
                    CreateRenameDialog(f_name)
                }
                .setIcon(R.drawable.ic_baseline_warning_24)
                .show()
    }

    fun CreateRenameDialog(f_name: String){
        val myedit = EditText(context)

        AlertDialog.Builder(context)
                .setTitle("名称変更")
                .setView(myedit)
                .setPositiveButton("変更"){_,_ ->
                    RenameFile(f_name,myedit.text.toString())
                    Reflesh()
                }
                .setNegativeButton("キャンセル"){_,_->
                }
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_spellcheck_24)
                .show()
    }

    fun RenameFile(f_name:String,new_name:String){
        val fOld=File(context?.filesDir.toString()+args.DIRECTORY+"/"+f_name)
        val fNew=File(context?.filesDir.toString()+args.DIRECTORY+"/"+new_name+getExtention(f_name))
        fOld.renameTo(fNew)
    }

    fun DeleteFile(f_name:String){
        val file=File(context?.filesDir.toString()+args.DIRECTORY+"/"+f_name)
        if(file.delete()){
            Toast.makeText(context,f_name+"を削除しました",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(context,"削除に失敗しました",Toast.LENGTH_SHORT).show()
        }
    }

    fun Reflesh(){
        val list=READFILELIST(context?.filesDir.toString()+args.DIRECTORY)

        ListAddItems(list, view)
    }

}