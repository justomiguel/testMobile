package com.jmv.frre.moduloestudiante;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ListarExamenes extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_examenes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listar_examenes, menu);
        return true;
    }
    
}
