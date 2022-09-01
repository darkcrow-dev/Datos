package externaldatabasedemo.javahelps.com.javahelps.com.datos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText txtCodigo;
    private EditText txtNombre;
    private TextView txtResultado;

    private Button btnInsertar;
    private Button btnActualizar;
    private Button btnEliminar;
    private Button btnConsultar;

    String numero1;
    String cod;
    String nom;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtenemos las referencias a los controles
        txtCodigo = (EditText)findViewById(R.id.txtReg);
        txtNombre = (EditText)findViewById(R.id.txtVal);
        txtResultado = (TextView)findViewById(R.id.txtResultado);


        btnInsertar = (Button)findViewById(R.id.btnInsertar);
        btnActualizar = (Button)findViewById(R.id.btnActualizar);
        btnEliminar = (Button)findViewById(R.id.btnEliminar);
        btnConsultar = (Button)findViewById(R.id.btnConsultar);

        txtCodigo.setText("");
        txtNombre.setText("");

        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        DatabaseOpenHelper usdbh = new DatabaseOpenHelper(this, "Usuarios", null, 1);
        db = usdbh.getWritableDatabase();

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Recuperamos los valores de los campos de texto
                    cod = txtCodigo.getText().toString();
                    nom = txtNombre.getText().toString();

                    if( (cod.length() > 0) && (nom.length() > 0) ) {

                            Cursor cursor1 = db.rawQuery("SELECT * FROM Usuarios WHERE codigo LIKE '%" + cod + "%'", null); //Comando para SQL

                            while (cursor1.moveToNext()) {
                                numero1 = cursor1.getString(0);
                            }

                            if (!cod.equals(numero1)) {


                                //Alternativa 1: método sqlExec()
                                //String sql = "INSERT INTO Usuarios (codigo,nombre) VALUES ('" + cod + "','" + nom + "') ";
                                //db.execSQL(sql);

                                //Alternativa 2: método insert()
                                ContentValues nuevoRegistro = new ContentValues();

                                nuevoRegistro.put("codigo", cod);
                                nuevoRegistro.put("nombre", nom);


                                db.insert("Usuarios", null, nuevoRegistro);
                            }
                    }
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Recuperamos los valores de los campos de texto
                cod = txtCodigo.getText().toString();
                nom = txtNombre.getText().toString();

                if( (cod.length() > 0) && (nom.length() > 0) ) {

                    //Alternativa 1: método sqlExec()
                    //String sql = "UPDATE Usuarios SET nombre='" + nom + "' WHERE codigo=" + cod;
                    //db.execSQL(sql);

                    //Alternativa 2: método update()
                    ContentValues valores = new ContentValues();
                    valores.put("codigo", cod);
                    valores.put("nombre", nom);
                    db.update("Usuarios", valores, "codigo=" + cod, null);
                }
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Recuperamos los valores de los campos de texto
                cod = txtCodigo.getText().toString();
                nom = txtNombre.getText().toString();

                //Alternativa 1: método sqlExec()
                //String sql = "DELETE FROM Usuarios WHERE codigo=" + cod;
                //db.execSQL(sql);

                //Alternativa 2: método delete()
                //db.delete("Usuarios", "codigo=" + cod, null);

                if(isTableExists("Usuarios") == true) {

                    if( (cod.length() > 0) && (nom.length() == 0)) {
                        db.delete("Usuarios", "codigo =" + cod, null);
                    }
                    else if( (cod.length() == 0) && (nom.equals("all")) )
                    {
                        db.delete("Usuarios", null, null);
                    }
                }
            }
        });

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Alternativa 1: método rawQuery()
                //Cursor c = db.rawQuery("SELECT codigo, nombre FROM Usuarios", null);

                //Alternativa 2: método delete()
                String[] campos = new String[] {"codigo", "nombre"};
                Cursor c = db.query("Usuarios", campos, null, null, null, null, "codigo" + " ASC", null);

                //Recorremos los resultados para mostrarlos en pantalla
                txtResultado.setText("");
                if (c.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {
                        cod = c.getString(0);
                        nom = c.getString(1);

                        txtResultado.append(" " + cod + " - " + nom + "\n");
                    } while(c.moveToNext());
                }
            }
        });
    }

    public boolean isTableExists(String nombreTabla) {
        boolean isExist = false;
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + nombreTabla + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }
}
