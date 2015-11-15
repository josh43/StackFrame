//
//  SFLoginViewController.m
//  GeoServices
//
//  Created by josh mcloskey on 9/25/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFLoginViewController.h"
#import "StackFrame-Swift.h"
#import "SocketIO.h"
@interface SFLoginViewController ()


@end

@implementation SFLoginViewController

SocketIOClient* chatSocket;

const int MAX_USERNAME_LENGTH = 16;
const int MAX_PASSWORD_LENGTH = 16;
- (instancetype)init
{

    self = [super init];
    if (!self) {
        NSLog(@"Error loading the view controller beeech\n");
    }
    if(_loginView){
   // self.view = self.loginView;
    }
        
    
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];

    chatSocket = [[SocketIOClient alloc] initWithSocketURL:@"http://nodejs-stackframe.rhcloud.com/" options:@{@"log": @YES, @"forcePolling": @YES}];
    
    [chatSocket on:@"connect" callback:^(NSArray* data, SocketAckEmitter* ack) {
        NSLog(@"CONECTED CONECT CALLBACK");
    }];
    
    [chatSocket connect];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



-(BOOL)validateInformation:(NSString *)userName :(NSString *)password{
    
    
    return true;
}
- (IBAction)createProfile:(id)sender {
    NSLog(@"I was pressed\n");
    [self.delegate appChangeToRegisterState];
}

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wdeprecated-declarations"
- (IBAction)loginPressed:(id)sender {
    
    [chatSocket on:@"register" callback:^(NSArray* data, SocketAckEmitter* ack) {
        NSLog(@"\n\nREGISTER CALLBACK\n\n");
        //_myObjectID = responseDict[@"ObjectID"];
        //NSLog(@"Got the objectID");
        self.doneRegistering = YES;
        self.uName= [[NSString alloc]initWithString:self.userName.text];
        self.pWord = [[NSString alloc ]initWithString:self.passWord.text];
        [self dismissViewControllerAnimated:YES completion:NULL];
        [self.delegate appDoneRegistering:self :self.uName :self.pWord];

    }];
    
    NSLog(@"\n\nREGISTER\n\n");
    
    NSString *registerString =  [NSString stringWithFormat:@"[{\"username\":\"%@\", \"password\":\"%@\", \"geoloc\":\"123\"}]",self.userName.text, self.passWord.text];
    NSLog(@"\n\nstringData=%@\n\n", registerString);
    NSArray *registerJSON = [NSJSONSerialization JSONObjectWithData:[registerString dataUsingEncoding:NSUTF8StringEncoding] options:0 error:NULL];
    NSArray *tableData = registerJSON;
    NSLog(@"\n\ntableData=%@\n\n", tableData);

    [chatSocket emit:@"register" withItems:registerJSON];
    

//    
//    NSInteger success = 0;
//    @try {
//        
//        if([[self.userName text] isEqualToString:@""] || [[self.passWord text] isEqualToString:@""] ) {
//            
//            [self alertStatus:@"Please enter Email and Password" :@"Sign in Failed!" :0];
//            
//        }else {
//            
//    
//            NSString * kBaseURL = [NSString stringWithFormat:@"http://localhost:3000/login/%@/%@",self.userName.text,self.passWord.text];
//    
//    
//    NSURL* url = [NSURL URLWithString:kBaseURL];
//    
//    NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:url];
//    request.HTTPMethod = @"GET"; //2
//    
//     //3
//    
//    
//    NSURLSessionConfiguration* config = [NSURLSessionConfiguration defaultSessionConfiguration];
//    NSURLSession* session = [NSURLSession sessionWithConfiguration:config];
//    
//            
//            
//    NSURLSessionDataTask* dataTask = [session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) { //5
//        NSLog(@"Handler being called\n");
//        if (!error) {
//            NSDictionary* responseDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:NULL];
//            @try {
//                if(responseDict[@"Error"]){
//                    NSString * em = responseDict[@"Error"];
//                    if([em isKindOfClass:[NSString class]])
//                        NSLog(@"%@",em);
//                    
//                    
//                }
//                if(responseDict[@"ObjectID"]){
//                    if([responseDict[@"ObjectID"] isKindOfClass:[NSString class]]){
//                        _myObjectID = responseDict[@"ObjectID"];
//                        NSLog(@"Got the objectID");
//                        self.doneRegistering = YES;
//                        self.uName= [[NSString alloc]initWithString:self.userName.text];
//                        self.pWord = [[NSString alloc ]initWithString:self.passWord.text];
//                        [self dismissViewControllerAnimated:YES completion:NULL];
//                        [self.delegate appDoneRegistering:self :self.uName :self.pWord];
//
//                       
//                    }else
//                        NSLog(@"Error converting format string\n");
//                    
//                }
//                
//            }
//            @catch (NSException *exception) {
//                
//            }
//            @finally {
//              
//            }
//            
//        }
//    }];
//   // [dataTask resume];
//    
//        }}
//    @catch(NSException * hoopla){
//            
//        }
//    @finally {
//        
//        NSLog(@"Power overwhelming\n");
//        }
//    
//  
    
    
}
#pragma GCC diagnostic pop

- (void) alertStatus:(NSString *)msg :(NSString *)title :(int) tag
{
    UIAlertController* alert = [UIAlertController
                                alertControllerWithTitle:title
                                message:msg
                                preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* ok = [UIAlertAction
                         actionWithTitle:@"OK"
                         style:UIAlertActionStyleDefault
                         handler:^(UIAlertAction * action)
                         {
                             [alert dismissViewControllerAnimated:YES completion:nil];
                             
                         }];
    
    [alert addAction:ok];
    [self presentViewController:alert animated:YES completion:nil];
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
