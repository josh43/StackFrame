//
//  SFRegister.m
//  StackFrame
//
//  Created by josh mcloskey on 10/31/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#import "SFRegister.h"

@interface SFRegister ()

@end

@implementation SFRegister

- (void)viewDidLoad {
    _doneRegistering = NO;
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}
- (IBAction)signUp:(id)sender {
    NSLog(@"Wow if this works \n");
    
    
    
    @try {
        
        
        if(_passwordOneField.text != _passwordTwoField.text){
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Password" message:@"Your passwords do not match brah" delegate:self cancelButtonTitle:@"cancel" otherButtonTitles:nil];
            // optional - add more buttons:
            [alert addButtonWithTitle:@"Yes"];
            [alert show];
            
        }
        else {
            
            // super hacky
            // [self otherMethod];
            //return;
            
            
            NSString * kBaseURL = [NSString stringWithFormat:@"http://localhost:3000/register/%@/%@",self.userNameField.text,self.passwordOneField.text];
            
            
            NSURL* url = [NSURL URLWithString:kBaseURL];
            
            NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:url];
            request.HTTPMethod = @"GET"; //2
            
            //3
            
            NSHTTPURLResponse * resp = nil;
            NSError * err = [[NSError alloc]init];
            NSData* config = [NSURLConnection sendSynchronousRequest:request returningResponse:&resp error:&err];
            if(config){
                if(err){
                    NSLog(@"Error someone\n");
                }
                NSDictionary* responseDict = [NSJSONSerialization JSONObjectWithData:config options:0 error:NULL];
                if(responseDict[@"ObjectID"]){
                    if([responseDict[@"ObjectID"] isKindOfClass:[NSString class]]){
                        _myObjectID = responseDict[@"ObjectID"];
                        NSLog(@"Got the objectID");
                        
                        NSString * strongPointa = [[NSString alloc]initWithString:_userNameField.text];
                        NSString * passWord = [[NSString alloc]initWithString:_passwordTwoField.text];
                        [_userNameField resignFirstResponder];
                        [_passwordOneField resignFirstResponder];
                        [_passwordTwoField resignFirstResponder];
                        //Code that presents or dismisses a view controller here
                        
                        [_delegate appDoneRegistering:self :strongPointa :passWord];
                        
                        return;
                    }
                    
                    
                    
                }
            }
            
            
            
        }
        
    }
    @catch(NSException * e){
        NSLog(@"Cathcing exception %@",e);
    }
}





- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
