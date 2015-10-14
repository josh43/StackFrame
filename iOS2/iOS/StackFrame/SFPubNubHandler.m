//
//  SFPubNubHandler.m
//  StackFrame
//
//  Created by josh mcloskey on 10/12/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFPubNubHandler.h"
#import "SFChatViewController.h"

@implementation SFPubNubHandler
-(id)init{
    self = [super init];
    PNConfiguration *configuration = [PNConfiguration configurationWithPublishKey:@"pub-c-1a6a9ba9-b6b2-45aa-8dbe-7f6b398fdf14"
                                                                     subscribeKey:@"sub-c-b5dbfc4e-6252-11e5-8a6a-02ee2ddab7fe"];
    
    self.client = [PubNub clientWithConfiguration:configuration];
    [self.client addListener:self];
    [self.client subscribeToChannels:@[@"UAChat"] withPresence:YES];
    self.currMessageCount = 0;
    
    
    // get old messages
    
    NSNumber *startDate = @((unsigned long long)([[NSDate dateWithTimeIntervalSinceNow:-(60*60)] timeIntervalSince1970]*10000000)); // pretty sure this will get messages
    // within the last 60 minutes
    NSNumber *endDate = @((unsigned long long)([[NSDate date] timeIntervalSince1970]*10000000));
    
    [self.client historyForChannel:@"UAChat" start:startDate end:endDate limit:100
                    withCompletion:^(PNHistoryResult *result, PNErrorStatus *status) {
                        
                        // Check whether request successfully completed or not.
                        if (!status.isError) {
                            for( NSDictionary * actual in result.data.messages){
                                   // NSNumber * num = 3;//[message.data.message timetoken];
                                    NSInteger toInt = 4;
                                    NSString * messageID;
                                    NSString * messageAction = nil;
                                    NSString * userName = nil;
                                    id  data = nil;
                                    NSInteger epoch = toInt / (10000000);
                                    // epoch -= (60*60*7); i thought it was off by that amt
                                    NSDate * myDate = [NSDate dateWithTimeIntervalSince1970:epoch];
                                    NSLog(@"%@",myDate);
                                    int action = -5;
                                    @try {
                                        messageAction = actual[@"action"];
                                        userName = actual[@"username"];
                                        data = actual[@"data"];
                                        action = (int)[messageAction integerValue ];
                                        messageID = actual[@"messageid"];
                                        
                                        
                                    }
                                    @catch (NSException *exception) {
                                        NSLog(@"Error no text mesage bro\n");
                                    }
                                    @finally {
                                        if(messageAction && userName){
                                            SFMessage * messageToAdd = nil;
                                            switch (action) {
                                                case ACTION_USER_POSTED_MESSAGE:{
                                                    NSLog(@"boom boom boom\n");
                                                    messageToAdd = [[SFMessage alloc]initChatMessageWithString:userName message:(NSString *)data withID:[messageID integerValue] withTime:myDate withLikes:0 withDisLikes:0];
                                                    break;
                                                }
                                                case ACTION_USER_POSTED_PICTURE:{
                                                    //messageToAdd = [[SFMessage alloc]initPictureMessage:userName picture:(NSString *)data withID:[messageID integerValue] withTime:myDate withLikes:0 withDisLikes:0];
                                                    
                                                    break;
                                                }case ACTION_USER_LIKED:{
                                                    NSLog(@"boom boom boom\n");
                                                    [_messageStore addLikeTo:[messageID integerValue]];
                                                    return;
                                                }case ACTION_USER_DISLIKED:{
                                                    [_messageStore addDisLikeTo:[messageID integerValue]];
                                                    NSLog(@"boom boom boom\n");
                                                    return;
                                                }
                                                default:
                                                    break;
                                            }
                                            //Idea is to have a global store
                                            if(messageToAdd != nil){
                                                [_messageStore.messageStore addObject:messageToAdd];
                                                

                                            }
                               
                                        }
                                       
                            // Handle downloaded history using:
                            //   result.data.start - oldest message time stamp in response
                            //   result.data.end - newest message time stamp in response
                            //   result.data.messages - list of messages
                                }
                            }
                        }else {
                            NSLog(@"OH NOOOOO loading messages from history\n");
                            // Handle message history download error. Check 'category' property to find
                            // out possible issue because of which request did fail.
                            //
                            // Request can be resent using: [status retry];
                        }
                        
     
                        
                    }];
    
        if(_cvc != nil){
        [_cvc reloadTableView];
    }
    
    
    return self;
}
#pragma mark - PubNubCub
-(NSDictionary *) createDictionaryWith : (NSString *) userName withAction: (NSInteger) action withData :(NSString *) data{
   
    NSDictionary * toReturn;
    @try {
       
        NSString * theAction = [NSString stringWithFormat:@"%ld",(long)action];
      
        NSString * messageNumber = [NSString stringWithFormat:@"%ld",_currMessageCount];
        NSString * messageID = [userName stringByAppendingString:messageNumber];
        
        toReturn = [NSDictionary dictionaryWithObjectsAndKeys:userName,@"username",messageID,@"messageid",theAction,@"action",data,@"data", nil];
        
        
        
        return toReturn;
    }
    @catch (NSException *exception) {
        NSLog(@"Error creating dictionary\n");
        return nil;
    }
    @finally {
        
    }
    /*
    NSDictionary *sentToUser = nil;
    @try {
        sentToUser= [NSDictionary
                     dictionaryWithObjectsAndKeys:_texField.text,@"text", nil];
     */
    //Never be reached
    return toReturn;
}

/*
-(NSDictionary *) createDictionaryWithPicture: (NSString *) userName withAction: (NSInteger) action withData :(NSData *) data{
    
    
    NSDictionary * toReturn;
    @try {
        
        NSString * theAction = [NSString stringWithFormat:@"%ld",(long)action];
        
        NSString * messageNumber = [NSString stringWithFormat:@"%ld",_currMessageCount];
        NSString * messageID = [userName stringByAppendingString:messageNumber];
        
        toReturn = [NSDictionary dictionaryWithObjectsAndKeys:userName,@"username",messageID,@"messageid",theAction,@"action",data,@"data", nil];
        
        
        
        return toReturn;
    }
    @catch (NSException *exception) {
        NSLog(@"Error creating dictionary\n");
        return nil;
    }
    @finally {
        
    }
    /*
     NSDictionary *sentToUser = nil;
     @try {
     sentToUser= [NSDictionary
     dictionaryWithObjectsAndKeys:_texField.text,@"text", nil];
 
    //Never be reached
    return toReturn;
    
}
*/

-(void)publish: (NSDictionary *) messageToSend{
    
    
    NSString * str;
    @try {
        // Make sure it is a valid message
        // could probably delete this code..
        str= messageToSend[@"username"];
        str= messageToSend[@"messageid"];
        str= messageToSend[@"action"];
        //str= messageToSend[@"data"];
    }
    @catch (NSException *exception) {
        NSLog(@"Could not send message returning\n");
        return;
    }
    @finally {
        
        [self.client publish:messageToSend toChannel:@"UAChat" storeInHistory:YES withCompletion:^(PNPublishStatus *status) {
            
            // Check whether request successfully completed or not.
            if (!status.isError) {
                self.currMessageCount++;

                // Message successfully published to specified channel.
            }// Request processing failed.
            else {
                NSLog(@"Error publishing message\n");
                // Handle message publish error. Check 'category' property to find out possible issue
                // because of which request did fail.
                //
                // Request can be resent using: [status retry];
            }
        }];
    }
    
    
    //_texField.text = @"";
    
}
// For now message ID will be the userName + his messageID that he sent
// so upon sending more messages he needs to increase it
-(void)client:(PubNub *)client didReceiveMessage:(PNMessageResult *)message{
    NSDictionary * actual = message.data.message;
//timetoken
    // time it was sent
    NSNumber * num = [message.data timetoken];
    NSInteger toInt = [num integerValue];
    NSString * messageID;
    NSString * messageAction = nil;
    NSString * userName = nil;
    NSString * data = nil;
    NSInteger epoch = toInt / (10000000);
   // epoch -= (60*60*7); i thought it was off by that amt
    NSDate * myDate = [NSDate dateWithTimeIntervalSince1970:epoch];
    NSLog(@"%@",myDate);
    int action = -5;
    
    
    
    
    
    @try {
        messageAction = actual[@"action"];
        userName = actual[@"username"];
        data = actual[@"data"];
        action = (int)[messageAction integerValue ];
        messageID = actual[@"messageid"];
        
        
    }
    @catch (NSException *exception) {
        NSLog(@"Error no text mesage bro\n");
    }
    @finally {
        if(num && messageAction && userName){
            SFMessage * messageToAdd = nil;
            switch (action) {
                case ACTION_USER_POSTED_MESSAGE:{
                    NSLog(@"boom boom boom\n");
                    messageToAdd = [[SFMessage alloc]initChatMessageWithString:userName message:data withID:[messageID integerValue] withTime:myDate withLikes:0 withDisLikes:0];
                    break;
                }
                case ACTION_USER_POSTED_PICTURE:{
                    
                    messageToAdd = [[SFMessage alloc]initPictureMessage:userName picture:data withID:[messageID integerValue] withTime:myDate withLikes:0 withDisLikes:0];
                    NSLog(@"boom boom boom\n");
                    break;
                }case ACTION_USER_LIKED:{
                    NSLog(@"boom boom boom\n");
                    [_messageStore addLikeTo:[messageID integerValue]];
                    return;
                }case ACTION_USER_DISLIKED:{
                    [_messageStore addDisLikeTo:[messageID integerValue]];
                    NSLog(@"boom boom boom\n");
                    return;
                }
                default:
                    break;
            }
            //Idea is to have a global store
            if(messageToAdd != nil){
                [_messageStore.messageStore addObject:messageToAdd];
                if(_cvc != nil){
                    [_cvc reloadTableView];
                }
            }
            
             
            
        }
    }
    
    
    
    
    
    
    
    
    
}
- (void)client:(PubNub *)client didReceiveStatus:(PNSubscribeStatus *)status {
    
    if (status.category == PNUnexpectedDisconnectCategory) {
        // This event happens when radio / connectivity is lost
    }
    
    else if (status.category == PNConnectedCategory) {
        
        // Connect event. You can do stuff like publish, and know you'll get it.
        // Or just use the connected event to confirm you are subscribed for
        // UI / internal notifications, etc
        NSString * pubMessage = @"Hello from : ";
       
        [self.client publish:pubMessage toChannel:@"my_channel"
              withCompletion:^(PNPublishStatus *status) {
                  
                  // Check whether request successfully completed or not.
                  if (!status.isError) {
                      
                      // Message successfully published to specified channel.
                  }
                  // Request processing failed.
                  else {
                      NSLog(@"Error connectin for some reason\n");
                      
                      // Handle message publish error. Check 'category' property to find out possible issue
                      // because of which request did fail.
                      //
                      // Request can be resent using: [status retry];
                  }
              }];
    }
    else if (status.category == PNReconnectedCategory) {
        
        // Happens as part of our regular operation. This event happens when
        // radio / connectivity is lost, then regained.
    }
    else if (status.category == PNDecryptionErrorCategory) {
        
        // Handle messsage decryption error. Probably client configured to
        // encrypt messages and on live data feed it received plain text.
    }
    
}

@end
