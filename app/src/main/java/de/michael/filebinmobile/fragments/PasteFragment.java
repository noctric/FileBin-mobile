package de.michael.filebinmobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.michael.filebinmobile.OnTabNavigationRequestedListener;
import de.michael.filebinmobile.R;
import de.michael.filebinmobile.adapters.OnDataRemovedListener;
import de.michael.filebinmobile.adapters.SelectedFilesAdapter;
import de.michael.filebinmobile.adapters.UploadUrlAdapter;
import de.michael.filebinmobile.controller.NetworkManager;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.model.PostInfo;
import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.UserProfile;
import de.michael.filebinmobile.util.FileChooserUtil;

import static android.content.ContentValues.TAG;

public class PasteFragment extends Fragment implements OnDataRemovedListener {

    public static final String FILE_NAME_DEFAULT = "stdin";
    private static final int READ_REQUEST_CODE = 1;

    private ArrayList<File> filesToUpload = new ArrayList<>();
    private UploadFilesTask fileUploadTask;
    private SelectedFilesAdapter adapter;
    private OnTabNavigationRequestedListener onTabNavigationRequestedListener;

    @BindView(R.id.edtPasteText)
    EditText edtPastedText;

    @BindView(R.id.rclAddedFiles)
    RecyclerView rclSelectedFiles;

    @BindView(R.id.pgbUploadProgress)
    ProgressBar pgbUploadProgress;

    @BindView(R.id.btnPasteUpload)
    FloatingActionButton fbaUpload;

    @BindView(R.id.txtSelectedServer)
    TextView txtSelectedServer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.adapter = new SelectedFilesAdapter(getActivity());
        this.adapter.setOnDataRemovedListener(this);

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

        edtPastedText.setTypeface(Typeface.MONOSPACE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.rclSelectedFiles.getContext(),
                linearLayoutManager.getOrientation());

        this.rclSelectedFiles.setLayoutManager(linearLayoutManager);
        this.rclSelectedFiles.setItemAnimator(new DefaultItemAnimator());
        this.rclSelectedFiles.setAdapter(this.adapter);
        this.rclSelectedFiles.addItemDecoration(dividerItemDecoration);

        PostInfo postInfo = SettingsManager.getInstance().getPostInfo(getActivity());
        String selectedServerName = "None";
        String selectedUserName = "None";

        if (postInfo != null) {
            selectedServerName = postInfo.getServer().getName();
            if (postInfo.getUserProfile() != null) {
                selectedUserName = postInfo.getUserProfile().getUsrName();
            }
        }
        this.txtSelectedServer.setText(String.format("Uploads to %s by %s", selectedServerName, selectedUserName));
        return view;
    }

    public void setOnTabNavigationRequestedListener(OnTabNavigationRequestedListener onTabNavigationRequestedListener) {
        this.onTabNavigationRequestedListener = onTabNavigationRequestedListener;
    }

    @OnClick(R.id.txtSelectedServer)
    public void openServerSettingsTab() {
        if (this.onTabNavigationRequestedListener != null) {
            this.onTabNavigationRequestedListener.onNavigationRequest(R.id.navigation_server_settings);
        }
    }


    @OnClick(R.id.btnPasteUpload)
    public void upload() {

        // make some UI adjustments to show uploading status
        this.pgbUploadProgress.setVisibility(View.VISIBLE);
        this.fbaUpload.setEnabled(false);

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
        } else {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Woops")
                    .setMessage("No Server for uploading selected. Please go to Server Settings" +
                            "and select one.")
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> {

                        dialogInterface.dismiss();

                        if (this.onTabNavigationRequestedListener != null) {
                            this.onTabNavigationRequestedListener
                                    .onNavigationRequest(R.id.navigation_server_settings);
                        }

                    }).setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

                        dialogInterface.dismiss();

                    }
            );
            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }

    @OnClick(R.id.btnSelectFiles)
    public void openFileSelector() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                assert uri != null;

                File file = null;
                try {
                    file = FileChooserUtil.createFileCopyFromUri(uri, getActivity());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (file != null) {
                    this.filesToUpload.add(file);
                }

                // safer to just readd the entire list so we don't run the danger of any
                // discrepancies between adapter data and actual data
                this.adapter.updateData(this.filesToUpload);

                Log.i(TAG, "Uri: " + uri.toString());
                Log.i(TAG, "Files to be uploaded: " + Arrays.toString(filesToUpload.toArray()));
            }
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

    @Override
    public void onDataRemoved(int pos) {

        // let's keep ALL the list manipulations in one place
        this.filesToUpload.remove(pos);
        this.adapter.removeItemAt(pos);

    }

    private class UploadFilesTask extends AsyncTask<PostInfo, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(PostInfo... postInfos) {

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
        protected void onPostExecute(ArrayList<String> urlList) {

            // upload completed, we can reset our UI
            pgbUploadProgress.setVisibility(View.INVISIBLE);
            fbaUpload.setEnabled(true);

            View view = getActivity().getLayoutInflater().inflate(R.layout.any_recycler_view, null);
            RecyclerView rclUrlList = view.findViewById(R.id.rclAnyRecyclerView);

            UploadUrlAdapter uploadUrlAdapter = new UploadUrlAdapter(getActivity());
            uploadUrlAdapter.updateData(urlList);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rclUrlList.getContext(),
                    linearLayoutManager.getOrientation());

            rclUrlList.setLayoutManager(linearLayoutManager);
            rclUrlList.setItemAnimator(new DefaultItemAnimator());
            rclUrlList.setAdapter(uploadUrlAdapter);
            rclUrlList.addItemDecoration(dividerItemDecoration);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Upload completed")
                    .setView(view)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

            super.onPostExecute(urlList);
        }
    }
}
