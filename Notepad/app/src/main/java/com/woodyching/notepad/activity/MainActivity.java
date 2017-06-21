package com.woodyching.notepad.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v4.view.GravityCompat;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.woodyching.notepad.R;
import com.woodyching.notepad.bean.User;
import com.woodyching.notepad.adapter.NoteAdapter;
import com.woodyching.notepad.bean.Note;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int CHOOSE_BACKGROUP_PHOTO = 2;
    public static final int CHOOSE_HEADSHOT_PHOTO = 3;

    public static MainActivity instance = null;

    private DrawerLayout mDrawerLayout;
    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    View headerView;

    private List<Note> noteList = new ArrayList<>();

    User user0;
    User user;
    TextView username;
    ImageView backgroup;
    ImageView headshot;

    String path;
    File file;
    List<File> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = MainActivity.this;
        file=context.getFilesDir();
        path=file.getAbsolutePath();

        initNoteList();
        initDrawerLayout();
        initUser();




    }

    public void  initNoteList(){
        noteList = DataSupport.findAll(Note.class);
        files = new ArrayList<File>();
        for (Note note : noteList){files.add(new File(path+"//"+note.getImagePath()));}
    }

    private static final String TAG = "MainActivity";
    public void initDrawerLayout(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar(); //调用已设定的ActionBar，也就是ToolBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  // 让导航栏按钮显示出来，HomeAsUp也就是Toolbar最左侧的按钮
            actionBar.setHomeAsUpIndicator(R.drawable.menu_white); //设置导航栏的菜单按钮
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note1 = new Note();
                noteList.add(note1);
                File f = new File(path+"//"+note1.getImagePath());
                files.add(f);
                note1.save();
                int position = noteList.size()-1;

                Intent intent = new Intent(MainActivity.this,NoteActivity.class);   //点击添加按钮之后自动跳转到便签页面
                intent.putExtra(NoteActivity.NOTE_TITLE,note1.getTitle());
                intent.putExtra(NoteActivity.NOTE_CONTENT,note1.getContent());
                intent.putExtra(NoteActivity.NOTE_IMAGE_PATH,note1.getImagePath());
                intent.putExtra(NoteActivity.NOTE_ID,note1.getId());
                intent.putExtra(NoteActivity.NOTE_POSITION,position);
                intent.putExtra(NoteActivity.NOTE_STATE,0);
                startActivity(intent);

                adapter.notifyDataSetChanged();
            }
        });
        navigationView = (NavigationView) findViewById(R.id.nav_view);              //获取Navigation头部biew
        headerView = navigationView.getHeaderView(0);

//        Menu menu = navigationView.getMenu();

//        navigationView.getMenu().add(1,123431,0,"aFuck全部");
//        navigationView.getMenu().add(1,1234231,0,"bFuck");
//        navigationView.getMenu().add(0,12334231,2,"sFuck");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case 123431:
                        Log.d(TAG, "onNavigationItemSelected: Fuck onClick");
                        break;
                    case R.id.menu_setting:
                        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_about:
                        AlertDialog.Builder about = new AlertDialog.Builder(MainActivity.this);
                        try {
                            about.setMessage("Tips:\n长按即可删除\n\n版本号："+getVersionName().toString()+"\n" +
                                    "作者：Ching");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        about.show();
                        break;
                    case R.id.support:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.pay, null);
                        builder.setView(view);

                        builder.setPositiveButton("丑拒", null);
                        builder.setNegativeButton("好的", null);
                        builder.show();
                        break;
                    case R.id.all_class:
//                        AlertDialog.Builder aa = new AlertDialog.Builder(MainActivity.this)
//                                .setMessage("很抱歉，分类功能暂未开放");
//                        aa.show();
                        break;
                    case R.id.add_class:
                        Log.d(TAG, "onNavigationItemSelected: Fuck"+ navigationView.getMenu().getItem(0).getOrder());
                        navigationView.getMenu().add(1,123431,0,"aFuck全部");
//                        navigationView.getMenu().add(1,1234231,3,"bFuck");
//                        navigationView.getMenu().add(0,12334231,2,"sFuck");
//                        AlertDialog.Builder bb = new AlertDialog.Builder(MainActivity.this)
//                                .setMessage("很抱歉，分类功能暂未开放");
//                        bb.show();
                        flush();

                        break;

                }
                return true;
            }
        });



    }

    public void initUser(){
        if(DataSupport.count(User.class)==0){
            user0 = new User();
            user0.save();
        }


        user = DataSupport.findFirst(User.class);
        username = (TextView) headerView.findViewById(R.id.header_username);
        backgroup = (ImageView) headerView.findViewById(R.id.backgroup);
        headshot = (ImageView) headerView.findViewById(R.id.headshot);
        TextView email = (TextView) headerView.findViewById(R.id.mail);

        username.setText(user.getUsername().toString());

        email.setText(user.getEmail().toString());

        backgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.
                        PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    openAlbum(CHOOSE_BACKGROUP_PHOTO);
                }
            }
        });

        headshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.
                        PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    openAlbum(CHOOSE_HEADSHOT_PHOTO);
                }
            }
        });

        if (user.getBackgroupPath().equals("default")){
            backgroup.setImageDrawable(getResources().getDrawable(R.drawable.bg9));
        } else {
            backgroup.setImageBitmap(BitmapFactory.decodeFile(path+"/"+user.getBackgroupPath()));
        }
        if (user.getHeadShotPath().equals("default")){
            headshot.setImageDrawable(getResources().getDrawable(R.drawable.hs));
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setAdapter(NoteAdapter adapter) {
        this.adapter = adapter;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public NoteAdapter getAdapter() {
        return adapter;
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //对HomeAsUP按钮的点击事件进行处理
        switch (item.getItemId()){
            case android.R.id.home:     //HomeAsUp按钮的id永远都是R.id.home
                mDrawerLayout.openDrawer(GravityCompat.START);  //让HSU显示出来
                break;
            case R.id.about:
                AlertDialog.Builder about = new AlertDialog.Builder(MainActivity.this);
                try {
                    about.setMessage("Tips:\n长按即可删除\n\n版本号："+getVersionName().toString()+"\n" +
                            "作者：Ching");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                about.show();
                break;
            case R.id.deleteAll:
                noteList.clear();
                for (File f : files){
                    if (f.exists()){
                        f.delete();
                    }
                }
                files.clear();
                DataSupport.deleteAll(Note.class);
                adapter.notifyDataSetChanged();
                break;
            case R.id.update:
                AlertDialog.Builder a1 =new  AlertDialog.Builder(MainActivity.this);
                a1.setTitle("更新日志");
                a1.setMessage(getResources().getString(R.string.update_log));
                a1.show();
                break;
            default:
        }
        return true;
    }

    public void flush(){
        adapter.notifyDataSetChanged();
    }   //刷新记事本列表

    @Override
    protected void onResume() {
        super.onResume();
        User user = DataSupport.findFirst(User.class);
        TextView username = (TextView) headerView.findViewById(R.id.header_username);
        username.setText(user.getUsername().toString());
        TextView email = (TextView) headerView.findViewById(R.id.mail);
        email.setText(user.getEmail().toString());
    }

    private void openAlbum(int choose){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,choose);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    openAlbum(2);
                }else {
                    Toast.makeText(this,"没有权利，就没有个性!",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHOOSE_BACKGROUP_PHOTO :
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data,"user_backgroup");
                    }
                    else {
                        handleImageBeforeKikat(data);
                    }
                }
                break;
            case CHOOSE_HEADSHOT_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data,"user_headshot");
                    }
                    else {
                        handleImageBeforeKikat(data);
                    }
                }
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data,String ss){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }

        try {
            Bitmap photo = BitmapFactory.decodeFile(imagePath);
            FileOutputStream localFileOutputStream1 = openFileOutput(ss+".jpg", 0);
            Bitmap.CompressFormat localCompressFormat = Bitmap.CompressFormat.JPEG;
            photo.compress(localCompressFormat, 100, localFileOutputStream1);
            localFileOutputStream1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileInputStream localStream = null;
        try {
            localStream = openFileInput(ss+".jpg");
            Bitmap b2 = BitmapFactory.decodeStream(localStream);

            if (ss.equals("user_headshot"))
                headshot.setImageBitmap(b2);
            else if (ss.equals("user_backgroup"))
                backgroup.setImageBitmap(b2);

            Context context = MainActivity.this;
            final File file=context.getFilesDir();
            final String path=file.getAbsolutePath();


            MainActivity.instance.user.setBackgroupPath(ss+".jpg");

            User u = DataSupport.findFirst(User.class);
            u.setBackgroupPath(ss+".jpg");
            u.save();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleImageBeforeKikat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
//        displayImage(imagePath);
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



    public void delete(int position){
        noteList.remove(position);
        if (files.get(position).exists()){
            files.get(position).delete();
        }
        files.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String getVersionName() throws Exception
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }


}
