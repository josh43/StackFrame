//
//  SFAppDelegate.m
//  GeoServices
//
//  Created by josh mcloskey on 9/26/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFAppDelegate.h"

@implementation SFAppDelegate




- (BOOL)application:(UIApplication *)application
didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    
    _window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch
    
    SFLoginViewController * loginViewC = [[SFLoginViewController alloc]init];
   
    
    /* Test code provided by stackOverFlow checking the fonts that I actually have loaded
     use for debugging
     */
    NSArray *fontFamilies = [UIFont familyNames];
    for (int i = 0; i < [fontFamilies count]; i++)
    {
        NSString *fontFamily = [fontFamilies objectAtIndex:i];
        NSArray *fontNames = [UIFont fontNamesForFamilyName:[fontFamilies objectAtIndex:i]];
        NSLog (@"%@: %@", fontFamily, fontNames);
    }
    
    loginViewC.delegate = self;
    _navigator = [[UINavigationController alloc]initWithRootViewController:loginViewC];
    
    _window.rootViewController = _navigator;

    
    _window.backgroundColor = [UIColor whiteColor];
    [_window makeKeyAndVisible];
    return YES;
    
}
- (void)timerFireMethod:(NSTimer *)timer{
   // [_mainPubNubHandler.cvc reloadTableView];
    
}
-(void)appFinishedLoadingScreen:(SFLoginViewController *)viewController :(NSString *) userName : (NSString *) password{
    NSLog(@"Coolio\n");
    
    _mainMessageStore = [[SFMessageStore alloc]init];
    _mainPubNubHandler = [[SFPubNubHandler alloc]init];
    _mainPubNubHandler.messageStore = _mainMessageStore;
    self.userName = [[NSString alloc]init];
    self.passWord = [[NSString alloc]init];
    self.userName = userName;
    self.passWord = password;
    
    SFChatViewController * chatViewControl = [[SFChatViewController alloc]initWithName:self.userName];
    _mainPubNubHandler.cvc = chatViewControl;
    chatViewControl.myStore = _mainMessageStore;
    chatViewControl.pubNubHandler = _mainPubNubHandler;
   // [chatViewControl setUserName:userName];
    chatViewControl.modalPresentationStyle = UIModalPresentationFullScreen;
    [_navigator presentViewController:chatViewControl animated:YES completion: nil];
    
   // NSTimer *myTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(updateTime:) userInfo:nil repeats:NO];


}

@end
