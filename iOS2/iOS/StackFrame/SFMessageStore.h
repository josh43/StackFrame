//
//  SFMessageStore.h
//  StackFrame
//
//  Created by josh mcloskey on 10/12/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SFMessage.h"

@interface SFMessageStore : NSObject
-(id) init;
// I like to provide direct access for that little bit of extra speeeeed
@property(nonatomic,strong) NSMutableArray * messageStore;
-(void) addLikeTo:(NSInteger )messageID;
-(void) removeLikeTo:(NSInteger )messageID;

-(void) addDisLikeTo:(NSInteger  )messageID;
-(void) removeDisLikeTo:(NSInteger  )messageID;
@end
