package ao.co.isptec.aplm.fileexplorer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class InternalStorage extends Activity {

    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String FILE_NAME = "test.txt";

    private EditText input;
    private TextView output;
    private Button write;
    private Button read;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.internal_storage);

        this.input = findViewById(R.id.internal_storage_input);
        this.output = findViewById(R.id.internal_storage_output);

        this.write = findViewById(R.id.internal_storage_write_button);
        this.write.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                new WriteTask().execute(input.getText().toString());
            }
        });

        this.read = findViewById(R.id.internal_storage_read_button);
        this.read.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                new ReadTask().execute();
            }
        });
    }

    private class WriteTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                fos.write(params[0].getBytes());
            } catch (FileNotFoundException e) {
                Log.e(Constants.LOG_TAG, "File not found", e);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IO problem", e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.d("FileExplorer", "Close error.");
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(InternalStorage.this, "File written", Toast.LENGTH_SHORT).show();
            input.setText("");
            output.setText("");
        }
    }

    private class ReadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            FileInputStream fis = null;
            Scanner scanner = null;
            StringBuilder sb = new StringBuilder();
            try {
                fis = openFileInput(FILE_NAME);
                scanner = new Scanner(fis);
                while (scanner.hasNextLine()) {
                    sb.append(scanner.nextLine()).append(LINE_SEP);
                }
            } catch (FileNotFoundException e) {
                Log.e(Constants.LOG_TAG, "File not found", e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        Log.d("FileExplorer", "Close error.");
                    }
                }
                if (scanner != null) {
                    scanner.close();
                }
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            output.setText(result);
            Toast.makeText(InternalStorage.this, "File read", Toast.LENGTH_SHORT).show();
        }
    }
}
