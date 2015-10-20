//
//  SFMessageStore.m
//  StackFrame
//
//  Created by josh mcloskey on 10/12/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFMessageStore.h"

@implementation SFMessageStore
-(id) init{
    self = [super init];
    
    if(self){
        self.messageStore = [[NSMutableArray alloc]init];
    }
    
    return self;
}
-(id) initWithMessages : (NSMutableArray *) theArr{
    self = [super init];
    
    if(self){
        self.messageStore = [[NSMutableArray alloc]init];
        self.messageStore = theArr;
    }
    
    return self;
}

// worlds shittiest find method
// I could code up a simple hash with chaining
// but my objective c skills are subbb parrrr
-(SFMessage *)  getObjectWithID:(NSInteger )ID{
    for(int i = 0; i < [_messageStore count]; i++){
        if([_messageStore[i] messageID] == ID )
            return _messageStore[i];
    }
    return nil;
}

-(void) addLikeTo:(NSInteger )messageID{
    SFMessage * ptr = [self getObjectWithID:messageID];
    if(ptr == nil){
        NSLog(@"Could not find message  to add a like\n :(((");
    }
    ptr.numLikes++;
}
-(void) removeLikeTo:(NSInteger  )messageID{
    SFMessage * ptr = [self getObjectWithID:messageID];
    if(ptr == nil){
        NSLog(@"Could not find message  to add a like\n :(((");
    }
    ptr.numLikes--;
}

-(void) addDisLikeTo:(NSInteger )messageID{
    SFMessage * ptr = [self getObjectWithID:messageID];
    if(ptr == nil){
        NSLog(@"Could not find message  to add a like\n :(((");
    }
    ptr.numDisLikes++;
}
-(void) removeDisLikeTo:(NSInteger  )messageID{
    SFMessage * ptr = [self getObjectWithID:messageID];
    if(ptr == nil){
        NSLog(@"Could not find message  to add a like\n :(((");
    }
    ptr.numDisLikes--;
}
@end
