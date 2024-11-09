package com.example.solaris.activitymenu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.solaris.Conexion;
import com.example.solaris.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class consultaActivity extends AppCompatActivity {

    TextView tvMostSaldo, tvMostCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consulta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Numero de cuenta recibida
        String numCuenta = getIntent().getStringExtra("numCuenta");

        tvMostSaldo = findViewById(R.id.tvMostSaldo);
        tvMostCuenta = findViewById(R.id.tvMostCuenta);
        tvMostCuenta.setText(numCuenta);

        TableLayout tableLayout = findViewById(R.id.data_table);
        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase database = conexion.getReadableDatabase();

        Cursor sald = database.rawQuery("SELECT saldo FROM cuenta WHERE numcuenta = ?", new String[]{numCuenta});
        if (sald.moveToFirst()) {
            float saldo = sald.getFloat(0);
            String saldoRedondeado = String.format("Q. %.2f", saldo);

            tvMostSaldo.setText(saldoRedondeado);
        } else {
            tvMostSaldo.setText("Número de cuenta no encontrado");
        }
        sald.close();

        // Crear un encabezado para la tabla
        TableRow headerRow = new TableRow(this);
        headerRow.setPadding(8, 15, 8, 15);
        headerRow.setBackgroundColor(Color.parseColor("#ad832f"));

        TextView headerId = new TextView(this);
        headerId.setText("ID");
        headerId.setGravity(Gravity.CENTER);
        headerId.setTextColor(Color.WHITE); // Color de texto blanco para mayor contraste
        headerId.setTypeface(null, Typeface.BOLD);
        headerRow.addView(headerId);

        TextView headerTipo = new TextView(this);
        headerTipo.setText("Tipo");
        headerTipo.setGravity(Gravity.CENTER);
        headerTipo.setTextColor(Color.WHITE);
        headerTipo.setTypeface(null, Typeface.BOLD);
        headerRow.addView(headerTipo);

        TextView headerMonto = new TextView(this);
        headerMonto.setText("Monto");
        headerMonto.setGravity(Gravity.CENTER);
        headerMonto.setTextColor(Color.WHITE);
        headerMonto.setTypeface(null, Typeface.BOLD);
        headerRow.addView(headerMonto);

        TextView headerFecha = new TextView(this);
        headerFecha.setText("Fecha");
        headerFecha.setGravity(Gravity.CENTER);
        headerFecha.setTextColor(Color.WHITE);
        headerFecha.setTypeface(null, Typeface.BOLD);
        headerRow.addView(headerFecha);

        tableLayout.addView(headerRow);

        try {
            // Consulta para obtener los datos de la tabla transacciones
            Cursor cursor = database.rawQuery(
                    "SELECT idtransaccion, monto, fecha, tipo FROM transacciones WHERE numcuenta = ?", new String[]{numCuenta});

            // Verificar que existan datos
            if (cursor.moveToFirst()) {
                int rowIndex = 0;
                do {
                    // Obtener valores de cada columna
                    int idTransaccion = cursor.getInt(0);
                    float monto = cursor.getFloat(1);
                    String fecha = cursor.getString(2);
                    String tipo = cursor.getString(3);

                    // Crear una nueva fila para la tabla
                    TableRow tableRow = new TableRow(this);
                    tableRow.setPadding(8, 15, 8, 15);

                    // Alternar color de fondo
                    if (rowIndex % 2 == 0) {
                        tableRow.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    } else {
                        tableRow.setBackgroundColor(Color.WHITE);
                    }
                    rowIndex++;

                    // Crear celdas para cada valor y agregar a la fila
                    TextView idTransaccionView = new TextView(this);
                    idTransaccionView.setText(String.valueOf(idTransaccion));
                    idTransaccionView.setGravity(Gravity.CENTER);
                    idTransaccionView.setTextColor(Color.BLACK);
                    tableRow.addView(idTransaccionView);

                    TextView tipoView = new TextView(this);
                    tipoView.setText(tipo);
                    tipoView.setGravity(Gravity.CENTER);
                    tipoView.setTextColor(Color.BLACK);
                    tableRow.addView(tipoView);

                    TextView montoView = new TextView(this);
                    montoView.setText(String.format("Q%.2f", monto)); // Formato para 2 decimales
                    montoView.setGravity(Gravity.CENTER);
                    montoView.setTextColor(Color.BLACK);
                    tableRow.addView(montoView);

                    TextView fechaView = new TextView(this);
                    fechaView.setText(fecha);
                    fechaView.setGravity(Gravity.CENTER);
                    fechaView.setTextColor(Color.BLACK);
                    tableRow.addView(fechaView);



                    // Agregar la fila completa al TableLayout
                    tableLayout.addView(tableRow);

                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "No se encontraron transacciones.", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            database.close();
        }

    }

    public void Imprimir(View view){
        String numCuenta = getIntent().getStringExtra("numCuenta");
        Conexion conexion = new Conexion(this, "DBSolaris", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        PdfDocument myPdfDocument = new PdfDocument();
        Paint myPaint = new Paint();

        try {
            // Consultar saldo y datos del cliente
            Cursor datosCuenta = BaseDeDatos.rawQuery(
                    "SELECT saldo, cliente.nombre, cliente.apellido " +
                            "FROM cuenta INNER JOIN cliente ON cuenta.dpi = cliente.dpi " +
                            "WHERE cuenta.numcuenta = ?", new String[]{numCuenta});

            Cursor cursor = BaseDeDatos.rawQuery(
                    "SELECT idtransaccion, cuenta_destino, monto, fecha, tipo " +
                            "FROM transacciones WHERE numcuenta = ?", new String[]{numCuenta});

            if (cursor != null && cursor.moveToFirst() && datosCuenta.moveToFirst()) {
                float saldo = datosCuenta.getFloat(0);
                String nombre = datosCuenta.getString(1);
                String apellido = datosCuenta.getString(2);

                int yPosition = 370; // Posición inicial para las transacciones
                PdfDocument.Page myPage = null;
                Canvas canvas = null;

                int rowIndex = 0; // Contador para alternar el color de fondo de las filas

                int contador=1;

                do {
                    //Agregar imagen
                    Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logopdf);

                    // Darle dimeciones a la imagen
                    Bitmap scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 110, 110, false);
                    // Crear nueva página si se sale del límite de página
                    if (myPage == null || yPosition > 1400) {
                        if (myPage != null) { // Terminar la página anterior si ya hay una creada
                            myPdfDocument.finishPage(myPage);
                        }
                        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1000, 1500, 1).create();
                        myPage = myPdfDocument.startPage(myPageInfo);
                        canvas = myPage.getCanvas();

                        canvas.drawBitmap(scaledLogo, canvas.getWidth() - 140, 50, myPaint);

                        // Encabezado y detalles de la cuenta en cada nueva página
                        myPaint.setTextSize(70);
                        canvas.drawText("Banco Solaris", 30, 80, myPaint);

                        myPaint.setTextSize(30);
                        canvas.drawText("Reporte", 30, 115, myPaint);

                        myPaint.setTextSize(25);
                        canvas.drawText("No. Cuenta: " + numCuenta, 30, 150, myPaint);
                        canvas.drawText("Nombre: " + nombre + " " + apellido, 30, 185, myPaint);
                        canvas.drawText("Saldo: Q. " + String.format("%.2f", saldo), 30, 220, myPaint);
                        canvas.drawText("Transacciones Realizadas",410, 250, myPaint);


                        myPaint.setColor(Color.rgb(150, 150, 150));
                        canvas.drawRect(30, 280, canvas.getWidth() - 30, 290, myPaint);

                        // Encabezados de las columnas de transacciones
                        myPaint.setTextSize(20);
                        myPaint.setColor(Color.BLACK);
                        canvas.drawText("No.", 50, 330, myPaint);
                        canvas.drawText("ID", 150, 330, myPaint);
                        canvas.drawText("Fecha", 330, 330, myPaint);
                        canvas.drawText("Tipo", 565, 330, myPaint);
                        canvas.drawText("Monto", 700, 330, myPaint);
                        canvas.drawText("Destinatario", 850, 330, myPaint);

                        // Línea divisoria debajo de los encabezados
                        myPaint.setColor(Color.rgb(150, 150, 150));
                        canvas.drawRect(30, 350, canvas.getWidth() - 30, 360, myPaint);

                        yPosition = 380; // Reiniciar posición de y para la nueva página después de los encabezados
                    }

                    // Alternar color de fondo de la fila
                    if (rowIndex % 2 == 0) {
                        myPaint.setColor(Color.parseColor("#F5F5F5"));
                    } else {
                        myPaint.setColor(Color.WHITE);
                    }
                    canvas.drawRect(30, yPosition - 20, canvas.getWidth() - 30, yPosition + 20, myPaint);

                    // Dibujar datos de transacción
                    myPaint.setColor(Color.BLACK); // Texto en color negro
                    int idTransaccion = cursor.getInt(0);
                    String cuentaDestino = cursor.isNull(1) ? "Ninguno" : cursor.getString(1);
                    float monto = cursor.getFloat(2);
                    String fecha = cursor.getString(3);
                    String tipo = cursor.getString(4);

                    canvas.drawText(String.valueOf(contador), 50, yPosition, myPaint); // Columna No.
                    canvas.drawText(String.valueOf(idTransaccion), 150, yPosition, myPaint); // Columna ID
                    canvas.drawText(fecha, 250, yPosition, myPaint); // Columna Fecha
                    canvas.drawText(tipo, 550, yPosition, myPaint); // Columna Tipo
                    canvas.drawText("Q" + String.format("%.2f", monto), 700, yPosition, myPaint); // Columna Monto
                    canvas.drawText(cuentaDestino, 850, yPosition, myPaint); // Columna Destinatario

                    yPosition += 30;
                    contador ++;

                    rowIndex++; // Incrementar el índice de fila para la próxima iteración

                } while (cursor.moveToNext());

                if (myPage != null) {
                    myPdfDocument.finishPage(myPage);
                }

                // Ruta en la carpeta Descargas
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), numCuenta + "_Reporte.pdf");
                myPdfDocument.writeTo(new FileOutputStream(file));

                Toast.makeText(this, "PDF generado exitosamente en Descargas.", Toast.LENGTH_SHORT).show();
                Log.d("PDFGeneration", "PDF generado exitosamente: " + file.getAbsolutePath());

            } else {
                Toast.makeText(this, "No se encontraron transacciones para esta cuenta.", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e("PDFGeneration", "Error al generar PDF", e);
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
        } finally {
            BaseDeDatos.close();
            myPdfDocument.close();
        }
    }
    public void RegMenu(View view){
        onBackPressed();
    }

    //Compartir el Pdf
    public void compartirPDF(View view) {
        String numCuenta = getIntent().getStringExtra("numCuenta");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), numCuenta + "_Reporte.pdf");

        if (file.exists()) {
            // Obtener URI usando FileProvider
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

            // Crear intent de compartir
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Iniciar intent para compartir
            startActivity(Intent.createChooser(shareIntent, "Compartir PDF usando"));
        } else {
            Toast.makeText(this, "Archivo PDF no encontrado.", Toast.LENGTH_SHORT).show();
        }
    }
}