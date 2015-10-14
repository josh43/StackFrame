//
//  SFImageTableCell.m
//  StackFrame
//
//  Created by McCloskey, Joshua R - (josh43) on 10/14/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFImageTableCell.h"
#import "TypeDeclerations.h"

@implementation SFImageTableCell
//
//  SFTableVCTest.m
//  StackFrame
//
//  Created by josh mcloskey on 10/12/15.
//  Copyright © 2015 Josh . All rights reserved.
//











- (void)awakeFromNib {
    // Initialization code
}


-(void) setMessageSFMessage :(SFMessage *) theMessage{
    if(theMessage == nil){
        NSLog(@"You are passing an empty message\n returning\n");
        return;
        
    }
    _myPost = theMessage;
    self.userName.text = [_myPost name];
    self.postTime.text = [NSString stringWithFormat:@"%d m ago",[_myPost getMinutesSincePosted]];
        if([[_myPost messageStorage] isKindOfClass:[SFPictureMessage class]]){
       
            self.theDisplayPic = [_myPost messageStorage];
            self.displayPictureView.image= self.theDisplayPic;
        
        }
    
        self.numberLikes.text = [NSString stringWithFormat:@"%ld",[_myPost numLikes]];
    
    self.numberDislikes.text = [NSString stringWithFormat:@"%ld",[_myPost numDisLikes]];
    
}
-(id) initMessageCellWithStyle : (UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
                     iOSDevice : (int) iOSDeviceID
                        userPic:(UIImage * )userPic likeButton :(UIImage *)    likeButton dislikeButton :(UIImage *) dislkeButton chatButtonImage:(UIImage *) chatImage;
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        self.middleFingerImage = dislkeButton;
        self.numberLikesImage = likeButton;
        self.userImage = userPic;
        self.chatButtonImage = chatImage;
        self.theDisplayPic= nil;
        //self.displayPicture = nil;
        self.displayPictureView = [[UIImageView alloc]init];
        
        self.likeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [self.likeButton setImage:self.numberLikesImage forState:UIControlStateNormal];
        [self.likeButton addTarget:self action:@selector(likedMe:) forControlEvents:UIControlEventTouchUpInside];
        
        self.dislikeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [self.dislikeButton setImage:dislkeButton forState:UIControlStateNormal];
        [self.dislikeButton addTarget:self action:@selector(disLikedMe:) forControlEvents:UIControlEventTouchUpInside];
        
        self.chatButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [self.chatButton setImage:self.chatButtonImage forState:UIControlStateNormal];
        [self.chatButton addTarget:self action:@selector(chatButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        self.canDisLike = true;
        self.canLike = true;
        
        
        
        /*
         
         
         UIButton * camButton = [UIButton buttonWithType:UIButtonTypeCustom];
         
         camButton.frame = CGRectMake((73+70), 0,50,50);
         [camButton setImage:_cameraIcon forState:UIControlStateNormal];
         UIBarButtonItem * camItem = [[UIBarButtonItem alloc]initWithCustomView:camButton];
         
         [camButton addTarget:self action:@selector(cameraTouched:) forControlEvents:UIControlEventTouchUpInside];
         
         */
        
        
        self.userName = [[UILabel alloc]init];
        
        self.postTime = [[UILabel alloc]init];
        
        
        
        self.numberLikes = [[UILabel alloc]init];
        
        
        self.numberDislikes = [[UILabel alloc]init];
        
        
        switch (iOSDeviceID) {
            case IPHONE6:{
                [self setUpIphoneSix];
                break;
            }case IPHONE6PLUS:{
                [self setUpIphoneSixPlus];
                break;
            }
            default:{
                NSLog(@"Error device not supported\n");
                return nil;
            }
        }
    }else{
        NSLog(@"Error initializing the SFtableViewCell\n");
    }
    
    
    
    
    
    return self;
    
}
-(void) updateSFMessageTime{
    if(_myPost != nil){
        _postTime.text = [NSString stringWithFormat:@"%d m ago",[_myPost getMinutesSincePosted]];
        
    }
}
-(IBAction)likedMe :(id)sender{
    if(_canLike){
        NSLog(@"You hit the like button woooop\n");
        _myPost.numLikes++;
        self.numberLikes.text = [NSString stringWithFormat:@"%ld",_myPost.numLikes];
        _canLike = false;
    }
}
-(IBAction)disLikedMe :(id)sender{
    if(_canDisLike){
        NSLog(@"You hit the dislike button SO MEAN\n");
        _myPost.numDisLikes++;;
        self.numberDislikes.text = [NSString stringWithFormat:@"%ld",self.myPost.numDisLikes];
        
        _canDisLike = false;
        
        CABasicAnimation* fadeAnim = [CABasicAnimation animationWithKeyPath:@"backgroundColor"];
        fadeAnim.fromValue = [CIColor colorWithRed:0.3f green:0.6f blue:0.6f alpha:1.0f];
        
        
        fadeAnim.toValue =  [CIColor colorWithRed:0.995 green:0.0 blue:0.0 alpha:1.0];
        
        fadeAnim.duration = 1.0;
        [_dislikeButton.layer addAnimation:fadeAnim forKey:@"backgroundColor"];
        
    }
}
-(IBAction)chatButtonClicked :(id)sender{
    NSLog(@"You hit the chat button cool bro!!\n");
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    
    // Configure the view for the selected state
}







#pragma boilerplate
-(void) addAllMySubViews{
    
    [self addSubview:self.userName];
    [self addSubview:self.displayPictureView];
    [self addSubview:self.postTime];
    [self addSubview:self.numberLikes];
    [self addSubview:self.numberDislikes];
    [self addSubview:self.likeButton];
    [self addSubview:self.dislikeButton];
    [self addSubview:self.chatButton];
    if(self.userImageButton != nil)
        [self addSubview:self.userImageButton];
  
    
    
}
-(void) setUpIphoneSix{
    // everything is in the form of
    // (int startX, int startY, int width, int height)
    // iphone6 label starts at 25,10, 117 , 49 o
    //                         65 ,10 , 120 , 91
    // remember to addSubviews for buttons, labels, images and so on
    // also to directly change frames;
    //NSString * pathDir = [[[NSBundle mainBundle] resourcePath]stringByAppendingPathComponent:@"/Fonts"];
    
    /*StackFrame[24509:844108] Marker Felt: (
     "MarkerFelt-Thin",
     "MarkerFelt-Wide"
     )
     2015-10-11 22:35:01.729 StackFrame[24509:844108] Orbitron: (
     "Orbitron-Bold",
     "Orbitron-Black"
     )
     
     2015-10-11 22:42:57.729 StackFrame[24846:848010] Helvetica: (
     "Helvetica-Bold",
     Helvetica,
     "Helvetica-LightOblique",
     "Helvetica-Oblique",
     "Helvetica-BoldOblique",
     "Helvetica-Light"
     )
     
     StackFrame[24846:848010] Helvetica Neue: (
     "HelveticaNeue-Italic",
     "HelveticaNeue-Bold",
     "HelveticaNeue-UltraLight",
     "HelveticaNeue-CondensedBlack",
     "HelveticaNeue-BoldItalic",
     "HelveticaNeue-CondensedBold",
     "HelveticaNeue-Medium",
     "HelveticaNeue-Light",
     "HelveticaNeue-Thin",
     "HelveticaNeue-ThinItalic",
     "HelveticaNeue-LightItalic",
     "HelveticaNeue-UltraLightItalic",
     "HelveticaNeue-MediumItalic",
     HelveticaNeue
     )
     
     */
    UIFont * test = [UIFont fontWithName:@"MarkerFelt-Thin" size:12.0f];
    UIFont * test2 = [UIFont fontWithName:@"MarkerFelt-Wide" size:12.0f];
    if(test == nil)
        NSLog(@"Error with test 1\n");
    if(test2 == nil){
        NSLog(@"Error with test 2\n");
    }
    
    
    UIFont * theFont = [UIFont fontWithName:@"HelveticaNeue" size:12.0f];
    
    if(theFont == nil){
        NSLog(@"Error unable to load font\n");
    }
    self.userName.frame = CGRectMake(25/2.0f, 10/2.0f, 117/2.0f, 49/2.0f);
    self.userName.font = theFont;
    
    self.postTime.frame = CGRectMake(35/2.0f, 160/2.0f, 63/2.0f, 28/2.0f);
    self.postTime.font = [UIFont fontWithName:@"Orbitron-Black" size:6];
    
    self.imageView.frame = CGRectMake(148/2.0f, 20/2.0f, 450/2.0f, 87/2.0f);
    
    self.numberLikes.frame = CGRectMake(631/2.0f, 16/2.0f, 71/2.0f, 29/2.0f);
    self.numberLikes.font = theFont;
    
    self.numberDislikes.frame = CGRectMake(631/2.0f, 75/2.0f, 71/2.0f, 29/2.0f);
    self.numberDislikes.font = theFont;
    
    self.likeButton.frame = CGRectMake(695/2.0f, 6/2.0f, 49.79/2.0f, 32.62/2.0f);
    self.dislikeButton.frame = CGRectMake(695/2.0f, 65/2.0f, 36/2.0f, 36/2.0f);
    self.chatButton.frame = CGRectMake(652/2.0f, 117/2.0f, 67.95/2.0f, 55/2.0f);
    
    
    [self addAllMySubViews];
}
-(void) setUpIphoneSixPlus{
    
    UIFont * theFont = [UIFont fontWithName:@"HelveticaNeue" size:13.25f];
    
    if(theFont == nil){
        NSLog(@"Error unable to load font\n");
    }
    self.userName.frame = CGRectMake(6.0f,14.0f,65.58f,27.24f);
    self.userName.font = theFont;
    
    self.postTime.frame = CGRectMake(19.0f,88.0f,34.78,15.7);
    self.postTime.font = [UIFont fontWithName:@"Orbitron-Black" size:6.62f];
    
    self.imageView.frame = CGRectMake(82,11,248.4,48.77);

    self.numberLikes.frame = CGRectMake(348.0,9.0,39.2,16.26);
    self.numberLikes.font = theFont;
    
    self.numberDislikes.frame = CGRectMake(348,42,39.2,16.26);
    self.numberDislikes.font = theFont;
    
    self.likeButton.frame = CGRectMake(380,4.0,(27.48)*1.3,(18.28)*1.5);
    self.dislikeButton.frame = CGRectMake(380,36,(19.87)*1.6,(20.18)*1.4);
    
    
    
    self.chatButton.frame = CGRectMake(360,66,37.37,30.83);
    
    [self addAllMySubViews];
    
}





@end
