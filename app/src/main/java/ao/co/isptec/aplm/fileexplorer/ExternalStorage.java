package ao.co.isptec.aplm.fileexplorer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask; // Importar AsyncTask
import android.os.Bundle;
import android.os.Environment;
import android.util.Log; // Importar Log
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class ExternalStorage extends Activity {

    private EditText input;
    private TextView output;
    private Button write;
    private Button read;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.external_storage);

        // Solicita permissões necessárias
        checkPermissions();

        this.input = findViewById(R.id.external_storage_input);
        this.output = findViewById(R.id.external_storage_output);

        this.write = findViewById(R.id.external_storage_write_button);
        this.write.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                write();
            }
        });

        this.read = findViewById(R.id.external_storage_read_button);
        this.read.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                read();
            }
        });
    }

    private void checkPermissions() {
        // Verifica se temos permissão para escrever no armazenamento externo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        // Verifica se temos permissão para ler do armazenamento externo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void write() {
        if (FileUtil.isExternalStorageWritable()) {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) {
                dir.mkdirs(); // Cria o diretório se não existir
            }
            File file = new File(dir, "test.txt");
            new FileWriteTask().execute(file); // Executa a tarefa assíncrona
        } else {
            Toast.makeText(this, "External storage not writable", Toast.LENGTH_SHORT).show();
        }
    }

    private void read() {
        if (FileUtil.isExternalStorageReadable()) {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(dir, "test.txt");
            new FileReadTask().execute(file); // Executa a tarefa assíncrona
        } else {
            Toast.makeText(this, "External storage not readable", Toast.LENGTH_SHORT).show();
        }
    }

    // Tarefa assíncrona para escrita de arquivo
    private class FileWriteTask extends AsyncTask<File, Void, String> {
        @Override
        protected String doInBackground(File... files) {
            // Verifica o nome da thread
            String threadName = Thread.currentThread().getName();
            Log.i("FileWriteTask", "Executing doInBackground in thread: " + threadName); // Log para verificar a thread

            File file = files[0];
            FileUtil.writeStringAsFile(input.getText().toString(), file);
            return file.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String result) {
            // Verifica o nome da thread
            String threadName = Thread.currentThread().getName();
            Log.i("FileWriteTask", "Executing onPostExecute in thread: " + threadName); // Log para verificar a thread

            Toast.makeText(ExternalStorage.this, "File written: " + result, Toast.LENGTH_SHORT).show();
            input.setText("");
            output.setText("");
        }
    }

    // Tarefa assíncrona para leitura de arquivo
    private class FileReadTask extends AsyncTask<File, Void, String> {
        @Override
        protected String doInBackground(File... files) {
            // Verifica o nome da thread
            String threadName = Thread.currentThread().getName();
            Log.i("FileReadTask", "Executing doInBackground in thread: " + threadName); // Log para verificar a thread

            File file = files[0];
            if (file.exists() && file.canRead()) {
                return FileUtil.readFileAsString(file);
            } else {
                return null; // Indica que o arquivo não pode ser lido
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Verifica o nome da thread
            String threadName = Thread.currentThread().getName();
            Log.i("FileReadTask", "Executing onPostExecute in thread: " + threadName); // Log para verificar a thread

            if (result != null) {
                output.setText(result);
                Toast.makeText(ExternalStorage.this, "File read", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ExternalStorage.this, "Unable to read file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
