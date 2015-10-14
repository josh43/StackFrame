//
//  SFChatViewController.m
//  GeoServices
//
//  Created by josh mcloskey on 9/25/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFChatViewController.h"
#import "SFTableViewCell.h"
@interface SFChatViewController ()

@end




@implementation SFChatViewController


-(id)initWithName:(NSString *)theName{
    self = [super init];
    if(self){
        _userName = [[NSString alloc]init];
        _userName = theName;
    }
    
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
   
    _region = [[CLCircularRegion alloc]init];
    _texField = [[UITextField alloc]initWithFrame:CGRectMake(0, 0, 300, 200)];
    
    _texField.textColor =([UIColor blackColor]);
    //_texField.frame = CGRectMake(0, 200, 300, 200);
   // _scrollView = [[UIScrollView alloc]init];
    _texField.delegate = self;
    _texField.text = @"Braaaah";
    //[self.view addSubview:_scrollView];
    
    [_texField setReturnKeyType:UIReturnKeyDone];
    [_texField addTarget:self
                  action:@selector(textFieldDidChange:)
        forControlEvents:UIControlEventAllEvents];
    [self.view addSubview:_texField];
    // Got this code from stack overflow, it allows us to see the message we are typing
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWasShown:)
                                                 name:UIKeyboardDidShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillBeHidden:)
                                                 name:UIKeyboardWillHideNotification object:nil];
    // end
    _screenRect = [[UIScreen mainScreen]bounds];

    NSString * magnification;
    if(_screenRect.size.width == 375){
        magnification = @".png";
        _phone_type = IPHONE6;
       // _chatView.frame = CGRectMake(0, 0, 750, 1334);
    }
    else if(_screenRect.size.width == 414){
        magnification = @"@2x.png";
        _phone_type = IPHONE6PLUS;
    }
    [self setUpIconsForDisplayingUsingMag:magnification];
    
    
    [self setupToolBar];
    
    NSTimer *theTimer = [NSTimer scheduledTimerWithTimeInterval:60.0 target:self selector:@selector(updateTime:) userInfo:nil repeats:NO];
    NSTimer *otherTimer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(updateEntries:) userInfo:nil repeats:NO];
    

    
   
}
-(void) updateEntries :(NSTimer*) t{
    if(_pubNubHandler != nil){
        [self reloadTableView];
    }else{
        NSTimer *otherTimer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(updateEntries:) userInfo:nil repeats:NO];

    }
    
}
-(void) updateTime :(NSTimer*) t{
    // thanks to Justin Paulson at stackExchange or overflow
      NSTimer *theTimer = [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(updateTime:) userInfo:nil repeats:NO];
    
NSArray *cells = [self.tableView visibleCells];
    
    for(SFTableViewCell * c in cells){
        [c updateSFMessageTime];
    }

}
// Got this code from stack overflow, it allows us to see the message we are typing

- (void)keyboardWasShown:(NSNotification*)aNotification
{
    
}

// Called when the UIKeyboardWillHideNotification is sent
- (void)keyboardWillBeHidden:(NSNotification*)aNotification
{
    [_texField resignFirstResponder];
   
}
//end

// These are delegate methods registerd with UITexView

-(void)textFieldDidBeginEditing:(UITextField *)textField{
    
}
-(IBAction)textFieldDidEndEditing:(UITextView *)sender{
    [self publish];
    NSLog(@"Done editing \n");
}
-(IBAction)textFieldDidChange:(UITextView *)sender{
    
    //NSLog(@"%@",textF);
}
// End delegate methods



-(void)setUpIconsForDisplayingUsingMag : (NSString *) magnification{

    NSString * pathDir = [[[NSBundle mainBundle] resourcePath]stringByAppendingPathComponent:@"AppImages"];
    NSString * dir = [NSString stringWithFormat:@"%@/",pathDir];
   // NSString * dir = @"/Users/josh43/Desktop/stackTemp/StackFrame/StackFrame/images/AppImages/";
    //_stackFrameTopBar.contentMode = nil;
    //_stackFrameTopBar = CGRectMake(0, 0, 175, 327);
    
    
    _stackFrameTopBar = [[UIImage alloc]initWithContentsOfFile:[NSString stringWithFormat:@"%@%@%@", dir,@"Topbar",magnification]];
    if(_stackFrameTopBar == nil){
        NSLog(@"Error loading topbar image\n");
    }
    
    switch (_phone_type) {
        case IPHONE6: {
            
        
            
            UIImageView * theView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 375, 65)];
            _tableView = [[UITableView alloc]initWithFrame:CGRectMake(0, 65, 375, 544)];
            // this if for iphone 6
            _tableView.rowHeight = 91;
            
            _tableView.delegate = self;
            _tableView.dataSource = self;
            
            _toolBar = [[UIToolbar alloc]initWithFrame:CGRectMake(0, (544+65), 375, 59)];
            theView.image = _stackFrameTopBar;
            [self.view addSubview:theView];
            [self.view addSubview:_tableView];
            [self.view addSubview:_toolBar];
            break;
        }
        case IPHONE6PLUS:{
          
            
            // top bar
            UIImageView * theView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, (414), (70))];
            // NOTE in points: 414 × 738 px
            _tableView = [[UITableView alloc]initWithFrame:CGRectMake(0, 70, (414), (1793/3))];
            // this if for iphone 6
            // 1793 /3 is height of tableView
            _tableView.rowHeight = (100);
            _tableView.delegate = self;
            _tableView.dataSource = self;

            _toolBar = [[UIToolbar alloc]initWithFrame:CGRectMake(0, ((1793/3)+70), 414, (196/3))];
            theView.image = _stackFrameTopBar;
            [self.view addSubview:theView];
            [self.view addSubview:_tableView];
            [self.view addSubview:_toolBar];
        }
            break;
        default:
            break;
    }
    // This makes it insert at the bottom
    //_tableView.transform = CGAffineTransformMakeScale (-1,-1);
    
    
    //_stackFrameTopBar.bounds = CGRectMake(0, 0, 250, 127);
    



    _composeIcon = [[UIImage alloc]initWithContentsOfFile:[NSString stringWithFormat:@"%@%@%@", dir,@"Compose",magnification]];
    if(!_composeIcon){
        NSLog(@"Error loading compose icon\n");
        
    }
    _worldIcon = [[UIImage alloc]initWithContentsOfFile:[NSString stringWithFormat:@"%@%@%@", dir,@"World",magnification]];
    _cameraIcon = [[UIImage alloc]initWithContentsOfFile:[NSString stringWithFormat:@"%@%@%@", dir,@"Camera",magnification]];
    _middleFingerIcon= [[UIImage alloc]initWithContentsOfFile:[NSString stringWithFormat:@"%@%@%@", dir,@"mfinger",magnification]];
    _coolBrahIcon = [[UIImage alloc]initWithContentsOfFile:[NSString stringWithFormat:@"%@%@%@", dir,@"coolbra", magnification]];
    _commentIcon = [[UIImage alloc]initWithContentsOfFile:[NSString stringWithFormat:@"%@%@%@", dir,@"Chat",magnification]];

    
    if(!_worldIcon){
        NSLog(@"Error loading _worldIcon icon\n");
        
    }
    if(!_cameraIcon){
        NSLog(@"Error loading _cameraIcon icon\n");
        
    }if(!_middleFingerIcon){
        NSLog(@"Error loading _middleFingerIcon icon\n");
        
    }if(!_coolBrahIcon){
        NSLog(@"Error loading _coolBrahIcon icon\n");
        
    }if(!_commentIcon){
        NSLog(@"Error loading _commentIcon icon\n");
        
    }
    
        
    
    
    
}


-(void)setupToolBar{
    // I really should clean this up
    // BUT YYYYYYYY!??!!
    switch (_phone_type) {
        case IPHONE6:{
            UIButton * sendButton = [UIButton buttonWithType:UIButtonTypeCustom];
            sendButton.frame = CGRectMake(67, 0,50,50);
            [sendButton setImage:_composeIcon forState:UIControlStateNormal];
            UIBarButtonItem * chatItem = [[UIBarButtonItem alloc]initWithCustomView:sendButton];
            
            [sendButton addTarget:self action:@selector(chatButtonTouched:) forControlEvents:UIControlEventTouchUpInside];
            
            
            UIButton * camButton = [UIButton buttonWithType:UIButtonTypeCustom];
            
            camButton.frame = CGRectMake((67+64), 0,50,50);
            [camButton setImage:_cameraIcon forState:UIControlStateNormal];
            UIBarButtonItem * camItem = [[UIBarButtonItem alloc]initWithCustomView:camButton];
            
            [camButton addTarget:self action:@selector(cameraTouched:) forControlEvents:UIControlEventTouchUpInside];
            
            UIButton * worldButton = [UIButton buttonWithType:UIButtonTypeCustom];
            worldButton.frame = CGRectMake((67+64+64), 0,50,50);
            [worldButton setImage:_worldIcon forState:UIControlStateNormal];
            UIBarButtonItem * worldItem = [[UIBarButtonItem alloc]initWithCustomView:worldButton];
            [worldButton addTarget:self action:@selector(worldTouched:) forControlEvents:UIControlEventTouchUpInside];
            UIBarButtonItem * seperator = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
            seperator.width = 40;
            
            NSArray * buttonItems = [[NSArray alloc]initWithObjects:seperator, chatItem,seperator, worldItem, seperator, camItem,nil];
            [_toolBar setItems:buttonItems];
        }
            break;
        case IPHONE6PLUS : {
            UIButton * sendButton = [UIButton buttonWithType:UIButtonTypeCustom];
            sendButton.frame = CGRectMake(73, 0,50,50);
            [sendButton setImage:_composeIcon forState:UIControlStateNormal];
            UIBarButtonItem * chatItem = [[UIBarButtonItem alloc]initWithCustomView:sendButton];
            
            [sendButton addTarget:self action:@selector(chatButtonTouched:) forControlEvents:UIControlEventTouchUpInside];
            
            
            UIButton * camButton = [UIButton buttonWithType:UIButtonTypeCustom];
            
            camButton.frame = CGRectMake((73+70), 0,50,50);
            [camButton setImage:_cameraIcon forState:UIControlStateNormal];
            UIBarButtonItem * camItem = [[UIBarButtonItem alloc]initWithCustomView:camButton];
            
            [camButton addTarget:self action:@selector(cameraTouched:) forControlEvents:UIControlEventTouchUpInside];
            
            UIButton * worldButton = [UIButton buttonWithType:UIButtonTypeCustom];
            worldButton.frame = CGRectMake((73+72+72), 0,50,50);
            [worldButton setImage:_worldIcon forState:UIControlStateNormal];
            UIBarButtonItem * worldItem = [[UIBarButtonItem alloc]initWithCustomView:worldButton];
            [worldButton addTarget:self action:@selector(worldTouched:) forControlEvents:UIControlEventTouchUpInside];
            UIBarButtonItem * seperator = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
            seperator.width = 50;
            
            NSArray * buttonItems = [[NSArray alloc]initWithObjects:seperator, chatItem,seperator, worldItem, seperator, camItem,nil];
            [_toolBar setItems:buttonItems];

        }
        default:
            break;
    }
  
    
   // UIBarButtonItem * sendMessageButton = [[UIBarButtonItem alloc]initWithImage: style:UIBarButtonItemStylePlain target:self action:typeMessage];
    
}
-(IBAction)worldTouched:(id)sender{
    NSLog(@"How dare though touch thy world!! in such a horrific way\n");
}
-(IBAction)chatButtonTouched:(id)sender{
    NSLog(@"You hit zee chatButton \n");
   
    _texField.keyboardAppearance = UIKeyboardAppearanceDark;
    //[self publish];
    _texField.hidden = NO;
    
   // _texField.textColor = [UIColor blackColor];
    //_texField.frame =CGRectMake(0, 300, _texField.frame.size.width, _texField.frame.size.height);
    [_texField becomeFirstResponder];
    //_texField.hidden = NO;
}

-(IBAction)cameraTouched:(id)sender{
    
    // Right now I will just send a picture of the good old eric cartmann braaahh
    NSString * path =[[NSBundle mainBundle]resourcePath];
                          
    NSString * dir = [NSString stringWithFormat:@"%@/cartman.jpg",path];
    UIImage * cartman = [[UIImage alloc]initWithContentsOfFile:dir];
    NSData * imgData = UIImageJPEGRepresentation(cartman, 0.2); // 0 is worst quality and 1 is best
    if(imgData.length < (1024 * 23)){ // less than 23 kilobyes
        NSString * data = [imgData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
        //NSDictionary * dict = [_pubNubHandler createDictionaryWith:_userName withAction:ACTION_USER_POSTED_PICTURE withData:imgData];
        NSDictionary * dict = [_pubNubHandler createDictionaryWith:_userName withAction:ACTION_USER_POSTED_PICTURE withData:data];
        [_pubNubHandler publish:dict];
    NSLog(@"Hooorayy i was touched\n");
    }else{
        NSLog(@"The image is too big you will need to code compression techniques\n");
    }
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
-(void)setUserName:(NSString *)userNameToAssign{
    if(self.userName == nil)
        self.userName = [[NSString alloc]init];
    self.userName = userNameToAssign;
}
-(void)setUserRegion:(CLCircularRegion *)userLocationToAssign{
    self.region = userLocationToAssign;
}
// setting number rows

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return [[_myStore messageStore] count];
}

// Testing out my new SFTableViewCell Custom class

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    NSString * simpleID = @"simpleID";
    
    // This makes it so the table view does not store the whole entire list, it will only store the ones that are visible
    // and the ones that are close to visible
    UITableViewCell * cell = [_tableView dequeueReusableCellWithIdentifier:simpleID];
    SFMessage * message = [_myStore messageStore][indexPath.row];
    
    if(cell == nil){
        
        
        if([[message messageStorage] isMemberOfClass:[SFChatMessage class]]){
            
            cell = [[SFTableViewCell alloc]initMessageCellWithStyle:UITableViewCellStyleDefault reuseIdentifier:simpleID iOSDevice:_phone_type userPic:nil likeButton:_coolBrahIcon dislikeButton:_middleFingerIcon chatButtonImage:_commentIcon];
            
        }else{
            cell = [[SFImageTableCell alloc]initMessageCellWithStyle:UITableViewCellStyleDefault reuseIdentifier:simpleID iOSDevice:_phone_type userPic:nil likeButton:_coolBrahIcon dislikeButton:_middleFingerIcon chatButtonImage:_commentIcon];
        }
        
        //24E3EB
        CGFloat r,b,g;
        r = 0x000024/256.0f;
        b = 0x0000E3/256.0f;
        g = 0x0000EB/256.0f;
        UIColor * col= [UIColor colorWithRed:r green:g blue:b alpha:1.0f];
        // when we initialize it it is nil
        // cell.contentView.transform = CGAffineTransformMakeScale (1,-1);
        // if you have an accessory view
        //cell.accessoryView.transform = CGAffineTransformMakeScale (1,-1);
        
        cell.editing = NO;
        
        // [cell.contentView.layer setBorderColor:col.CGColor];
        [cell.contentView.layer setBorderColor:col.CGColor];
        [cell.contentView.layer setBorderWidth:1.0f];
        
        
        
    }
    if([cell isKindOfClass:[SFTableViewCell class]]){
        SFTableViewCell * theC = (SFTableViewCell *)cell;
        if([message.messageStorage isKindOfClass:[SFChatMessage class]]){
            [theC setMessageSFMessage:message];
        }
    }else if([cell isKindOfClass:[SFImageTableCell class]]){
        SFImageTableCell * theC = (SFImageTableCell *)cell;
        if([message.messageStorage isKindOfClass:[SFPictureMessage class]]){
            [theC setMessageSFMessage:message];
        }
    
    }
    
    return cell;
    
}

-(IBAction)middleFingerTouched:(id)sender{
    NSLog(@"You touched the middle finger brah \n");
}
-(void)printMessageList{
    
    for(SFMessage * actual in _messages){
        if([[actual messageStorage] isKindOfClass:[SFChatMessage class]]){
            SFChatMessage * m1 = [actual messageStorage];
                // then print!
            NSString * toPrint = [m1 chatMessage];
            NSLog(@"%@\n",toPrint);
            }
    }
    
    
}

#pragma mark - PubNubCub
-(void)publish{
    if(_pubNubHandler != nil){
        NSDictionary * dict = [_pubNubHandler createDictionaryWith:_userName withAction:ACTION_USER_POSTED_MESSAGE withData:_texField.text];
        
        [_pubNubHandler publish:dict];
}
  
    _texField.text = @"";

}
-(void) reloadTableView{
    [self.tableView reloadData];
}
-(void)client:(PubNub *)client didReceiveMessage:(PNMessageResult *)message{
    NSDictionary * actual = message.data.message;
    NSString * theT = nil;
    @try {
        theT = actual[@"text"];

    }
    @catch (NSException *exception) {
        NSLog(@"Error no text mesage bro\n");
    }
    @finally {
        if(theT){
            SFMessage * messageToAdd = [[SFMessage alloc]initChatMessageWithString:theT];
            
            [self.messages addObject:messageToAdd];
            [self.tableView reloadData];
            
        }
    }
    
   
   
    
    
   
    
   
    
}





@end
