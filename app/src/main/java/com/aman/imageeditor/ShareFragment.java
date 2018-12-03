package com.aman.imageeditor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.io.File;
import java.io.IOException;

public class ShareFragment extends Fragment implements View.OnClickListener {
    private static final String URI = "uri";

    private OnFragmentInteractionListener mListener;
    private String uri;
    private ImageView mIvImage;
    private Context mContext;
    private TwitterSession session;
    private TwitterLoginButton mTwitterLoginButton;
    private Uri photoURI;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    public ShareFragment() {
        // Required empty public constructor
    }

    public static ShareFragment newInstance(String uri) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            uri = getArguments().getString(URI);
        }
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        fbshareDialogCallback();

        photoURI = FileProvider.getUriForFile(mContext,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(uri));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mIvImage = view.findViewById(R.id.iv_image);
        mTwitterLoginButton = view.findViewById(R.id.twitter_login_button);
        view.findViewById(R.id.ll_facebook).setOnClickListener(this);
        view.findViewById(R.id.ll_instagram).setOnClickListener(this);
        view.findViewById(R.id.ll_twitter).setOnClickListener(this);
        view.findViewById(R.id.ll_other).setOnClickListener(this);


        mIvImage.setImageURI(photoURI);
        initializeTwitterLoginCallbacks();
        session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((EditImageActivity)mContext).hideLoading();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_facebook:
                ((EditImageActivity)mContext).showLoading("Loading...");
                /*ProgressDialog progressBar = new ProgressDialog(mContext);
                progressBar.setCancelable(false);//you can cancel it by pressing back button
                progressBar.setMessage("File downloading ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(0);//initially progress is 0
                progressBar.setMax(100);//sets the maximum value 100
                progressBar.show();//displays the progress bar

 */               Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(image)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();

                    shareDialog.show(content);
                }

                break;
            case R.id.ll_instagram:
                String type = "image/*";
                createInstagramIntent(type, photoURI);
                break;
            case R.id.ll_twitter:
                if (session != null) {
                    final Intent intent = new ComposerActivity.Builder(mContext)
                            .session(session)
                            .image(photoURI)
                            .text("#soclo")
                            // .hashtags("#twitter")
                            .createIntent();
                    startActivity(intent);
                } else {
                    mTwitterLoginButton.performClick();
                }
                break;
            case R.id.ll_other:

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Share App");
                intent.setType("text/plain");

                intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                intent.setType("image/jpeg");
                intent.setPackage("com.whatsapp");
                startActivity(intent);
                break;
        }
    }

    private void createInstagramIntent(String type, Uri uri) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);
        // Set the MIME type
        share.setType(type);
        share.setPackage("com.instagram.android");
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    private void initializeTwitterLoginCallbacks() {

        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                session = TwitterCore.getInstance().getSessionManager()
                        .getActiveSession();

                final Intent intent = new ComposerActivity.Builder(mContext)
                        .session(session)
                        .image(Uri.fromFile(new File(uri)))
                        .text("#soclo")
//                         .hashtags("#soclo")
                        .createIntent();
                startActivity(intent);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        else return "";
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static Bitmap decodeSampledBitmapFromResource(String resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
// Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    @Override
    public void onPause() {
        super.onPause();
        //((EditImageActivity)mContext).hideLoading();

    }

    private void fbshareDialogCallback() {
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(mContext, "Post shared successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                //Called when the dialog is canceled.
            }

            @Override
            public void onError(FacebookException error) {
                // Called when the dialog finishes with an error.
            }
        });
    }

    public void authbutton(int requestCode, int resultCode, Intent data) {

        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);

    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
