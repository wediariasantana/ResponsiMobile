package wediariasantana.gmail.aplikasiakhirsmester;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Random;

import wediariasantana.gmail.tampilSemuaPgw;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Dibawah ini merupakan perintah untuk mendefinikan View
    private EditText editTextName;
    private EditText editTextDesg;
    private EditText editTextSal;

    private Button buttonAdd;
    private Button buttonView;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDesg = (EditText) findViewById(R.id.editTextDesg);
        editTextSal = (EditText) findViewById(R.id.editTextSalary);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonView = (Button) findViewById(R.id.buttonView);

        //Setting listeners to button
        buttonAdd.setOnClickListener(this);
        buttonView.setOnClickListener(this);

        cekBaca();
        cekTulis();
        //TODO 1 : mengaitkan object dengan komponen view
        Button camera= (Button) findViewById(R.id.open);
        iv=(ImageView)findViewById(R.id.img1);
        //TODO 2 : membuat event untuk tombol open untuk membuka kamera
        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO 3 : membuat intent untuk mengaksesk kamera dengan nilai kembaian berupa gambar bitmap request code 0
                Intent tp=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(tp,0);
            }
        });
    }
    //TODO 4 : membuat method untuk pengembailian nilai
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){

        //TODO 5 : mengirimkan data parameter ke super klas
        super.onActivityResult(requestCode,resultCode,data);

        //TODO 6 : menyeleksi kondisi apakah gambar jadi iambil menggunakan kamera atau tidak jika iya maka result ok yang akan tereksekusi jika tidak maka result censle yang akan tereksekusi
        if (resultCode==RESULT_OK) {
            //TODO 7 : tahap penampil gambar
            Bitmap bi = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bi);
            //TODO 8 : bagaian ini memanggil method untuk menyimpan gambar yang diambil kedalam memori external
            SaveImage(bi);
        }else if (resultCode==RESULT_CANCELED){
            //TODO 9 : menampilkan pesan jika kamera tidak jadi digunakan
            Toast.makeText(this, "kamera tidak jadi digunakan", Toast.LENGTH_LONG).show();
        }
    }
    private void SaveImage(Bitmap finalBitmap) {
        //TODO 10 : Bagian ini mengarahkan lokasi penyimpanan ke direktori external Picture, jika tidak ada folder tersebut maka akan dibuat saat itu juga
        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        //TODO 11 : menampilkan lokasi penyimpanan dalam bentuk Toast
        Toast.makeText(this, myDir.toString(), Toast.LENGTH_LONG).show();

        //TODO 12 memberi nama file secara acak
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        File file = new File (myDir,  "Image-"+ n +".jpg");
        if (file.exists ())
            file.delete ();

        //TODO 13 : memasukan data gambar kedalam file yang telah dibuat
        try {
            FileOutputStream out = new FileOutputStream(file);
            //TODO 14 : mengkopres data kedalam file jpg ter kompres (seperti yang kita tahu format jpg merupakan fil gambar ter kompresi)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            //TODO 15 : menutup file
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Ini adalah method untuk mengecek apkah external storage dapat ditulis atau tidak
    public void cekTulis() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(this,"Dapat ditulis",Toast.LENGTH_LONG).show();
        }else Toast.makeText(this,"Tidak dapat ditulis",Toast.LENGTH_LONG).show();
    }
    //Ini adalah method untuk mengecek apkah external storage dapat dibaca atau tidak
    public void cekBaca() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Toast.makeText(this,"Dapat dibaca",Toast.LENGTH_LONG).show();
        }else Toast.makeText(this,"Tidak dapat dibaca",Toast.LENGTH_LONG).show();
    }
    //Dibawah ini merupakan perintah untuk Menambahkan Pegawai (CREATE)
    private void addEmployee(){

        final String name = editTextName.getText().toString().trim();
        final String desg = editTextDesg.getText().toString().trim();
        final String sal = editTextSal.getText().toString().trim();

        class AddEmployee extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Menambahkan...","Tunggu...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(konfigurasi.KEY_EMP_NAMA,name);
                params.put(konfigurasi.KEY_EMP_POSISI,desg);
                params.put(konfigurasi.KEY_EMP_GAJIH,sal);

                RequsetHandler.RequestHandler rh = new RequsetHandler.RequestHandler();
                String res = rh.sendPostRequest(konfigurasi.URL_ADD, params);
                return res;
            }
        }

        AddEmployee ae = new AddEmployee();
        ae.execute();
    }


    public void onClick(View v) {
        if(v == buttonAdd){
            addEmployee();
        }

        if(v == buttonView){
            startActivity(new Intent(this, tampilSemuaPgw.class));
        }

    }
}