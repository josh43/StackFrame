//
//  AppDelegate.h
//  GeoServices
//
//  Created by Josh  on 9/17/15.
//  Copyright (c) 2015 Josh . All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SFLocation.h"
#import "ViewController.h"
#import <PubNub/PubNub.h>
#import "SFLoginViewController.h"
#import "SFChatViewController.h"

@interface AppDelegate : UIViewController <UIApplicationDelegate, PNObjectEventListener>

// Stores reference on PubNub client to make sure what it won't be released.
@property (nonatomic) PubNub *client;
@property (strong, nonatomic)  UIViewController * viewController;
@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) SFLocation * locationManager;

@end

