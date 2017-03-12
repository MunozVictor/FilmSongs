package com.example.vicapps.filmsongs_victor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    SoundPool sp;
    AssetManager assetManager;
    int soundId;
    AssetFileDescriptor descriptor;
    ListView lista;
    Random r = new Random();
    int posRepr;

    ArrayList<Pelicula> peliculas = new ArrayList<>();
    ArrayList<String>titulosPeliculas=new ArrayList<>();
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lista= (ListView) findViewById(R.id.listView);

        initArrays();

        buildAdapter();



        //Utilizo esta metodologia de reproduccion de sonidos debido a que los sonidos son de corta duración
        //y apenas ocuparan memoria en el telefono. Al no ser archivos de gran tamaño y pocos, utilizo la clase
        // soundpool la cual carga los archivos en memoria. Ademas borro de memoria
        //cuando acaba , con el metodo .unload que se ejecuta en el onPause

        assetManager=this.getAssets();
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            sp = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(aa).build();
            lanzarCancion();
        }else{
            sp = new SoundPool(1, AudioManager.STREAM_MUSIC,1);
            lanzarCancion();
        }
    }

    private void buildAdapter() {
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,titulosPeliculas);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(peliculas.get(posRepr).getTitulo().equals(titulosPeliculas.get(i))){
                    Log.i("Es","Es la cancion");
                    peliculas.remove(posRepr);
                    titulosPeliculas.remove(i);

                    if(peliculas.size()>0){
                        buildAdapter();
                        lanzarCancion();
                        int t = peliculas.size();
                        Toast.makeText(getBaseContext(),getResources().getQuantityString(R.plurals.msg_acierto,t,t),Toast.LENGTH_SHORT).show();
                    }else {
                        showDialog();
                    }
                }else{
                    Log.i("No","No Es la cancion");
                    Toast.makeText(getBaseContext(),R.string.falloToast,Toast.LENGTH_SHORT).show();
                    lanzarCancion();
                }
            }
        });
    }
    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage(R.string.dialogMsg)
                        .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                initArrays();
                                buildAdapter();
                                lanzarCancion();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        });

        alertDialogBuilder.show();
    }



    public void initArrays() {
        peliculas.add(new Pelicula("La Sirenita","Bajo el mar.mp3"));
        peliculas.add(new Pelicula("El Rey León","Hakuna Matata.mp3"));
        peliculas.add(new Pelicula("Toy Story 3","Hay un amigo en mi.mp3"));
        peliculas.add(new Pelicula("Blancanieves y los 7 Enanitos","Hi-ho.mp3"));
        peliculas.add(new Pelicula("El Libro de la Selva","La marcha de los elefantes.mp3"));
        peliculas.add(new Pelicula("Frozen","Let it Go.mp3"));
        peliculas.add(new Pelicula("Aladdin","Un mundo ideal.mp3"));

        for(Pelicula p : peliculas){
            titulosPeliculas.add(p.toString());
        }
    }

    private void lanzarCancion() {
        posRepr= r.nextInt(peliculas.size());
        String cancionEnRepr = peliculas.get(posRepr).getCancion();
        try{
            descriptor = assetManager.openFd(cancionEnRepr);
            soundId = sp.load(descriptor, 1);
            sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener(){
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    sp.play(soundId,1,1,0,0,1);
                }
            });
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sp.unload(soundId);
    }
}
