//
//  SFRegister.m
//  StackFrame
//
//  Created by josh mcloskey on 10/31/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFRegister.h"
#import "SocketIO.h"

@interface SFRegister ()

@end

@implementation SFRegister	

- (void)viewDidLoad {
    _doneRegistering = NO;
    [super viewDidLoad];
    CGFloat r,b,g;
    r = 0x000024/256.0f;
    b = 0x0000E3/256.0f;
    g = 0x0000EB/256.0f;
    UIColor * col= [UIColor colorWithRed:r green:g blue:b alpha:1.0f];
    self.view.backgroundColor =  col;
    // Do any additional setup after loading the view from its nib.
    
    [chatSocket on:@"register" callback:^(NSArray* data, SocketAckEmitter* ack) {
        
        NSLog(@"\n\nREGISTER CALLBACK\n\n");
        
        id json = data[0];
        
        NSLog(@"\n\n\nDATA: %@\n\n\n", data);
        
        if ([[NSString stringWithFormat:@"%@",[json objectForKey:@"token"]] isEqualToString:@"-1"]) {
            [self alertStatus:@"Username is in use" :@"Sign up Failed!" :0];
        }else{
            
            NSString * strongPointa = [[NSString alloc]initWithString:_userNameField.text];
            NSString * passWord = [[NSString alloc]initWithString:_passwordTwoField.text];
            [_userNameField resignFirstResponder];
            [_passwordOneField resignFirstResponder];
            [_passwordTwoField resignFirstResponder];
            //Code that presents or dismisses a view controller here
            [_delegate appDoneRegistering:self :strongPointa :passWord];
        }
        
    }];
}
- (IBAction)signUp:(id)sender {
    NSLog(@"signUp \n");
    if(_passwordOneField.text != _passwordTwoField.text){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Password" message:@"Your passwords do not match." delegate:self cancelButtonTitle:@"Okay"otherButtonTitles:nil];
        [alert show];
    }else {
    
    if([[self.userNameField text] isEqualToString:@""] || [[self.passwordOneField text] isEqualToString:@""] || [[self.passwordTwoField text] isEqualToString:@""]) {
        
        [self alertStatus:@"Please enter Username and Password" :@"Sign up Failed!" :0];
    }else{
    
        NSLog(@"\n\nREGISTER\n\n");
    
        NSString *registerString =  [NSString stringWithFormat:@"[{\"username\":\"%@\", \"password\":\"%@\", \"geoloc\":\"123\"}]",self.self.userNameField.text, self.self.passwordOneField.text];
        NSLog(@"\n\nstringData=%@\n\n", registerString);
        NSArray *registerJSON = [NSJSONSerialization JSONObjectWithData:[registerString dataUsingEncoding:NSUTF8StringEncoding] options:0 error:NULL];
        NSArray *tableData = registerJSON;
        NSLog(@"\n\ntableData=%@\n\n", tableData);
    
        [chatSocket emit:@"register" withItems:registerJSON];
                
            }
    }
    
//    @try {
//        
//        

//            
//            // super hacky
//            // [self otherMethod];
//            //return;
//            
//            
//            NSString * kBaseURL = [NSString stringWithFormat:@"http://localhost:3000/register/%@/%@",self.userNameField.text,self.passwordOneField.text];
//            
//            
//            NSURL* url = [NSURL URLWithString:kBaseURL];
//            
//            NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:url];
//            request.HTTPMethod = @"GET"; //2
//            
//            //3
//            
//            NSHTTPURLResponse * resp = nil;
//            NSError * err = [[NSError alloc]init];
//            NSData* config = [NSURLConnection sendSynchronousRequest:request returningResponse:&resp error:&err];
//            if(config){
//                if(err){
//                    NSLog(@"Error someone\n");
//                }
//                NSDictionary* responseDict = [NSJSONSerialization JSONObjectWithData:config options:0 error:NULL];
//                if(responseDict[@"ObjectID"]){
//                    if([responseDict[@"ObjectID"] isKindOfClass:[NSString class]]){
//                        _myObjectID = responseDict[@"ObjectID"];
//                        NSLog(@"Got the objectID");
//                        
//                        NSString * strongPointa = [[NSString alloc]initWithString:_userNameField.text];
//                        NSString * passWord = [[NSString alloc]initWithString:_passwordTwoField.text];
//                        [_userNameField resignFirstResponder];
//                        [_passwordOneField resignFirstResponder];
//                        [_passwordTwoField resignFirstResponder];
//                        //Code that presents or dismisses a view controller here
//                        
//                        [_delegate appDoneRegistering:self :strongPointa :passWord];
//                        
//                        return;
//                    }
//                    
//                    
//                    
//                }
//            }
//            
//            
//            
//        }
//        
//    }
//    @catch(NSException * e){
//        NSLog(@"Cathcing exception %@",e);
//    }
}





- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

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
