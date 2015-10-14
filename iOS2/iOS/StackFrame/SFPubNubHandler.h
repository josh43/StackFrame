//
//  SFPubNubHandler.h
//  StackFrame
//
//  Created by josh mcloskey on 10/12/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <Foundation/Foundation.h>
#import <PubNub/PubNub.h>
#import "TypeDeclerations.h"
#import "SFMessageStore.h"
@class SFChatViewController;


@interface SFPubNubHandler : NSObject
<PNObjectEventListener>
-(id) init;
// Stores reference on PubNub client to make sure what it won't be released.
@property (nonatomic) PubNub *client;
@property (weak,nonatomic) SFMessageStore * messageStore;
@property (weak, nonatomic)SFChatViewController * cvc;
@property(nonatomic) NSInteger currMessageCount;
-(void) publish : (NSDictionary *) messageToSend;
-(NSDictionary *) createDictionaryWith : (NSString *) userName withAction: (NSInteger) action withData :(NSString *) data;
-(NSDictionary *) createDictionaryWithPicture: (NSString *) userName withAction: (NSInteger) action withData :(NSData *) data;
@end
