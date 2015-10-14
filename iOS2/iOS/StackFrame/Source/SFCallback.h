//
//  SFCallback.h
//  GeoServices
//
//  Created by josh mcloskey on 9/26/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
@class SFLoginViewController;
@protocol SFCallBackDelegate <NSObject>

- (void)appFinishedLoadingScreen:(SFLoginViewController *) viewController :(NSString *) userName : (NSString*) password;


@end