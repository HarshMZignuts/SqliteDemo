package com.example.sqlitedemo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    lateinit var db : SQLiteDatabase
    lateinit var adapter: SimpleCursorAdapter
    lateinit var rs : Cursor
    lateinit var search : SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var helper =  MyHelper(applicationContext)
        db = helper.readableDatabase
        var name =  findViewById<EditText>(R.id.et_name)
        var meaning = findViewById<EditText>(R.id.et_meaning)
        var first = findViewById<Button>(R.id.btn_first)
        var next = findViewById<Button>(R.id.btn_next)
        var prev = findViewById<Button>(R.id.btn_prev)
        var last =  findViewById<Button>(R.id.btn_last)
        var insert =  findViewById<Button>(R.id.btn_insert)
        var update =  findViewById<Button>(R.id.btn_update)
        var delete = findViewById<Button>(R.id.btn_delete)
        var clear = findViewById<Button>(R.id.btn_clear)
        var viewall =  findViewById<Button>(R.id.btn_viewall)
        search = findViewById(R.id.serchview)
        var list =  findViewById<ListView>(R.id.listvieew)



        rs  = db!!.rawQuery("SELECT * FROM ACTABLE ORDER BY NAME",null)

        adapter =  SimpleCursorAdapter(applicationContext,android.R.layout.simple_expandable_list_item_2,rs,
                                            arrayOf("NAME","MEANING"),
                                            intArrayOf(android.R.id.text1,android.R.id.text2),0
        )
        list.adapter = adapter

        registerForContextMenu(list)


        viewall.setOnClickListener {

            adapter.notifyDataSetChanged()

            search.queryHint = "Search Among the ${rs!!.count} Data"
            search.visibility = View.VISIBLE
            list.visibility = View.VISIBLE


        }
            search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                   return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {

                    rs=db.rawQuery("SELECT * FROM ACTABLE WHERE NAME LIKE '%$p0%' OR MEANING LIKE %$p0%'",null)
                    adapter.changeCursor(rs)
                    return false
                }
            })




        insert.setOnClickListener {

            var cv = ContentValues()
            cv.put("NAME",name.text.toString())
            cv.put("MEANING",meaning.text.toString())
            db.insert("ACTABLE",null,cv)
            rs!!.requery()

            var ad = AlertDialog.Builder(this)
            ad.setTitle("Insert..")
            ad.setMessage("Data Iserted Successfully....!!!")
            ad.setPositiveButton("Ok",DialogInterface.OnClickListener { dialogInterface, i ->
                name.setText("")
                meaning.setText("")
                name.requestFocus()
            })
            ad.show()



        }

        update.setOnClickListener {
            var cv = ContentValues()
            cv.put("NAME",name.text.toString())
            cv.put("MEANING",meaning.text.toString())
            db.update("ACTABLE",cv,"_id = ?", arrayOf(rs!!.getString(0)))
            rs.requery()

            var ad = AlertDialog.Builder(this)
            ad.setTitle("Update..")
            ad.setMessage("Data Updated Successfully....!!!")
            ad.setPositiveButton("Ok",DialogInterface.OnClickListener { dialogInterface, i ->
                if(rs.moveToFirst()){

                    name.setText(rs.getString(1))
                    meaning.setText(rs.getString(2))


                }
            })
            ad.show()

        }

        delete.setOnClickListener {

            db.delete("ACTABLE","_id = ?",arrayOf(rs!!.getString(0)))
            rs.requery()
            var ad = AlertDialog.Builder(this)
            ad.setTitle("Delete..")
            ad.setMessage("Data Deleted Successfully....!!!")
            ad.setPositiveButton("Ok",DialogInterface.OnClickListener { dialogInterface, i ->
                if(rs.moveToFirst()){

                    name.setText(rs.getString(1))
                    meaning.setText(rs.getString(2))


                }
                else
                {
                    name.setText("No data Found")
                    meaning.setText("No data Found")

                }

            })
            ad.show()
        }
        clear.setOnClickListener {
            name.setText("")
            meaning.setText("")
            name.requestFocus()
        }




        first.setOnClickListener {

            if(rs!!.moveToFirst())
            {
                name.setText(rs.getString(1))
                meaning.setText(rs.getString(2))
            }
            else
            {
                Toast.makeText(applicationContext,"Nodata Found",Toast.LENGTH_LONG).show()

            }
        }
        last.setOnClickListener {

            if(rs!!.moveToLast())
            {
                name.setText(rs.getString(1))
                meaning.setText(rs.getString(2))
            }
            else
            {
                Toast.makeText(applicationContext,"Nodata Found",Toast.LENGTH_LONG).show()

            }

        }
        next.setOnClickListener {
            if(rs!!.moveToNext())
            {

                name.setText(rs.getString(1))
                meaning.setText(rs.getString(2))
            }
            else if(rs!!.moveToFirst())
            {
                name.setText(rs.getString(1))
                meaning.setText(rs.getString(2))
            }
            else
            {
                Toast.makeText(applicationContext,"Nodata Found",Toast.LENGTH_LONG).show()

            }
        }
        prev.setOnClickListener {
            if(rs!!.moveToPrevious())
            {

                name.setText(rs.getString(1))
                meaning.setText(rs.getString(2))
            }
            else if(rs!!.moveToLast())
            {
                name.setText(rs.getString(1))
                meaning.setText(rs.getString(2))
            }
            else
            {
                Toast.makeText(applicationContext,"Nodata Found",Toast.LENGTH_LONG).show()

            }
        }

    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(1,11,1,"Delete")
        menu?.setHeaderTitle("Removing data")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        db.delete("ACTABLE","_id = ?",arrayOf(rs!!.getString(0)))
        rs.requery()
        adapter.notifyDataSetChanged()
        search.queryHint = "Search Among the ${rs!!.count} Data"
        return super.onContextItemSelected(item)
    }

}


