package com.erysage.softh.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.erysage.softh.app.helpers.ViewHelper;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

public class ShareActivity extends AppCompatActivity {

    private ImageView mainImageView;
    private ImageButton vkButon;
    private static final String MESSAGE_DONE = "Done!";
    private ProgressDialog progressDialog;
    private Context currentContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initializeControlls();
        VKSdk.login(this, Variables.vkScope);




    }
    private void initializeControlls(){
        currentContext = this;
        mainImageView = (ImageView)findViewById(R.id.shareImageView);
        mainImageView.setImageBitmap(Variables.processed_bitmap);
        vkButon = (ImageButton)findViewById(R.id.VkButton);
        vkButon.setOnClickListener(new shareVkOnClickListener());
        progressDialog = new ProgressDialog(currentContext);
        progressDialog.setMessage(getString(R.string.loading_text));
    }

    private class shareVkOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String text = "According to this application, I am" + Variables.age + "years old";
            text += "\n" + "Download Erysage from url:" + "https://blablabla.bla";
            try{
                uploadPhotoToUserWall(Variables.processed_bitmap, text);
            }catch (Exception ex){
                ViewHelper.showMessageInToast(currentContext, ex.getLocalizedMessage());
            }
        }
    }
    void uploadPhotoToUserWall(final Bitmap photo, final String message) {
        try{
            progressDialog.show();
            VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo,
                    VKImageParameters.jpgImage(0.9f)), getUserId(), 0);
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
                    makePost(new VKAttachments(photoModel), message, getUserId());
                }
                @Override
                public void onError(VKError error) {
                    ViewHelper.showMessageInToast(currentContext, error.errorMessage);
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
    private VKParameters preparePost(VKAttachments attachments, String message, final int ownerId){
        VKParameters parameters = new VKParameters();
        parameters.put(VKApiConst.OWNER_ID, String.valueOf(ownerId));
        parameters.put(VKApiConst.ATTACHMENTS, attachments);
        parameters.put(VKApiConst.MESSAGE, message);
        return  parameters;
    }

    private void makePost(VKAttachments att, String msg, final int ownerId) {
        VKParameters parameters = preparePost(att, msg, ownerId);
        VKRequest post = VKApi.wall().post(parameters);
        post.setModelClass(VKWallPostResult.class);
        progressDialog.dismiss();
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                ViewHelper.showMessageInToast(currentContext, MESSAGE_DONE);
            }
            @Override
            public void onError(VKError error) {
                ViewHelper.showMessageInToast(currentContext, error.errorMessage);
            }
        });
    }
    private int getUserId() {
        final VKAccessToken vkAccessToken = VKAccessToken.currentToken();
        return vkAccessToken != null ? Integer.parseInt(vkAccessToken.userId) : 0;
    }
}
