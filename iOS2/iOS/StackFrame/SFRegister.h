//
//  SFRegister.h
//  StackFrame
//
//  Created by josh mcloskey on 10/31/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SFAppDelegate.h"
@interface SFRegister : UIViewController
@property (weak, nonatomic) IBOutlet UITextField *userNameField;
@property (weak, nonatomic) IBOutlet UITextField *passwordOneField;
@property (weak, nonatomic) IBOutlet UITextField *passwordTwoField;
@property(weak,nonatomic) id<SFCallBackDelegate> delegate;

@property (strong,nonatomic) NSString * myObjectID;


@property BOOL doneRegistering;
@end
