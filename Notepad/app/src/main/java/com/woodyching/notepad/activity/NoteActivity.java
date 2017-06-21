package com.woodyching.notepad.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.woodyching.notepad.R;
import com.woodyching.notepad.adapter.NoteAdapter;
import com.woodyching.notepad.bean.Note;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static NoteActivity instance;

    public static final String NOTE_TITLE = "note_name";

    public static final String NOTE_IMAGE_PATH = "note_image_path";

    public static final String NOTE_CONTENT = "note_content";

    public static final String NOTE_ID = "note_id";

    public static final String NOTE_POSITION = "note_position";

    public static final String NOTE_STATE = "note_state";   //状态码用于判断是通过创建进入(0)便签还是通过点击(1)

    public static final int CHOOSE_PHOTO = 2;

    CollapsingToolbarLayout collapsingToolbar;

    ImageView noteImageView,note_image_view;

    TextView noteContentTextView;

    EditText noteContentEditText;

    FloatingActionButton fab;

    String noteTitle;
    String noteImagePath ;
    String noteContext;
    int noteID;
    int notePosition ;
    int noteState ;

    private static final String TAG = "NoteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        instance = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        noteTitle = intent.getStringExtra(NOTE_TITLE);
        noteImagePath = intent.getStringExtra(NOTE_IMAGE_PATH);
        noteContext = intent.getStringExtra(NOTE_CONTENT);
        noteID = intent.getIntExtra(NOTE_ID,-1);
        notePosition = intent.getIntExtra(NOTE_POSITION,-1);
        noteState = intent.getIntExtra(NOTE_STATE,1);

        initLayout();

    }

    public void initLayout(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        noteImageView = (ImageView) findViewById(R.id.note_image_view);
        noteContentTextView = (TextView) findViewById(R.id.note_context_text);
        noteContentEditText = (EditText) findViewById(R.id.note_context_edit_text);
        note_image_view = (ImageView) findViewById(R.id.note_image_view);


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(noteTitle);
//        Glide.with(this).load(noteImageID).into(noteImageView);
        noteContentTextView.setText(noteContext);
        noteContentEditText.setFocusable(true);

        int[] pic = {R.drawable.bg1,R.drawable.bg2,R.drawable.bg3,R.drawable.bg4,R.drawable.bg5,R.drawable.bg6,R.drawable.bg7,R.drawable.bg8,R.drawable.bg9
                ,R.drawable.bg10,R.drawable.bg11,R.drawable.bg12,R.drawable.bg13,R.drawable.bg14,R.drawable.bg15};



        if (noteImagePath.equals("default")){
            int i = noteID%15;
            note_image_view.setImageBitmap(((BitmapDrawable) getResources().getDrawable(pic[i])).getBitmap());

            MainActivity.instance.getNoteList().get(notePosition).setImagePath("random"+noteID%15);
            MainActivity.instance.files.set(notePosition,new File("random"+noteID%15));

            Note tt = new Note();
            tt.setImagePath("random"+noteID%15);
            tt.update(noteID);

        } else if(noteImagePath.equals("random"+noteID%15)){
            int i = noteID%15;
            note_image_view.setImageBitmap(((BitmapDrawable) getResources().getDrawable(pic[i])).getBitmap());
        } else{
            FileInputStream localStream = null;
            try {
                localStream = openFileInput("noteID_"+noteID+".jpg");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap b2 = BitmapFactory.decodeStream(localStream);
            note_image_view.setImageBitmap(b2);
        }


        collapsingToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(NoteActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.
                        PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(NoteActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    openAlbum();
                }

            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab_note);




        fab.setOnClickListener(new View.OnClickListener() {
            String temp_1 = noteContentTextView.getText().toString();
            @Override
            public void onClick(View v) {
                switch (noteContentTextView.getVisibility()){
                    case View.VISIBLE:  //非编辑-即将编辑状态
                        Log.d(TAG, "onClick: View.VISIBLE!");
                        temp_1 = noteContentTextView.getText().toString();

                        noteContentEditText.setText(noteContentTextView.getText().toString());
                        noteContentTextView.setVisibility(View.INVISIBLE);
                        noteContentEditText.setVisibility(View.VISIBLE);

                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_36dp));

                        noteContentEditText.requestFocus();
                        InputMethodManager imm = (InputMethodManager) noteContentEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

                        break;
                    case View.INVISIBLE:    //编辑-即将保存状态
                        Log.d(TAG, "onClick: View.INVISIBLE!");
                        InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   //收起键盘
                        imm2.hideSoftInputFromWindow(noteContentEditText.getWindowToken(), 0);

                        noteContentTextView.setText(noteContentEditText.getText().toString());
                        noteContentEditText.setVisibility(View.INVISIBLE);
                        noteContentTextView.setVisibility(View.VISIBLE);

                        MainActivity.instance.getNoteList().get(notePosition).setContent(noteContentEditText.getText().toString());
                        List<Note> notes_1 = DataSupport.select("ID").where("ID = ?",noteID+"").find(Note.class);

                        Note tt = new Note();
                        tt.setContent(noteContentEditText.getText().toString());
                        tt.update(noteID);

                        fab.setImageDrawable(getResources().getDrawable(R.drawable.pen));

                        MainActivity.instance.flush();

                        break;
                    default:
                }
            }
        });

        switch (noteState){
            case 0:
                break;
            case 1:
                break;
            default:
                break;
        }
    }

    public void click(){
        this.fab.performClick();
    }


    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"Denied!",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO :
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                        Log.d(TAG, "onClick: SettingsActivity"+data);
                    }
                    else {
                        handleImageBeforeKikat(data);
                    }
                }
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"),Long.valueOf(docId));
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }

        Intent intent = getIntent();
        String noteImagePath = intent.getStringExtra(NOTE_IMAGE_PATH);
        int noteID = intent.getIntExtra(NOTE_ID,-1);
        int notePosition = intent.getIntExtra(NOTE_POSITION,-1);

        try {
//            Bitmap photo = ((BitmapDrawable)getResources().getDrawable(R.drawable.md2)).getBitmap();
            Bitmap photo = BitmapFactory.decodeFile(imagePath);
            FileOutputStream localFileOutputStream1 = openFileOutput("noteID_"+noteID+".jpg", 0);
            Bitmap.CompressFormat localCompressFormat = Bitmap.CompressFormat.JPEG;
            photo.compress(localCompressFormat, 100, localFileOutputStream1);
            localFileOutputStream1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileInputStream localStream = null;
        try {
            localStream = openFileInput("noteID_"+noteID+".jpg");
            Bitmap b2 = BitmapFactory.decodeStream(localStream);
            note_image_view.setImageBitmap(b2);

            Context context = NoteActivity.this;
            File file=context.getFilesDir();
            String path=file.getAbsolutePath();
            MainActivity.instance.getNoteList().get(notePosition).setImagePath("noteID_"+noteID+".jpg");
            MainActivity.instance.files.set(notePosition,new File(path+"//noteID_"+noteID+".jpg"));

            Note tt = new Note();
            tt.setImagePath("noteID_"+noteID+".jpg");
            tt.update(noteID);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleImageBeforeKikat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri , String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            note_image_view.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                AlertDialog.Builder aa = new AlertDialog.Builder(this);
                aa.setMessage(getResources().getString(R.string.delect_info));
                aa.setNegativeButton(getResources().getString(R.string.delete_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        int notePosition = intent.getIntExtra(NOTE_POSITION,-1);
                        int id = intent.getIntExtra(NOTE_ID,-1);
                        Note note = NoteAdapter.instance.getNote(notePosition);
                        DataSupport.delete(Note.class,id);
                        MainActivity.instance.delete(notePosition);
                        finish();
                    }
                });
                aa.setPositiveButton(getResources().getString(R.string.delete_negative),null);
                aa.show();
                break;
            case R.id.share:
                break;
            case R.id.edit_title:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final View view = LayoutInflater.from(NoteActivity.this).inflate(R.layout.edit_title_dialog,null);
                final EditText ed = (EditText) view.findViewById(R.id.title);

                ed.requestFocus();

                builder.setView(view);
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        final int noteID = intent.getIntExtra(NOTE_ID,-1);
                        final int notePosition = intent.getIntExtra(NOTE_POSITION,-1);

                        Note tt = new Note();
                        tt.setTitle(ed.getText().toString());
                        tt.update(noteID);

                        MainActivity.instance.getNoteList().get(notePosition).setTitle(ed.getText().toString());
                        MainActivity.instance.flush();
                        collapsingToolbar = (CollapsingToolbarLayout)
                                findViewById(R.id.collapsing_toolbar);
                        if(ed.getText().toString().equals("")){
                            Log.d(TAG, "onClick1: SettingsActivity");
                            collapsingToolbar.setTitle(" ");
                        }
                        else
                            collapsingToolbar.setTitle(ed.getText().toString());


                    }
                });
                builder.setPositiveButton("取消", null);

                builder.show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }




}
