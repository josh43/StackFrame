//
//  SFLoginViewController.h
//  GeoServices
//
//  Created by josh mcloskey on 9/25/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SFCallback.h"
@interface SFLoginViewController : UIViewController
//@property (strong, nonatomic) IBOutlet UIView *loginView;

@property(assign,nonatomic) id<SFCallBackDelegate> delegate;

@property (strong, nonatomic) IBOutlet UIView *loginView;
@property (weak, nonatomic) IBOutlet UITextField *userName;
@property (weak, nonatomic) IBOutlet UITextField *passWord;
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UIButton *createProfileButton;
@property(strong, nonatomic) NSString * uName;
@property(strong,nonatomic) NSString * pWord;
@property (strong,nonatomic)NSString * myObjectID;
@property BOOL doneRegistering;

-(BOOL) validateInformation:(NSString * )userName :(NSString *) password;
@end
