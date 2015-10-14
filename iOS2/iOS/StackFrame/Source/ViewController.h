//
//  ViewController.h
//  GeoServices
//
//  Created by Josh  on 9/17/15.
//  Copyright (c) 2015 Josh . All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MKMapView.h>
#import <MapKit/MKUserLocation.h>

@interface ViewController : UIViewController <MKMapViewDelegate>
//@property(nonatomic,strong) UIView * myView;
@property (strong, nonatomic) IBOutlet MKMapView *mapKitView;
@property (strong ,nonatomic) UIBarButtonItem * currLocationButton;
@property (strong, nonatomic) UIToolbar * toolBar;

@property (weak, nonatomic) IBOutlet UIToolbar *mapToolBar;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *currentLocation;
-(void)updateMessageActivity:(NSString*)message;
-(IBAction)currentLocationClicked:(UIBarButtonItem*)sender;

@end

