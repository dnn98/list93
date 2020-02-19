package com.example.list93;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db=null;
    private Cursor constantsCursor=null;
    private ListView list1;
    private static final int ADD_ID = Menu.FIRST+1;
    private static final int DELETE_ID = Menu.FIRST+3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list1 = findViewById(R.id.list1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db=new DatabaseHelper(this);
        constantsCursor=db
                .getReadableDatabase()
                .rawQuery("SELECT _ID, title, value "+
                                "FROM constants ORDER BY title",
                        null);

        ListAdapter adapter=new SimpleCursorAdapter(this,
                R.layout.row, constantsCursor,
                new String[] {DatabaseHelper.TITLE,
                        DatabaseHelper.VALUE},
                new int[] {R.id.title, R.id.value});

        list1.setAdapter(adapter);
        registerForContextMenu(list1);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        constantsCursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, ADD_ID, Menu.NONE, "Add")
                .setIcon(R.drawable.add)
                .setAlphabeticShortcut('a');

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ADD_ID:
                add();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Delete")
                .setAlphabeticShortcut('d');
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info=
                        (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

                delete(info.id);
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    private void add() {
        LayoutInflater inflater=LayoutInflater.from(this);
        View addView=inflater.inflate(R.layout.add_edit, null);
        final DialogWrapper wrapper=new DialogWrapper(addView);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_title)
                .setView(addView)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                processAdd(wrapper);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // ignore, just dismiss
                            }
                        })
                .show();
    }

    private void delete(final long rowId) {
        if (rowId>0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_title)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    processDelete(rowId);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // ignore, just dismiss
                                }
                            })
                    .show();
        }
    }

    private void processAdd(DialogWrapper wrapper) {
        ContentValues values=new ContentValues(2);

        values.put(DatabaseHelper.TITLE, wrapper.getTitle());
        values.put(DatabaseHelper.VALUE, wrapper.getValue());

        db.getWritableDatabase().insert("constants", DatabaseHelper.TITLE, values);
        constantsCursor.requery();
    }

    private void processDelete(long rowId) {
        String[] args={String.valueOf(rowId)};

        db.getWritableDatabase().delete("constants", "_ID=?", args);
        constantsCursor.requery();
    }

    class DialogWrapper {
        EditText titleField=null;
        EditText valueField=null;
        View base=null;

        DialogWrapper(View base) {
            this.base=base;
            valueField=(EditText)base.findViewById(R.id.value);
        }

        String getTitle() {
            return(getTitleField().getText().toString());
        }

        float getValue() {
            return(new Float(getValueField().getText().toString())
                    .floatValue());
        }

        private EditText getTitleField() {
            if (titleField==null) {
                titleField=(EditText)base.findViewById(R.id.title);
            }

            return(titleField);
        }

        private EditText getValueField() {
            if (valueField==null) {
                valueField=(EditText)base.findViewById(R.id.value);
            }

            return(valueField);
        }
    }




}
