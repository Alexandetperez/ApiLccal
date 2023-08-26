package com.breynnerperez.camara;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FormularioActivity extends AppCompatActivity {
    Button Foto, Enviar;
    ImageView imageView;
    EditText Codigó, Nombre, Precio, Descripción;
    String directorioFoto;
    Bitmap bitmap;
    Spinner spnMarca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        Foto=findViewById(R.id.Foto);
        imageView=findViewById(R.id.imageView);
        Codigó=findViewById(R.id.Codigó);
        Nombre=findViewById(R.id.Nombre);
        Precio=findViewById(R.id.Precio);
        Descripción=findViewById(R.id.Descripción);
        Enviar=findViewById(R.id.Enviar);
        Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamara();
            }
        });
        Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarDatos();
            }


        });
        ConsultarMarca();
    }

    private void ConsultarMarca() {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get("http://192.168.172.1/tienda/wsercicio/marcas/listar.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(respuesta);
                    List listaMarcas = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Marca miMarca = new Marca();
                        miMarca.setId(Integer.parseInt(jsonArray.getJSONObject(i).get("id").toString()));

                        miMarca.setNombre((String) jsonArray.getJSONObject(i).get("Nombre"));
                        listaMarcas.add(miMarca);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter<>(FormularioActivity.this, android.R.layout.simple_dropdown_item_1line, listaMarcas);
                    spnMarca.setAdapter(arrayAdapter);
                    } catch (JSONException e) {
                    //throw new RuntimeException(e);
                    Toast.makeText(FormularioActivity.this, "Error en el JSON", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(FormularioActivity.this, "Fallo en la conexion", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void enviarDatos() {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        //Capturar datos para enviar al servidor
        RequestParams datosEnviar = new RequestParams();
        datosEnviar.put("Codigo", Codigó.getText().toString());
        datosEnviar.put("Nombre", Nombre.getText().toString());
        datosEnviar.put("Precio", Precio.getText().toString());
        datosEnviar.put("Descripcion", Descripción.getText().toString());
        datosEnviar.put("idMarca", spnMarca.getSelectedItemId());
        //Imagen
        String imagen = imagenAString(bitmap);
        datosEnviar.put("imagen", imagen);

        httpClient.post("http://192.168.172.1/tienda/wservicio/insertar.php", datosEnviar, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(FormularioActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(FormularioActivity.this, "Fallo de conexion", Toast.LENGTH_SHORT).show();

            }
        });

    }
    private String imagenAString(Bitmap bitmap) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
        byte[] imgByte = arrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }
    private void abrirCamara(){
        Intent intento=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imgFile=null;
        try{
            imgFile=crearImagen();
        }catch (IOException e){
            Log.e("Error File",e.getMessage());
        }
        camaraLauncher.launch(intento);
    }
    ActivityResultLauncher<Intent> camaraLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //validar con requestsCode y resultCode
            Bundle bundle=result.getData().getExtras();
            Bitmap miImagen=(Bitmap) bundle.get("data");
            imageView.setImageBitmap(miImagen);
        }
    });
    //metodo para crear file
    public File crearImagen() throws IOException {
        File dirAlmacena=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String imgFileFecha= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgNameFile="JPG_"+imgFileFecha;
        File imagen=File.createTempFile(imgNameFile,".jpg", dirAlmacena);

        return imagen;
    }
}