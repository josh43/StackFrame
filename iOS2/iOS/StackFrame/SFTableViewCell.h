//
//  SFTableVCTest.h
//  StackFrame
//
//  Created by josh mcloskey on 10/12/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SFMessage.h"
@interface SFTableViewCell : UITableViewCell
// This class is ment to model my SFMessage
// Wierd things will happen when deleting the data while all this sheet is pointed at it

@property CGFloat cellHeight;
@property CGFloat cellWidth;
// We will only ever keep weak references to these images
// as we do not want to recopy UIImages multiple times
// it will take up too much memory. Instead we will load these images once
// and set their references
@property (weak,nonatomic) UIImage * middleFingerImage;
@property (weak, nonatomic) UIImage * numberLikesImage;
@property (weak, nonatomic) UIImage * userImage;
@property (weak, nonatomic) UIImage * chatButtonImage;

@property (weak, nonatomic) UIImageView * displayPicture;

// We may have to change these or delete them entirely
// so we keep a strong reference
@property (strong, nonatomic) UILabel * userName;
@property (strong, nonatomic) UILabel * postTime;
@property (strong, nonatomic) UILabel * numberLikes;
@property (strong, nonatomic) UILabel * numberDislikes;
@property (strong, nonatomic) UILabel * chatMessage;

@property (strong, nonatomic) UIButton * userImageButton;
@property (strong, nonatomic) UIButton * likeButton;
@property (strong,nonatomic) UIButton * dislikeButton;
@property (strong, nonatomic) UIButton * chatButton;
@property(nonatomic) BOOL canLike;
@property(nonatomic) BOOL canDisLike;

@property(weak, nonatomic) SFMessage * myPost;
// super organized riiight
// you like inheritence? siiiike not using it
-(id) initMessageCellWithStyle : (UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
                     iOSDevice : (int) iOSDeviceID
                        userPic:(UIImage * )userPic likeButton :(UIImage *)    likeButton dislikeButton :(UIImage *) dislkeButton chatButtonImage:(UIImage *) chatImage;


-(void) setMessageSFMessage :(SFMessage *) theMessage;
-(void) updateSFMessageTime;
@end
