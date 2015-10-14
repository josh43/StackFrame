//
//  SFMessage.h
//  StackFrame
//
//  Created by josh mcloskey on 9/29/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <Foundation/Foundation.h>
#import <stdio.h>
#import "TypeDeclerations.h"
#import "UIKit/UIKit.h"

@interface SFChatMessage : NSObject
@property(nonatomic,strong)NSString * chatMessage;
-(instancetype)initWithString:(NSString *) theStr;

@end
@interface SFPictureMessage : UIImage
//-(instancetype) initWithData:(NSData *)data;
@end

@interface SFMessage : NSObject

-(instancetype)init;
-(instancetype)initChatMessageWithString:(NSString *) theT;
-(instancetype)initChatMessageWithString:(NSString *) userName message :(NSString *)theMessage
                                 withID :(NSInteger) messageID withTime:(NSDate *) time
                               withLikes:(NSInteger )likes
                            withDisLikes:(NSInteger) dislikes;
-(instancetype)initPictureMessage       :(NSString *) userName picture :(NSString*)thePicture
                                 withID :(NSInteger) messageID withTime:(NSDate *) time
                               withLikes:(NSInteger )likes
                            withDisLikes:(NSInteger) dislikes;

@property(nonatomic, strong) NSString * name;
@property(nonatomic) NSDate * datePosted;
@property(nonatomic) NSInteger  numLikes;
@property(nonatomic) NSInteger  numDisLikes;
@property(nonatomic) NSInteger messageID;
@property(nonatomic) NSInteger messageType;
@property(nonatomic, strong) id messageStorage;


-(int) getMinutesSincePosted;
@end
