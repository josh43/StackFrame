//
//  SFAppDelegate.h
//  GeoServices
//
//  Created by josh mcloskey on 9/26/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SFLoginViewController.h"
#import "SFChatViewController.h"
#import "SFCallback.h"
#import "SFMessageStore.h"
#import "SFPubNubHandler.h"//roller.h"
#import "SFRegister.h"

@interface SFAppDelegate : UIResponder <UIApplicationDelegate, SFCallBackDelegate>
@property(nonatomic,strong) UIWindow * window;
@property(nonatomic,strong) UINavigationController * navigator;
@property(nonatomic,strong) NSString * userName;
@property(nonatomic,strong) NSString * passWord;
@property(nonatomic, strong) SFMessageStore * mainMessageStore;
@property(nonatomic, strong) SFPubNubHandler * mainPubNubHandler;

- (void)timerFireMethod:(NSTimer *)timer;
/*
 We need to have something query the server to give us a set of unique message ID's
 say a 100 ints. Because if I say my messageID is 0, how will i determine everyoneElse's
 message ID? I could keep everyones message ID based on their username which could be a temp fix + some appended value but it will be less desireable
 
 
 */
@end
