/*
Copyright (C) 2015 Apple Inc. All Rights Reserved.
See LICENSE.txt for this sample’s licensing information

Abstract:
Application preview view.
*/

@import AVFoundation;

#import "AAPLPreviewView.h"

@implementation AAPLPreviewView

+ (Class)layerClass
{
	return [AVCaptureVideoPreviewLayer class];
}

- (AVCaptureSession *)session
{
    
	AVCaptureVideoPreviewLayer *previewLayer = (AVCaptureVideoPreviewLayer *)self.layer;
	return previewLayer.session;
}

- (void)setSession:(AVCaptureSession *)session
{
    // self.layer is the core layer used for rendering HOW COOOL !!!!! :OOOO
    // an AVCaptureVideoPreviewLayer is simply the layer being captured
    /*
     AVCaptureVideoPreviewLayer is a subclass of CALayer that you use to display video as it is being captured by an input device.
     
     From apple
     
     AVCaptureSession *captureSession = <#Get a capture session#>;
     AVCaptureVideoPreviewLayer *previewLayer = [AVCaptureVideoPreviewLayer layerWithSession:captureSession];
     UIView *aView = <#The view in which to present the layer#>;
     previewLayer.frame = aView.bounds; // Assume you want the preview layer to fill the view.
     [aView.layer addSublayer:previewLayer];
     */

	AVCaptureVideoPreviewLayer *previewLayer = (AVCaptureVideoPreviewLayer *)self.layer;
	previewLayer.session = session;
}

@end
