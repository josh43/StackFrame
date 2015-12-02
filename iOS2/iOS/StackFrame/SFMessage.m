//
//  SFMessage.m
//  StackFrame
//
//  Created by josh mcloskey on 9/29/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFMessage.h"

@implementation SFMessage
const int NAME = 10;
// Messages will at least always have the form
// UserName --- who posted
// MessageID
// postedTime

// A message recieved from pubnub will always have the form
// {userName:"string_here" messageID:"MessageID" action "int" data:"Data"
- (instancetype)init
{
    self = [super init];
    _name = [[NSString alloc]init];
    _messageStorage = nil;
    _datePosted = [[NSDate alloc]init];

    _numDisLikes = 0;
    _numLikes = 0;
    _messageType =0;
    _messageStorage = nil;
    return self;
}
// No idea why it looks soooo sheety
-(instancetype)initChatMessageWithString:(NSString *) theT{
    self = [self init];
    
    self.messageStorage = [[SFChatMessage alloc]initWithString:theT];
    self.messageType = CHAT_MESSAGE_ID;
    return self;
}
-(instancetype)initChatMessageWithString:(NSString *) userName message :(NSString *)theMessage
                                 withID :(NSInteger ) messageID withTime:(NSDate *) time withLikes:(NSInteger )likes
                            withDisLikes:(NSInteger) dislikes
                         withUserPicture:(UIImage *) myPicture
{
    

    self = [self init];
    _myPictureImage = myPicture;
    _messageID = messageID;
    _datePosted = time;
    _numLikes = likes;
    _numDisLikes = dislikes;
    _name = userName;
   
    self.messageStorage = [[SFChatMessage alloc]initWithString:theMessage];
    self.messageType = CHAT_MESSAGE_ID;
    
    return self;
}
-(instancetype)initPictureMessage       :(NSString *) userName picture :(NSString*)thePicture
                                 withID :(NSInteger) messageID withTime:(NSDate *) time
                               withLikes:(NSInteger )likes
                            withDisLikes:(NSInteger) dislikes{
    
    
    self = [self init];
    
    _messageID = messageID;
    _datePosted = time;
    _numLikes = likes;
    _numDisLikes = dislikes;
    _name = userName;
    NSData * data = [[NSData alloc]initWithBase64EncodedString:thePicture options:NSDataBase64DecodingIgnoreUnknownCharacters];
    
    self.messageStorage = [[SFPictureMessage alloc]initWithData:data];
    self.messageType = PICTURE_MESSAGE_ID;
    return self;
    
}

-(int) getMinutesSincePosted{
    if(self.datePosted != nil){
        NSTimeInterval  interval =[self.datePosted timeIntervalSinceNow];
        
        int mins = interval/60.0f;
        return -mins;
    }
    NSLog(@"Error with time posted\ n");
    return 0;
}
@end
@implementation SFPictureMessage : UIImage
//-(instancetype) initWithData:(NSData *)data;
@end

@implementation SFChatMessage

-(instancetype)init{
    self = [super init];

    return self;
}
-(instancetype)initWithString:(NSString *) theStr{
    
    self = [super init];
    self.chatMessage = [[NSString alloc]initWithString:theStr];
    
    return self;
}


@end