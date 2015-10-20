//
//  SFChatViewController.h
//  GeoServices
//
//  Created by josh mcloskey on 9/25/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <UIKit/UIKit.h>
#import <PubNub/PubNub.h>
#import <stdlib.h>
#import <stdio.h>
#import <MapKit/MapKit.h>
#import "SFMessage.h"
#import "TypeDeclerations.h"
#import <QuartzCore/QuartzCore.h>
#import "SFPubNubHandler.h"
#import "SFImageTableCell.h"
#import "SFMessageStore.h"
@interface SFChatViewController : UIViewController
<UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate>

// Stores reference on PubNub client to make sure what it won't be released.
@property (nonatomic,strong) NSString * userName;
@property (nonatomic,strong) CLCircularRegion * region;
@property (strong, nonatomic) IBOutlet UIView *chatView;
@property (strong, nonatomic) IBOutlet UITableView *tableView;

@property (strong, nonatomic) IBOutlet UIToolbar *toolBar;
@property(weak, nonatomic) SFPubNubHandler * pubNubHandler;
//@property (strong, nonatomic) UIScrollView * scrollView;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *chatMessage;
@property (strong,nonatomic) UITextView * textView;
@property (strong, nonatomic) UITextField * texField;
@property(strong, nonatomic) NSMutableArray * messages;
@property(weak,nonatomic) SFMessageStore * myStore;
@property (strong, nonatomic) IBOutlet UIImage *stackFrameTopBar;
@property (strong, nonatomic) UIImage * composeIcon;
@property (strong, nonatomic) UIImage * worldIcon;
@property (strong, nonatomic) UIImage * cameraIcon;
@property (strong, nonatomic) UIImage * middleFingerIcon;
@property (strong, nonatomic) UIImage * coolBrahIcon;
@property (strong, nonatomic) UIImage * commentIcon;
@property int phone_type;

@property CGRect screenRect;


-(id)initWithName:(NSString *)theName;
-(void) updateEntries :(NSTimer*) t;

-(void) updateTime :(NSTimer*) t;
-(void) setUserName :(NSString * )userNameToAssign;
-(void) setUserRegion :(CLCircularRegion *) userLocationToAssign;
-(void) printMessageList;
-(void) setupToolBar;
-(void) publish;
-(void) reloadTableView;

-(IBAction)middleFingerTouched:(id)sender;

-(IBAction)cameraTouched:(id)sender;
-(IBAction)chatButtonTouched:(id)sender;
@end


/*

Image

Image

*/