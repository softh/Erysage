package com.erysage.softh.app.helpers;

import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.face.contract.Face;

/**
 * Created by softh on 28.12.2016.
 */

public class FaceRecognitionResult extends Face {
    private Face[]faces;
    private String message;

    public FaceRecognitionResult(Face[]faces, String message){
        this.faces = faces;
        this.message = message;
    }

    public Face[] getFaces(){
        return faces;
    }

    public String getMessage(){
        return  message;
    }
    public void setMessage(String message){
        this.message = message;
    }
}


