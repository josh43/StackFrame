//
//  AppDelegate.m
//  GeoServices
//
//  Created by Josh  on 9/17/15.
//  Copyright (c) 2015 Josh . All rights reserved.
//

#import "AppDelegate.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
   /*
    
    _view = [[ViewController alloc]init];
    //[_window addSubview:view]; this seems dumb because i dont think the delegate has a view :))))
    _locationManager = [[SFLocation alloc]init];
    [_locationManager startStandardUpdates];
    
    
    // porn hub code
    
    PNConfiguration *configuration = [PNConfiguration configurationWithPublishKey:@"pub-c-1a6a9ba9-b6b2-45aa-8dbe-7f6b398fdf14"
                                                                     subscribeKey:@"sub-c-b5dbfc4e-6252-11e5-8a6a-02ee2ddab7fe"];
    self.client = [PubNub clientWithConfiguration:configuration];
    [self.client addListener:self];
    [self.client subscribeToChannels:@[@"UAChat"] withPresence:YES];
    
    // end porn hub code
    */
    SFLoginViewController * loginVC = [[SFLoginViewController alloc]init];
    UINavigationController * controller =[[UINavigationController alloc]init];
    [controller addChildViewController:loginVC];
    [controller presentViewController:loginVC animated:NO completion:nil];
    
   /* self.viewController = [[SFLoginViewController alloc]init];

    [self presentViewController:self.viewController animated:NO completion:nil];
   
    SFLoginViewController * addController = [[SFLoginViewController alloc] init];
    
    addController.modalPresentationStyle = UIModalPresentationFullScreen;
    //addController.tr
    //addController.transitionStyle =UIModalTransitionStyleCoverVertical;
    [self presentViewController:addController animated:YES completion:nil];
    
  //  NSString * theMessage = [message.data description];
    */
    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}
//PUBNUB CODE
- (void)client:(PubNub *)client didReceiveMessage:(PNMessageResult *)message {
    
    // Handle new message stored in message.data.message
    if (message.data.actualChannel) {
        
        // Message has been received on channel group stored in
        // message.data.subscribedChannel
    }
    else {
        
        // Message has been received on channel stored in
        // message.data.subscribedChannel
    }
    NSLog(@"Received message: %@ on channel %@ at %@", message.data.message,
          message.data.subscribedChannel, message.data.timetoken);
    /// updateMessageActivity
    //message.data.message
    
   // [_view updateMessageActivity:theMessage];
}
// PUBNUB CODE
- (void)client:(PubNub *)client didReceiveStatus:(PNSubscribeStatus *)status {
    
    if (status.category == PNUnexpectedDisconnectCategory) {
        // This event happens when radio / connectivity is lost
    }
    
    else if (status.category == PNConnectedCategory) {
        
        // Connect event. You can do stuff like publish, and know you'll get it.
        // Or just use the connected event to confirm you are subscribed for
        // UI / internal notifications, etc
        
        [self.client publish:@"Hello from the PubNub Objective-C SDK" toChannel:@"my_channel"
              withCompletion:^(PNPublishStatus *status) {
                  
                  // Check whether request successfully completed or not.
                  if (!status.isError) {
                      
                      // Message successfully published to specified channel.
                  }
                  // Request processing failed.
                  else {
                      
                      // Handle message publish error. Check 'category' property to find out possible issue
                      // because of which request did fail.
                      //
                      // Request can be resent using: [status retry];
                  }
              }];
    }
    else if (status.category == PNReconnectedCategory) {
        
        // Happens as part of our regular operation. This event happens when
        // radio / connectivity is lost, then regained.
    }
    else if (status.category == PNDecryptionErrorCategory) {
        
        // Handle messsage decryption error. Probably client configured to
        // encrypt messages and on live data feed it received plain text.
    }
    
}


@end
