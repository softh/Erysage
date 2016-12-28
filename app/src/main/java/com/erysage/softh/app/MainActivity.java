package com.erysage.softh.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erysage.softh.app.helpers.FaceRecognitionResult;
import com.erysage.softh.app.helpers.ViewHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;



public class MainActivity extends AppCompatActivity {
    private ImageView photoView;
    private static final int CONTENT_REQUEST = 1337;
    private static final String PROCESS_FILE_NAME = "RecImage.jpeg";
    private static final String MESSAGE_DONE = "Done!";
    private static final String MAIN_FONT = "fonts/Esqadero FF CY 4F-Regular.ttf";
    private static final String MAIN_LOGO_TEXT = "Erysage";
    private static final String SHARE_TEXT =  "Share";
    private static final int RECTANGLE_PADDING = 100;
    private static final int BITMAP_COMPRESSING_SCALE = 100;
    private static final int ALPHA = 90;
    private static final int BOTTOM_BORDER_PADDING = 150;
    private static final int TEXT_PADDING = 40;
    private static final int SIDE_PADDING = 5;
    private static final int HEIGHT_DIVIDER = 3;
    private static final int WIDTH_DIVIDER = 4;
    private static final float STROKE_WIDTH = 10.f;
    private static final double FONT_SCALE = 0.04;

    private File workDir;
    private File outputFile;
    private Bitmap mBitmap;
    private FloatingActionButton takePhotoButton;
    private FloatingActionButton sharePhotoButton;
    private TextView stepTextView;
    private Typeface customFonts;
    private LinearLayout mainLayout;
    private Snackbar snackbar;
    public Context currentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeControlls();
        Variables.processed_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                photoView.setImageBitmap(BitmapFactory.decodeFile(outputFile.getAbsolutePath()));
                detect();
                //mGPUImage.saveToPictures("GPUImage", "ImageWithFilter.jpg", null);
            }
        }
    }

    private void initializeControlls(){
        currentContext = this;
        mainLayout = (LinearLayout)findViewById(R.id.MainLayout);
        stepTextView = (TextView)findViewById((R.id.TakePhotoTextView));
        customFonts = Typeface.createFromAsset(getAssets(),MAIN_FONT);
        stepTextView.setTypeface(customFonts);
        photoView = (ImageView)findViewById(R.id.photoView) ;
        takePhotoButton = (FloatingActionButton)findViewById(R.id.fab);
        sharePhotoButton = (FloatingActionButton)findViewById(R.id.fab_share);
        snackbar = Snackbar
                .make(mainLayout, R.string.share_text, Snackbar.LENGTH_INDEFINITE)
                .setAction(SHARE_TEXT, new ShowSnackbarOnClickListener());
        takePhotoButton.setOnClickListener(new TakePhotoOnClickListener());
        sharePhotoButton.setOnClickListener(new SharePhotoOnClickListener());
    }

    public void detect() {
        mBitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        mBitmap.compress(Bitmap.CompressFormat.JPEG, BITMAP_COMPRESSING_SCALE, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        new FaceDetector().execute(inputStream);
    }


    private class TakePhotoOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            workDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            outputFile = new File(workDir, PROCESS_FILE_NAME);
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
            startActivityForResult(i, CONTENT_REQUEST);
        }
    }

    private class SharePhotoOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            snackbar.show();
        }
    }
    private class ShowSnackbarOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(currentContext, ShareActivity.class);
            startActivity(intent);
        }
    }
    public class FaceDetector extends AsyncTask<InputStream, String, FaceRecognitionResult> {

        private ProgressDialog progressDialog;
        @Override
        protected FaceRecognitionResult doInBackground(InputStream... params) {
            FaceServiceClient faceServiceClient = MainApp.getFaceServiceClient();
            FaceRecognitionResult faces = new FaceRecognitionResult(null, MESSAGE_DONE);
            try {
                faces = new FaceRecognitionResult(faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        new FaceServiceClient.FaceAttributeType[] {
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Gender,
                                FaceServiceClient.FaceAttributeType.Glasses,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.HeadPose,
                                FaceServiceClient.FaceAttributeType.FacialHair
                        }), MESSAGE_DONE);
                /*EmotionServiceClient emotionServiceClient = MainApp.getEmotionServiceClient();
                  List<RecognizeResult> res_list =  emotionServiceClient.recognizeImage(params[0]);*/
                return faces;
            } catch (Exception e) {
                faces.setMessage(e.getLocalizedMessage());
                return faces;
            }

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(currentContext);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(FaceRecognitionResult result) {
            progressDialog.dismiss();
            ViewHelper.showMessageInToast(currentContext, result.getMessage());
            if(result.getFaces() == null){
                return;
            }
            drawImage(result);
        }

        private void drawImage(FaceRecognitionResult faces){
            Bitmap workBitmap;
            Bitmap mutableBitmap;
            workBitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
            mutableBitmap = workBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas c = new Canvas(mutableBitmap);
            for (Face face : faces.getFaces()) {
                double age = face.faceAttributes.age;
                double smile = face.faceAttributes.smile;
                String gender = face.faceAttributes.gender;
                int x0 = face.faceRectangle.left;
                int y0 = face.faceRectangle.top;
                int x1 = face.faceRectangle.width + x0 + RECTANGLE_PADDING;
                int y1 = face.faceRectangle.height + y0 + RECTANGLE_PADDING;

                int myColor = currentContext.getResources().getColor(com.erysage.softh.app.R.color.colorPrimary);
                Paint p = new Paint();
                p.setColor(myColor);
                float fontSize = (float) (workBitmap.getHeight()*FONT_SCALE);
                p.setTextSize(fontSize);
                p.setTypeface(customFonts);

                c.drawRect(x0-SIDE_PADDING, y0, x1+SIDE_PADDING, (y0-BOTTOM_BORDER_PADDING),p);
                p.setStrokeWidth(STROKE_WIDTH);
                p.setStyle(Paint.Style.STROKE);
                c.drawRect(x0,y0,x1,y1,p);
                p.setColor(Color.WHITE);
                p.setStyle(Paint.Style.FILL);
                c.drawText(getString(R.string.age_text) + age, x0, y0-TEXT_PADDING,p);
                p.setColor(myColor);
                p.setAlpha(ALPHA);
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_logo);
                c.drawText(MAIN_LOGO_TEXT, (bm.getHeight()/HEIGHT_DIVIDER),
                        workBitmap.getHeight()-RECTANGLE_PADDING, p);
                bm = Bitmap.createScaledBitmap(bm, (bm.getHeight()/WIDTH_DIVIDER),
                        (bm.getWidth()/WIDTH_DIVIDER), false);
                c.drawBitmap(bm, 0, workBitmap.getHeight()-bm.getHeight(),p);
                Variables.processed_bitmap = mutableBitmap;
                photoView.setImageBitmap(mutableBitmap);
                Variables.age = Double.toString(age);
            }
        }
    }

}
