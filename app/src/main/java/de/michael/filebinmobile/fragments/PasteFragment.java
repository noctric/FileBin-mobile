package de.michael.filebinmobile.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.michael.filebinmobile.R;
import de.michael.filebinmobile.controller.NetworkManager;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.model.PostInfo;
import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.UserProfile;

public class PasteFragment extends Fragment {

    public static final String FILE_NAME_DEFAULT = "stdin";

    ArrayList<File> filesToUpload = new ArrayList<>();
    private UploadFilesTask fileUploadTask;

    @BindView(R.id.edtPasteText)
    EditText edtPastedText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        cancelDownload();
        super.onDestroy();
    }

    private void cancelDownload() {
        if (this.fileUploadTask != null) {

            this.fileUploadTask.cancel(false);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.paste_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick(R.id.btnPasteUpload)
    public void upload() {

        // TODO make file name editable before uploading
        // grab any text from our text field and create a new stdin file
        String textToPaste = this.edtPastedText.getText().toString();

        if (!textToPaste.isEmpty()) {

            try {

                writeToFile(textToPaste);

            } catch (IOException e) {
                e.printStackTrace();
            }

            String filePath = getContext().getFilesDir() + File.separator + FILE_NAME_DEFAULT;

            File file = new File(filePath);

            this.filesToUpload.add(file);

        }

        // TODO add all the other files

        // finally upload
        PostInfo postInfo = SettingsManager.getInstance().getPostInfo(getActivity());

        if (postInfo != null) {

            cancelDownload();

            this.fileUploadTask = new UploadFilesTask();
            this.fileUploadTask.execute(postInfo);
        }

    }

    private void writeToFile(String data) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(FILE_NAME_DEFAULT, Context.MODE_PRIVATE));
        outputStreamWriter.write(data);
        outputStreamWriter.close();
    }

    private String readFile() throws IOException {

        FileInputStream fileInputStream = getContext().openFileInput(FILE_NAME_DEFAULT);

        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        StringBuilder sb = new StringBuilder();

        while ((receiveString = bufferedReader.readLine()) != null) {
            sb.append(receiveString);
        }

        fileInputStream.close();

        return sb.toString();
    }

    private class UploadFilesTask extends AsyncTask<PostInfo, Integer, String> {

        @Override
        protected String doInBackground(PostInfo... postInfos) {

            if (postInfos.length > 0) {

                PostInfo postInfo = postInfos[0];

                Server server = postInfo.getServer();
                UserProfile userProfile = postInfo.getUserProfile();

                File[] files = filesToUpload.toArray(new File[]{});

                return NetworkManager.getInstance().pasteUploadFiles(userProfile, server, files);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Upload completed")
                    .setMessage(s)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

            super.onPostExecute(s);
        }
    }
}
